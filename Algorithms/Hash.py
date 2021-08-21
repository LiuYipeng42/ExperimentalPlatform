import hashlib
import sys


def get_hash(data: str, type: str):  # 对data加密
    data_en = data.encode('utf-8')
    if type == "hash256":
       hash256 = hashlib.sha256()
       hash256.update(data_en)
       return hash256.hexdigest()
    elif type == "hash1":
        hash1= hashlib.sha1()
        hash1.update(data_en)
        return hash1.hexdigest()

    elif type == "hash224":
        hash224 = hashlib.sha224()
        hash224.update(data_en)
        return hash224.hexdigest()
    elif type == "hash384":
        hash384 = hashlib.sha384()
        hash384.update(data_en)
        return hash384.hexdigest()
    elif type == "512":
        hash512 = hashlib.sha512()
        hash512.update(data_en)
        return hash512.hexdigest()
    else:
        return "parameter error."


print(get_hash(*sys.argv[1:]))

