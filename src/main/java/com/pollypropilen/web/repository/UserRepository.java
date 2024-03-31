package com.pollypropilen.web.repository;

import com.pollypropilen.web.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByUsernameIgnoreCase(String userName);

    Optional<User> findUserById(Long id);

    List<User> findAllByUsernameContainingIgnoreCase(String userName);

}