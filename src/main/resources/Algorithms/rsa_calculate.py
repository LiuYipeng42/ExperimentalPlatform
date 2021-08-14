import sys


def rsa_calculate_m():
    m = int(args[2])
    e = int(args[3])
    n = int(args[4])

    print((m ** e) % n)


def rsa_calculate_c():
    c = int(args[2])
    d = int(args[3])
    n = int(args[4])

    print((c ** d) % n)


args = sys.argv


if args[1] == "calculate_m":
    rsa_calculate_m()

if args[1] == "calculate_c":
    rsa_calculate_c()