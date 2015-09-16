#!/bin/sh

for file in *.html; do
  sed "s/<font face=\'Arial\'>/<font face=\'Arial\' size=\'4\'>/g" $file > temp; mv temp $file;
done
