package com.guet.ExperimentalPlatform.pojo;

import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
public class TransmissionInfo {
    private long connectId;
    private long encryptionInformationId;
}
