package com.example.blog.service;

import com.example.blog.dto.PostDto;
import com.example.blog.entity.Post;
import com.example.blog.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PostService {
    Optional<Post> getById(Long id);

    Post publishPost(String title, String content, User author);

    Post editPost(Long id, String title, String content);

    boolean existById(Long id);

    void deletePostById(Long id);

    public Page<PostDto> findAll(Pageable pageable);

    public Page<PostDto> findByAuthor(User author, Pageable pageable);
}
