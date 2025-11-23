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

		// Hot 게시판 (최근 3일간 좋아요가 가장 많은 글 3개)
		List<BoardDTO> hotPosts = boardService.findHotPosts(3);
		response.put("hot", hotPosts);

		// 나머지 게시판 (최신 글 3개씩)
		List<CategoryDTO> categories = boardService.findAllCategories();
		for (CategoryDTO category : categories) {
			if (!"hot".equalsIgnoreCase(category.getName())) { // 'hot' 카테고리는 제외
				List<BoardDTO> recentPosts = boardService.findRecentPosts(category.getName(), 3);
				response.put(category.getName().toLowerCase(), recentPosts);
			}
		}

		return ResponseEntity.ok(response);
	}
	
	@GetMapping("freeboard")
	public ResponseEntity<List<BoardDTO>> getFreeBoardPage(@RequestParam(defaultValue = "1") int page) {
		List<BoardDTO> freeBoardPosts = boardService.findPostsByCategory("자유게시판", page, 5);
		return ResponseEntity.ok(freeBoardPosts);
	}
}