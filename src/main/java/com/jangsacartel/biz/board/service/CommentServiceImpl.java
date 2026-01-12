package com.jangsacartel.biz.board.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jangsacartel.biz.board.dto.CommentDTO;
import com.jangsacartel.biz.board.dto.LikeCommentDTO;
import com.jangsacartel.biz.board.mapper.BoardMapper;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private BoardMapper boardMapper;

    @Override
    public List<CommentDTO> getCommentsByPostId(int postId) {
        return boardMapper.findCommentsByPostId(postId);
    }
    
    @Override
    public CommentDTO getCommentById(int commentId) {
        return boardMapper.findCommentById(commentId);
    }

    @Override
    public void createComment(CommentDTO comment) {
        boardMapper.insertComment(comment);
    }

    @Override
    public void updateComment(CommentDTO comment) {
        boardMapper.updateComment(comment);
    }

    @Override
    public void deleteComment(int commentId) {
        boardMapper.deleteComment(commentId);
    }

    @Override
    public void likeComment(int commentId, int userId) {
        CommentDTO comment = boardMapper.findCommentById(commentId);
        if (comment == null) {
            throw new RuntimeException("Comment not found");
        }
        if (comment.getUserId() == userId) {
            throw new RuntimeException("You cannot like your own comment.");
        }

        int likeCount = boardMapper.findCommentLikeByUser(commentId, userId);
        if (likeCount > 0) {
            throw new RuntimeException("You have already liked this comment.");
        }

        LikeCommentDTO like = new LikeCommentDTO();
        like.setCommentId(commentId);
        like.setUserId(userId);
        like.setPostId(comment.getPostId());
        
        boardMapper.insertCommentLike(like);
    }
}
