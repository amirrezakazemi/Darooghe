name := "Darooghe"

version := "0.1"

organization := "ir.darooghe"

scalaVersion := "2.12.7"

mainClass in assembly := Some("ir.darooghe.Application")

//libraryDependencies += "com.squareup.okhttp" % "okhttp" % "2.7.0"
libraryDependencies += "org.apache.kafka" %% "kafka" % "2.2.0"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.3" % Runtime
//libraryDependencies += "com.fasterxml.jackson.core" % "jackson-core" % "2.9.8"
libraryDependencies += "org.java-websocket" % "Java-WebSocket" % "1.3.9"
//libraryDependencies += "io.crossbar.autobahn" % "autobahn-java" % "19.3.1"
libraryDependencies += "com.typesafe" % "config" % "1.3.4"
