package com.example.qnai.service;

import com.example.qnai.dto.user.request.UserPasswordUpdateRequest;
import com.example.qnai.entity.Users;
import com.example.qnai.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
//
//    @Mock
//    private UserRepository userRepository;
//
//    // 1. PasswordEncoder의 encode 메서드를 사용하기 위해 Mock 처리
//    @Mock
//    private PasswordEncoder passwordEncoder;
//
//    // 테스트 대상인 UserService에 Mock 객체들을 주입
//    @InjectMocks
//    private UserService userService;
//
//    private Users user;
//    private final String oldEncodedPassword = "oldHashedPassword";
//    private final String newEncodedPassword = "newHashedPassword";
//    private final String currentPassword = "correctPassword123";
//    private final String newPassword = "newCorrectPassword456";
//
//    @BeforeEach
//    void setUp() {
//        // Mocking할 Users 객체 생성 (현재 암호화된 비밀번호 설정)
//        user = spy(new Users(1L, "testuser", oldEncodedPassword));
//
//        // updatePassword 메서드가 호출되는지 검증하기 위해 spy 객체 사용
//        doNothing().when(user).updatePassword(any(String.class));
//    }
//
//    @Test
//    @DisplayName("성공적으로 비밀번호가 변경되는 경우를 테스트")
//    void changePassword_Success() {
//        // 2-1. Users 객체 찾기 Mocking
//        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
//
//        // 2-2. currentPassword와 현재 비밀번호 일치 확인 Mocking
//        // currentPassword(correctPassword123)와 oldEncodedPassword(oldHashedPassword)를 비교
//        when(passwordEncoder.matches(currentPassword, oldEncodedPassword)).thenReturn(true);
//
//        // 2-3. newPassword 암호화 Mocking
//        // newPassword(newCorrectPassword456)를 암호화해서 newEncodedPassword(newHashedPassword) 반환
//        when(passwordEncoder.encode(newPassword)).thenReturn(newEncodedPassword);
//
//        // --- 실행 ---
//        boolean result = userService.updateUserPassword(1L, currentPassword, newPassword);
//
//        // --- 검증 ---
//
//        // 4. 개발자가 암호화된 문자열을 미리 알 수 있다고 가정하지 않으므로,
//        // Mockito의 Argument Captor를 사용하는 대신, 암호화된 결과값(newEncodedPassword)이
//        // Users 객체의 updatePassword 메서드에 전달되는지 확인합니다.
//
//        // 1. changePassword 호출 결과가 true인지 확인
//        assertTrue(result, "비밀번호 변경은 성공해야 합니다.");
//
//        // 2. passwordEncoder.matches가 올바른 인자(currentPassword, oldEncodedPassword)로 호출되었는지 확인
//        verify(passwordEncoder).matches(currentPassword, oldEncodedPassword);
//
//        // 3. passwordEncoder.encode가 새로운 비밀번호(newPassword)로 호출되었는지 확인
//        verify(passwordEncoder).encode(newPassword);
//
//        // 4. Users 객체의 updatePassword 메서드가 새로운 암호화된 비밀번호로 호출되었는지 확인
//        // 3. db에 직접 값을 남기지 않으므로, updatePassword가 호출되었는지 확인하는 것으로 충분합니다.
//        verify(user).updatePassword(newEncodedPassword);
//
//        // 5. UserRepository의 save 메서드가 호출되었는지 확인
//        verify(userRepository).save(user);
//    }
//
//    // --- 실패 시나리오 (선택 사항) ---
//
//    @Test
//    @DisplayName("현재 비밀번호가 일치하지 않아 실패하는 경우")
//    void changePassword_Fails_WrongCurrentPassword() {
//        // Mocking: Users 객체 찾기
//        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
//
//        // Mocking: 비밀번호 불일치
//        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
//
//        // --- 실행 & 검증 ---
//        boolean result = userService.updateUserPassword(1L, "wrongPassword", newPassword);
//
//        assertFalse(result, "현재 비밀번호가 일치하지 않으면 실패해야 합니다.");
//
//        // updatePassword 및 encode는 호출되면 안 됩니다.
//        verify(passwordEncoder, never()).encode(anyString());
//        verify(user, never()).updatePassword(anyString());
//        verify(userRepository, never()).save(any(Users.class));
//    }
//
//    @Test
//    @DisplayName("현재 비밀번호와 새 비밀번호가 같은 경우 실패")
//    void changePassword_Fails_SamePassword() {
//        // newPassword를 currentPassword와 동일하게 설정
//        String samePassword = currentPassword;
//
//        // Mocking: Users 객체 찾기
//        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
//
//        // Mocking: 비밀번호 일치 (현재 비밀번호 검증)
//        when(passwordEncoder.matches(samePassword, oldEncodedPassword)).thenReturn(true);
//
//        // Service 로직에서 currentPassword != newPassword 조건으로 인해 실패해야 함
//
//        // --- 실행 & 검증 ---
//        boolean result = userService.changePassword(1L, samePassword, samePassword);
//
//        assertFalse(result, "현재 비밀번호와 새 비밀번호가 같으면 실패해야 합니다.");
//
//        // updatePassword 및 encode는 호출되면 안 됩니다.
//        verify(passwordEncoder, never()).encode(anyString());
//        verify(user, never()).updatePassword(anyString());
//        verify(userRepository, never()).save(any(Users.class));
//    }
}
