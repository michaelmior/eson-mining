name         := "ESON Mining"
version      := "0.1.0-SNAPSHOT"
organization := "ca.uwaterloo.dsg"

scalaVersion := "2.12.2"

mainClass in Compile := Some("ca.uwaterloo.dsg.eson.TaneRunner")

libraryDependencies ++= Seq(
  "com.fasterxml.jackson.core" % "jackson-annotations" % "2.3.2",
  "com.google.guava" % "guava" % "17.0",
  "it.unimi.dsi" % "fastutil" % "6.5.9",
  "org.apache.lucene" % "lucene-core" % "4.6.0"
)
