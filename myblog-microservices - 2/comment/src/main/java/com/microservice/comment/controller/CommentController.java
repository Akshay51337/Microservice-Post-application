package com.microservice.comment.controller;

import com.microservice.comment.entity.Comment;
import com.microservice.comment.service.CommentService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;

    //http://localhost:8082/api/comments
    @CircuitBreaker(name = "postBreaker", fallbackMethod = "postFallback")
    @PostMapping
    public ResponseEntity<Comment> saveComment(@RequestBody Comment comment){
        Comment c = commentService.saveComment(comment);
        return new ResponseEntity<>(c, HttpStatus.OK);
    }
    public ResponseEntity<Comment> postFallback(@RequestBody Comment comment, Exception ex) {
        System.out.println("Fallback is executed because service is down : "+ ex.getMessage());

        ex.printStackTrace();

        Comment dto = new Comment();
        dto.setCommentId("1234");
        dto.setBody("Service Down");
        dto.setName("Service Down");
        dto.setEmail("Service Down");

        return new ResponseEntity<>(dto, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("{postId}")
    public List<Comment> getAllCommentsByPostId(@PathVariable String postId){
        List<Comment> comments = commentService.getAllCommentsByPostId(postId);
        return comments;
    }
}
