name         := "ESON Mining"
version      := "0.1.0-SNAPSHOT"
organization := "ca.uwaterloo.dsg"

scalaVersion := "2.12.2"

mainClass in Compile := Some("ca.uwaterloo.dsg.eson.DiscoveryRunner")

resolvers += Resolver.mavenLocal

libraryDependencies ++= Seq(
  "com.datastax.cassandra" % "cassandra-driver-core" % "3.1.4",
  "com.fasterxml.jackson.core" % "jackson-annotations" % "2.8.9",
  "com.google.guava" % "guava" % "19.0",
  "it.unimi.dsi" % "fastutil" % "7.2.1",
  "org.apache.calcite" % "calcite-cassandra" % "1.13.0",
  "org.apache.lucene" % "lucene-core" % "4.6.0",
  "org.scalikejdbc" %% "scalikejdbc" % "3.0.2"
)

fork in run := true
