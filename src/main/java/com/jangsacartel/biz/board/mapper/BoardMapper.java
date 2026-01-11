package com.jangsacartel.biz.board.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.jangsacartel.biz.board.dto.BoardDTO;
import com.jangsacartel.biz.board.dto.CategoryDTO;
import com.jangsacartel.biz.board.dto.CommentDTO;
import com.jangsacartel.biz.board.dto.FileDTO;
import com.jangsacartel.biz.board.dto.LikeCommentDTO;
import com.jangsacartel.biz.board.dto.LikePostDTO;

public interface BoardMapper {

    // Post
    void insertPost(BoardDTO board);
    BoardDTO findPostById(@Param("postId") int postId, @Param("userId") Integer userId);
    List<BoardDTO> findAllPosts();
    void updatePost(BoardDTO board);
    void deletePost(int postId);

    // Category
    List<CategoryDTO> findAllCategories();

    // Comment
    void insertComment(CommentDTO comment);
    List<CommentDTO> findCommentsByPostId(int postId);
    CommentDTO findCommentById(int commentId);
    void updateComment(CommentDTO comment);
    void deleteComment(int commentId);

    // File
    void insertFile(FileDTO file);
    List<FileDTO> findFilesByPostId(int postId);
    void deleteFile(int fileId);

    // Post Like
    void insertPostLike(LikePostDTO likePost);
    void deletePostLike(LikePostDTO likePost);
    int countPostLikes(int postId);
    int findPostLikeByUser(@Param("postId") int postId, @Param("userId") int userId);

    // Comment Like
    void insertCommentLike(LikeCommentDTO likeComment);
    void deleteCommentLike(LikeCommentDTO likeComment);
    int countCommentLikes(int commentId);
    int findCommentLikeByUser(@Param("commentId") int commentId, @Param("userId") int userId);
    
    List<BoardDTO> findHotPosts(int limit);
    
    List<BoardDTO> findRecentPosts(@Param("categoryId") int categoryId, @Param("limit") int limit);
    
    List<BoardDTO> findPostsByCategory(@Param("categoryId") int categoryId, @Param("offset") int offset, @Param("limit") int limit);
    
    int countPostsByCategory(@Param("categoryId") int categoryId);
    
    List<BoardDTO> selectHotBoardPosts(@Param("offset") int offset, @Param("limit") int limit);
    
    int countHotBoardPosts();
}
