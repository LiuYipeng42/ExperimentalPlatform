#!/usr/bin/python3
import socket
from binascii import hexlify, unhexlify
import sys

# XOR two bytearrays
def xor(first, second):
   return bytearray(x^y for x,y in zip(first, second))

class PaddingOracle:

    def __init__(self, host, port) -> None:
        self.s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.s.connect((host, port))

        ciphertext = self.s.recv(4096).decode().strip()
        self.ctext = unhexlify(ciphertext)

    def decrypt(self, ctext: bytes) -> None:
        self._send(hexlify(ctext))
        return self._recv()

    def _recv(self):
        resp = self.s.recv(4096).decode().strip()
        return resp 

    def _send(self, hexstr: bytes):
        self.s.send(hexstr + b'\n')

    def __del__(self):
        self.s.close()


if __name__ == "__main__":

    if len(sys.argv) < 2: 
        sys.exit("Please select a block")

    block = int(sys.argv[1])
    # oracle = PaddingOracle('10.9.0.80', 6000)
    oracle = PaddingOracle("containerIP", 6000)

    # Get the IV + Ciphertext from the oracle
    iv_and_ctext = bytearray(oracle.ctext)
    C0    = iv_and_ctext[00:16]  # C0 is IV
    C1    = iv_and_ctext[16:32]  # 1st block of ciphertext
    C2    = iv_and_ctext[32:48]  # 2nd block of ciphertext
    C3    = iv_and_ctext[48:64]  # 3rd block of ciphertext

    #######################################################
    # Choose the target block
    if block==1: C = bytearray(C0)  # Use C0 to unlock P1
    if block==2: C = bytearray(C1)  # Use C1 to unlock P2  
    if block==3: C = bytearray(C2)  # Use C1 to unlock P3  
    #######################################################

    # D is the output of the AES block cipher. 
    # Make D and C the same, so when they are XOR-ed, 
    # The result is 0. This step is not necessary for the 
    # attack. Its purpose is to make the printout look neat.
    D = bytearray(16)
    D[0:16] = C[0:16]

    # Save the original copy of the C 
    C_original = bytearray(C)
    last = C[15]

    # We will try padding K = 01, 02, ...  16
    for K in range(1, 17):
       for j in range(1, K):
           # Making sure C[16-j] XOR D[16-j] = K.
           C[16-j] = D[16-j]^K
   
       # For padding = K, we will try to find a value for C[16-K],
       # such that the padding is valid
       for i in range(256):
           C[16 - K] = i
         
           # Send the constructed data to the oracle, and get the status 

           ##########################################################################
           # Choose the target block 
           if block==1: status = oracle.decrypt(C + C1)            # For unlocking P1
           if block==2: status = oracle.decrypt(C0 + C + C2)       # For unlocking P2
           if block==3: status = oracle.decrypt(C0 + C1 + C + C3)  # For unlocking P3
           ##########################################################################

           if status == "Valid":
               # The padding is valid, but for K=1, we will have two valid cases,
               # and we will eliminate one case (i.e., the case using the original data).
               if K != 1 or i != last: 
                  # This gives us one byte of D, we use i and K to update D
                  print("i = 0x{:02X}  -- D[{}] =  0x{:02X} ".format(i, 16 - K, i^K))
                  D[16 - K] = i^K

                  # XOR C_original with D will give us P
                  P = xor(C_original, D)
                  print(P.hex())
 
    print("----------------------------------------")
    try:  
        print("Secret Message (P): " + P.decode("utf-8"))
    except: 
        print("The message P cannot be decoded to ascii!")
        print("    Here is the hex string " + P.hex())
