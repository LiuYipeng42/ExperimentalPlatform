package com.guet.ExperimentalPlatform.Entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
@TableName("FT_encryption_information")
public class FTEncryptionInformation {

    @TableId(type = IdType.AUTO)
    Long id;

    long connectId;

    String receiverPublicKey;

    String senderPublicKey;

    String aesKeyCryptText;

    String fileTextCryptText;

    String digitalSign;
}
