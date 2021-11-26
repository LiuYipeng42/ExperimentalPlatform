from binascii import b2a_hex, a2b_hex
from Cryptodome.Cipher import AES
import sys


def getSBox():
    S_Box = [0x63, 0x7c, 0x77, 0x7b, 0xf2, 0x6b, 0x6f, 0xc5, 0x30, 0x01, 0x67, 0x2b, 0xfe, 0xd7, 0xab, 0x76, 0xca,
             0x82,
             0xc9, 0x7d, 0xfa, 0x59, 0x47, 0xf0, 0xad, 0xd4, 0xa2, 0xaf, 0x9c, 0xa4, 0x72, 0xc0, 0xb7, 0xfd, 0x93,
             0x26,
             0x36, 0x3f, 0xf7, 0xcc, 0x34, 0xa5, 0xe5, 0xf1, 0x71, 0xd8, 0x31, 0x15, 0x04, 0xc7, 0x23, 0xc3, 0x18,
             0x96,
             0x05, 0x9a, 0x07, 0x12, 0x80, 0xe2, 0xeb, 0x27, 0xb2, 0x75, 0x09, 0x83, 0x2c, 0x1a, 0x1b, 0x6e, 0x5a,
             0xa0,
             0x52, 0x3b, 0xd6, 0xb3, 0x29, 0xe3, 0x2f, 0x84, 0x53, 0xd1, 0x00, 0xed, 0x20, 0xfc, 0xb1, 0x5b, 0x6a,
             0xcb,
             0xbe, 0x39, 0x4a, 0x4c, 0x58, 0xcf, 0xd0, 0xef, 0xaa, 0xfb, 0x43, 0x4d, 0x33, 0x85, 0x45, 0xf9, 0x02,
             0x7f,
             0x50, 0x3c, 0x9f, 0xa8, 0x51, 0xa3, 0x40, 0x8f, 0x92, 0x9d, 0x38, 0xf5, 0xbc, 0xb6, 0xda, 0x21, 0x10,
             0xff,
             0xf3, 0xd2, 0xcd, 0x0c, 0x13, 0xec, 0x5f, 0x97, 0x44, 0x17, 0xc4, 0xa7, 0x7e, 0x3d, 0x64, 0x5d, 0x19,
             0x73,
             0x60, 0x81, 0x4f, 0xdc, 0x22, 0x2a, 0x90, 0x88, 0x46, 0xee, 0xb8, 0x14, 0xde, 0x5e, 0x0b, 0xdb, 0xe0,
             0x32,
             0x3a, 0x0a, 0x49, 0x06, 0x24, 0x5c, 0xc2, 0xd3, 0xac, 0x62, 0x91, 0x95, 0xe4, 0x79, 0xe7, 0xc8, 0x37,
             0x6d,
             0x8d, 0xd5, 0x4e, 0xa9, 0x6c, 0x56, 0xf4, 0xea, 0x65, 0x7a, 0xae, 0x08, 0xba, 0x78, 0x25, 0x2e, 0x1c,
             0xa6,
             0xb4, 0xc6, 0xe8, 0xdd, 0x74, 0x1f, 0x4b, 0xbd, 0x8b, 0x8a, 0x70, 0x3e, 0xb5, 0x66, 0x48, 0x03, 0xf6,
             0x0e,
             0x61, 0x35, 0x57, 0xb9, 0x86, 0xc1, 0x1d, 0x9e, 0xe1, 0xf8, 0x98, 0x11, 0x69, 0xd9, 0x8e, 0x94, 0x9b,
             0x1e,
             0x87, 0xe9, 0xce, 0x55, 0x28, 0xdf, 0x8c, 0xa1, 0x89, 0x0d, 0xbf, 0xe6, 0x42, 0x68, 0x41, 0x99, 0x2d,
             0x0f,
             0xb0, 0x54, 0xbb, 0x16]
    S_Box = list(map(lambda x: hex(x), S_Box))
    S_Box = grouped(S_Box, 16)

    return S_Box


def grouped(key, number):
    result = []
    for i in range(0, len(key), number):
        result.append(key[i:i + number])
    return result


def KeySchedule(key, addPart, sBox):
    key = list(key)
    key = list(map(lambda x: hex(ord(x)), key))
    key = key + addPart * (16 - len(key))

    Rcon = [0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1b, 0x36]
    key = key + addPart * 4 * 40
    key = grouped(key, 4)
    for i in range(40):
        if (i + 4) % 4 == 0:  #
            temp_w = [hex(0)] * 4
            temp_w[3] = key[i + 3][0]
            temp_w[0] = key[i + 3][1]
            temp_w[1] = key[i + 3][2]
            temp_w[2] = key[i + 3][3]  # 移位
            # ---------换字----------
            for j in range(4):
                if len(temp_w[j]) != 3:
                    temp_w[j] = sBox[int(temp_w[j][2:3], 16)][int(temp_w[j][3:], 16)]
                else:
                    temp_w[j] = sBox[0][int(temp_w[j][2:], 16)]
            # --------三方异或----------
            output_key = [hex(0)] * 4
            for k in range(4):
                Rcon_temp = [hex(0)] * 4
                Rcon_temp[0] = hex(Rcon[int(i / 4)])
                output_key[k] = hex(int(key[i][k], 16) ^ int(temp_w[k], 16) ^ int(Rcon_temp[k], 16))
            key[i + 4] = output_key
        else:
            temp_q = [hex(0)] * 4
            for j in range(4):
                temp_q[j] = hex(int(key[i][j], 16) ^ int(key[i + 3][j], 16))
            key[i + 4] = temp_q

    key = [j for i in key for j in i]
    return key


def AddRoundKey(state: list, key: list):  # hex
    if not isinstance(state[0], str):
        state = list(map(lambda x: hex(x), state))
    temp = [0x0] * 16
    for i in range(4):
        for j in range(4):
            temp[4 * j + i] = int(state[4 * j + i], 16) ^ int(key[4 * j + i], 16)  # int(hex)
    return temp


def SubBytes(state, sBox):
    state = list(map(lambda x: hex(x), state))
    for j in range(len(state)):
        if len(state[j]) == 3:
            state[j] = sBox[0][int(state[j][2:], 16)]
        else:
            state[j] = sBox[int(state[j][2:3], 16)][int(state[j][3:], 16)]
    return state


def ShiftRows(state: list):
    for i in range(4):
        temp_s = [hex(0)] * 4
        temp_s[0] = hex(int(state[(5 * i + 5) % 16], 16))
        temp_s[1] = hex(int(state[(5 * i + 9) % 16], 16))
        temp_s[2] = hex(int(state[(5 * i + 13) % 16], 16))
        temp_s[3] = hex(int(state[(5 * i + 17) % 16], 16))
        state[(i + 1) % 16] = temp_s[0]
        state[(i + 5) % 16] = temp_s[1]
        state[(i + 9) % 16] = temp_s[2]
        state[(i + 13) % 16] = temp_s[3]

    return state


def MixColumns(state: list):  # int list
    state = list(map(lambda s: int(s, 16), state))
    tmp = [0] * 4
    xt = [0] * 4
    for x in range(4):
        collan = state[4 * x:4 * x + 4]
        xt[0] = xtime(collan[0])
        xt[1] = xtime(collan[1])
        xt[2] = xtime(collan[2])
        xt[3] = xtime(collan[3])
        tmp[0] = xt[0] ^ xt[1] ^ collan[1] ^ collan[2] ^ collan[3]
        tmp[1] = collan[0] ^ xt[1] ^ xt[2] ^ collan[2] ^ collan[3]
        tmp[2] = collan[0] ^ collan[1] ^ xt[2] ^ xt[3] ^ collan[3]
        tmp[3] = xt[0] ^ collan[0] ^ collan[1] ^ collan[2] ^ xt[3]
        state[4 * x] = tmp[0]
        state[4 * x + 1] = tmp[1]
        state[4 * x + 2] = tmp[2]
        state[4 * x + 3] = tmp[3]
    return state


def xtime(x):
    if x & 0x80:
        return ((x << 1) ^ 0x1B) & 0xFF
    return x << 1


def formatHex(nums):
    formattedNums = []
    for num in nums:
        num = num[2:].upper()
        if len(num) < 2:
            num = '0' + num
        formattedNums.append(num)
    return formattedNums


def toHex(nums):
    return formatHex(list(map(lambda x: hex(x).upper(), nums)))


def ScheduleKey(state, key, sBox):
    roundsData = {'初始变换': {}}

    roundsData['初始变换']['输入的状态矩阵'] = formatHex(state)

    roundsData['初始变换']['W0~W3'] = formatHex(key[0:16])

    state = AddRoundKey(state, key[0:16])  # int  State
    roundsData['初始变换']['AddRoundKey'] = toHex(state)
    save_Sub = []

    for i in range(9):
        roundNum = str(i + 1)

        roundsData.setdefault(roundNum, {})
        roundsData[roundNum]['输入的状态矩阵'] = toHex(state)

        state = SubBytes(state, sBox)
        roundsData[roundNum]['SubBytes'] = formatHex(state)
        save_Sub.extend(state)

        state = ShiftRows(state)
        roundsData[roundNum]['ShiftRows'] = formatHex(state)

        state = MixColumns(state)
        roundsData[roundNum]['MixColumns'] = toHex(state)

        roundsData[roundNum]['W' + str(4 * i + 4) + '~W' + str(4 * i + 7)] = formatHex(key[16 + i * 16: 32 + i * 16])

        state = AddRoundKey(state, key[16 + i * 16: 32 + i * 16])
        roundsData[roundNum]['AddRoundKey'] = toHex(state)

    roundsData['最后变换'] = {}

    roundsData['最后变换']['输入的状态矩阵'] = toHex(state)

    state = SubBytes(state, sBox)
    roundsData['最后变换']['SubBytes'] = formatHex(state)

    state = ShiftRows(state)
    roundsData['最后变换']['ShiftRows'] = formatHex(state)

    roundsData['最后变换']['W40~W43'] = formatHex(key[160:176])

    state = AddRoundKey(state, key[160:176])
    roundsData['最后变换']['AddRoundKey'] = toHex(state)

    for roundNum, data in roundsData.items():
        print(roundNum)
        for k, v in data.items():
            print(k, v)

    return roundsData


def plaintextToStateHex(plaintext, addPart):
    # ------------------将明文转换为16进制字符串State------------------
    plaintext = list(plaintext)
    state = []
    for i in range(len(plaintext)):
        state.append(hex(ord((plaintext[i]))))
    state = state + addPart * (16 - len(state))  # 将State的内部补齐16个元素
    return state


def flatten(state):
    show = ''
    for i in range(len(state)):
        if state[i][0] == '0' and len(state[0]) == 2:
            show = show + state[i][1:]
        else:
            show = show + state[i]

    return show.lower()


def convert(segment):
    save = ''
    for i in range(len(segment)):
        save = save + chr(int(segment[i:i + 2], 16))
    return save


def AESAlgorithm(plaintext, key):
    sBox = getSBox()
    addPart = [hex(0)]

    state = plaintextToStateHex(plaintext, addPart)
    print('plaintext', plaintext)
    print('State', state)

    key = KeySchedule(key, addPart, sBox)
    print("Key:", key)
    print('After KeySchedule:', key)

    roundsData = ScheduleKey(state, key, sBox)

    result = roundsData['最后变换']['AddRoundKey']

    aesResultHex = flatten(result)
    aesResult = convert(aesResultHex)
    result = {'roundsData': roundsData, 'result': aesResult, 'resultHex': aesResultHex, '密钥拓展': formatHex(key)}

    return result


class AESTest:
    def __init__(self, key):
        self.key = key.encode('utf-8')
        self.mode = AES.MODE_CBC

    def encrypt(self, text):
        text = text.encode("utf-8")
        length = 16
        count = len(text)
        add = length - (count % length)
        text = text + (b'\0' * add)
        return b2a_hex(AES.new(self.key, self.mode, self.key).encrypt(text))

    def decrypt(self, text):
        plaintext = AES.new(self.key, self.mode, self.key).decrypt(a2b_hex(text))
        return plaintext.rstrip(b'\0')


def hex_to_bin(hexNum):
    binary = ''
    for i in range(0, len(hexNum)):
        if ord('a') <= ord(hexNum[i]) <= ord('f'):
            dec = ord(hexNum[i]) - 97 + 10
        elif ord('0') <= ord(hexNum[i]) <= ord('9'):
            dec = ord(hexNum[i]) - 48
        else:
            continue
        test = [8, 4, 2, 1]
        for j in range(0, 4):
            if dec / test[j] >= 1:
                binary = binary + '1'
                dec = dec - test[j]
            else:
                binary = binary + '0'
    return binary


def cmpCount(str1, str2):
    count = 0
    dif = 0
    length = len(str1)
    if len(str1) > len(str2):
        length = len(str2)
        dif = len(str1) - len(str2)
    elif len(str1) < len(str2):
        length = len(str1)
        dif = len(str2) - len(str1)
    for i in range(length):
        if str1[i] != str2[i]:
            count = count + 1
    count = count + dif
    return count


def change_text_cmp(key, plaintext1, plaintext2):
    """
    :param key: 密钥
    :param plaintext1: 明文1
    :param plaintext2: 明文2
    :return: changePlaintext: 改变明文 结果
    """
    changePlaintext = {}

    try:
        aes = AESTest(key)
        enc = aes.encrypt(plaintext1)
        enc1 = aes.encrypt(plaintext2)
        hexStr1 = enc.decode('utf-8')
        hexStr2 = enc1.decode('utf-8')
        binStr1 = hex_to_bin(hexStr1)
        changePlaintext["明文1加密"] = binStr1
        binStr2 = hex_to_bin(hexStr2)
        changePlaintext["明文2加密"] = binStr2
        count = str(cmpCount(binStr1, binStr2))
        changePlaintext["差异位数"] = count
    except:
        print("错误:", "AES加密出错！")

    return changePlaintext


def change_key_cmp(plaintext, key1, key2):
    """
    :param plaintext: 明文
    :param key1: 密钥1
    :param key2: 密钥2
    :return: changeKey: 改变明文 结果
    """
    changeKey = {}

    if len(key1) == 16 and len(key2) == 16:
        try:
            aes1 = AESTest(key1)
            aes2 = AESTest(key2)
            enc1 = aes1.encrypt(plaintext)
            enc2 = aes2.encrypt(plaintext)
            hexStr1 = enc1.decode('utf-8')
            hexStr2 = enc2.decode('utf-8')
            binStr1 = hex_to_bin(hexStr1)
            changeKey["密钥1加密"] = binStr1
            binStr2 = hex_to_bin(hexStr2)
            changeKey["密钥2加密"] = binStr2
            count = str(cmpCount(binStr1, binStr2))
            changeKey["差异位数"] = count
            return changeKey
        except:
            print("错误:", "AES加密出错！")
    else:
        print("警告:", "输入的密钥必须为128位！")

class AESCFB:
    def __init__(self, key):
        self.key = key.encode('utf-8')
        self.mode = AES.MODE_CFB

    def add_to_16(self, text):
        if len(text.encode('utf-8')) % 16:
            add = 16 - len(text.encode('utf-8')) % 16
        else:
            add = 0
        text = text + ("\0" * add)
        return text.encode('utf-8')

    def encrypt(self, text):
        key = self.key
        mode = self.mode
        text = self.add_to_16(text)
        cryptos = AES.new(key, mode, key)
        cipher_text = cryptos.encrypt(text)
        return b2a_hex(cipher_text)

    def decrypt(self, text):
        key = self.key
        mode = self.mode
        cryptor = AES.new(key, mode, key)
        plain_text = cryptor.decrypt(a2b_hex(text))
        return plain_text.rstrip(b'\0')

class AESECB:
    def __init__(self, key):
        self.key = key.encode('utf-8')
        self.mode = AES.MODE_ECB

    def add_to_16(self, text):
        if len(text.encode('utf-8')) % 16:
            add = 16 - len(text.encode('utf-8')) % 16
        else:
            add = 0
        text = text + ("\0" * add)
        return text.encode('utf-8')

    def encrypt(self, text):
        key = self.key
        mode = self.mode
        text = self.add_to_16(text)
        cryptos = AES.new(key, mode)
        cipher_text = cryptos.encrypt(text)
        return b2a_hex(cipher_text)

    def decrypt(self, text):
        key = self.key
        mode = self.mode
        cryptor = AES.new(key, mode)
        plain_text = cryptor.decrypt(a2b_hex(text))
        return plain_text.rstrip(b'\0')

def AESEncryption(plaintext, key, type):
    if plaintext == "" and key == "":
        return ("请输入", "请输入明文和128位密钥！")
    if len(key) == 16:
       if type == "CBC":
          aes = AESTest(key)
       elif type == "CFB":
          aes = AESCFB(key)
       elif type =="ECB":
           aes = AESECB(key)
       else:
           aes = AESTest(key)
       try:
          enc = aes.encrypt(plaintext)
          return str(enc)[2: -1]
       except:
              return ("错误", "AES加密出错！")
    else:
        return ("请重新输入", "输入密钥必须为128位！")



def AESDecryption(ciphertext, key, type):
   if ciphertext == "" and key == "":
      return ("请输入", "请输入明文和128位密钥！")

   if len(key) == 16:
       if type == "CBC":
          aes = AESTest(key)
       elif type == "CFB":
          aes = AESCFB(key)
       else:
           aes = AESECB(key)
       try:
           dec = aes.decrypt(ciphertext)
           return str(dec)[2: -1]
       except:
             return("错误", "AES解密出错！")
   else:
        return ("请重新输入", "输入密钥必须为128位！")


args = sys.argv

if args[1] == "encryption":
    print(AESEncryption(*args[2:]))

if args[1] == "decryption":
    print(AESDecryption(*args[2:]))
    
# print(AESEncryption("11311", "2222222222222222","CBC"))
# print(AESDecryption(AESEncryption("11311", "2222222222222222","CBC"),"2222222222222222","CBC"))
