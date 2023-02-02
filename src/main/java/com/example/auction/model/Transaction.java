package com.example.auction.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transactions")
@Schema(name = "Transaction)")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long sellerId;
    private Double price;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date time;
    @ManyToOne
    @JsonIgnore
    @JsonBackReference
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private User customer;
    @ManyToOne
    @JsonIgnore
    @JsonBackReference
    @JoinColumn(name = "post_id", referencedColumnName = "id")
    private Post post;

    @Override
    public String toString() {
        return "Transaction{" +
                "sellerId=" + sellerId +
                ", price=" + price +
                ", time=" + time +
                '}';
    }
}

