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

import de.metanome.algorithm_integration.results.FunctionalDependency
import de.metanome.algorithm_integration.result_receiver.FunctionalDependencyResultReceiver

class PrintingFunctionalDependencyReceiver extends FunctionalDependencyResultReceiver {
  override def acceptedResult(fd: FunctionalDependency): java.lang.Boolean = true

  override def receiveResult(fd: FunctionalDependency): Unit = {
    // scalastyle:off println
    println(fd.toString)
    // scalastyle:on println
  }
}
