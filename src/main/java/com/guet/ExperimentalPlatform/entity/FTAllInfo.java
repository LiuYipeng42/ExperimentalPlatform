package com.guet.ExperimentalPlatform.entity;

import lombok.Data;

import java.util.Date;


@Data
public class FTAllInfo {

    Long connect_id;

    private long senderId;

    private long receiverId;

    private Date connectTime;

    private String status;

    String receiverPublicKey;

    String senderPublicKey;

    String aesKeyCryptText;

    String fileTextCryptText;

    String digitalSign;
}
