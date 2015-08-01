#!/bin/bash

DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )

cp $DIR/makefile makefile
chmod +x $DIR/update_gitignore.sh
$DIR/update_gitignore.sh
chmod +x $DIR/update.sh
