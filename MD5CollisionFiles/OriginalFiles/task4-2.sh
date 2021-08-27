#!/bin/bash

# You need to figure out the correct value for U
# 只可在虚线内写代码，否则将不会运行
# --------------------- START ---------------------  
U=$((16#000))
V=$((U + 128 + 1))
head -c  $U suffix > suffix_1
tail -c +$V suffix > suffix_2
# ---------------------- END ----------------------

# You need to figure out what M1-M5 and N1-N5 should be
# 只可在虚线内写代码，否则将不会运行
# --------------------- START ---------------------
cat M1 M2 M3 M4 M5 > a1.out
cat N1 N2 N3 N4 N5 > a2.out
# ---------------------- END ----------------------

chmod a+x a1.out
chmod a+x a2.out

echo "Generating md5 hash"
md5sum a1.out a2.out

echo "Generating sha256 hash"
sha256sum a1.out a2.out

