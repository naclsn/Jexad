#!/bin/sh
while ! [ -d .git/ ]
  do cd ..
done
set -ex

ant classes
[ -d classes/com/hi/ ] && rm -rf classes/com/hi/
javac -cp classes/ -d classes/ ext/example/src/com/hi/HiLookup.java
exec java -cp classes/ com.jexad.Jexad -xl com.hi.HiLookup -i '>> '
