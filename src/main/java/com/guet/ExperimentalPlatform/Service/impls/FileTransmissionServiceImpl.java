package com.guet.ExperimentalPlatform.Service.impls;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.guet.ExperimentalPlatform.Entity.FTAllInfo;
import com.guet.ExperimentalPlatform.Entity.FTConnectRecord;
import com.guet.ExperimentalPlatform.Entity.FTEncryptionInformation;
import com.guet.ExperimentalPlatform.mapper.FTConnectRecordMapper;
import com.guet.ExperimentalPlatform.mapper.FTEncryptionInfoMapper;
import com.guet.ExperimentalPlatform.Service.FileTransmissionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
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
                            ConcurrentHashMap<String, Long> connectIds) {
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
                connectIds.put(
                        userId + " " + toUserId, ftConnectRecord.getId()
                );
            break;
            case "@?)_!__#$%&7reciever":
                // 信息发送者 是 文件接受者
                ftConnectRecord = new FTConnectRecord(toUserId, userId, new Date());
                ftConnectRecordMapper.insert(ftConnectRecord);
                connectIds.put(
                        userId + " " + toUserId, ftConnectRecord.getId()
                );
            break;
            case "OK":
                connectId = connectIds.get(toUserId + " " + userId);
                ftConnectRecordMapper.update(
                        null,
                        new UpdateWrapper<FTConnectRecord>().set("status", "accepted").eq("id", connectId)
                );
                break;
            case "busy":
                connectId = connectIds.get(toUserId + " " + userId);
                ftConnectRecordMapper.update(
                        null,
                        new UpdateWrapper<FTConnectRecord>().set("status", "refused").eq("id", connectId)
                );
                break;
            default:
                if (messageText.startsWith("-----BEGIN PUBLIC KEY-----")) {
                    ftEncryptionInfoMapper.insert(
                            new FTEncryptionInformation()
                                    .setConnectId(connectIds.get(toUserId + " " + userId))
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
                                    .eq("connect_id", connectIds.get(userId + " " + toUserId))
                                    .isNull("digital_sign")
                    );
                    connectIds.remove(userId + " " + toUserId);
                }
        }
    }

    public List<FTAllInfo> getAllInfo(long userId){
        return ftEncryptionInfoMapper.getAllInfo(userId);
    }

}
