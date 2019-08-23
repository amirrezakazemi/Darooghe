name := "SparkBitcoinPrice"

version := "0.1"

scalaVersion := "2.11.12"

libraryDependencies += "org.apache.kafka" %% "kafka" % "2.2.0"
libraryDependencies += "org.apache.spark" %% "spark-streaming-kafka-0-10" % "2.4.1"
libraryDependencies += "org.apache.spark" %% "spark-core" % "2.4.1"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.3" % Runtime
//libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.0.3"
libraryDependencies += "org.apache.spark" %% "spark-streaming" % "2.4.1" % "provided"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.4.1"
libraryDependencies += "org.apache.spark" %% "spark-sql-kafka-0-10" % "2.4.1"
libraryDependencies += "com.datastax.spark" %% "spark-cassandra-connector" % "2.4.1"
dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.7"
