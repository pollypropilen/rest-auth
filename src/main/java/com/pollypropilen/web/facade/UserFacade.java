package com.pollypropilen.web.facade;

import com.pollypropilen.web.dto.UserDTO;
import com.pollypropilen.web.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserFacade {
    public static UserDTO userToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId().toString());
        userDTO.setUsername(user.getUsername());
        return userDTO;
    }
}