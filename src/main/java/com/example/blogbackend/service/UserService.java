package com.example.blogbackend.service;

import com.example.blogbackend.dto.UserDto;
import com.example.blogbackend.entity.UserEntity;
import com.example.blogbackend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<GrantedAuthority> authorities = new ArrayList<>();
        UserEntity userEntity = userRepository.findByUserId(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        if (userEntity.getUserId().equals("admin01")) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }
        return new User(userEntity.getUserId(), userEntity.getUserPw(), authorities);
    }

    @Transactional
    public UserEntity saveUser(UserDto userDto) {
        UserEntity userEntity = UserEntity.builder()
            .userId(userDto.getUserId())
            .userName(userDto.getUserName())
            .userPw(passwordEncoder.encode(userDto.getUserPw()))
            .build();

        return userRepository.save(userEntity);
    }

    public UserEntity findByIdx(Long idx) {
        return userRepository.findById(idx).orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }

    public UserEntity findByUserId(String userId) {
        return userRepository.findByUserId(userId).orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }

    public String findUserNameByUserId(String userId) {
        return userRepository.findByUserId(userId).orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다.")).getUserName();
    }

    public Long findUserIdxByUserId(String userId) {
        return userRepository.findByUserId(userId).orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다.")).getIdx();
    }
}
