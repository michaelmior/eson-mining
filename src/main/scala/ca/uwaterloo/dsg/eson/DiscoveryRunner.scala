/*
 * This file is part of the eson-mining distribution (https://github.com/michaelmior/eson-mining).
 * Copyright (c) 2017 Michael Mior.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ca.uwaterloo.dsg.eson

import de.metanome.algorithm_integration.configuration.{ConfigurationSettingDatabaseConnection, ConfigurationSettingTableInput, DbSystem}
import de.metanome.algorithms.binder.BINDERDatabase
import de.metanome.algorithms.tane.TaneAlgorithm
import de.metanome.backend.input.database.{DefaultDatabaseConnectionGenerator, DefaultTableInputGenerator}
import java.sql.{Connection, DriverManager}
import java.util.Properties

class PrivateMethodCaller(x: AnyRef, methodName: String) {
  def apply(_args: Any*): Any = {
    val args = _args.map(_.asInstanceOf[AnyRef])
    def _parents: Stream[Class[_]] = Stream(x.getClass) #::: _parents.map(_.getSuperclass)
    val parents = _parents.takeWhile(_ != null).toList
    val methods = parents.flatMap(_.getDeclaredMethods)
    val method = methods.find(_.getName == methodName).getOrElse(throw new IllegalArgumentException("Method " + methodName + " not found"))
    method.setAccessible(true)
    method.invoke(x, args : _*)
  }
}

class PrivateMethodExposer(x: AnyRef) {
  def apply(method: scala.Symbol): PrivateMethodCaller = new PrivateMethodCaller(x, method.name)
}

object DiscoveryRunner {
  def main(args: Array[String]): Unit = {
    val connString = "jdbc:calcite:model=src/main/resources/model.json"
    val connectionProps = new Properties()
    connectionProps.put("user", "admin")
    connectionProps.put("password", "admin")
    val conn = DriverManager.getConnection(connString, connectionProps)
    val tables = conn.getMetaData().getTables(null, null, "%", null)
    tables.next; tables.next // skip the header
    val tableNames = new scala.collection.mutable.MutableList[String]
    while (tables.next) {
      tableNames += "\"" + tables.getString(3) + "\""
    }
    conn.close

    val db = new ConfigurationSettingDatabaseConnection(connString, "admin", "admin", DbSystem.Oracle)
    tableNames.foreach(tableName => {
      val config = new ConfigurationSettingTableInput(tableName, db)
      val table = new DefaultTableInputGenerator(config)
      val tane = new TaneAlgorithm()
      tane.setRelationalInputConfigurationValue(TaneAlgorithm.INPUT_TAG, table)
      tane.setResultReceiver(new PrintingFunctionalDependencyReceiver)
      tane.execute
    })

    val binder = new BINDERDatabase()
    val dbGen = new DefaultDatabaseConnectionGenerator(connString, "admin", "admin", DbSystem.Oracle)
    (new PrivateMethodExposer(dbGen))('connect)()
    dbGen.getConnection.setAutoCommit(true)
    binder.setStringConfigurationValue(BINDERDatabase.Identifier.DATABASE_TYPE.name, BINDERDatabase.Database.CALCITE.name)
    binder.setDatabaseConnectionGeneratorConfigurationValue(BINDERDatabase.Identifier.INPUT_DATABASE.name, dbGen)
    binder.setStringConfigurationValue(BINDERDatabase.Identifier.DATABASE_NAME.name, "rubis")
    binder.setStringConfigurationValue(BINDERDatabase.Identifier.INPUT_TABLES.name, tableNames: _*)
    binder.setBooleanConfigurationValue(BINDERDatabase.Identifier.DETECT_NARY.name, true)
    binder.setResultReceiver(new PrintingInclusionDependencyReceiver)
    binder.execute

    System.exit(0)
  }
}
