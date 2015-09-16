#!/bin/sh

for file in *.html; do
  sed "s/‘/'/g" $file > temp; mv temp $file;
done
