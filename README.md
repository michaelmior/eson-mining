# ESON Mining

[![CI](https://github.com/michaelmior/eson-mining/actions/workflows/ci.yml/badge.svg)](https://github.com/michaelmior/eson-mining/actions/workflows/ci.yml)

Dependency mining for use with the [ESON](https://github.com/michaelmior/eson) normalization tool.
Currently functional and inclusion dependencies can be mined from Cassandra database instances.

## Running

To compile the mining tool, run `sbt assembly`.
The tool can then be executed by running the JAR file generated in `target/scala-2.12`.
