#!/bin/sh

docker run -d \
    --restart=unless-stopped \
    --network host \
    --name spark-analytics de/spark-analytics