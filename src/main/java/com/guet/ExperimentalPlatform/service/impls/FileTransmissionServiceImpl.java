package com.guet.ExperimentalPlatform.service.impls;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.guet.ExperimentalPlatform.entity.FTConnectRecord;
import com.guet.ExperimentalPlatform.entity.FTEncryptionInformation;
import com.guet.ExperimentalPlatform.mapper.FTConnectRecordMapper;
import com.guet.ExperimentalPlatform.mapper.FTEncryptionInfoMapper;
import com.guet.ExperimentalPlatform.pojo.TransmissionInfo;
import com.guet.ExperimentalPlatform.service.FileTransmissionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class FileTransmissionServiceImpl implements FileTransmissionService {

    FTConnectRecordMapper ftConnectRecordMapper;
    FTEncryptionInfoMapper ftEncryptionInfoMapper;

    @Resource
    public void setConnectRecordMapper(FTConnectRecordMapper ftConnectRecordMapper) {
        this.ftConnectRecordMapper = ftConnectRecordMapper;
    }

    @Resource
    public void setEncryptionInformationMapper(FTEncryptionInfoMapper ftEncryptionInfoMapper) {
        this.ftEncryptionInfoMapper = ftEncryptionInfoMapper;
    }

    @Override
    public void saveMessage(String messageText, long userId, long toUserId,
                            ConcurrentHashMap<String, TransmissionInfo> transmissionId) {
        // userId 信息发送者, toUserId 信息接受者
        FTConnectRecord ftConnectRecord;
        long connectId;
        switch (messageText) {
            // Message构造器的
            // 第一个参数是 文件发送者
            // 第二个参数是 文件接受者
            case "@_#$%&7sender":
                // 信息发送者 是 文件发送者
                ftConnectRecord = new FTConnectRecord(userId, toUserId, new Date());
                ftConnectRecordMapper.insert(ftConnectRecord);
                transmissionId.put(userId + " " + toUserId,
                        new TransmissionInfo().setConnectId(ftConnectRecord.getId()));
            break;
            case "@?)_!__#$%&7reciever":
                // 信息发送者 是 文件接受者
                ftConnectRecord = new FTConnectRecord(toUserId, userId, new Date());
                ftConnectRecordMapper.insert(ftConnectRecord);
                transmissionId.put(userId + " " + toUserId,
                        new TransmissionInfo().setConnectId(ftConnectRecord.getId()));
            break;
            case "OK":
                connectId = transmissionId.get(toUserId + " " + userId).getConnectId();
                ftConnectRecordMapper.update(
                        null,
                        new UpdateWrapper<FTConnectRecord>().set("status", "accepted").eq("id", connectId)
                );
                break;
            case "busy":
                connectId = transmissionId.get(toUserId + " " + userId).getConnectId();
                ftConnectRecordMapper.update(
                        null,
                        new UpdateWrapper<FTConnectRecord>().set("status", "refused").eq("id", connectId)
                );
                break;
            default:
                if (messageText.startsWith("-----BEGIN PUBLIC KEY-----")) {
                    ftEncryptionInfoMapper.insert(
                            new FTEncryptionInformation()
                                    .setConnectId(transmissionId.get(toUserId + " " + userId).getConnectId())
                                    .setReceiverPublicKey(messageText.substring(27, messageText.length() - 25))
                    );
                }

                if (messageText.startsWith("{\"senderPublicKey\":\"-----BEGIN PUBLIC KEY-----")) {
                    JSONObject encryptJsonObject = JSON.parseObject(messageText);
                    String senderPublicKey = encryptJsonObject.getString("senderPublicKey");
                    ftEncryptionInfoMapper.update(
                            null,
                            new UpdateWrapper<FTEncryptionInformation>()
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
