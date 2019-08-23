#!/bin/sh

WD="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

sudo docker run -d \
    -v $WD/src/main/resources/application.conf:/opt/data-provider/application.conf \
    --restart=unless-stopped \
    --name data-provider de/data-provider