package com.idcard.idcardsystem.repository;

import com.idcard.idcardsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
