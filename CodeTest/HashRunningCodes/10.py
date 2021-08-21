import hashlib


import traceback, sys

try:
    def get_hash(data, type):
        # 对 data加密，直接输出加密结果
        # 只可在虚线内写代码，否则将不会运行
        if type == "hash1":
            # --------------------- START ---------------------
            data_en = data.encode('utf-8')
            hash1 = hashlib.sha1()
            hash1.update(data_en)
            print(hash1.hexdigest())
            # ---------------------- END ----------------------

        elif type == "hash224":
            # --------------------- START ---------------------
            data_en = data.encode('utf-8')
            hash224 = hashlib.sha224()
            hash224.update(data_en)
            print(hash224.hexdigest())
            # ---------------------- END ----------------------

        elif type == "hash256":
            # --------------------- START ---------------------
            data_en = data.encode('utf-8')
            hash256 = hashlib.sha256()
            hash256.update(data_en)
            print(hash256.hexdigest())
            # ---------------------- END ----------------------

        elif type == "hash384":
            # --------------------- START ---------------------
            data_en = data.encode('utf-8')
            hash384 = hashlib.sha384()
            hash384.update(data_en)
            print(hash384.hexdigest())
            # ---------------------- END ----------------------

        elif type == "hash512":
            # --------------------- START ---------------------
            data_en = data.encode('utf-8')
            hash512 = hashlib.sha512()
            hash512.update(data_en)
            print(hash512.hexdigest())
            # ---------------------- END ----------------------


    if __name__ == '__main__':
        plaintext = "this is plaintext"
        get_hash(plaintext, "hash1")
        get_hash(plaintext, "hash224")
        get_hash(plaintext, "hash256")
        get_hash(plaintext, "hash384")
        get_hash(plaintext, "hash512")

except:
    exception = ""
    value, tb = sys.exc_info()[1:]
    for line in traceback.TracebackException(type(value), value, tb, limit=None).format(chain=True):
        exception += line
    print(exception)