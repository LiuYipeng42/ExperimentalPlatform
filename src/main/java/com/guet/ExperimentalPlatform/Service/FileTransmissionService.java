package com.guet.ExperimentalPlatform.Service;


import com.guet.ExperimentalPlatform.Entity.FTAllInfo;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public interface FileTransmissionService {

    void saveMessage(String messageText, long userId, long toUserId,
                     ConcurrentHashMap<String, Long> connectIds);

    List<FTAllInfo> getAllInfo(long userId);


}
