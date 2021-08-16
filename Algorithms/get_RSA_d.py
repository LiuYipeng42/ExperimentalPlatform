# -*- coding:UTF-8 -*-
import sys


def ext_gcd(a, b):
    if b == 0:
        x1 = 1
        y1 = 0
        x = x1
        y = y1
        r = a
        return r, x, y
    else:
        r, x1, y1 = ext_gcd(b, a % b)
        x = y1
        y = x1 - a // b * y1
        return r, x, y


# 生成公钥私钥，p、q为两个超大质数
def gen_key(p, q, e):

    p = int(p)
    q = int(q)
    e = int(e)

    fy = (p - 1) * (q - 1)  # 计算与n互质的整数个数 欧拉函数
    # e = 65537  # 选取e   一般选取65537
    # generate d
    a = e
    b = fy
    r, x, y = ext_gcd(a, b)
    # 计算出的x不能是负数，如果是负数，说明p、q、e选取失败，不过可以把x加上fy，使x为正数，才能计算。
    if x < 0:
        x = x + fy
    d = x
    # 返回：   公钥     私钥
    return d


print(gen_key(*sys.argv[1:]))

