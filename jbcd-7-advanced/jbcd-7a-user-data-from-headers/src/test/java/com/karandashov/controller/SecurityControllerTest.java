package com.karandashov.controller;

import com.karandashov.BaseTest;
import com.karandashov.ProxyHeadersUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class SecurityControllerTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void meShouldReturnAnonymousUserWithoutForwardedHeaders() throws Exception {
        mockMvc.perform(get("/api/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(false))
                .andExpect(jsonPath("$.userInfo", aMapWithSize(0)));
    }

    @Test
    void meShouldReturnForwardedUserInfo() throws Exception {
        mockMvc.perform(ProxyHeadersUtils.withAuthenticatedUser(get("/api/me")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(jsonPath("$.userInfo.user").value("student"))
                .andExpect(jsonPath("$.userInfo.email").value("student@example.com"))
                .andExpect(jsonPath("$.userInfo.preferred_username").value("student"))
                .andExpect(jsonPath("$.userInfo.groups[0]").value("students"))
                .andExpect(jsonPath("$.userInfo.groups[1]").value("authors"));
    }
}
