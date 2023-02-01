package com.example.auction.service;

import com.example.auction.dto.PostDto;
import com.example.auction.exception.ApiRequestException;
import com.example.auction.model.Post;
import com.example.auction.model.Transaction;
import com.example.auction.model.User;
import com.example.auction.repository.PostRepository;
import com.example.auction.repository.TransactionRepository;
import com.example.auction.util.FileNamingUtil;
import com.example.auction.util.FileUploadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional(readOnly = true)
public class PostService {
    private String PATH ="";
    //Введите место где будут сохраняться фотографии,Я выбрал ресурсы
    private final PostRepository postRepository;
    private final FileNamingUtil fileNamingUtil;
    private final UserService userService;
    private final FileUploadUtil fileUploadUtil;
    private final TransactionRepository transactionRepository;

    public PostService(PostRepository postRepository, FileNamingUtil fileNamingUtil, UserService userService, FileUploadUtil fileUploadUtil, TransactionRepository transactionRepository) {
        this.postRepository = postRepository;
        this.fileNamingUtil = fileNamingUtil;
        this.userService = userService;
        this.fileUploadUtil = fileUploadUtil;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public Post createNewPost(PostDto postDto, MultipartFile postPhoto) {
        User authUser = userService.getAuthenticatedUser();
        Post newPost = new Post();
        newPost.setAuthor(authUser);
        newPost.setDateCreated(new Date());
        newPost.setDescription(postDto.getDescription());
        newPost.setDateCreated(new Date());
        newPost.setName(postDto.getName());
        newPost.setActive(true);
        newPost.setPrice(postDto.getPrice());
        if (postPhoto != null && postPhoto.getSize() > 0) {
            String newPhotoName = fileNamingUtil.nameFile(postPhoto);
            String newPhotoUrl = authUser.getUsername() + newPost.getId() + newPhotoName;
            newPost.setPostPhoto(newPhotoUrl);
            try {
                fileUploadUtil.saveNewFile("SAS", newPhotoName, postPhoto);
            } catch (IOException e) {
                log.error("PATH NOT FOUND");
                throw new RuntimeException();
            }
        }
        return postRepository.save(newPost);
    }
    public List<Post> findAll(){
        return postRepository.findAll();
    }

    @Transactional
    public void deletePostById(Long postId){
        Optional<Post> post = postRepository.findById(postId);
        if(post.isEmpty()){
            throw new ApiRequestException("Doesnt exists post with id : "+postId);
        }else{
            if(post.get().getAuthor().equals(userService.getAuthenticatedUser())){
                postRepository.deleteById(postId);
            }else{
                throw new ApiRequestException("You don't have opportunity to delete this post by id :"+postId);
            }
        }
    }

    public Post getPostById(Long postId){
        Optional<Post> post = postRepository.findById(postId);
        if(post.isEmpty()){
            throw new ApiRequestException("post is empty");
        }
        return post.get();
    }
    @Transactional
    public void processOfPurchasing(Long postId,Double price){

        User purchasingUser = userService.getAuthenticatedUser();
        Post post = postRepository.findById(postId).get();
        if(purchasingUser.equals(post.getAuthor())){
            throw new ApiRequestException("You can not buy own good");
        }
        if(transactionRepository.findByPostId(postId).isEmpty()){
            Transaction transaction = new Transaction(post.getAuthor().getId(),purchasingUser.getId(),post.getId(),price,new Date());
            transactionRepository.save(transaction);
        }
        else{
            Transaction transaction = transactionRepository.findByPostId(postId).get();
            if(price<transaction.getPrice()){
                throw new ApiRequestException("Your price is not enough to purchase,please offer more than :"+transaction.getPrice());
            }

        }
    }
}
