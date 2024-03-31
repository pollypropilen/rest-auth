package com.pollypropilen.web.services;

import com.pollypropilen.web.entity.Token;
import com.pollypropilen.web.entity.User;
import com.pollypropilen.web.model.TokenStatus;
import com.pollypropilen.web.repository.TokenRepository;
import com.pollypropilen.web.repository.UserRepository;
import com.pollypropilen.web.security.SecurityConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository,
                                    TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findUserByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return build(user);
    }

    public User loadUserById(Long id) {
        return userRepository.findUserById(id).orElse(null);
    }


    public static User build(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());

        return new User(user, authorities);
    }

    public void lockAllUserTokens(User user) {
        List<Token> tokenList = tokenRepository.findAllByUser(user);
        tokenList.forEach(tkn -> {
            tkn.setStatus(TokenStatus.CLOSE);
            tokenRepository.save(tkn);
        });
    }

    public boolean checkUserToken(User user, String token) {
        List<Token> tokenList = tokenRepository.findAllByUserAndToken(user, token);
        Boolean result = false;
        for (Token tkn : tokenList) {
            if (tkn.getStatus() == TokenStatus.ACTIVE) {
                if (tkn.getCreatedDate().plusSeconds(SecurityConstants.EXPIRATION_TIME / 1000).isAfter(LocalDateTime.now())) {
                    result = true;
                } else {
                    tkn.setStatus(TokenStatus.CLOSE);
                    tokenRepository.save(tkn);
                }
            }
        }
        return result;
    }

    public void addNewUserToken(User user, String token) {
        lockAllUserTokens(user);
        Token tkn = new Token(token, TokenStatus.ACTIVE, user);
        tokenRepository.save(tkn);
        user.setLastVisitedDate(LocalDateTime.now());
        userRepository.save(user);
    }

}