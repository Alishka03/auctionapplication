package com.example.auction.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "posts")
@Setter
@Getter
@NoArgsConstructor
@Schema(name = "Post")
@AllArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Double price;
    private String postPhoto;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dateCreated;
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    private User author;
    private Boolean active;
    @JsonManagedReference
    @OneToMany(mappedBy = "post")
    private List<Transaction> transactions;

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", postPhoto='" + postPhoto + '\'' +
                ", dateCreated=" + dateCreated +
                ", active=" + active +
                '}';
    }
}
