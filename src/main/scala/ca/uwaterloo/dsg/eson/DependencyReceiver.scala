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

import de.metanome.algorithm_integration.results.{FunctionalDependency, InclusionDependency}
import de.metanome.algorithm_integration.result_receiver.{FunctionalDependencyResultReceiver, InclusionDependencyResultReceiver}

class DependencyReceiver extends FunctionalDependencyResultReceiver with InclusionDependencyResultReceiver {
  val fds = new scala.collection.mutable.MutableList[FunctionalDependency]
  val inds = new scala.collection.mutable.MutableList[InclusionDependency]

  override def acceptedResult(fd: FunctionalDependency): java.lang.Boolean = true
  override def acceptedResult(ind: InclusionDependency): java.lang.Boolean = true

  override def receiveResult(fd: FunctionalDependency): Unit = {
    // scalastyle:off println
    fds += fd
    println(fd.toString)
    // scalastyle:on println
  }

  override def receiveResult(ind: InclusionDependency): Unit = {
    // scalastyle:off println
    inds += ind
    println(ind.toString)
    // scalastyle:on println
  }
}
