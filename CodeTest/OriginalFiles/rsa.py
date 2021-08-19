from Cryptodome.PublicKey import RSA
from Cryptodome.Cipher import PKCS1_v1_5 as p5
from Cryptodome.Hash import SHA256
from Crypto.Signature import PKCS1_v1_5
import base64
import random


class Rsa_test:
    def __init__(self):
        key = RSA.generate(1024)
        self.public_key = key.publickey().export_key()
        self.private_key = key.export_key()

    def encrypt_rsa(self, text):
        # 此处编写你的rsa加密方法
        # 用自己的rsa公钥 加密 参数text
        # 用base64.b64encode编码加密结果
        # 只可在虚线内写代码，否则将不会运行
        # --------------------- START ---------------------
        rsa_publicKey = RSA.import_key(self.public_key)
        cipKey = p5.new(rsa_publicKey)
        encrypt_text = base64.b64encode(cipKey.encrypt(text.encode(encoding="utf-8")))
        return encrypt_text.decode()
        # ---------------------- END ----------------------

    def decrypt_rsa(self, text):
        # 此处编写你的rsa解密方法
        # 只可在虚线内写代码，否则将不会运行
        # --------------------- START ---------------------
        rsa_privateKey = RSA.import_key(self.private_key)
        cipKey = p5.new(rsa_privateKey)
        decrypt_text = cipKey.decrypt(base64.b64decode(text), "ERROR")
        return decrypt_text.decode()
        # ---------------------- END ----------------------

    def sign_rsa(self, text):
        # 此处编写你的rsa对文本的签名方法
        # 用到的hash：SHA256
        # 将二进制签名转base64加密
        # 只可在虚线内写代码，否则将不会运行
        # --------------------- START ---------------------
        sha256 = SHA256.new()
        sha256.update(text.encode())
        rsa_private = RSA.import_key(self.private_key)
        sign_text = PKCS1_v1_5.new(rsa_private).sign(sha256)
        return base64.b64encode(sign_text)
        # ---------------------- END ----------------------

    def verify(self, sign, text):
        # 此处编写你的验签方法
        # 只可在虚线内写代码，否则将不会运行
        # --------------------- START ---------------------
        rsa_public = RSA.import_key(self.public_key)
        sha256 = SHA256.new()
        sha256.update(text.encode())
        return PKCS1_v1_5.new(rsa_public).verify(sha256, base64.b64decode(sign))
        # ---------------------- END ----------------------


# 测试代码勿动
if __name__ == '__main__':
    i = 0
    text = ''
    while i < 8:
        text += random.choice(['桂', '林', '电', '子', '科', '技', '大', '学'])
        i += 1
    rsa = Rsa_test()
    encryText = rsa.encrypt_rsa(text)
    print(encryText)
    print(rsa.decrypt_rsa(encryText))

    sign_text = rsa.sign_rsa(text)
    print(sign_text)
    print(rsa.verify(sign_text, text))
