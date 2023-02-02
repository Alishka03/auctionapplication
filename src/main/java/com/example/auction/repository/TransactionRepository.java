package com.example.auction.repository;

import com.example.auction.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {
    List<Transaction> findByPostId(Long postId);
    List<Transaction> findAllByOrderByTimeAsc();
    List<Transaction> findAllByPostIdOrderByTimeAsc(Long postId);
}
