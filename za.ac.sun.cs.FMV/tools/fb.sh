#!/bin/bash
FB_DIR=/home/disco/prog/findbugs
echo $*
java -jar $FB_DIR/lib/findbugs.jar $*
