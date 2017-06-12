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

import scala.collection.JavaConverters._

import de.metanome.algorithm_integration.input.RelationalInput

class TestRelationalInput extends RelationalInput {
  def numberOfColumns(): Int = 3
  def columnNames(): java.util.List[String] = List("A", "B", "C").asJava
  def relationName(): String = "Test"

  private final var _data = List(
    List("1", "2", "3"),
    List("2", "3", "4"),
    List("3", "3", "3"),
    List("4", "3", "3")
  )
  private var _position = 0

  def hasNext(): Boolean = {
    _position < _data.size
  }

  def next(): java.util.List[String] = {
    val row = _data(_position).asJava
    _position += 1

    row
  }

  def close(): Unit = ()
}
