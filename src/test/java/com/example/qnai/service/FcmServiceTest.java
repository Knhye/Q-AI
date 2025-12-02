package com.example.qnai.service;

import com.example.qnai.dto.fcm.request.MessagePushServiceRequest;
import com.example.qnai.entity.Users;
import com.example.qnai.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FcmServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FcmService fcmService;

    @Test
    void 배치_메시지_구성_테스트() {
        // given
        Users user = Users.builder()
                .id(1L)
                .fcmToken("test_token_123")
                .build();

        when(userRepository.findAllById(anyList()))
                .thenReturn(List.of(user));
        System.out.println();

        List<MessagePushServiceRequest> requests = List.of(
                new MessagePushServiceRequest(1L, 100L, "제목", "내용")
        );

        // when : 메서드 실행
        fcmService.send(requests);

        // then
        verify(userRepository).findAllById(List.of(1L));
    }

}
