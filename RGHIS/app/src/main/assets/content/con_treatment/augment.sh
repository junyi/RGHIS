#!/bin/sh

for file in *.html; do
   echo "<body style='margin : 0px; height :100%'>" >> $file.tmp
   cat $file >> $file.tmp
   echo "</body>" >> $file.tmp
   mv $file.tmp $file
done
