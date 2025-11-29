package com.jangsacartel.biz.board.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jangsacartel.biz.board.dto.BoardDTO;
import com.jangsacartel.biz.board.dto.CategoryDTO;
import com.jangsacartel.biz.board.dto.CommentDTO;
import com.jangsacartel.biz.board.dto.FileDTO;
import com.jangsacartel.biz.board.dto.LikeCommentDTO;
import com.jangsacartel.biz.board.dto.LikePostDTO;
import com.jangsacartel.biz.board.mapper.BoardMapper;

@Service
public class BoardServiceImpl implements BoardService {

    @Autowired
    private BoardMapper boardMapper;

    // 게시글(Post) 관련 서비스 구현
    @Override
    public void insertPost(BoardDTO board) {
        boardMapper.insertPost(board);
    }

    @Override
    public BoardDTO findPostById(int postId) {
        return boardMapper.findPostById(postId);
    }

    @Override
    public List<BoardDTO> findAllPosts() {
        return boardMapper.findAllPosts();
    }

    @Override
    public void updatePost(BoardDTO board) {
        boardMapper.updatePost(board);
    }

    @Override
    public void deletePost(int postId) {
        boardMapper.deletePost(postId);
    }

    // 카테고리(Category) 관련 서비스 구현
    @Override
    public List<CategoryDTO> findAllCategories() {
        return boardMapper.findAllCategories();
    }

    // 댓글(Comment) 관련 서비스 구현
    @Override
    public void insertComment(CommentDTO comment) {
        boardMapper.insertComment(comment);
    }

    @Override
    public List<CommentDTO> findCommentsByPostId(int postId) {
        return boardMapper.findCommentsByPostId(postId);
    }

    @Override
    public void updateComment(CommentDTO comment) {
        boardMapper.updateComment(comment);
    }

    @Override
    public void deleteComment(int commentId) {
        boardMapper.deleteComment(commentId);
    }

    // 파일(File) 관련 서비스 구현
    @Override
    public void insertFile(FileDTO file) {
        boardMapper.insertFile(file);
    }

    @Override
    public List<FileDTO> findFilesByPostId(int postId) {
        return boardMapper.findFilesByPostId(postId);
    }

    @Override
    public void deleteFile(int fileId) {
        boardMapper.deleteFile(fileId);
    }

    // 게시글 좋아요(Post Like) 관련 서비스 구현
    @Override
    public void insertPostLike(LikePostDTO likePost) {
        boardMapper.insertPostLike(likePost);
    }

    @Override
    public void deletePostLike(LikePostDTO likePost) {
        boardMapper.deletePostLike(likePost);
    }

    @Override
    public int countPostLikes(int postId) {
        return boardMapper.countPostLikes(postId);
    }

    // 댓글 좋아요(Comment Like) 관련 서비스 구현
    @Override
    public void insertCommentLike(LikeCommentDTO likeComment) {
        boardMapper.insertCommentLike(likeComment);
    }

    @Override
    public void deleteCommentLike(LikeCommentDTO likeComment) {
        boardMapper.deleteCommentLike(likeComment);
    }

    @Override
    public int countCommentLikes(int commentId) {
        return boardMapper.countCommentLikes(commentId);
    }
    
    @Override
	public List<BoardDTO> findHotPosts(int limit) {
		return boardMapper.findHotPosts(limit);
	}

	@Override
	public List<BoardDTO> findRecentPosts(int categoryId, int limit) {
		return boardMapper.findRecentPosts(categoryId, limit);
	}

	@Override
	public List<BoardDTO> findPostsByCategory(int categoryId, int page, int pageSize) {
		int offset = (page - 1) * pageSize;
		return boardMapper.findPostsByCategory(categoryId, offset, pageSize);
	}

	@Override
	public int countPostsByCategory(int categoryId) {
		return boardMapper.countPostsByCategory(categoryId);
	}
}
