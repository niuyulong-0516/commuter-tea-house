package com.commutertea.user;

import com.commutertea.common.TransportType;
import com.commutertea.user.dto.LoginRequest;
import com.commutertea.user.dto.RegisterRequest;
import com.commutertea.user.dto.UpdateProfileRequest;
import com.commutertea.user.dto.UserResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        String username = request.getUsername() == null ? "" : request.getUsername().trim();
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("这个用户名已经有人用了");
        }

        User user = new User();
        user.setUsername(username);
        user.setNickname(username);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setTransportType(TransportType.valueOf(request.getTransportType()));
        user.setCreatedAt(LocalDateTime.now());
        // 注册即登录，直接发令牌，前端无需再调一次登录。
        user.setToken(UUID.randomUUID().toString().replace("-", ""));
        userRepository.save(user);

        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse login(LoginRequest request) {
        String username = request.getUsername() == null ? "" : request.getUsername().trim();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("用户名或密码不正确"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("用户名或密码不正确");
        }

        user.setToken(UUID.randomUUID().toString().replace("-", ""));
        return UserResponse.from(user);
    }

    /** 取出当前请求携带令牌对应的用户，令牌无效时返回 null。 */
    public User findByToken(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        return userRepository.findByToken(token).orElse(null);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("用户不存在"));
    }

    @Transactional
    public UserResponse updateTransportType(User user, String transportType) {
        user.setTransportType(TransportType.valueOf(transportType));
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse updateProfile(User user, UpdateProfileRequest request) {
        user.setAvatar(request.avatar());
        user.setNickname(request.nickname());
        user.setGender(request.gender());
        user.setBio(request.bio());
        return UserResponse.from(user);
    }

    @Transactional
    public void logout(User user) {
        user.setToken(null);
    }
}
