name := "kafka-spark"

version := "0.1"

scalaVersion := "2.12.7"

libraryDependencies += "org.apache.kafka" %% "kafka" % "2.2.0"
libraryDependencies += "org.apache.spark" %% "spark-streaming-kafka-0-10" % "2.4.2"
libraryDependencies += "org.apache.spark" %% "spark-core" % "2.4.2"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.3" % Runtime
libraryDependencies += "org.apache.spark" %% "spark-streaming" % "2.4.2"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.4.2"
libraryDependencies += "org.apache.spark" %% "spark-sql-kafka-0-10" % "2.4.2"
dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.7"
