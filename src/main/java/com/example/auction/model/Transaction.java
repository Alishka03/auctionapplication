package com.example.auction.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long sellerId;
    private Long customerId;
    private Long postId;
    private Double price;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date time;

    public Transaction(Long sellerId, Long customerId, Long postId, Double price, Date time) {
        this.sellerId = sellerId;
        this.customerId = customerId;
        this.postId = postId;
        this.price = price;
        this.time = time;
    }
}
