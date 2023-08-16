#!/bin/sh
set -e
while ! [ -d .git ]
  do cd ..
done

ant jar

( echo 'the initial text' >test/C/changing.txt
  echo 'a = Read "test/C/changing.txt"'
  echo 'b = Slice a 8 15'
  echo '?b' # expects "ial tex"
  sleep 1
  echo "but now it's something else!" >test/C/changing.txt
  echo '!a'
  echo '?b' # expects "it's so"
) | java -jar jar/Jexad.jar -i | tee /dev/tty | awk '
  $0 == "(show Slice) [105, 97, 108, 32, 116, 101, 120]" { printf 1" " }
  $0 == "(show Slice) [105, 116, 39, 115, 32, 115, 111]" { printf 2" " }
' | ( read one two
  if [ 1 -eq $one ] && [ 2 -eq $two ]
    then echo found both
    else echo got one: $one and two: $two
  fi
)
