package com.jangsacartel.biz.board.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.jangsacartel.biz.board.dto.BoardDTO;
import com.jangsacartel.biz.board.dto.CategoryDTO;
import com.jangsacartel.biz.board.dto.CommentDTO;
import com.jangsacartel.biz.board.dto.FileDTO;
import com.jangsacartel.biz.board.dto.LikeCommentDTO;
import com.jangsacartel.biz.board.dto.LikePostDTO;
import com.jangsacartel.biz.board.dto.PostUpdateRequestDTO;

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
    
    List<BoardDTO> findPostsByCategory(@Param("categoryId") int categoryId, @Param("offset") int offset, @Param("limit") int limit, @Param("region") String region);
    
    int countPostsByCategory(@Param("categoryId") int categoryId, @Param("region") String region);
    
    List<BoardDTO> selectHotBoardPosts(@Param("offset") int offset, @Param("limit") int limit);
    
    List<BoardDTO> findHotPostsByRegion(@Param("region") String region, @Param("limit") int limit);
    
    int countHotBoardPosts();

    // 게시글 수정 (작성자 본인 확인 기능이 포함된 수정 메서드)
    // 리턴값 int: 수정 성공 시 1, 실패(본인 글 아님/글 없음) 시 0 반환
    int updatePostByUser(@Param("postId") int postId,
        @Param("userId") int userId,
        @Param("dto") PostUpdateRequestDTO dto);
    
    int deleteExpiredPosts();
    int deleteExpiredComments();
}
