# Darooghe
This is a project in my Data Engineering course. The goal was developing stack to analyze crypto currency digits. we deal with big data platforms because of volume and velocity of obtained data. Our architecture consists of Java code, Kafka, spark structured streaming, cassnadra nosql database nodes and php code for showing analysis.
## Java 
we crawled data by using free existing apis to gather live crypto dataset(bitcoin mostly).
## Kafka
we stroed data in kafka queues
## Spark
we used spark structured streaming feautre to analyze the data and preprocess , extract meaningful content and performing analysis on them
## Cassandra
cassandra is used to store our analysis ready for php. we used docker to create 3 distributed nodes.
## PHP
php and js used to show graphs and charts 
