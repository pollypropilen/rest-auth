package com.pollypropilen.web.repository;

import com.pollypropilen.web.entity.Token;
import com.pollypropilen.web.entity.User;
import com.pollypropilen.web.model.TokenStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    List<Token> findAllByUser(User user);

    List<Token> findAllByUserAndToken(User user, String token);

    List<Token> findAllByUserAndStatus(User user, TokenStatus status);

}
