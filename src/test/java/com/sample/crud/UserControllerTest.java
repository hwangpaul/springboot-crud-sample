package com.sample.crud;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.crud.dao.UserDAO;
import com.sample.crud.dto.UserRequest;
import com.sample.crud.vo.UserVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        // Clean up database before each test
        userDAO.findAll().forEach(user -> userDAO.deleteById(user.getId()));
    }

    @Test
    void testCreateUser() throws Exception {
        UserRequest request = new UserRequest("John Doe", "john@example.com", "1234567890", "123 Main St");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void testGetUserById() throws Exception {
        UserVO userVO = UserVO.builder()
                .name("Jane Doe")
                .email("jane@example.com")
                .phone("0987654321")
                .address("456 Oak Ave")
                .build();
        userDAO.insert(userVO);

        mockMvc.perform(get("/api/users/" + userVO.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jane Doe"))
                .andExpect(jsonPath("$.email").value("jane@example.com"));
    }

    @Test
    void testGetAllUsers() throws Exception {
        UserVO user1 = UserVO.builder().name("User 1").email("user1@example.com").phone("1111111111").build();
        UserVO user2 = UserVO.builder().name("User 2").email("user2@example.com").phone("2222222222").build();
        userDAO.insert(user1);
        userDAO.insert(user2);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testUpdateUser() throws Exception {
        UserVO userVO = UserVO.builder()
                .name("Original Name")
                .email("original@example.com")
                .phone("0000000000")
                .build();
        userDAO.insert(userVO);

        UserRequest updateRequest = new UserRequest("Updated Name", "updated@example.com", "9999999999", "New Address");

        mockMvc.perform(put("/api/users/" + userVO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    void testDeleteUser() throws Exception {
        UserVO userVO = UserVO.builder()
                .name("To Delete")
                .email("delete@example.com")
                .phone("0000000000")
                .build();
        userDAO.insert(userVO);

        mockMvc.perform(delete("/api/users/" + userVO.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/users/" + userVO.getId()))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void testValidation() throws Exception {
        UserRequest invalidRequest = new UserRequest("", "invalid-email", "", "");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
