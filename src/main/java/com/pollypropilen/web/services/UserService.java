package com.pollypropilen.web.services;

import com.pollypropilen.web.dto.UserDTO;
import com.pollypropilen.web.entity.User;
import com.pollypropilen.web.exception.UserExistException;
import com.pollypropilen.web.model.UserPermissionRole;
import com.pollypropilen.web.payload.misc.ObjectType;
import com.pollypropilen.web.payload.response.MessageResponse;
import com.pollypropilen.web.payload.request.SignupRequest;
import com.pollypropilen.web.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {
    public static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       BCryptPasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(SignupRequest userIn) throws UserExistException {
        LOG.debug("[USER] createUser()...");
        User user = new User();
        user.setUsername(userIn.getUsername());
        user.setPassword(passwordEncoder.encode(userIn.getPassword()));
        user.getRoles().add(UserPermissionRole.ROLE_USER);

        try {
            LOG.debug("[USER] createUser()...done");
            return userRepository.save(user);
        } catch (Exception e) {
            LOG.error("[USER] Error during registration. {}", e.getMessage());
            throw new UserExistException("The user with username '" + user.getUsername() + "' is already exist");
        }
    }

    public User updateUser(UserDTO userDTO, Principal principal) {
        LOG.debug("[USER] updateUser()...");
        User user = getUserByPrincipal(principal);
        LocalDateTime date = LocalDateTime.now();
        user.setUpdatedDate(date);
        user.setLastVisitedDate(date);
        LOG.debug("[USER] updateUser()...done");
        return userRepository.save(user);
    }

    public User getCurrentUser(Principal principal) {
        return getUserByPrincipal(principal);
    }

    private User getUserByPrincipal(Principal principal) {
        String username = principal.getName();
        return userRepository.findUserByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username " + username));
    }

    public User getUserByName(String userName) {
        return userRepository.findUserByUsernameIgnoreCase(userName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username " + userName));
    }


    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public void updateVisitedDate(Principal principal) {
        User user = getCurrentUser(principal);
        updateVisitedDate(user);
    }

    public void updateVisitedDate(User user) {
        LOG.debug("[USER] updateVisitedDate()...");
        user.setLastVisitedDate(LocalDateTime.now());
        userRepository.save(user);
        LOG.debug("[USER] updateVisitedDate()...done");
    }

    public MessageResponse checkUserNameForExisting(String userName) {
        Optional<User> ou = userRepository.findUserByUsernameIgnoreCase(userName);
        if (!ou.isPresent()) {
            return MessageResponse.OK("Username not found and can be used for new user", ObjectType.USER);
        }
        return MessageResponse.ERROR("Username already exists and cannot be used for new user", ObjectType.USER);
    }
}