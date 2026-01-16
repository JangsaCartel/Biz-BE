package com.jangsacartel.biz.board.service;

import java.util.List;

import com.jangsacartel.biz.board.dto.CommentDTO;

public interface CommentService {

    	List<CommentDTO> getCommentsByPostId(int postId, Integer userId);    
    CommentDTO getCommentById(int commentId);

    void createComment(CommentDTO comment);

    void updateComment(CommentDTO comment);

    void deleteComment(int commentId);

    void likeComment(int commentId, int userId);
}
