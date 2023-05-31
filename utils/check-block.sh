#!/bin/bash

tmpdir=$(mktemp -d)
tmpProgram="$tmpdir/codeBlock"
cat > $tmpProgram

MATCH=$(head -n 1 $tmpProgram | sed 's/\//\\\//g')
TXT=$(cat $tmpProgram | sed 's/\//\\\//g' | sed -e ':a' -e 'N' -e '$!ba' -e 's/\n/\\n/g')

SOURCEDIR=$( dirname -- "$0"; )

grep -rl "$MATCH" $1 | xargs -I {} $SOURCEDIR/sed-add-change.sh "$MATCH" "$TXT" "{}"

