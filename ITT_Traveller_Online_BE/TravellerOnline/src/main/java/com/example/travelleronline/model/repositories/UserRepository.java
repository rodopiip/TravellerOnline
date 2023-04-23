package com.example.travelleronline.model.repositories;

import com.example.travelleronline.model.entities.Comment;
import com.example.travelleronline.model.entities.Post;
import com.example.travelleronline.model.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.*;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    Optional<User> findById(int id);
    Optional<User> findByVerificationCode(String code);
    boolean existsByEmail(String email);


}