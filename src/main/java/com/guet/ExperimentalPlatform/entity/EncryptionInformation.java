package com.guet.ExperimentalPlatform.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
@TableName("encryption_information")
public class EncryptionInformation {

    @TableId(type = IdType.AUTO)
    Long id;

    long connectId;

    String receiverPublicKey;

    String senderPublicKey;

    String aesKeyCryptText;

    String fileTextCryptText;

    String digitalSign;
}
