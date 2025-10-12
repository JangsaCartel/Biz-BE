package com.jangsacartel.biz.mapper;

import com.jangsacartel.biz.config.RootConfig;
import com.jangsacartel.biz.user.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag; // 💡 JUnit Tag import 추가
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RootConfig.class})
@Tag("db") // 💡 DB에 의존하는 테스트임을 나타내는 태그 추가
public class UserMapperTests {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testGetTime() {
        System.out.println("현재 시간: " + userMapper.getTime());
    }
}