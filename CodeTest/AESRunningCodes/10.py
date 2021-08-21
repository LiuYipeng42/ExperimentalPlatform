from Cryptodome.Cipher import AES
from binascii import b2a_hex, a2b_hex


import traceback, sys

try:
    class AES_CBC:
        def __init__(self, key):
            self.key = key.encode('utf-8')
            self.mode = AES.MODE_CBC

        def encrypt(self, text):
            # 此处编写AES的CBC加密方法
            # 不足十六位用 ‘\0' 补够
            # 偏移iv与key相同
            # 返回 b2a_hex 加密的结果
            # 只可在虚线内写代码，否则将不会运行
            # --------------------- START ---------------------
            text = text.encode("utf-8")
            length = 16
            count = len(text)
            add = length - (count % length)
            text = text + (b'\0' * add)
            return b2a_hex(AES.new(self.key, self.mode, self.key).encrypt(text))
            # ---------------------- END ----------------------

        def decrypt(self, text):
            # 编写AES CBC模式的解密方法
            # 返回明文
            # 只可在虚线内写代码，否则将不会运行
            # --------------------- START ---------------------
            plaintext = AES.new(self.key, self.mode, self.key).decrypt(a2b_hex(text))
            return bytes.decode(plaintext).rstrip('\0')
            # ---------------------- END ----------------------


    class AES_EBC:
        def __init__(self, key):
            self.key = key.encode('utf-8')
            self.mode = AES.MODE_ECB

        def encrypt(self, text):
            # 此处编写AES的EBC加密方法
            # 不足十六位用 ‘\0' 补够
            # 返回 b2a_hex 加密的结果
            # 只可在虚线内写代码，否则将不会运行
            # --------------------- START ---------------------
            text = text.encode("utf-8")
            length = 16
            count = len(text)
            add = length - (count % length)
            text = text + (b'\0' * add)
            return b2a_hex(AES.new(self.key, self.mode).encrypt(text))
            # ---------------------- END ----------------------

        def decrypt(self, text):
            # 编写AES EBC模式的解密方法
            # 返回明文
            # 只可在虚线内写代码，否则将不会运行
            # --------------------- START ---------------------
            plaintext = AES.new(self.key, self.mode).decrypt(a2b_hex(text))
            return bytes.decode(plaintext).rstrip('\0')
            # ---------------------- END ----------------------


    if __name__ == "__main__":
        aes_cbc = AES_CBC("guet123456789011")
        cbc_encryText = aes_cbc.encrypt("桂林电子科技大学")
        print(cbc_encryText)
        print(aes_cbc.decrypt(cbc_encryText))

        aes_ecb = AES_EBC("guet123456789011")
        ecb_encryText = aes_ecb.encrypt("桂林电子科技大学")
        print(ecb_encryText)
        print(aes_ecb.decrypt(ecb_encryText))

except:
    exception = ""
    value, tb = sys.exc_info()[1:]
    for line in traceback.TracebackException(type(value), value, tb, limit=None).format(chain=True):
        exception += line
    print(exception)