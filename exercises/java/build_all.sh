#!/bin/bash -eu

cd plaintext-base
gradle fatJar

cd ../exercise-1
gradle fatJar

cd ../exercise-2
gradle fatJar

cd ../exercise-3
gradle fatJar

cd ../exercise-4
gradle fatJar
