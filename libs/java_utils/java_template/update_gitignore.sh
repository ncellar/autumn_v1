#!/bin/bash

DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )

cat $DIR/template.gitignore own.gitignore > .gitignore 2>/dev/null
