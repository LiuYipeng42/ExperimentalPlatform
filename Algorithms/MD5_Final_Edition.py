import math
import json

# 定义常量，用于初始化128位变量，注意字节顺序，文中的A=0x01234567，这里低值存放低字节，即01 23 45 67，所以运算时A=0x67452301，其他类似。
# 这里用字符串的形式是为了和hex函数的输出统一，hex(10)输出为'0xA',注意结果为字符串。
import sys

A = '0x67452301'
B = '0xefcdab89'
C = '0x98badcfe'
D = '0x10325476'

# 定义第一轮链接变量, 最后一轮链接变量以及第一步和最后一步相加的结果(求和运算)
first_abcd = ['67452301', 'efcdab89', '98badcfe', '10325476']
last_abcd = [0] * 4
last_result = [0] * 4

# 定义每轮中循环左移的位数, 对应的是F、G、H、I的左移位数S;
# 定义操作程序中的每个数值(4轮运算)
shi_1 = (7, 12, 17, 22) * 4
shi_2 = (5, 9, 14, 20) * 4
shi_3 = (4, 11, 16, 23) * 4
shi_4 = (6, 10, 15, 21) * 4
first_a = [[], [], [], []]
second_a = [[], [], [], []]
first_b = [[], [], [], []]
logic_value = [[], [], [], []]
mk = [[], [], [], []]
Ti = [[], [], [], []]
sleft = [['7', '12', '17', '22', '7', '12', '17', '22', '7', '12', '17', '22', '7', '12', '17', '22'],
         ['5', '9', '14', '20', '5', '9', '14', '20',
          '5', '9', '14', '20', '5', '9', '14', '20'],
         ['4', '11', '16', '23', '4', '11', '16', '23',
          '4', '11', '16', '23', '4', '11', '16', '23'],
         ['6', '10', '15', '21', '6', '10', '15', '21', '6', '10', '15', '21', '6', '10', '15', '21']]
operating_program = [[], [], [], []]

# 封装逻辑函数中的F、G、H、I每个数值
total_logic = []
ans_logic = [[], [], [], []]

# 封装左上角初始链接变量和右下角的操作后的链接变量
init_linked = [[], [], [], []]
after_operating_linked = [[], [], [], []]

Ti_count = 1

result = {}
# 每轮中用到的M[i]次序
m_1 = (0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)
m_2 = (1, 6, 11, 0, 5, 10, 15, 4, 9, 14, 3, 8, 13, 2, 7, 12)
m_3 = (5, 8, 11, 14, 1, 4, 7, 10, 13, 0, 3, 6, 9, 12, 15, 2)
m_4 = (0, 7, 14, 5, 12, 3, 10, 1, 8, 15, 6, 13, 4, 11, 2, 9)


# 定义每轮中用到的函数。L为循环左移，注意左移之后可能会超过32位，所以要和0xffffffff做与运算，确保结果为32位。


def F(x, y, z): return ((x & y) | ((~x) & z))


def G(x, y, z): return ((x & z) | (y & (~z)))


def H(x, y, z): return (x ^ y ^ z)


def I(x, y, z): return (y ^ (x | (~z)))


def L(x, n): return (((x << n) | (x >> (32 - n))) & (0xffffffff))


# 定义函数，用来产生常数T[i]，常数有可能超过32位，同样需要&0xffffffff操作。注意返回的是十进制的数


def T(i):
    result = (int(4294967296 * abs(math.sin(i)))) & 0xffffffff
    if len(Ti[0]) < 16:
        Ti[0].append(hex(result)[2:])
    elif len(Ti[1]) < 16:
        Ti[1].append(hex(result)[2:])
    elif len(Ti[2]) < 16:
        Ti[2].append(hex(result)[2:])
    else:
        Ti[3].append(hex(result)[2:])
    return result


# 定义函数，用来将列表中的元素循环右移。原因是在每轮操作中，先运算A的值，然后是D，C，B，16轮之后右恢复原来顺序，所以只要每次操作第一个元素即可。
def shift(shift_list):
    shift_list = [shift_list[3], shift_list[0], shift_list[1], shift_list[2]]
    return shift_list


# 定义主要的函数，参数为当做种子的列表，每轮用到的F，G，H，I，生成的M[]，以及循环左移的位数。该函数完成一轮运算


def fun(fun_list, f, m, shi):
    count = 0
    global Ti_count
    # 引入全局变量，T(i)是从1到64循环的。
    while count < 16:
        logic_list = []
        tmp3 = int(fun_list[1], 16)
        if len(first_b[0]) < 16:
            first_b[0].append(hex(tmp3)[2:])
        elif len(first_b[1]) < 16:
            first_b[1].append(hex(tmp3)[2:])
        elif len(first_b[2]) < 16:
            first_b[2].append(hex(tmp3)[2:])
        else:
            first_b[3].append(hex(tmp3)[2:])
        logic_list.append(hex(tmp3)[2:])
        logic_list.append(hex(int(fun_list[2], 16))[2:])
        logic_list.append(hex(tmp3)[2:])
        logic_list.append(hex(int(fun_list[3], 16))[2:])
        init_list = []
        init_list.append(fun_list[0][2:])
        init_list.append(fun_list[1][2:])
        init_list.append(fun_list[2][2:])
        init_list.append(fun_list[3][2:])
        if len(init_linked[0]) < 16:
            init_linked[0].append(init_list)
        elif len(init_linked[1]) < 16:
            init_linked[1].append(init_list)
        elif len(init_linked[2]) < 16:
            init_linked[2].append(init_list)
        else:
            init_linked[3].append(init_list)
        tmp = f(int(fun_list[1], 16), int(
            fun_list[2], 16), int(fun_list[3], 16)) & 0xffffffff
        if len(logic_value[0]) < 16:
            logic_value[0].append(hex(tmp)[2:])
        elif len(logic_value[1]) < 16:
            logic_value[1].append(hex(tmp)[2:])
        elif len(logic_value[2]) < 16:
            logic_value[2].append(hex(tmp)[2:])
        else:
            logic_value[3].append(hex(tmp & 0xffffffff)[2:])
        logic_list.append(hex(tmp)[2:])
        total_logic.append(logic_list)
        tmp2 = int(fun_list[0], 16)
        if len(second_a[0]) < 16:
            second_a[0].append(hex(tmp2)[2:])
        elif len(second_a[1]) < 16:
            second_a[1].append(hex(tmp2)[2:])
        elif len(second_a[2]) < 16:
            second_a[2].append(hex(tmp2)[2:])
        else:
            second_a[3].append(hex(tmp2)[2:])
        xx = tmp2 + tmp + int(m[count], 16) + T(Ti_count)
        xx = xx & 0xffffffff
        ll = L(xx, shi[count])
        # fun_list[0] = hex((int(fun_list[1],16) + ll)&(0xffffffff))[:-1]
        fun_list[0] = hex((int(fun_list[1], 16) + ll) & (0xffffffff))
        if len(first_a[0]) < 16:
            first_a[0].append(fun_list[0][2:])
        elif len(first_a[1]) < 16:
            first_a[1].append(fun_list[0][2:])
        elif len(first_a[2]) < 16:
            first_a[2].append(fun_list[0][2:])
        else:
            first_a[3].append(fun_list[0][2:])
        # 最后的[:-1]是为了去除类似'0x12345678L'最后的'L'
        fun_list = shift(fun_list)
        init_list = []
        init_list.append(fun_list[0][2:])
        init_list.append(fun_list[1][2:])
        init_list.append(fun_list[2][2:])
        init_list.append(fun_list[3][2:])
        if len(after_operating_linked[0]) < 16:
            after_operating_linked[0].append(init_list)
        elif len(after_operating_linked[1]) < 16:
            after_operating_linked[1].append(init_list)
        elif len(after_operating_linked[2]) < 16:
            after_operating_linked[2].append(init_list)
        else:
            after_operating_linked[3].append(init_list)
        count += 1
        Ti_count += 1
    return fun_list


# 该函数生成每轮需要的M[]，最后的参数是为了当有很多分组时，进行偏移。


def genM16(order, ascii_list, f_offset):
    ii = 0
    m16 = [0] * 16
    f_offset = f_offset * 64
    for i in order:
        i = i * 4
        m16[ii] = '0x' + ''.join((ascii_list[i + f_offset] + ascii_list[i + 1 + f_offset] +
                                  ascii_list[i + 2 + f_offset] + ascii_list[i + 3 + f_offset]).split('0x'))
        ii += 1
    for c in m16:
        ind = m16.index(c)
        m16[ind] = reverse_hex(c)
    return m16


# 翻转十六进制数


def reverse_hex(hex_str):
    hex_str = hex_str[2:]
    hex_str_list = []
    for i in range(0, len(hex_str), 2):
        hex_str_list.append(hex_str[i:i + 2])
    hex_str_list.reverse()
    hex_str_result = '0x' + ''.join(hex_str_list)
    # print("十六进制",hex_str_result)
    return hex_str_result


# 显示结果函数，将最后运算的结果列表进行翻转，合并成字符串的操作。


def show_result(f_list):
    result = ''
    f_list1 = [0] * 4
    for i in f_list:
        f_list1[f_list.index(i)] = reverse_hex(i)[2:]
        result = result + f_list1[f_list.index(i)]
    # print("摘要",result)
    return result


def md5(plainText):
    # 程序运行开始
    abcd_list = [A, B, C, D]
    input_m = plainText

    # 对每一个输入先添加一个'0x80'，即'10000000'
    ascii_list = list((map(hex, map(ord, input_m))))
    msg_lenth = len(ascii_list) * 8
    ascii_list.append('0x80')

    # 补充0
    while (len(ascii_list) * 8 + 64) % 512 != 0:
        ascii_list.append('0x00')

    # 最后64为存放消息长度，注意长度存放顺序低位在前。
    # 例如，消息为'a'，则长度为'0x0800000000000000'
    msg_lenth_0x = hex(msg_lenth)[2:]
    msg_lenth_0x = '0x' + msg_lenth_0x.rjust(16, '0')
    msg_lenth_0x_big_order = reverse_hex(msg_lenth_0x)[2:]
    msg_lenth_0x_list = []
    for i in range(0, len(msg_lenth_0x_big_order), 2):
        msg_lenth_0x_list.append('0x' + msg_lenth_0x_big_order[i:i + 2])
    ascii_list.extend(msg_lenth_0x_list)

    # 对每个分组进行4轮运算
    for i in range(0, len(ascii_list) // 64):
        # 将最初128位种子存放在变量中，
        aa, bb, cc, dd = abcd_list
        # 根据顺序产生每轮M[]列表
        order_1 = genM16(m_1, ascii_list, i)
        order_2 = genM16(m_2, ascii_list, i)
        order_3 = genM16(m_3, ascii_list, i)
        order_4 = genM16(m_4, ascii_list, i)
        # 主要四轮运算，注意打印结果列表已经被进行过右移操作！
        abcd_list = fun(abcd_list, F, order_1, shi_1)
        abcd_list = fun(abcd_list, G, order_2, shi_2)
        abcd_list = fun(abcd_list, H, order_3, shi_3)
        abcd_list = fun(abcd_list, I, order_4, shi_4)
        # 将最后输出与最初128位种子相加，注意，最初种子不能直接使用abcd_list[0]等，因为abcd_list已经被改变
        output_a = hex((int(abcd_list[0], 16) + int(aa, 16)) & 0xffffffff)
        output_b = hex((int(abcd_list[1], 16) + int(bb, 16)) & 0xffffffff)
        output_c = hex((int(abcd_list[2], 16) + int(cc, 16)) & 0xffffffff)
        output_d = hex((int(abcd_list[3], 16) + int(dd, 16)) & 0xffffffff)
        last_abcd[0] = hex((int(abcd_list[0], 16)))[2:]
        last_abcd[1] = hex((int(abcd_list[1], 16)))[2:]
        last_abcd[2] = hex((int(abcd_list[2], 16)))[2:]
        last_abcd[3] = hex((int(abcd_list[3], 16)))[2:]
        # 将输出放到列表中，作为下一次128位种子
        abcd_list = [output_a, output_b, output_c, output_d]
        for i in range(4):
            last_result[i] = abcd_list[i][2:]
        # 将全局变量Ti_count恢复，一遍开始下一个分组的操作。
        Ti_count = 1
    # order_1, order_2, order_3, order_4就是对应于F、G、H、I的M[k]
    for i in range(16):
        mk[0].append(order_1[i][2:])
        mk[1].append(order_2[i][2:])
        mk[2].append(order_3[i][2:])
        mk[3].append(order_4[i][2:])
    for i in range(4):
        for j in range(16):
            tmp_list = []
            tmp_list.append(first_a[i][j])
            tmp_list.append(first_b[i][j])
            tmp_list.append(second_a[i][j])
            tmp_list.append(logic_value[i][j])
            tmp_list.append(mk[i][j])
            tmp_list.append(Ti[i][j])
            tmp_list.append(sleft[i][j])
            operating_program[i].append(tmp_list)
    for i in range(len(total_logic)):
        for j in range(len(total_logic[i])):
            if len(total_logic[i][j]) < 8:
                total_logic[i][j] = '0' + total_logic[i][j]
        if i < 16:
            ans_logic[0].append(total_logic[i])
        elif i < 32:
            ans_logic[1].append(total_logic[i])
            ans_logic[1][-1][1], ans_logic[1][-1][2] = ans_logic[1][-1][2], ans_logic[1][-1][1]
            ans_logic[1][-1][1] = ans_logic[1][-1][-2]
        elif i < 48:
            ans_logic[2].append(total_logic[i])
            ans_logic[2][-1].pop(2)
        else:
            ans_logic[3].append(total_logic[i])
            ans_logic[3][-1].pop(2)
            ans_logic[3][-1][0], ans_logic[3][-1][1] = ans_logic[3][-1][1], ans_logic[3][-1][0]

    # print("操作程序",operating_program)
    # print("明文十六进制分组",mk[0])
    # print("逻辑函数",ans_logic)
    # print("初始链接变量",init_linked)
    # print("经过操作之后的链接变量",after_operating_linked)
    # print("",last_result)
    result["mk"] = mk[0]
    result["abstract"] = show_result(abcd_list)
    result["ans_logic"] = ans_logic
    result["init_linked"] = init_linked
    result["after_operating_linked"] = after_operating_linked
    result["operating_program"] = operating_program
    result["last_result"] = last_result
    # print(json.dumps(result))
    return result


# run this function
print(md5(sys.argv[1]))
