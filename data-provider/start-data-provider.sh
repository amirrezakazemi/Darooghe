#!/bin/sh

WD="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

docker run -d \
    -v $WD/src/main/resources/application.conf:/opt/data-provider/application.conf \
    --restart=unless-stopped \
    --network host \
    --name data-provider de/data-provider