package com.guet.ExperimentalPlatform.service.impls;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.guet.ExperimentalPlatform.entity.ConnectRecord;
import com.guet.ExperimentalPlatform.entity.EncryptionInformation;
import com.guet.ExperimentalPlatform.mapper.ConnectRecordMapper;
import com.guet.ExperimentalPlatform.mapper.EncryptionInformationMapper;
import com.guet.ExperimentalPlatform.pojo.TransmissionInfo;
import com.guet.ExperimentalPlatform.service.MessageService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class MessageServiceImpl implements MessageService{

    ConnectRecordMapper connectRecordMapper;
    EncryptionInformationMapper encryptionInformationMapper;

    @Resource
    public void setConnectRecordMapper(ConnectRecordMapper connectRecordMapper) {
        this.connectRecordMapper = connectRecordMapper;
    }

    @Resource
    public void setEncryptionInformationMapper(EncryptionInformationMapper encryptionInformationMapper) {
        this.encryptionInformationMapper = encryptionInformationMapper;
    }

    @Override
    public void saveMessage(String messageText, long userId, long toUserId,
                            ConcurrentHashMap<String, TransmissionInfo> transmissionId) {
        // userId 信息发送者, toUserId 信息接受者
        ConnectRecord connectRecord;
        long connectId;
        switch (messageText) {
            // Message构造器的
            // 第一个参数是 文件发送者
            // 第二个参数是 文件接受者
            case "@_#$%&7sender":
                // 信息发送者 是 文件发送者
                connectRecord = new ConnectRecord(userId, toUserId, new Date());
                connectRecordMapper.insert(connectRecord);
                transmissionId.put(userId + " " + toUserId,
                        new TransmissionInfo().setConnectId(connectRecord.getId()));
            break;
            case "@?)_!__#$%&7reciever":
                // 信息发送者 是 文件接受者
                connectRecord = new ConnectRecord(toUserId, userId, new Date());
                connectRecordMapper.insert(connectRecord);
                transmissionId.put(userId + " " + toUserId,
                        new TransmissionInfo().setConnectId(connectRecord.getId()));
            break;
            case "OK":
                connectId = transmissionId.get(toUserId + " " + userId).getConnectId();
                connectRecordMapper.update(
                        null,
                        new UpdateWrapper<ConnectRecord>().set("status", "accepted").eq("id", connectId)
                );
                break;
            case "busy":
                connectId = transmissionId.get(toUserId + " " + userId).getConnectId();
                connectRecordMapper.update(
                        null,
                        new UpdateWrapper<ConnectRecord>().set("status", "refused").eq("id", connectId)
                );
                break;
            default:
                if (messageText.startsWith("-----BEGIN PUBLIC KEY-----")) {
                    encryptionInformationMapper.insert(
                            new EncryptionInformation()
                                    .setConnectId(transmissionId.get(toUserId + " " + userId).getConnectId())
                                    .setReceiverPublicKey(messageText.substring(27, messageText.length() - 25))
                    );
                }
                if (messageText.startsWith("{\"senderPublicKey\":\"-----BEGIN PUBLIC KEY-----")) {
                    JSONObject encryptJsonObject = JSON.parseObject(messageText);
                    String senderPublicKey = encryptJsonObject.getString("senderPublicKey");
                    encryptionInformationMapper.update(
                            null,
                            new UpdateWrapper<EncryptionInformation>()
                                    .set("sender_public_key", senderPublicKey.substring(27, senderPublicKey.length() - 25))
                                    .set("aes_key_crypt_text", encryptJsonObject.getString("aesKeyCryptText"))
                                    .set("file_text_crypt_text", encryptJsonObject.getString("fileTextCryptText"))
                                    .set("digital_sign", encryptJsonObject.getString("numberSign"))
                                    .eq("connect_id", transmissionId.get(userId + " " + toUserId).getConnectId())
                                    .isNull("digital_sign")
                    );
                    transmissionId.remove(userId + " " + toUserId);
                }
        }
    }
}
