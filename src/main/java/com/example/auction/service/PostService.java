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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
@Transactional(readOnly = false)
public class PostService {
    private String PATH = "src/main/resources/images";
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
            String newPhotoUrl = authUser.getUsername()+newPhotoName;
            newPost.setPostPhoto(newPhotoUrl);
            try {
                fileUploadUtil.saveNewFile(PATH, newPhotoName, postPhoto);
            } catch (IOException e) {
                log.error("PATH NOT FOUND");
                throw new RuntimeException();
            }
        }
        return postRepository.save(newPost);
    }

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public void deletePostById(Long postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) {
            log.error("Doesnt exists post with id : " + postId);
            throw new ApiRequestException("Doesnt exists post with id : " + postId);
        } else {
            if (post.get().getAuthor().equals(userService.getAuthenticatedUser())) {
                postRepository.deleteById(postId);
                log.error("Deleted post By ID :" + postId);
            } else {
                throw new ApiRequestException("You don't have opportunity to delete this post by id :" + postId);
            }
        }
    }

    public Post getPostById(Long postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) {
            throw new ApiRequestException("post is empty");
        }
        return post.get();
    }

    public Transaction processOfPurchasing(Long postId, Double price) {
        User purchasingUser = userService.getAuthenticatedUser();
        Post post = postRepository.findById(postId).get();
        if (!post.getActive()) {
            log.info("Post with id :" + post.getId() + " is in archive");
            throw new ApiRequestException("Post with id :" + post.getId() + " is in archive");
        }
        if (purchasingUser.equals(post.getAuthor())) {
            throw new ApiRequestException("You can not buy own good");
        }
        if (!post.getTransactions().isEmpty()) {
            Transaction maxTransaction = post.getTransactions().stream().max(Comparator.comparing(Transaction::getPrice)).orElseThrow(NoSuchElementException::new);
            if (price < maxTransaction.getPrice()) {
                throw new ApiRequestException("Your price is not enough to purchase,please offer more than :" + maxTransaction.getPrice());
            } else {
                log.info("Message to :" + maxTransaction.getCustomer().getUsername() + " ,message : " + purchasingUser.getUsername() + " is beating your process by offering amount : " + price);
            }
        }
        Transaction transactionToAdd = new Transaction();
        transactionToAdd.setCustomer(purchasingUser);
        transactionToAdd.setPrice(price);
        transactionToAdd.setTime(new Date());
        transactionToAdd.setSellerId(post.getAuthor().getId());
        transactionToAdd.setPost(post);
        log.trace("Counting 2 minutes");
        log.trace("Adding new transaction :" + transactionToAdd.toString());
        return transactionRepository.save(transactionToAdd);
    }

    @Scheduled(fixedDelay = 30000)
    public void someJob() throws InterruptedException {
        List<Post> posts = postRepository.findAllByActiveIsTrue();
        for (Post post : posts) {
            List<Transaction> transactions = post.getTransactions();
            if (!transactions.isEmpty()) {
                Transaction maxTransaction = post.getTransactions().stream().max(Comparator.comparing(Transaction::getPrice)).orElseThrow(NoSuchElementException::new);
                System.out.println(maxTransaction);
                if (new Date().getMinutes() - maxTransaction.getTime().getMinutes() >= 2) {
                    log.trace("Timer for 2 minutes has finished " + maxTransaction.getCustomer().getUsername() + " bought " + post.getName());
                    post.setPrice(maxTransaction.getPrice());
                    post.setActive(false);
                    postRepository.flush();
                    postRepository.saveAndFlush(post);
                    confirmingPurchase(post, maxTransaction.getCustomer());
                }
            }
        }
        Thread.sleep(1000);
    }

    @Transactional
    public void confirmingPurchase(Post post, User user) {
        post.getAuthor().getPostsList().remove(post);
        user.getPostsList().add(post);
        post.setAuthor(user);
        post.setActive(false);
        postRepository.findById(post.getId()).get().setActive(false);
        log.trace(user.getUsername() + " bought Product : " + post.getName());
    }
}