package com.jangsacartel.biz.board.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jangsacartel.biz.board.dto.CommentDTO;
import com.jangsacartel.biz.board.service.CommentService;
import com.jangsacartel.biz.global.jwt.util.JwtUtil;

import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api")
@Api(tags = "댓글 컨트롤러")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private JwtUtil jwtUtil;

    @ApiOperation(value = "게시글 댓글 목록 조회", notes = "특정 게시글에 달린 댓글 목록을 조회합니다.")
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentDTO>> getCommentsByPostId(@PathVariable("postId") int postId, HttpServletRequest request) {
        Integer userId = null;
        try {
            String token = request.getHeader("Authorization");
            if (token != null) {
                Claims claims = jwtUtil.validateToken(token);
                Number userIdNumber = claims.get("userId", Number.class);
                if (userIdNumber != null) {
                    userId = userIdNumber.intValue();
                }
            }
        } catch (Exception e) {
            // 토큰이 유효하지 않은 경우 등의 예외 처리, userId는 null으로 유지
        }
        
        List<CommentDTO> comments = commentService.getCommentsByPostId(postId, userId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @ApiOperation(value = "댓글 작성", notes = "새로운 댓글을 작성합니다.")
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentDTO> createComment(@PathVariable("postId") int postId, @RequestBody CommentDTO comment, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token == null) {
                return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
            }
            Claims claims = jwtUtil.validateToken(token);
            Number userIdNumber = claims.get("userId", Number.class);
            if (userIdNumber == null) {
                return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
            }
            int userId = userIdNumber.intValue();
            
            comment.setPostId(postId);
            comment.setUserId(userId);
            
            commentService.createComment(comment);
            
            return new ResponseEntity<>(comment, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "댓글 수정", notes = "기존 댓글을 수정합니다.")
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentDTO> updateComment(@PathVariable("commentId") int commentId, @RequestBody CommentDTO comment, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token == null) {
                return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
            }
            Claims claims = jwtUtil.validateToken(token);
            Number userIdNumber = claims.get("userId", Number.class);
            if (userIdNumber == null) {
                return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
            }
            int userId = userIdNumber.intValue();
            
            CommentDTO existingComment = commentService.getCommentById(commentId);
            if (existingComment == null) {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }

            if (existingComment.getUserId() != userId) {
                return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
            }

            comment.setCommentId(commentId);
            commentService.updateComment(comment);
            
            return new ResponseEntity<>(comment, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "댓글 삭제", notes = "댓글을 삭제합니다.")
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable("commentId") int commentId, HttpServletRequest request) {
        try {
			String token = request.getHeader("Authorization");
            if (token == null) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            Claims claims = jwtUtil.validateToken(token);
            Number userIdNumber = claims.get("userId", Number.class);
            if (userIdNumber == null) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            int userId = userIdNumber.intValue();

            CommentDTO existingComment = commentService.getCommentById(commentId);
            if (existingComment == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            if (existingComment.getUserId() != userId) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            commentService.deleteComment(commentId);
            
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "댓글 좋아요", notes = "댓글에 좋아요를 누릅니다.")
    @PostMapping("/comments/{commentId}/like")
    public ResponseEntity<?> likeComment(@PathVariable("commentId") int commentId, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token == null) {
                return new ResponseEntity<>("Token is missing", HttpStatus.UNAUTHORIZED);
            }
            Claims claims = jwtUtil.validateToken(token);
            Number userIdNumber = claims.get("userId", Number.class);
            if (userIdNumber == null) {
                return new ResponseEntity<>("User ID not found in token", HttpStatus.UNAUTHORIZED);
            }
            int userId = userIdNumber.intValue();

            commentService.likeComment(commentId, userId);
            return new ResponseEntity<>("Like successful", HttpStatus.OK);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("An internal error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
