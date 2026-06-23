package com.yaroslav.partnerflow.comment.repository;

import com.yaroslav.partnerflow.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByClientIdOrderByCreatedAtDesc(Long clientId);

    List<Comment> findByDealIdOrderByCreatedAtDesc(Long dealId);
}