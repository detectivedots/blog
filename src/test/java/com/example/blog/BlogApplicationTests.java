package com.example.blog;

import com.example.blog.controller.PostController;
import com.example.blog.controller.UserController;
import com.example.blog.dto.CreatePostDto;
import com.example.blog.dto.EditPostDto;
import com.example.blog.dto.PostDto;
import com.example.blog.dto.UserDto;
import com.example.blog.entity.Post;
import com.example.blog.entity.User;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.UserRepository;
import com.example.blog.service.PostService;
import com.example.blog.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;

import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BlogApplicationTests {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserController userController;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostService postService;
    @Autowired
    private PostController postController;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TestRestTemplate testRestTemplate;


    @BeforeEach
    void cleanDb() {
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void contextLoads() {
    }

    @Test
    @DirtiesContext
    void userRegisteredCorrectly() {
        String username = "Alice", password = "some_password", email = "Alice@gmail.com";
        User u = userService.register(username, password, email);
        assertThat(u).isNotNull();
        User user = userService.getByEmail(email).orElseThrow();
        assertThat(passwordEncoder.matches(password, user.getPassword())).isTrue();
    }

    @Test
    @DirtiesContext
    void shouldReturnConfilctIfEmailDuplicated() {
        Map<String, String> payload = Map.of("username", "dupuser", "password", "password123", "email", "dup@example.com");
        ResponseEntity<Void> res = testRestTemplate.postForEntity("/api/auth/register", payload, Void.class);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        payload = Map.of("username", "mohamed", "password", "password1234", "email", "dup@example.com");
        res = testRestTemplate.postForEntity("/api/auth/register", payload, Void.class);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DirtiesContext
    void searchTest() {
        userRepository.save(new User(null, "mohamed", "password", "mohamed@m.com"));
        userRepository.save(new User(null, "mahmod", "password", "mahmod@m.com"));
        userRepository.save(new User(null, "mostafa", "password", "mostafa@m.com"));
        userRepository.save(new User(null, "ahmed", "password", "ahmed@m.com"));
        Page<UserDto> page = userService.searchByUsername("mo", PageRequest.of(0, 5));
        assertThat(page.getTotalElements() == 3).isTrue();
        TreeSet<String> requiredNames = new TreeSet<>();
        requiredNames.add("mohamed");
        requiredNames.add("mahmod");
        requiredNames.add("mostafa");
        TreeSet<String> foundNames = new TreeSet<>();
        foundNames.add(page.getContent().get(0).getUsername());
        foundNames.add(page.getContent().get(1).getUsername());
        foundNames.add(page.getContent().get(2).getUsername());
        assertThat(requiredNames).isEqualTo(foundNames);
    }

    @Test
    @DirtiesContext
    void postIsCreatedByUser() {
        String email = "mohamed@m.com";
        userRepository.save(new User(null, "mohamed", passwordEncoder.encode("abc123"), email));
        User user = userRepository.findByEmail("mohamed@m.com").orElseThrow();
        String title = "Hello world!";
        String content = "This is my first post";

        CreatePostDto postDto = new CreatePostDto(title, content);
        ResponseEntity<Void> res = testRestTemplate.withBasicAuth("mohamed@m.com", "abc123").postForEntity("/api/posts/create", postDto, Void.class);
        String location = Objects.requireNonNull(res.getHeaders().get("location")).get(0);
        String[] splits = location.split("/");
        String createdPostId = splits[splits.length - 1];
        ResponseEntity<PostDto> getResponse = testRestTemplate.withBasicAuth("mohamed@m.com", "abc123").getForEntity("/api/posts/" + createdPostId, PostDto.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        PostDto createdPost = getResponse.getBody();
        assertThat(createdPost.getTitle()).isEqualTo(title);
        assertThat(createdPost.getContent()).isEqualTo(content);
        assertThat(createdPost.getAuthorUsername()).isEqualTo("mohamed");
    }

    @Test
    @DirtiesContext
    void postCanBeEditedOnlyByOwner() {
        String mohamedEmail = "mohamed@m.com";
        String mohamedPassword = "abc123";
        userRepository.save(new User(null, "mohamed", passwordEncoder.encode(mohamedPassword), mohamedEmail));
        String ahmedEmail = "ahmed@m.com";
        String ahmedPassword = "abc1234";
        userRepository.save(new User(null, "ahmed", passwordEncoder.encode(ahmedPassword), ahmedEmail));
        User user = userRepository.findByEmail(mohamedEmail).orElseThrow();
        String title = "Hello world!";
        String content = "This is my first post";
        CreatePostDto postDto = new CreatePostDto(title, content);
        ResponseEntity<Void> res = testRestTemplate.withBasicAuth(mohamedEmail, mohamedPassword).postForEntity("/api/posts/create", postDto, Void.class);
        String location = Objects.requireNonNull(res.getHeaders().get("location")).get(0);
        String[] splits = location.split("/");
        String createdPostId = splits[splits.length - 1];
        ResponseEntity<PostDto> getResponse = testRestTemplate.withBasicAuth(mohamedEmail, mohamedPassword).getForEntity("/api/posts/" + createdPostId, PostDto.class);
        PostDto createdPost = getResponse.getBody();
        EditPostDto editPostDto = new EditPostDto(createdPost.getId(), "Hacker", "I edited the post", createdPost.getAuthorID());
        ResponseEntity<PostDto> editedPostResponse = testRestTemplate.withBasicAuth(ahmedEmail, ahmedPassword).exchange("/api/posts/edit", HttpMethod.PUT, new HttpEntity<>(editPostDto), PostDto.class);
        assertThat(editedPostResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DirtiesContext
    void postIsEdited() {
        String mohamedEmail = "mohamed@m.com";
        String mohamedPassword = "abc123";
        userRepository.save(new User(null, "mohamed", passwordEncoder.encode(mohamedPassword), mohamedEmail));
        String title = "Hello world!";
        String content = "This is my first post";
        CreatePostDto postDto = new CreatePostDto(title, content);
        ResponseEntity<Void> res = testRestTemplate.withBasicAuth(mohamedEmail, mohamedPassword).postForEntity("/api/posts/create", postDto, Void.class);
        String location = Objects.requireNonNull(res.getHeaders().get("location")).get(0);
        String[] splits = location.split("/");
        String createdPostId = splits[splits.length - 1];
        ResponseEntity<PostDto> getResponse = testRestTemplate.withBasicAuth(mohamedEmail, mohamedPassword).getForEntity("/api/posts/" + createdPostId, PostDto.class);
        PostDto createdPost = getResponse.getBody();
        String newTitle = "Edited The Post";
        String newContent = "Just edited my own post!";
        EditPostDto editPostDto = new EditPostDto(createdPost.getId(), newTitle, newContent, createdPost.getAuthorID());
        ResponseEntity<PostDto> editedPostResponse = testRestTemplate.withBasicAuth(mohamedEmail, mohamedPassword).exchange("/api/posts/edit", HttpMethod.PUT, new HttpEntity<>(editPostDto), PostDto.class);
        assertThat(editedPostResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        PostDto retrievedEditedPost = testRestTemplate.getForObject("/api/posts/{id}", PostDto.class, editedPostResponse.getBody().getId());
        assertThat(retrievedEditedPost.getTitle()).isEqualTo(newTitle);
        assertThat(retrievedEditedPost.getContent()).isEqualTo(newContent);
    }

    @Test
    @DirtiesContext
    void postIsDeleted() {
        String mohamedEmail = "mohamed@m.com";
        String mohamedPassword = "abc123";
        userRepository.save(new User(null, "mohamed", passwordEncoder.encode(mohamedPassword), mohamedEmail));
        String title = "Hello world!";
        String content = "This is my first post";
        CreatePostDto postDto = new CreatePostDto(title, content);
        ResponseEntity<Void> res = testRestTemplate.withBasicAuth(mohamedEmail, mohamedPassword).postForEntity("/api/posts/create", postDto, Void.class);
        String location = Objects.requireNonNull(res.getHeaders().get("location")).get(0);
        String[] splits = location.split("/");
        String createdPostId = splits[splits.length - 1];
        ResponseEntity<Void> deleteResponse = testRestTemplate.withBasicAuth(mohamedEmail, mohamedPassword).exchange("/api/posts/{id}", HttpMethod.DELETE, null, Void.class, createdPostId);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        ResponseEntity<PostDto> retrieveDeletedPostResponse = testRestTemplate.getForEntity("/api/posts/{id}", PostDto.class, createdPostId);
        assertThat(retrieveDeletedPostResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DirtiesContext
    void onlyAuthorCanDelete() {
        String mohamedEmail = "mohamed@m.com";
        String mohamedPassword = "abc123";
        String ahmedEmail = "ahmed@m.com";
        String ahmedPassword = "abc1234";
        userRepository.save(new User(null, "mohamed", passwordEncoder.encode(mohamedPassword), mohamedEmail));
        userRepository.save(new User(null, "ahmed", passwordEncoder.encode(ahmedPassword), ahmedEmail));
        String title = "Hello world!";
        String content = "This is my first post";
        CreatePostDto postDto = new CreatePostDto(title, content);
        ResponseEntity<Void> res = testRestTemplate.withBasicAuth(mohamedEmail, mohamedPassword).postForEntity("/api/posts/create", postDto, Void.class);
        String location = Objects.requireNonNull(res.getHeaders().get("location")).get(0);
        String[] splits = location.split("/");
        String createdPostId = splits[splits.length - 1];
        ResponseEntity<Void> deleteResponse = testRestTemplate.withBasicAuth(ahmedEmail, ahmedPassword).exchange("/api/posts/{id}", HttpMethod.DELETE, null, Void.class, createdPostId);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        deleteResponse = testRestTemplate.withBasicAuth(mohamedEmail, mohamedPassword).exchange("/api/posts/{id}", HttpMethod.DELETE, null, Void.class, createdPostId);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
