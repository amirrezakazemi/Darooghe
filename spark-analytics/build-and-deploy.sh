#!/bin/bash

sbt assembly

docker build -t de/spark-analytics .

