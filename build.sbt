name         := "ESON Mining"
version      := "0.1.0-SNAPSHOT"
organization := "ca.uwaterloo.dsg"

scalaVersion := "2.12.2"

mainClass in Compile := Some("ca.uwaterloo.dsg.eson.DiscoveryRunner")

libraryDependencies ++= Seq(
  "com.fasterxml.jackson.core" % "jackson-annotations" % "2.8.9",
  "com.google.guava" % "guava" % "19.0",
  "it.unimi.dsi" % "fastutil" % "7.2.1",
  "org.apache.calcite" % "calcite-cassandra" % "1.12.0",
  "org.apache.lucene" % "lucene-core" % "4.6.0"
)
