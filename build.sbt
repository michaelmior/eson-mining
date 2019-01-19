name         := "ESON Mining"
version      := "0.1.0-SNAPSHOT"
organization := "ca.uwaterloo.dsg"

scalaVersion := "2.12.2"

mainClass in Compile := Some("ca.uwaterloo.dsg.eson.DiscoveryRunner")

resolvers += Resolver.mavenLocal
resolvers += "michaelmior-Metanome" at "https://packagecloud.io/michaelmior/Metanome/maven2"

libraryDependencies ++= Seq(
  "com.datastax.cassandra" % "cassandra-driver-core" % "3.1.4",
  "com.fasterxml.jackson.core" % "jackson-annotations" % "2.8.9",
  "com.google.guava" % "guava" % "19.0",
  "de.metanome" % "algorithm_integration" % "1.2-calcite-SNAPSHOT",
  "de.metanome" % "backend" % "1.2-calcite-SNAPSHOT",
  "de.metanome.algorithms.binder" % "BINDERAlgorithm" % "1.2-calcite-SNAPSHOT",
  "de.metanome.algorithms.binder" % "BINDERDatabase" % "1.2-calcite-SNAPSHOT",
  "de.metanome.algorithms.tane" % "TANE" % "1.2-calcite-SNAPSHOT",
  "de.uni-potsdam.hpi" % "dao" % "0.0.1-calcite-SNAPSHOT",
  "de.uni-potsdam.hpi" % "utils" % "0.0.1-calcite-SNAPSHOT",
  "it.unimi.dsi" % "fastutil" % "7.2.1",
  "org.apache.calcite" % "calcite-cassandra" % "1.13.0",
  "org.apache.lucene" % "lucene-core" % "4.6.0",
  "org.scalikejdbc" %% "scalikejdbc" % "3.0.2"
)

fork in run := true
