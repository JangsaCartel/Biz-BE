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

    // 게시글 ID로 댓글 일괄 삭제 (게시글 삭제 시 사용)
    void deleteCommentsByPostId(int postId);

    // 본인 확인 후 댓글 수정
    void updateComment(int commentId, int userId, String content);

    // 본인 확인 후 댓글 삭제
    void deleteComment(int commentId, int userId);
}
