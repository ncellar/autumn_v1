#!/bin/bash

DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )

(cd $DIR; git pull)
$DIR/update_gitignore.sh