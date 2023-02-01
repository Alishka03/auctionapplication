package com.example.auction.controller;

import com.example.auction.dto.PostDto;
import com.example.auction.exception.ApiRequestException;
import com.example.auction.model.Post;
import com.example.auction.service.PostService;
import com.example.auction.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@Slf4j
@Tag(name = "Controller for Posts")
@SecurityRequirement(name = "basicAuth")
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;
    private final UserService userService;

    public PostController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @RequestMapping(
            path = "/create",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(description = "Creating new post")
    public ResponseEntity<?> create(@RequestPart(value = "file", required = true) Optional<MultipartFile> file,
                                    @RequestParam(value = "name", required = true) Optional<String> name,
                                    @RequestParam(value = "description", required = true) Optional<String> description,
                                    @RequestParam(value = "price", required = true) Optional<String> price
    ) {
        if ((file.isEmpty() || file.get().getSize() <= 0)) {
            throw new ApiRequestException("You did not upload image of good");
        }
        if (name.isEmpty() || description.isEmpty() || price.isEmpty()) {
            throw new ApiRequestException("Please , fill all fields");
        }
        MultipartFile postImageToAdd = file.isEmpty() ? null : file.get();
        String nameToAdd = name.isEmpty() ? null : name.get();
        String descriptionToAdd = description.isEmpty() ? null : description.get();
        Double priceToAdd = price.isEmpty() ? null : Double.valueOf(price.get());
        System.out.println(priceToAdd);
        System.out.println(price.get());
        PostDto postDto = new PostDto(nameToAdd, descriptionToAdd, priceToAdd);
        Post createdPost = postService.createNewPost(postDto, postImageToAdd);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    @DeleteMapping("/{postId}")
    @Operation(description = "Delete post by ID")
    public ResponseEntity<?> deletePostById(@PathVariable Long postId) {
        postService.deletePostById(postId);
        return new ResponseEntity<>("Deleted", HttpStatus.OK);
    }

    @GetMapping("/{postId}")
    @Operation(description = "Get Post by ID")
    public ResponseEntity<?> getPost(@PathVariable Long postId){
        Post post = postService.getPostById(postId);
        return new ResponseEntity<>(post,HttpStatus.OK);
    }

    @PostMapping("/{postId}/purchase")
    @Operation(description = "Process of purchasing good")
    public ResponseEntity<?> purchase(@PathVariable Long postId ,
                                      @RequestParam(name = "price",required = true) Optional<String> price){
        Double purchasingPrice = price.isEmpty() ? null : Double.valueOf(price.get());
        postService.processOfPurchasing(postId,purchasingPrice);
        return new ResponseEntity<>("Created new transaction",HttpStatus.OK);
        //TODO
    }
}
