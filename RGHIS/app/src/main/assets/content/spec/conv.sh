#!/bin/sh

for file in *.html; do
   iconv -f WINDOWS-1251 -t UTF-8 $file > tmp.html;
   mv tmp.html $file
   echo $file
done
