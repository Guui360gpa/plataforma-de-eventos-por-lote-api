package com.contatodireto.eventflow.service;

import com.contatodireto.eventflow.dto.UserRegisterDto;
import com.contatodireto.eventflow.dto.UserResponseDto;
import com.contatodireto.eventflow.model.User;

public interface UserService {
    UserResponseDto register(UserRegisterDto dto);
    User searchByEmail(String email);
    UserResponseDto searchById(Long id);
    void banUser(Long id);
}
