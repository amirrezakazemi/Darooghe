#!/bin/bash

sbt assembly

docker build -t de/data-provider .

