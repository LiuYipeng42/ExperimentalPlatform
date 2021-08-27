package com.guet.ExperimentalPlatform.service;


import com.guet.ExperimentalPlatform.entity.FTAllInfo;
import com.guet.ExperimentalPlatform.pojo.TransmissionInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public interface FileTransmissionService {

    void saveMessage(String messageText, long userId, long toUserId,
                     ConcurrentHashMap<String, TransmissionInfo> transmissionId);

    List<FTAllInfo> getAllInfo(long userId);


}
