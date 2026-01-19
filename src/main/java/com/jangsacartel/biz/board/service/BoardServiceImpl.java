package com.jangsacartel.biz.board.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jangsacartel.biz.board.dto.BoardDTO;
import com.jangsacartel.biz.board.dto.CategoryDTO;
import com.jangsacartel.biz.board.dto.CommentDTO;
import com.jangsacartel.biz.board.dto.FileDTO;
import com.jangsacartel.biz.board.dto.LikeCommentDTO;
import com.jangsacartel.biz.board.dto.LikePostDTO;
import com.jangsacartel.biz.board.dto.PostUpdateRequestDTO;
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
    public BoardDTO findPostById(int postId, Integer userId) {
        return boardMapper.findPostById(postId, userId);
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

    @Override
    public void likePost(int postId, int userId) {
        // 이미 좋아요를 눌렀는지 확인
        if (boardMapper.findPostLikeByUser(postId, userId) > 0) {
            throw new RuntimeException("이미 좋아요를 누른 게시글입니다.");
        }
        // 좋아요 추가
        LikePostDTO likePostDTO = new LikePostDTO();
        likePostDTO.setPostId(postId);
        likePostDTO.setUserId(userId);
        boardMapper.insertPostLike(likePostDTO);
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
	public List<BoardDTO> findPostsByCategory(int categoryId, int page, int pageSize, String region) {
		int offset = (page - 1) * pageSize;
		return boardMapper.findPostsByCategory(categoryId, offset, pageSize, region);
	}

	@Override
	public int countPostsByCategory(int categoryId, String region) {
		return boardMapper.countPostsByCategory(categoryId, region);
	}

	@Override
	public List<BoardDTO> findHotBoardPosts(int page, int pageSize) {
		int offset = (page - 1) * pageSize;
		return boardMapper.selectHotBoardPosts(offset, pageSize);
	}

	@Override
	public int getHotBoardPostsCount() {
		int totalCount = boardMapper.countHotBoardPosts();
		return Math.min(totalCount, 100);
	}

	// 유저 페이지 - 게시글 수정
	@Override
	@Transactional
	public void updatePost(int postId, int userId, PostUpdateRequestDTO requestDTO) {
		// 본인 확인 및 수정 쿼리 실행
		int result = boardMapper.updatePostByUser(postId, userId, requestDTO);

		// 수정된 행이 0개라면 -> 글이 없거나, 작성자가 아님
		if (result == 0) {
			throw new IllegalArgumentException("게시글이 존재하지 않거나 수정 권한이 없습니다.");
		}
	}
}
