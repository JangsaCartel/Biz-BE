package com.jangsacartel.biz.board.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import com.jangsacartel.biz.board.dto.BoardDTO;
import com.jangsacartel.biz.board.dto.CategoryDTO;
import com.jangsacartel.biz.board.service.BoardService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;


@RestController
@RequestMapping("/")
@Api(tags = "게시판 컨트롤러")
public class BoardController {

	@Autowired
	private BoardService boardService;

	@PostMapping("/posts")
	@ApiOperation(value="게시글 등록", notes="새로운 게시글을 등록합니다.")
    public ResponseEntity<BoardDTO> createPost(@RequestBody BoardDTO board) {
        try {
            boardService.insertPost(board);
            return new ResponseEntity<>(board, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

	@GetMapping
	@ApiOperation(value="메인 페이지", notes="메인 페이지에 필요한 데이터를 조회합니다.")
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
	@ApiOperation(value = "Hot 게시판", notes = "Hot 게시판의 게시글 목록을 조회합니다.")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "page", value = "페이지 번호", dataType = "int", paramType = "query", defaultValue = "1", example = "1")
	})
	public ResponseEntity<Map<String, Object>> getHotBoardPage(@RequestParam(defaultValue = "1") int page) {
		List<BoardDTO> hotBoardPosts = boardService.findHotBoardPosts(page, 5);
		int totalCount = boardService.getHotBoardPostsCount();

		Map<String, Object> response = new HashMap<>();
		response.put("posts", hotBoardPosts);
		response.put("totalCount", totalCount);
		
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("free")
	@ApiOperation(value = "자유 게시판", notes = "자유 게시판의 게시글 목록을 조회합니다.")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "page", value = "페이지 번호", dataType = "int", paramType = "query", defaultValue = "1", example = "1")
	})
	public ResponseEntity<Map<String, Object>> getFreeBoardPage(@RequestParam(defaultValue = "1") int page) {
		List<BoardDTO> freeBoardPosts = boardService.findPostsByCategory(2, page, 5);
		int totalCount = boardService.countPostsByCategory(2);
		
		Map<String, Object> response = new HashMap<>();
		response.put("posts", freeBoardPosts);
		response.put("totalCount", totalCount);
		
		return ResponseEntity.ok(response);
	}

	@GetMapping("info")
	@ApiOperation(value = "정보 게시판", notes = "정보 게시판의 게시글 목록을 조회합니다.")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "page", value = "페이지 번호", dataType = "int", paramType = "query", defaultValue = "1", example = "1")
	})
	public ResponseEntity<Map<String, Object>> getInfoBoardPage(@RequestParam(defaultValue = "1") int page) {
		List<BoardDTO> infoBoardPosts = boardService.findPostsByCategory(3, page, 5);
		int totalCount = boardService.countPostsByCategory(3);

		Map<String, Object> response = new HashMap<>();
		response.put("posts", infoBoardPosts);
		response.put("totalCount", totalCount);
		
		return ResponseEntity.ok(response);
	}

	@GetMapping("local")
	@ApiOperation(value = "지역 게시판", notes = "지역 게시판의 게시글 목록을 조회합니다.")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "page", value = "페이지 번호", dataType = "int", paramType = "query", defaultValue = "1", example = "1")
	})
	public ResponseEntity<Map<String, Object>> getLocalBoardPage(@RequestParam(defaultValue = "1") int page) {
		List<BoardDTO> localBoardPosts = boardService.findPostsByCategory(4, page, 5);
		int totalCount = boardService.countPostsByCategory(4);
		
		Map<String, Object> response = new HashMap<>();
		response.put("posts", localBoardPosts);
		response.put("totalCount", totalCount);

		return ResponseEntity.ok(response);
	}
}