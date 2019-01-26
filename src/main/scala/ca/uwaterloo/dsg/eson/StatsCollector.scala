/*
 * Copyright 2017-2019 Michael Mior
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
        val distinct = sql"""SELECT COUNT(DISTINCT "${SQLSyntax.createUnsafely(columnName)}")
                             FROM "${SQLSyntax.createUnsafely(table)}"""".map(_.int(1)).single.apply().get
        val maxLength = if (column("typeName").toString startsWith "CHAR(1)") {
          1
        } else {
          sql"""SELECT MAX(CHAR_LENGTH(CAST("${SQLSyntax.createUnsafely(columnName)}" AS VARCHAR(999999999))))
                FROM "${SQLSyntax.createUnsafely(table)}"""".map(_.int(1)).single.apply().get
        }
        println(s"${table} ${columnName} ${distinct} ${maxLength}")
      }
    }

    session.close()
    System.exit(0)
  }
}
