package com.jangsacartel.biz.board.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import com.jangsacartel.biz.board.dto.BoardDTO;
import com.jangsacartel.biz.board.dto.CategoryDTO;
import com.jangsacartel.biz.board.service.BoardService;


@RestController
@RequestMapping("/")
public class BoardController {

	@Autowired
	private BoardService boardService;

	@GetMapping
	public ResponseEntity<Map<String, Object>> getMainPage() {
		Map<String, Object> response = new HashMap<>();

		// Hot 게시판 (카테고리 ID 1, 최근 3일간 좋아요 가장 많은 글 3개)
		List<BoardDTO> hotPosts = boardService.findHotPosts(3);
		response.put("hot", hotPosts);

		// 나머지 게시판 (최신 글 3개씩)
		response.put("free", boardService.findRecentPosts(2, 3));
		response.put("info", boardService.findRecentPosts(3, 3));
		response.put("local", boardService.findRecentPosts(4, 3));

		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/hot")
	public ResponseEntity<Map<String, Object>> getHotBoardPage(@RequestParam(defaultValue = "1") int page) {
		List<BoardDTO> hotBoardPosts = boardService.findHotBoardPosts(page, 5);
		int totalCount = boardService.getHotBoardPostsCount();

		Map<String, Object> response = new HashMap<>();
		response.put("posts", hotBoardPosts);
		response.put("totalCount", totalCount);
		
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("free")
	public ResponseEntity<Map<String, Object>> getFreeBoardPage(@RequestParam(defaultValue = "1") int page) {
		List<BoardDTO> freeBoardPosts = boardService.findPostsByCategory(2, page, 5);
		int totalCount = boardService.countPostsByCategory(2);
		
		Map<String, Object> response = new HashMap<>();
		response.put("posts", freeBoardPosts);
		response.put("totalCount", totalCount);
		
		return ResponseEntity.ok(response);
	}

	@GetMapping("info")
	public ResponseEntity<Map<String, Object>> getInfoBoardPage(@RequestParam(defaultValue = "1") int page) {
		List<BoardDTO> infoBoardPosts = boardService.findPostsByCategory(3, page, 5);
		int totalCount = boardService.countPostsByCategory(3);

		Map<String, Object> response = new HashMap<>();
		response.put("posts", infoBoardPosts);
		response.put("totalCount", totalCount);
		
		return ResponseEntity.ok(response);
	}

	@GetMapping("local")
	public ResponseEntity<Map<String, Object>> getLocalBoardPage(@RequestParam(defaultValue = "1") int page) {
		List<BoardDTO> localBoardPosts = boardService.findPostsByCategory(4, page, 5);
		int totalCount = boardService.countPostsByCategory(4);
		
		Map<String, Object> response = new HashMap<>();
		response.put("posts", localBoardPosts);
		response.put("totalCount", totalCount);

		return ResponseEntity.ok(response);
	}
}