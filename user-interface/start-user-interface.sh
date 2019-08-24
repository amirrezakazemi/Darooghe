#!/bin/sh

docker run -d \
    --restart=unless-stopped \
    --network host \
    --name user-interface de/user-interface