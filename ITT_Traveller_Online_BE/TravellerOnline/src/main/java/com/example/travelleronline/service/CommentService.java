package com.example.travelleronline.service;

import com.example.travelleronline.controllers.UserController;
import com.example.travelleronline.model.DTOs.comment.ContentDTO;
import com.example.travelleronline.model.entities.Comment;
import com.example.travelleronline.model.exceptions.BadSaveToDBException;
import com.example.travelleronline.model.repositories.CommentRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService extends AbstractService{

    @Autowired
    private CommentRepository commentRepository;

    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    public Comment findById(Integer id) {

        Optional<Comment> commentOptional = commentRepository.findById(id);
        //todo: MAYBE WHEN GETTING A SPECIFIC COMMENT GET ALL SUB COMMENTS
        return commentOptional.orElseThrow(() -> new RuntimeException("Comment not found with id: " + id));
    }

    public Comment saveByPost(ContentDTO contentData, int postId,HttpSession session) {
        isLogged(session);
        int userId=UserController.getId(session);
        try {
            Comment comment = Comment.builder()
                    .userId(userId)
                    .content(contentData.getContent())
                    .postId(postId)
                    .dateAdded(LocalDateTime.now())
                    .rating(0)
                    .superCommentId(null)
                    .build();
            System.out.println(comment);
            return commentRepository.save(comment);
        }catch (RuntimeException e){
            throw new BadSaveToDBException("Cannot respond to that post, because it doesn't exist");
        }
    }
    public Comment saveByComment(ContentDTO contentData, int commentedCommentId,HttpSession session) {
        isLogged(session);
        int userId=UserController.getId(session);
        try {
            Comment comment = Comment.builder()
                    .userId(userId)
                    .content(contentData.getContent())
                    .superCommentId(commentedCommentId)
                    .dateAdded(LocalDateTime.now())
                    .rating(0)
                    .postId(null)
                    .build();
            System.out.println(comment);
            return commentRepository.save(comment);
        }catch (RuntimeException e){
            throw new BadSaveToDBException("Cannot respond to that comment, because it doesn't exist");
        }
    }

    public Comment update(Integer id, Comment comment) {
        Comment existingComment = findById(id);
        existingComment.setContent(comment.getContent());
        existingComment.setPostId(comment.getPostId());
        existingComment.setSuperCommentId(comment.getSuperCommentId());
        existingComment.setRating(comment.getRating());
        return commentRepository.save(existingComment);
    }

    public Comment deleteById(int id, HttpSession s) {
        Comment toBeDeleted=findById(id);
        checkOwner(s,toBeDeleted.getUserId());
        isLogged(s);

        deleteSubComments(toBeDeleted.getId());
        commentRepository.delete(toBeDeleted);
        return toBeDeleted;
    }

    public List<Comment> getAllSubComments(int commendId){
        return commentRepository.findBySuperCommentId(commendId);
    }
    private void deleteSubComments(int superCommentId) {
        List<Comment> subComments = commentRepository.findBySuperCommentId(superCommentId);

        if (!subComments.isEmpty()) {
          /* subComments.stream().forEach(
                comment -> {
                deleteSubComments(comment.getId());
                commentRepository.deleteById(comment.getId());
                });
           */
            for (Comment subComment : subComments) {
                deleteSubComments(subComment.getId());
                commentRepository.deleteById(subComment.getId());
            }
        }
    }

    public List<Comment> getAllPostComments(int postId) {
        return commentRepository.findAllByPostId(postId);
    }

    public List<Comment> getAllCommentOfUser(HttpSession session){
        isLogged(session);
        return commentRepository.findAllByUserId(getUserId(session));
    }
}
