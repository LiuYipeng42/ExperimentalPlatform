from Crypto.Cipher import AES
from binascii import b2a_hex, a2b_hex
import sys

class aestest():
    def __init__(self,key):
        self.key=key.encode("utf-8")

    def encrypt(self,text):
        c=AES.new(self.key, AES.MODE_ECB)
        Ciphertext=c.encrypt(text.encode("utf-8"))
#         Ciphertext=c.encrypt(text)
        return b2a_hex(Ciphertext)

def hex_to_bin(hexdata):
    bindata=bin(int(hexdata,16))[2:]
    if(len(bindata)<128):
        bind="0"*(128-len(bindata))+bindata
    else:
        bind=bindata
    return bind

def cmpcount(binstr1,binstr2):
    count=0
    for i in range(len(binstr1)):
        if(binstr1[i]!=binstr2[i]):
            count=count+1
        
    return count


def Evidence1(text1,text2):
    key = 'keyskeyskeyskeys'
    aes = aestest(key)
    enc = aes.encrypt(text1)
    enc1 = aes.encrypt(text2)
    hexstr1 = enc.decode('utf-8')
    hexstr2 = enc1.decode('utf-8')
    binstr1 = hex_to_bin(hexstr1)
    binstr2 = hex_to_bin(hexstr2)
    count = str(cmpcount(binstr1, binstr2))

    result = {"明文1加密": binstr1, "明文2加密": binstr2, "差异位数": count}
    return result


def Evidence2(key1,key2):
    test = b'GUETGUETGUETGUET'.decode("utf-8")
    aes1 = aestest(key1)
    aes2 = aestest(key2)
    enc1 = aes1.encrypt(test)
    enc2 = aes2.encrypt(test)
    hexstr1 = enc1.decode('utf-8')
    hexstr2 = enc2.decode('utf-8')
    binstr1 = hex_to_bin(hexstr1)
    binstr2 = hex_to_bin(hexstr2)
    count = str(cmpcount(binstr1, binstr2))

    result = {"密钥1加密": binstr1, "密钥2加密": binstr2, "差异位数": count}
    return result
    
    
#str1,str2 = input().split()
#Evidence1(str1,str2)
#Evidence2(str1,str2)
#1234567891234566 1234567891234567


args = sys.argv

if args[1] == "different_plaintext":
    print(Evidence1(*args[2:]))

if args[1] == "different_key":
    print(Evidence2(*args[2:]))


