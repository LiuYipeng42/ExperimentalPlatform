import hashlib


def get_hash(data, type):
    # 对 data加密，直接输出加密结果
    # 只可在虚线内写代码，否则将不会运行
    if type == "hash1":
        # --------------------- START ---------------------

        # ---------------------- END ----------------------

    elif type == "hash224":
        # --------------------- START ---------------------

        # ---------------------- END ----------------------

    elif type == "hash256":
        # --------------------- START ---------------------

        # ---------------------- END ----------------------

    elif type == "hash384":
        # --------------------- START ---------------------

        # ---------------------- END ----------------------

    elif type == "hash512":
        # --------------------- START ---------------------

        # ---------------------- END ----------------------


if __name__ == '__main__':
    plaintext = "this is plaintext"
    get_hash(plaintext, "hash1")
    get_hash(plaintext, "hash224")
    get_hash(plaintext, "hash256")
    get_hash(plaintext, "hash384")
    get_hash(plaintext, "hash512")
