package com.jangsacartel.biz.board.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jangsacartel.biz.board.dto.BoardDTO;
import com.jangsacartel.biz.board.dto.CommentDTO;
import com.jangsacartel.biz.board.dto.LikeCommentDTO;
import com.jangsacartel.biz.board.mapper.BoardMapper;
import com.jangsacartel.biz.notification.domain.NotificationType;
import com.jangsacartel.biz.notification.service.NotificationFacade;

import java.util.UUID;

import com.jangsacartel.biz.notification.domain.NotificationEvent;


@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private BoardMapper boardMapper;
    
    @Autowired
    private NotificationFacade notificationFacade;

    @Override
    public List<CommentDTO> getCommentsByPostId(int postId, Integer userId) {
        List<CommentDTO> comments = boardMapper.findCommentsByPostId(postId);
        if (userId != null) {
            for (CommentDTO comment : comments) {
                int likeCount = boardMapper.findCommentLikeByUser(comment.getCommentId(), userId);
                comment.setLiked(likeCount > 0);
            }
        }
        return comments;
    }
    
    @Override
    public CommentDTO getCommentById(int commentId) {
        return boardMapper.findCommentById(commentId);
    }

    @Override
    public void createComment(CommentDTO comment) {
        boardMapper.insertComment(comment);
        
        dispatchCommentNotifications(comment);
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
    
    private void dispatchCommentNotifications(CommentDTO comment) {
        if (comment == null) return;

        final int actorUserId = comment.getUserId();
        final int postId = comment.getPostId();
        final Integer parentCommentId = comment.getParentCommentId();

        // 댓글 ID는 반드시 있어야 알림 이벤트도 안정적
        final Integer newCommentId = comment.getCommentId();

        // (A) 게시글 주인 조회
        BoardDTO post = boardMapper.findPostById(postId, null);
        if (post == null) return;
        final int postOwnerId = post.getUserId();
        
        // 게시글 제목 / 댓글 내용 준비
        String rawTitle = post.getTitle();
        if (rawTitle == null || rawTitle.trim().isEmpty()) rawTitle = "제목없음";
        final String postTitle = ellipsis(rawTitle, 20);

        // (B) parent_comment 주인 조회(대댓글인 경우만)
        Integer parentOwnerId = null;
        String parentCommentPreview = null;
        
        if (parentCommentId != null) {
            CommentDTO parent = boardMapper.findCommentById(parentCommentId);
            if (parent != null) {
                parentOwnerId = parent.getUserId();

                String rawParentContent = parent.getContent();
                if (rawParentContent == null || rawParentContent.trim().isEmpty()) rawParentContent = "내용없음";
                parentCommentPreview = ellipsis(rawParentContent, 20);
            }
        }

        // (C) 수신자 결정 (중복 제거)
        Set<Integer> receivers = new HashSet<>();

        // 1) post owner (작성자==post owner면 제외)
        if (actorUserId != postOwnerId) receivers.add(postOwnerId);

        // 2) 대댓글이면 parent owner도 (작성자==parent owner면 제외)
        if (parentOwnerId != null && actorUserId != parentOwnerId) receivers.add(parentOwnerId);

        if (receivers.isEmpty()) return;

        // (D) 이벤트 만들고 notify
        NotificationType type = (parentCommentId == null)
            ? NotificationType.COMMENT_CREATED
            : NotificationType.REPLY_CREATED;

        for (Integer receiverId : receivers) {
            NotificationEvent event = new NotificationEvent();
            event.setEventId("cmt-" + newCommentId);
            event.setType(type);

            event.setActorUserId(actorUserId);
            event.setReceiverUserId(receiverId);

            event.setPostId(postId);
            event.setCommentId(newCommentId);
            
            String msg;
            if (receiverId == postOwnerId) {
                // 게시글 작성자에게: [글 제목]
                String actionText = (parentCommentId == null) ? "새 댓글" : "새 대댓글";
                event.setTitle(parentCommentId == null ? "댓글 알림" : "대댓글 알림");
                msg = "[" + postTitle + "]에 " + actionText + "이 달렸습니다.";
            } else if (parentOwnerId != null && receiverId.equals(parentOwnerId)) {
                // 대댓글 대상 댓글 작성자에게: [댓글]
                event.setTitle("대댓글 알림");
                msg = "[" + (parentCommentPreview != null ? parentCommentPreview : "내용없음") + "]에 새 대댓글이 달렸습니다.";
            } else {
                // 예외 케이스 방어
                event.setTitle("알림");
                msg = "[" + postTitle + "]에 새 알림이 있습니다.";
            }

            event.setMessage(msg);

            event.setCreatedAt(LocalDateTime.now().toString());

            notificationFacade.notify(event);
        }
    }
    
    private static String ellipsis(String s, int max) {
    	if(s==null)return "";
    	String t = s.trim();
    	if (t.length() <= max) return t;
        return t.substring(0, max) + "…";
    }

}
