package com.example.blog.dto;

import java.time.LocalDateTime;

public class PostDto {
    private Long id;
    private String title;
    private String content;
    private Long authorID;
    private String authorUsername;
    private LocalDateTime postDate;

    public PostDto() {
    }

    public PostDto(Long id, String title, String content, Long authorID, String authorUsername, LocalDateTime postDate) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.authorID = authorID;
        this.authorUsername = authorUsername;
        this.postDate = postDate;
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

    public Long getAuthorID() {
        return authorID;
    }

    public void setAuthorID(Long authorID) {
        this.authorID = authorID;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public LocalDateTime getPostDate() {
        return postDate;
    }

    public void setPostDate(LocalDateTime postDate) {
        this.postDate = postDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
