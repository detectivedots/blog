package com.example.blog.controller;

import com.example.blog.dto.CreatePostDto;
import com.example.blog.dto.EditPostDto;
import com.example.blog.dto.PostDto;
import com.example.blog.entity.Post;
import com.example.blog.entity.User;
import com.example.blog.repository.PostRepository;
import com.example.blog.service.PostService;
import com.example.blog.service.UserService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;
    private final UserService userService;

    public PostController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<Void> createPost(@RequestBody @Valid CreatePostDto postDto, Principal principal, UriComponentsBuilder ucb) {
        User author = userService.getByEmail(principal.getName()).orElseThrow();
        Post post = postService.publishPost(postDto.getTitle(), postDto.getContent(), author);
        URI savedLocation = ucb.path("/api/posts/{id}").buildAndExpand(post.getId()).toUri();
        return ResponseEntity.created(savedLocation).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getById(@PathVariable Long id) {
        Optional<Post> ret = postService.getById(id);
        if (ret.isEmpty()) return ResponseEntity.notFound().build();
        Post post = ret.get();
        PostDto postDto = new PostDto(post.getId(), post.getTitle(), post.getContent(), post.getAuthor().getId(),
                post.getAuthor().getUsername(), post.getPostTime()
        );
        return ResponseEntity.ok(postDto);
    }

    @PutMapping("/edit")
    public ResponseEntity<PostDto> editPost(@RequestBody @Valid EditPostDto postDto, Principal principal) {
        Optional<Post> targetPostOptional = postService.getById(postDto.getId());
        if (targetPostOptional.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        Post targetPost = targetPostOptional.get();
        String authorEmail = targetPost.getAuthor().getEmail();
        if (!principal.getName().equals(authorEmail))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        Post editedPost = postService.editPost(postDto.getId(), postDto.getTitle(), postDto.getContent());
        PostDto editedPostDTO = new PostDto(editedPost.getId(), editedPost.getTitle(), editedPost.getContent(),
                editedPost.getAuthor().getId(), editedPost.getAuthor().getUsername(), editedPost.getPostTime());
        return ResponseEntity.ok(editedPostDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id, Principal principal) {
        Optional<Post> postOptional = postService.getById(id);
        if (postOptional.isEmpty()) return ResponseEntity.notFound().build();
        String authorEmail = postOptional.get().getAuthor().getEmail();
        if (!authorEmail.equals(principal.getName())) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        postService.deletePostById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/find_all")
    public Page<PostDto> findAll(@ParameterObject @PageableDefault(page = 0, size = 20, sort = "postTime", direction = Sort.Direction.DESC) Pageable pageable) {
        return postService.findAll(pageable);
    }

    @GetMapping("/find_by_user/{user_id}")
    public Page<PostDto> findByUser(@PathVariable Long user_id, @ParameterObject @PageableDefault(page = 0, size = 20, sort = "postTime", direction = Sort.Direction.DESC) Pageable pageable) {
        Optional<User> userOptional = userService.getById(user_id);
        if (userOptional.isEmpty()) return Page.empty();
        return postService.findByAuthor(userOptional.get(), pageable);
    }


}
