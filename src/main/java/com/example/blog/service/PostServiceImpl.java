package com.example.blog.service;


import com.example.blog.dto.EditPostDto;
import com.example.blog.dto.PostDto;
import com.example.blog.entity.Post;
import com.example.blog.entity.User;
import com.example.blog.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;

    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public Post publishPost(String title, String content, User author) {
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setAuthor(author);
        post.setPostTime(LocalDateTime.now());
        return postRepository.save(post);
    }

    @Override
    public Post editPost(Long id, String title, String content) {
        Post post = postRepository.findById(id).orElseThrow();
        post.setTitle(title);
        post.setContent(content);
        return postRepository.save(post);
    }

    @Override
    public Optional<Post> getById(Long id) {
        return postRepository.findById(id);
    }

    @Override
    public boolean existById(Long id) {
        return postRepository.existsById(id);
    }

    @Override
    public void deletePostById(Long id) {
        postRepository.deleteById(id);
    }

    @Override
    public Page<PostDto> findAll(Pageable pageable) {
        return postRepository.findAll(pageable).map(post -> new PostDto(post.getId(), post.getTitle(), post.getContent(), post.getAuthor().getId(), post.getAuthor().getUsername(), post.getPostTime()));
    }

    @Override
    public Page<PostDto> findByAuthor(User author, Pageable pageable) {
        return postRepository.findAllByAuthor(author, pageable).map(post -> new PostDto(post.getId(), post.getTitle(), post.getContent(), post.getAuthor().getId(), post.getAuthor().getUsername(), post.getPostTime()));
    }
}
