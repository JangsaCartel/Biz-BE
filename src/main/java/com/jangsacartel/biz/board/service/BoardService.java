package com.jangsacartel.biz.board.service;

import java.util.List;

import com.jangsacartel.biz.board.dto.BoardDTO;
import com.jangsacartel.biz.board.dto.CategoryDTO;
import com.jangsacartel.biz.board.dto.CommentDTO;
import com.jangsacartel.biz.board.dto.FileDTO;
import com.jangsacartel.biz.board.dto.LikeCommentDTO;
import com.jangsacartel.biz.board.dto.LikePostDTO;

public interface BoardService {
    // 게시글(Post) 관련 서비스 메서드
    void insertPost(BoardDTO board);
    BoardDTO findPostById(int postId);
    List<BoardDTO> findAllPosts();
    void updatePost(BoardDTO board);
    void deletePost(int postId);

    // 카테고리(Category) 관련 서비스 메서드
    List<CategoryDTO> findAllCategories();

    // 댓글(Comment) 관련 서비스 메서드
    void insertComment(CommentDTO comment);
    List<CommentDTO> findCommentsByPostId(int postId);
    void updateComment(CommentDTO comment);
    void deleteComment(int commentId);

    // 파일(File) 관련 서비스 메서드
    void insertFile(FileDTO file);
    List<FileDTO> findFilesByPostId(int postId);
    void deleteFile(int fileId);

    // 게시글 좋아요(Post Like) 관련 서비스 메서드
    void insertPostLike(LikePostDTO likePost);
    void deletePostLike(LikePostDTO likePost);
    int countPostLikes(int postId);

    // 댓글 좋아요(Comment Like) 관련 서비스 메서드
    void insertCommentLike(LikeCommentDTO likeComment);
    void deleteCommentLike(LikeCommentDTO likeComment);
    int countCommentLikes(int commentId);
    
    List<BoardDTO> findHotPosts(int limit);
    
    List<BoardDTO> findRecentPosts(int categoryId, int limit);
    
    List<BoardDTO> findPostsByCategory(int categoryId, int page, int pageSize);
    
    int countPostsByCategory(int categoryId);
    
    List<BoardDTO> findHotBoardPosts(int page, int pageSize);
    
    int getHotBoardPostsCount();
}
