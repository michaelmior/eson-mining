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

import collection.JavaConverters._
import com.datastax.driver.core.Cluster
import de.metanome.algorithm_integration.configuration.{ConfigurationSettingDatabaseConnection, ConfigurationSettingTableInput, DbSystem}
import de.metanome.algorithm_integration.result_receiver.{FunctionalDependencyResultReceiver, InclusionDependencyResultReceiver}
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
    val receiver = new PrintingDependencyReceiver
    run("127.0.0.1", 9042, "rubis", receiver)
    receiver.output

    System.exit(0)
  }

  def run(host: String, port: Integer, keyspaceName: String, receiver: DependencyReceiver) {
    Class.forName("org.apache.calcite.jdbc.Driver");
    val session = Cluster.builder().addContactPoint(host).withPort(port).build().connect(keyspaceName)
    val keyspace = session.getCluster().getMetadata().getKeyspace(keyspaceName)

    val connString = "jdbc:calcite:schemaFactory=org.apache.calcite.adapter.cassandra.CassandraSchemaFactory; schema.host=" +
                     host + "; schema.port=" + port + "; schema.keyspace=" + keyspaceName
    val connectionProps = new Properties()
    connectionProps.put("user", "admin")
    connectionProps.put("password", "admin")
    val conn = DriverManager.getConnection(connString, connectionProps)
    val tables = conn.getMetaData().getTables(null, null, "%", null)
    tables.next; tables.next // skip the header
    val tableNames = new scala.collection.mutable.MutableList[String]
    while (tables.next) {
      val table = tables.getString(3)
      tableNames += "\"" + table + "\""

      val cassTable = keyspace.getTable(table)
      val columns = cassTable.getColumns

      val pk = cassTable.getPartitionKey.asScala.map(_.getName)
      val clustering = cassTable.getClusteringColumns.asScala.map(_.getName)
      val keyFields = Set[String]() ++ pk ++ clustering

      print(s"${table}(")
      val fieldNames = columns.asScala.map(column =>
        (if (keyFields(column.getName)) "*" else "") + column.getName
      )
      println(fieldNames.mkString(", ") + ")")
    }
    println("")
    conn.close

    val db = new ConfigurationSettingDatabaseConnection(connString, "admin", "admin", DbSystem.Oracle)
    tableNames.foreach(tableName => {
      val config = new ConfigurationSettingTableInput(tableName, db)
      val table = new DefaultTableInputGenerator(config)
      val tane = new TaneAlgorithm()
      tane.setRelationalInputConfigurationValue(TaneAlgorithm.INPUT_TAG, table)
      tane.setResultReceiver(receiver)
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
    binder.setResultReceiver(receiver)
    binder.execute
  }
}
