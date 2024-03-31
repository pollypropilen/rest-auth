package com.pollypropilen.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class TokenDTO {
    private String token;
    private Date expiryAt;
}
