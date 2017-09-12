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

import java.io.{ByteArrayOutputStream, ObjectOutputStream}

import scalikejdbc._

object StatsCollector {
  def main(args: Array[String]): Unit = {
    Class.forName("org.apache.calcite.jdbc.Driver")
    ConnectionPool.singleton("jdbc:calcite:model=src/main/resources/model.json", "user", "pass")
    implicit val session = AutoSession

    val md = ConnectionPool.borrow().getMetaData()
    val tables = md.getTables(null, null, "%", null)
    tables.next; tables.next
    val tableNames = new scala.collection.mutable.MutableList[String]

    while (tables.next) {
      val table = tables.getString(3)
      val columns = sql"""SELECT DISTINCT "columnName", "typeName", "ordinalPosition"
                          FROM "metadata"."COLUMNS"
                          WHERE LOWER("tableName") = LOWER('${SQLSyntax.createUnsafely(table)}')
                          AND LOWER("tableSchem") = LOWER('rubis')
                          ORDER BY "ordinalPosition" ASC""".map(_.toMap).list.apply()
      val rows = sql"""select count(*) from "${SQLSyntax.createUnsafely(table)}"""".map(_.int(1)).single.apply().get

      println(s"${table} ${rows}")
      columns.foreach { column =>
        val columnName = column("columnName").toString
        val distinct = sql"""SELECT COUNT(DISTINCT "${SQLSyntax.createUnsafely(columnName)}") FROM "${SQLSyntax.createUnsafely(table)}"""".map(_.int(1)).single.apply().get
        val maxLength = if (column("typeName").toString startsWith "CHAR(1)") {
          1
        } else {
          sql"""SELECT MAX(CHAR_LENGTH(CAST("${SQLSyntax.createUnsafely(columnName)}" AS VARCHAR(999999999)))) FROM "${SQLSyntax.createUnsafely(table)}"""".map(_.int(1)).single.apply().get
        }
        println(s"${table} ${columnName} ${distinct} ${maxLength}")
      }
    }

    session.close()
    System.exit(0)
  }
}
