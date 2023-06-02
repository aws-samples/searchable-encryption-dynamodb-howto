#!/bin/bash

# make -i portable
if [[ "$OSTYPE" == "darwin"* ]]; then
  sed -i '' "s/$1/$2/g" "$3"
else
  sed -i "s/$1/$2/g" "$3"
fi
