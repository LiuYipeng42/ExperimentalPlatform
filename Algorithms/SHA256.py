import sys


def sha256_final_edition(test_str):
    binaries = test_str.encode('utf8')
    M = binaries + b'\x80' + b'\x00' * (64 - len(binaries) - 1 - 8) + (len(binaries) * 8).to_bytes(8, byteorder='big') #对输入数据进行补位

    H = [0x6a09e667, 0xbb67ae85, 0x3c6ef372, 0xa54ff53a, 0x510e527f, 0x9b05688c, 0x1f83d9ab, 0x5be0cd19]

    first_result = ["6a09e667", "bb67ae85", "3c6ef372", "a54ff53a", "510e527f", "9b05688c", "1f83d9ab", "5be0cd19"] #初始链接变量(求和运算)
    last_result = [0] * 8      #最后一步链接变量(求和运算)
    sha256_result = [0] * 8    #右边的结果(求和运算)
    first_linked_var = []      #初始链接变量(64步运算)封装64份
    beyond_linked_var = []     #结果操作后的链接变量(64步运算)封装64份
    operating_prog_var = []    #操作程序中的结果变量(64步运算), 顺序按照H, G, F, E, D, T1, D, C, B, A, T1, T2封装64份
    ch_var = []                #CH(x, y, z)逻辑函数按照x, y, x, z, result封装64份
    ma_var = []                #MA(x, y, z)逻辑函数按照x, y, x, z, y, z, result封装64份
    s0_var = []                #S0(x, y, z)逻辑函数按照S^2(x), S^13(x), S^22(x), result封装64份
    s1_var = []                #S1(x, y, z)逻辑函数按照S^6(x), S^11(x), S^25(x), result封装64份
    t0_var = []                #T0(x, y, z)逻辑函数按照S^7(x), S^18(x), R^3(x), result封装64份
    t1_var = []                #T1(x, y, z)逻辑函数按照S^17(x), S^19(x), R^10(x), result封装64份
    sha256_system_result = []

    K = [
    0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5, 0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
    0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3, 0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
    0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc, 0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
    0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7, 0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
    0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13, 0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
    0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3, 0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
    0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5, 0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
    0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208, 0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2]

    W = [0] * 64
    for t in range(0, 16):
        W[t] = M[t * 4 : t * 4 + 4]
        W[t] = int(W[t].hex(), 16)

    def ROTR(x, n):
        x = (x >> n) | (x << 32 - n)
        return x

    for t in range(16, 64):
        S1 = ROTR(W[t - 2], 17) ^ ROTR(W[t - 2], 19) ^ (W[t - 2] >> 10)
        S0 = ROTR(W[t - 15], 7) ^ ROTR(W[t - 15], 18) ^ (W[t - 15] >> 3)
        W[t] = (S1 + W[t - 7] + S0 + W[t - 16]) & 0xFFFFFFFF

    a, b, c, d, e, f, g, h = H[0], H[1], H[2], H[3], H[4], H[5], H[6], H[7]
    for t in range(0, 64):
        S1 = ROTR(e, 6) ^ ROTR(e, 11) ^ ROTR(e, 25)
        s1_temp = [0] * 4
        s1_temp[0], s1_temp[1] = (ROTR(e, 6) & 0xFFFFFFFF).to_bytes(4, byteorder='big').hex(), (ROTR(e, 11) & 0xFFFFFFFF).to_bytes(4, byteorder='big').hex()
        s1_temp[2], s1_temp[3] = (ROTR(e, 25) & 0xFFFFFFFF).to_bytes(4, byteorder='big').hex(), (S1 & 0xFFFFFFFF).to_bytes(4, byteorder='big').hex()
        s1_var.append(s1_temp)
        Ch = (e & f) ^ ((~e) & g)
        ch_temp = [0] * 5
        ch_temp[0], ch_temp[1] = e.to_bytes(4, byteorder='big').hex(), f.to_bytes(4, byteorder='big').hex()
        ch_temp[2], ch_temp[3] = e.to_bytes(4, byteorder='big').hex(), g.to_bytes(4, byteorder='big').hex()
        ch_temp[4] = Ch.to_bytes(4, byteorder='big').hex()
        ch_var.append(ch_temp)
        S0 = ROTR(a, 2) ^ ROTR(a, 13) ^ ROTR(a, 22)
        s0_temp = [0] * 4
        s0_temp[0], s0_temp[1] = (ROTR(a, 2) & 0xFFFFFFFF).to_bytes(4, byteorder='big').hex(), (ROTR(a, 13) & 0xFFFFFFFF).to_bytes(4, byteorder='big').hex()
        s0_temp[2], s0_temp[3] = (ROTR(a, 22) & 0xFFFFFFFF).to_bytes(4, byteorder='big').hex(), (S0 & 0xFFFFFFFF).to_bytes(4, byteorder='big').hex()
        s0_var.append(s0_temp)
        Maj = (a & b) ^ (a & c) ^ (b & c)
        ma_temp = [0] * 7
        ma_temp[0], ma_temp[1] = a.to_bytes(4, byteorder='big').hex(), b.to_bytes(4, byteorder='big').hex()
        ma_temp[2], ma_temp[3] = a.to_bytes(4, byteorder='big').hex(), c.to_bytes(4, byteorder='big').hex()
        ma_temp[4], ma_temp[5] = b.to_bytes(4, byteorder='big').hex(), c.to_bytes(4, byteorder='big').hex()
        ma_temp[6] = (Maj & 0xFFFFFFFF).to_bytes(4, byteorder='big').hex()
        ma_var.append(ma_temp)
        T1 = h + S1 + Ch + K[t] + W[t]
        t0_temp = [0] * 4
        t0_temp[0], t0_temp[1] = (ROTR(a, 7) & 0xFFFFFFFF).to_bytes(4, byteorder='big').hex(), (ROTR(a, 18) & 0xFFFFFFFF).to_bytes(4, byteorder='big').hex()
        t0_temp[2], t0_temp[3] = ((ROTR(a, 7) ^ ROTR(a, 18) ^ T1) & 0xFFFFFFFF).to_bytes(4, byteorder='big').hex(), (T1 & 0xFFFFFFFF).to_bytes(4, byteorder='big').hex()
        t0_var.append(t0_temp)
        T2 = S0 + Maj
        t1_temp = [0] * 4
        t1_temp[0], t1_temp[1] = (ROTR(e, 17) & 0xFFFFFFFF).to_bytes(4, byteorder='big').hex(), (ROTR(e, 19) & 0xFFFFFFFF).to_bytes(4, byteorder='big').hex()
        t1_temp[2], t1_temp[3] = ((ROTR(e, 17) ^ ROTR(e, 19) ^ T2) & 0xFFFFFFFF).to_bytes(4, byteorder='big').hex(), (T2 & 0xFFFFFFFF).to_bytes(4, byteorder='big').hex()
        t1_var.append(t1_temp)
        first_temp = [0] * 8
        first_temp[0], first_temp[1] = a.to_bytes(4, byteorder='big').hex(), b.to_bytes(4, byteorder='big').hex()
        first_temp[2], first_temp[3] = c.to_bytes(4, byteorder='big').hex(), d.to_bytes(4, byteorder='big').hex()
        first_temp[4], first_temp[5] = e.to_bytes(4, byteorder='big').hex(), f.to_bytes(4, byteorder='big').hex()
        first_temp[6], first_temp[7] = g.to_bytes(4, byteorder='big').hex(), h.to_bytes(4, byteorder='big').hex()
        first_linked_var.append(first_temp)
        operating_prog_temp = [0] * 12
        operating_prog_temp[0], operating_prog_temp[1] = g.to_bytes(4, byteorder='big').hex(), f.to_bytes(4, byteorder='big').hex()
        operating_prog_temp[2] = e.to_bytes(4, byteorder='big').hex()
        operating_prog_temp[4], operating_prog_temp[5] = d.to_bytes(4, byteorder='big').hex(), (T1 & 0xFFFFFFFF).to_bytes(4, byteorder='big').hex()
        operating_prog_temp[6], operating_prog_temp[7] = c.to_bytes(4, byteorder='big').hex(), b.to_bytes(4, byteorder='big').hex()
        operating_prog_temp[8] = a.to_bytes(4, byteorder='big').hex()
        operating_prog_temp[10], operating_prog_temp[11] = (T1 & 0xFFFFFFFF).to_bytes(4, byteorder='big').hex(), (T2 & 0xFFFFFFFF).to_bytes(4, byteorder='big').hex()
        h = g
        g = f
        f = e
        e = (d + T1) & 0xFFFFFFFF
        operating_prog_temp[3] = e.to_bytes(4, byteorder='big').hex()
        d = c
        c = b
        b = a
        a = (T1 + T2) & 0xFFFFFFFF
        operating_prog_temp[9] = a.to_bytes(4, byteorder='big').hex()
        operating_prog_var.append(operating_prog_temp)
        beyond_temp = [0] * 8
        beyond_temp[0], beyond_temp[1] = a.to_bytes(4, byteorder='big').hex(), b.to_bytes(4, byteorder='big').hex()
        beyond_temp[2], beyond_temp[3] = c.to_bytes(4, byteorder='big').hex(), d.to_bytes(4, byteorder='big').hex()
        beyond_temp[4], beyond_temp[5] = e.to_bytes(4, byteorder='big').hex(), f.to_bytes(4, byteorder='big').hex()
        beyond_temp[6], beyond_temp[7] = g.to_bytes(4, byteorder='big').hex(), h.to_bytes(4, byteorder='big').hex()
        beyond_linked_var.append(beyond_temp)

    H[0] = a + H[0] & 0xFFFFFFFF
    H[1] = b + H[1] & 0xFFFFFFFF
    H[2] = c + H[2] & 0xFFFFFFFF
    H[3] = d + H[3] & 0xFFFFFFFF
    H[4] = e + H[4] & 0xFFFFFFFF
    H[5] = f + H[5] & 0xFFFFFFFF
    H[6] = g + H[6] & 0xFFFFFFFF
    H[7] = h + H[7] & 0xFFFFFFFF
    last_result[0], last_result[1] = a.to_bytes(4, byteorder='big').hex(), b.to_bytes(4, byteorder='big').hex()
    last_result[2], last_result[3] = c.to_bytes(4, byteorder='big').hex(), d.to_bytes(4, byteorder='big').hex()
    last_result[4], last_result[5] = e.to_bytes(4, byteorder='big').hex(), f.to_bytes(4, byteorder='big').hex()
    last_result[6], last_result[7] = g.to_bytes(4, byteorder='big').hex(), h.to_bytes(4, byteorder='big').hex()

    sha256, i = '', 0
    for sha in H:
        temp = sha.to_bytes(4, byteorder='big').hex()
        sha256_result[i] = temp
        sha256 = sha256 + temp
        i += 1
    sha256_system_result.append(first_result)
    sha256_system_result.append(last_result)
    sha256_system_result.append(sha256_result)
    sha256_system_result.append(first_linked_var)
    sha256_system_result.append(beyond_linked_var)
    sha256_system_result.append(operating_prog_var)
    sha256_system_result.append(ch_var)
    sha256_system_result.append(ma_var)
    sha256_system_result.append(s0_var)
    sha256_system_result.append(s1_var)
    sha256_system_result.append(t0_var)
    sha256_system_result.append(t1_var)
    res = {
        "abstract":sha256,
        "array":sha256_system_result
    }
    # res[sha256] = sha256_system_result
    return res


plaintext = sys.argv[1]


print(sha256_final_edition(plaintext))
