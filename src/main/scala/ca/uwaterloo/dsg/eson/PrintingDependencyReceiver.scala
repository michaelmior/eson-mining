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

import collection.JavaConverters._
import scala.collection.mutable.{HashMap, MutableList}

import de.metanome.algorithm_integration.results.{FunctionalDependency, InclusionDependency}

class PrintingDependencyReceiver extends DependencyReceiver {
  val _fds = new HashMap[String, MutableList[FunctionalDependency]]
  val _inds = new MutableList[InclusionDependency]

  def fds: Iterable[FunctionalDependency] = _fds.values.flatten
  def inds: Iterable[InclusionDependency] = _inds

  override def acceptedResult(fd: FunctionalDependency): java.lang.Boolean = true
  override def acceptedResult(ind: InclusionDependency): java.lang.Boolean = true

  override def receiveResult(fd: FunctionalDependency): Unit = {
    val table = fd.getDependant.getTableIdentifier
    if (!_fds.contains(table)) {
      _fds(table) = new MutableList[FunctionalDependency]
    }
    _fds(table) += fd
  }

  override def receiveResult(ind: InclusionDependency): Unit = {
    _inds += ind
  }

  def output() {
    _fds.foreach { case (table, table_fds) =>
      table_fds.filter(_.getDeterminant.getColumnIdentifiers.size > 0).foreach { fd =>
        val detFields = fd.getDeterminant.getColumnIdentifiers.asScala.map(_.getColumnIdentifier)
        val depField = fd.getDependant.getColumnIdentifier
        println(s"${table} ${detFields.mkString(", ")} -> ${depField}")
      }
    }

    println("")

    _inds.foreach { ind =>
      val depTable = ind.getDependant.getColumnIdentifiers.get(0).getTableIdentifier.replaceAll("^\"|\"$", "")
      val depFields = ind.getDependant.getColumnIdentifiers.asScala.map(_.getColumnIdentifier)
      val refTable = ind.getReferenced.getColumnIdentifiers.get(0).getTableIdentifier.replaceAll("^\"|\"$", "")
      val refFields = ind.getReferenced.getColumnIdentifiers.asScala.map(_.getColumnIdentifier)
      if (depTable != refTable) {
        print(s"${depTable}(${depFields.mkString(", ")}) <= ")
        println(s"${refTable}(${refFields.mkString(", ")})")
      }
    }
  }
}
