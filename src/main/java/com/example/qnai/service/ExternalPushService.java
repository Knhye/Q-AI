package com.example.qnai.service;


import com.example.qnai.dto.fcm.request.MessagePushServiceRequest;

import java.util.List;

public interface ExternalPushService {
    void send(List<MessagePushServiceRequest> request);
}
