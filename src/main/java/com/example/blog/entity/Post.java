package com.example.blog.entity;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
public class Post {
    @Id @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private String title;
    @Column(nullable = false, columnDefinition = "TEXT") private String content;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) private User author;
    @Column private LocalDateTime postTime = LocalDateTime.now();
    public Post(){

    }
    public Post(Long id, String title, String content, User author){
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.postTime = LocalDateTime.now();
    }

    public Post(Long id, String title, String content, User author, LocalDateTime postTime){
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.postTime = postTime;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public LocalDateTime getPostTime() {
        return postTime;
    }

    public void setPostTime(LocalDateTime postTime) {
        this.postTime = postTime;
    }
}
