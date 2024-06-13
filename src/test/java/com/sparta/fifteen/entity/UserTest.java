package com.sparta.fifteen.entity;

import com.sparta.fifteen.dto.UserRegisterRequestDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class UserTest {

    @Autowired
    private Validator validatorInjected; // Inject the validator

    @ParameterizedTest
    @ValueSource(strings = {"window12345", "user", "user_1234", "user@1234", "user1234!@"})
    @DisplayName("유효한 아이디 생성 테스트")
    void createUserWithValidUsername(String username) {
        // given
        UserRegisterRequestDto requestDto = new UserRegisterRequestDto();
        requestDto.setUsername(username);
        requestDto.setPassword("password123");
        requestDto.setName("Test User");
        requestDto.setOneLine("Hello, I'm a test user.");
        requestDto.setEmail("test@example.com");

        // when
        User user = new User(requestDto);
        Set<ConstraintViolation<User>> violations = validatorInjected.validate(user);

        displayViolationDetails(violations);

        // then
        assertTrue(violations.isEmpty());
    }
    
    // 유효한 비밀번호 테스트
    @ParameterizedTest
    @ValueSource(strings = {"", "a","abcd1234"})
    @DisplayName("유효한 비밀번호 테스트")
    void createUserWithValidPassword(String password){
        // given
        UserRegisterRequestDto requestDto = new UserRegisterRequestDto();
        requestDto.setUsername("window12345");
        requestDto.setPassword(password);
        requestDto.setName("Test User");
        requestDto.setOneLine("Hello, I'm a test user.");
        requestDto.setEmail("test@example.com");

        // when
        User user = new User(requestDto);
        Set<ConstraintViolation<User>> violations = validatorInjected.validate(user);

        displayViolationDetails(violations);

        // then
        assertTrue(violations.isEmpty());
    }

    private void displayViolationDetails(Set<ConstraintViolation<User>> violations){
        // Print violation details
        for (ConstraintViolation<User> violation : violations) {
            System.out.println("Property: " + violation.getPropertyPath());
            System.out.println("Message: " + violation.getMessage());
            System.out.println("Invalid Value: " + violation.getInvalidValue());
            System.out.println("---");
        }
    }
}