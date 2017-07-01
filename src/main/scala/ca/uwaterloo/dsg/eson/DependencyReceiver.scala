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
import scala.collection.mutable.{HashMap, MutableList}

import de.metanome.algorithm_integration.results.{FunctionalDependency, InclusionDependency}
import de.metanome.algorithm_integration.result_receiver.{FunctionalDependencyResultReceiver, InclusionDependencyResultReceiver}

class DependencyReceiver extends FunctionalDependencyResultReceiver with InclusionDependencyResultReceiver {
  val fds = new HashMap[String, MutableList[FunctionalDependency]]
  val inds = new MutableList[InclusionDependency]

  override def acceptedResult(fd: FunctionalDependency): java.lang.Boolean = true
  override def acceptedResult(ind: InclusionDependency): java.lang.Boolean = true

  override def receiveResult(fd: FunctionalDependency): Unit = {
    val table = fd.getDependant.getTableIdentifier
    if (!fds.contains(table)) {
      fds(table) = new MutableList[FunctionalDependency]
    }
    fds(table) += fd
  }

  override def receiveResult(ind: InclusionDependency): Unit = {
    inds += ind
  }

  def output() {
    println("$INDS = [")
    inds.foreach { ind =>
      val depTable = ind.getDependant.getColumnIdentifiers.get(0).getTableIdentifier.replaceAll("^\"|\"$", "")
      val depFields = ind.getDependant.getColumnIdentifiers.asScala.map(_.getColumnIdentifier)
      val refTable = ind.getReferenced.getColumnIdentifiers.get(0).getTableIdentifier.replaceAll("^\"|\"$", "")
      val refFields = ind.getReferenced.getColumnIdentifiers.asScala.map(_.getColumnIdentifier)
      println(s"  IND.new('${depTable}', %w(${depFields.mkString(" ")}),")
      println(s"          '${refTable}', %w(${refFields.mkString(" ")})),")
    }
    println("]")

    println("$FDS = {")
    fds.foreach { case (table, table_fds) =>
      println(s"  '${table}' => [")
      table_fds.filter(_.getDeterminant.getColumnIdentifiers.size > 0).foreach { fd =>
        val detFields = fd.getDeterminant.getColumnIdentifiers.asScala.map(_.getColumnIdentifier)
        val depField = fd.getDependant.getColumnIdentifier
        println(s"    FD.new(%w(${detFields.mkString(" ")}), %w(${depField})),")
      }
      println("  ],")
    }
    println("}")
  }
}
