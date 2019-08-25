name := "data-provider"

version := "0.1"

organization := "de"

scalaVersion := "2.12.7"

mainClass in assembly := Some("de.dataprovider.Application")
assemblyJarName in assembly := s"${name.value}-${version.value}.jar"

libraryDependencies += "com.squareup.okhttp" % "okhttp" % "2.7.0"
libraryDependencies += "org.apache.kafka" %% "kafka" % "2.2.0"
libraryDependencies += "org.java-websocket" % "Java-WebSocket" % "1.3.9"
libraryDependencies += "com.typesafe" % "config" % "1.3.4"
