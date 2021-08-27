#!/bin/bash

gcc -o task3 task3.c

# You need to find the correct value for X
# 只可在虚线内写代码，否则将不会运行
# --------------------- START ---------------------  
X=$((16#0000))
Y=$((X + 128 + 1))
# ---------------------- END ----------------------

head -c $X  task3 > prefix
tail -c +$Y task3 > suffix


./md5collgen -p prefix -o t1.bin t2.bin
tail -c 128 t1.bin > P
tail -c 128 t2.bin > Q

# Put everything together
# You need to decide what M1-M3 and N1-N3 should be
# 只可在虚线内写代码，否则将不会运行
# --------------------- START --------------------
cat M1 M2 M3 > a1.out
cat N1 N2 N3 > a2.out
# ---------------------- END ----------------------

chmod a+x a1.out a2.out

echo "Generating md5 hash"
md5sum a1.out a2.out

echo "Generating sha256 hash"
sha256sum a1.out a2.out

