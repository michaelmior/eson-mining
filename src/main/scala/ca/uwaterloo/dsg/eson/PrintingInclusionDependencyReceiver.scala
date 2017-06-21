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

import de.metanome.algorithm_integration.results.InclusionDependency
import de.metanome.algorithm_integration.result_receiver.InclusionDependencyResultReceiver

class PrintingInclusionDependencyReceiver extends InclusionDependencyResultReceiver {
  override def acceptedResult(ind: InclusionDependency): java.lang.Boolean = true

  override def receiveResult(ind: InclusionDependency): Unit = {
    // scalastyle:off println
    println(ind.toString)
    // scalastyle:on println
  }
}
