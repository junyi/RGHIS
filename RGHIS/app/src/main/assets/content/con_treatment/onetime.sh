#!/bin/sh

for file in *.html; do
  sed "s/<body style='margin : 0px; height :100%'>/<body style='margin : 10px; height :100%'>/g" $file > temp; mv temp $file;
done
