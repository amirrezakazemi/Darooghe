#!/bin/bash

cd ../../data-provider

sbt assembly

docker build -t de/data-provider .