package com.sample.crud.service.impl;

import com.sample.crud.dao.UserDAO;
import com.sample.crud.dto.UserRequest;
import com.sample.crud.dto.UserResponse;
import com.sample.crud.service.UserService;
import com.sample.crud.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;

    @Override
    public UserResponse createUser(UserRequest request) {
        if (userDAO.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }

        UserVO userVO = UserVO.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .build();

        userDAO.insert(userVO);
        return UserResponse.from(userVO);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        UserVO userVO = userDAO.findById(id);
        if (userVO == null) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        return UserResponse.from(userVO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userDAO.findAll().stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest request) {
        UserVO existingUser = userDAO.findById(id);
        if (existingUser == null) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }

        // Check if email is being changed and if it already exists
        if (!existingUser.getEmail().equals(request.getEmail()) && userDAO.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }

        UserVO userVO = UserVO.builder()
                .id(id)
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .build();

        userDAO.update(userVO);

        // Fetch updated user to get the updated_at timestamp
        UserVO updatedUser = userDAO.findById(id);
        return UserResponse.from(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        UserVO userVO = userDAO.findById(id);
        if (userVO == null) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        userDAO.deleteById(id);
    }
}
