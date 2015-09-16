#!/bin/sh

for file in *.html; do
  sed "s/вЂўВ / /g" $file > temp; mv temp $file;
  sed "s/вЂў/-/g" $file > temp; mv temp $file;
  sed "s/В / /g" $file > temp; mv temp $file;
  sed "s/href/xxxx/g" $file > temp; mv temp $file;
  sed "s/вЂњ/'/g" $file > temp; mv temp $file;
  sed "s/вЂќ/'/g" $file > temp; mv temp $file;
  sed "s/РІР‚СћР’/-/g" $file > temp; mv temp $file;
  sed "s/РІР‚Сљ’/'/g" $file > temp; mv temp $file;
  sed "s/РІР‚Сњ’/'/g" $file > temp; mv temp $file;
  sed "s/Р’/ /g" $file > temp; mv temp $file;
  sed "s/В·/-/g" $file > temp; mv temp $file;
  sed "s/вЂ”/ /g" $file > temp; mv temp $file;
  sed "s/Сћ/ /g" $file > temp; mv temp $file;
  sed "s/Сљ//g" $file > temp; mv temp $file;
  sed "s/Сњ//g" $file > temp; mv temp $file;
  sed "s/РІР‚//g" $file > temp; mv temp $file;
  sed "s/вЂ“//g" $file > temp; mv temp $file;
  sed "s/вЂ™//g" $file > temp; mv temp $file;
done
