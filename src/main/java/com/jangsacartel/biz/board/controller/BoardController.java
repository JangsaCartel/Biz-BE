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
	
	@GetMapping("free")
	public ResponseEntity<List<BoardDTO>> getFreeBoardPage(@RequestParam(defaultValue = "1") int page) {
		List<BoardDTO> freeBoardPosts = boardService.findPostsByCategory(2, page, 5);
		return ResponseEntity.ok(freeBoardPosts);
	}

	@GetMapping("info")
	public ResponseEntity<List<BoardDTO>> getInfoBoardPage(@RequestParam(defaultValue = "1") int page) {
		List<BoardDTO> infoBoardPosts = boardService.findPostsByCategory(3, page, 5);
		return ResponseEntity.ok(infoBoardPosts);
	}

	@GetMapping("local")
	public ResponseEntity<List<BoardDTO>> getLocalBoardPage(@RequestParam(defaultValue = "1") int page) {
		List<BoardDTO> localBoardPosts = boardService.findPostsByCategory(4, page, 5);
		return ResponseEntity.ok(localBoardPosts);
	}
}