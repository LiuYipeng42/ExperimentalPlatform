import string
import sys


def kaisa(plaintext, key):

    key = int(key)

    lower = string.ascii_lowercase

    upper = string.ascii_uppercase

    before = string.ascii_letters

    after = lower[key:] + lower[:key] + upper[key:] + upper[:key]

    table = ''.maketrans(before, after)
    alphaMap = {}
    for key, v in table.items():
        alphaMap[chr(key)] = chr(v)

    # result = {"ciphertext": plaintext.translate(table)}
    return plaintext.translate(table).swapcase()


print(kaisa(*sys.argv[1:]))
