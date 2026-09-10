package com.contatodireto.eventflow.service;

import com.contatodireto.eventflow.Enum.Role;
import com.contatodireto.eventflow.dto.UserRegisterDto;
import com.contatodireto.eventflow.dto.UserResponseDto;
import com.contatodireto.eventflow.exception.EmailAlreadyRegisteredException;
import com.contatodireto.eventflow.exception.UserNotFoundException;
import com.contatodireto.eventflow.model.RefreshToken;
import com.contatodireto.eventflow.model.User;
import com.contatodireto.eventflow.repository.RefreshTokenRepository;
import com.contatodireto.eventflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public UserResponseDto register(UserRegisterDto dto) {
        if (userRepository.existsByEmail(dto.email())){
            throw new EmailAlreadyRegisteredException("It was not possible to complete the registration.");
        }

        User user = new User();
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setRole(Role.PARTICIPANT);
        user.setBanned(false);

        User save = userRepository.save(user);
        return toResponseDTO(save);
    }

    @Override
    public User searchByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found for the provided email."));
    }

    @Override
    public UserResponseDto searchById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found."));
        return toResponseDTO(user);
    }

    @Override
    public void banUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found."));
        user.setBanned(true);
        userRepository.save(user);

        List<RefreshToken> tokensAtivos = refreshTokenRepository.findAllByUserAndRevokedFalse(user);
        tokensAtivos.forEach(token -> token.setRevoked(true));
        refreshTokenRepository.saveAll(tokensAtivos);
    }

    private UserResponseDto toResponseDTO(User user) {
        UserResponseDto dto = new UserResponseDto(user.getId(),
                user.getName(),
                user.getEmail());
        return dto;
    }
}
