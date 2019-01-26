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
