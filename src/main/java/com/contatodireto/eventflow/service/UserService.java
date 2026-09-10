package com.contatodireto.eventflow.service;

import com.contatodireto.eventflow.dto.UserRegisterDto;
import com.contatodireto.eventflow.dto.UserResponseDto;

public interface UserService {
    UserResponseDto register(UserRegisterDto dto);
    UserResponseDto searchByEmail(String email);
    UserResponseDto searchById(Long id);
    void banUser(Long id);
}
