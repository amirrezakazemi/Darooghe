#!/bin/bash

cp ../../../target/scala-2.12/Darooghe-assembly-0.1.jar .
docker build -t trader/darooghe .