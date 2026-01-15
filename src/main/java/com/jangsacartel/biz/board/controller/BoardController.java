package com.jangsacartel.biz.board.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import com.jangsacartel.biz.board.dto.BoardDTO;
import com.jangsacartel.biz.board.dto.PostUpdateRequestDTO;
import com.jangsacartel.biz.board.dto.BoardListResponseDTO;
import com.jangsacartel.biz.board.dto.MainPageResponseDTO;
import com.jangsacartel.biz.board.service.BoardService;
import com.jangsacartel.biz.global.jwt.filter.CustomUserDetails;
import com.jangsacartel.biz.global.jwt.util.JwtUtil;
import com.jangsacartel.biz.user.service.UserService;

import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


@RestController
@RequestMapping("/api")
@Api(tags = "게시판 컨트롤러")
public class BoardController {

	private static final Logger logger = LoggerFactory.getLogger(BoardController.class);

	@Autowired
	private BoardService boardService;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private UserService userService;

	@GetMapping("/board/{postId}")
	@ApiOperation(value="게시글 상세 조회", notes="게시글 ID로 특정 게시글의 상세 정보를 조회합니다.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully retrieved post details", response = BoardDTO.class),
			@ApiResponse(code = 404, message = "Post not found")
	})
	public ResponseEntity<BoardDTO> getPostById(@PathVariable("postId") int postId, HttpServletRequest request) {
		Integer userId = null;
		try {
			String token = request.getHeader("Authorization");
			if (token != null && !token.isEmpty()) {
				Claims claims = jwtUtil.validateToken(token);
				Number userIdNumber = claims.get("userId", Number.class);
				if (userIdNumber != null) {
					userId = userIdNumber.intValue();
				}
			}
		} catch (Exception e) {
			logger.warn("Could not validate token for getPostById. Proceeding as unauthenticated. Error: {}", e.getMessage());
		}

		BoardDTO post = boardService.findPostById(postId, userId);
		if (post != null) {
			return new ResponseEntity<>(post, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@PostMapping("/board/{postId}/like")
	@ApiOperation(value="게시글 좋아요", notes="게시글에 좋아요를 누릅니다. 중복은 허용되지 않습니다.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "좋아요 처리가 완료되었습니다."),
			@ApiResponse(code = 401, message = "인증 정보가 유효하지 않습니다."),
			@ApiResponse(code = 409, message = "이미 좋아요를 누른 게시글입니다.")
	})
	public ResponseEntity<?> likePost(@PathVariable("postId") int postId, HttpServletRequest request) {
		try {
			String token = request.getHeader("Authorization");
            if (token == null) {
                return new ResponseEntity<>("Token is missing", HttpStatus.UNAUTHORIZED);
            }
            Claims claims = jwtUtil.validateToken(token);
			Number userIdNumber = claims.get("userId", Number.class);

			if (userIdNumber == null) {
				return new ResponseEntity<>("사용자 ID를 찾을 수 없습니다.", HttpStatus.UNAUTHORIZED);
			}
			
			int userId = userIdNumber.intValue();

			boardService.likePost(postId, userId);
			return new ResponseEntity<>("좋아요 처리가 완료되었습니다.", HttpStatus.OK);
		} catch (RuntimeException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
		}
	}
	
	@PostMapping("/posts")
	@ApiOperation(value="게시글 등록", notes="새로운 게시글을 등록합니다.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "게시글이 성공적으로 등록되었습니다.", response = BoardDTO.class),
        @ApiResponse(code = 401, message = "인증 정보가 유효하지 않습니다."),
        @ApiResponse(code = 500, message = "서버 오류로 게시글 등록에 실패했습니다.")
})
    public ResponseEntity<BoardDTO> createPost(@RequestBody BoardDTO board, HttpServletRequest request) {
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
			board.setUserId(userId);

            boardService.insertPost(board);
            return new ResponseEntity<>(board, HttpStatus.CREATED);
        } catch (Exception e) {
			logger.error("Error creating post", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

	@GetMapping
	@ApiOperation(value="메인 페이지", notes="메인 페이지에 필요한 데이터를 조회합니다.")
    @ApiResponse(code = 200, message = "Successfully retrieved main page data", response = MainPageResponseDTO.class)
	public ResponseEntity<MainPageResponseDTO> getMainPage() {
		List<BoardDTO> hotPosts = boardService.findHotPosts(3);
		List<BoardDTO> freePosts = boardService.findRecentPosts(2, 3);
		List<BoardDTO> infoPosts = boardService.findRecentPosts(3, 3);
		List<BoardDTO> localPosts = boardService.findRecentPosts(4, 3);

        MainPageResponseDTO response = new MainPageResponseDTO(hotPosts, freePosts, infoPosts, localPosts);

		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/hot")
	@ApiOperation(value = "Hot 게시판", notes = "Hot 게시판의 게시글 목록을 조회합니다.")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "page", value = "페이지 번호", dataType = "int", paramType = "query", defaultValue = "1", example = "1")
	})
    @ApiResponse(code = 200, message = "Successfully retrieved hot board posts", response = BoardListResponseDTO.class)
	public ResponseEntity<BoardListResponseDTO> getHotBoardPage(@RequestParam(defaultValue = "1") int page) {
		List<BoardDTO> hotBoardPosts = boardService.findHotBoardPosts(page, 4);
		int totalCount = boardService.getHotBoardPostsCount();

        BoardListResponseDTO response = new BoardListResponseDTO(hotBoardPosts, totalCount);
		
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/free")
	@ApiOperation(value = "자유 게시판", notes = "자유 게시판의 게시글 목록을 조회합니다.")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "page", value = "페이지 번호", dataType = "int", paramType = "query", defaultValue = "1", example = "1")
	})
    @ApiResponse(code = 200, message = "Successfully retrieved free board posts", response = BoardListResponseDTO.class)
	public ResponseEntity<BoardListResponseDTO> getFreeBoardPage(@RequestParam(defaultValue = "1") int page) {
		List<BoardDTO> freeBoardPosts = boardService.findPostsByCategory(2, page, 4);
		int totalCount = boardService.countPostsByCategory(2);
		
        BoardListResponseDTO response = new BoardListResponseDTO(freeBoardPosts, totalCount);
		
		return ResponseEntity.ok(response);
	}

	@GetMapping("/info")
	@ApiOperation(value = "정보 게시판", notes = "정보 게시판의 게시글 목록을 조회합니다.")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "page", value = "페이지 번호", dataType = "int", paramType = "query", defaultValue = "1", example = "1")
	})
    @ApiResponse(code = 200, message = "Successfully retrieved info board posts", response = BoardListResponseDTO.class)
	public ResponseEntity<BoardListResponseDTO> getInfoBoardPage(@RequestParam(defaultValue = "1") int page) {
		List<BoardDTO> infoBoardPosts = boardService.findPostsByCategory(3, page, 4);
		int totalCount = boardService.countPostsByCategory(3);

        BoardListResponseDTO response = new BoardListResponseDTO(infoBoardPosts, totalCount);
		
		return ResponseEntity.ok(response);
	}

	@GetMapping("/local")
	@ApiOperation(value = "지역 게시판", notes = "지역 게시판의 게시글 목록을 조회합니다.")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "page", value = "페이지 번호", dataType = "int", paramType = "query", defaultValue = "1", example = "1")
	})
    @ApiResponse(code = 200, message = "Successfully retrieved local board posts", response = BoardListResponseDTO.class)
	public ResponseEntity<BoardListResponseDTO> getLocalBoardPage(@RequestParam(defaultValue = "1") int page) {
		List<BoardDTO> localBoardPosts = boardService.findPostsByCategory(4, page, 4);
		int totalCount = boardService.countPostsByCategory(4);
		
		BoardListResponseDTO response = new BoardListResponseDTO(localBoardPosts, totalCount);

		return ResponseEntity.ok(response);
	}

	// 유저 페이지 - 게시글 수정
	@ApiOperation(value = "게시글 수정", notes = "작성자가 자신의 게시글을 수정합니다.")
	@PatchMapping("/board/{postId}")
	public ResponseEntity<String> updatePost(
		@PathVariable("postId") int postId,
		@RequestBody PostUpdateRequestDTO requestDTO,
		@ApiIgnore @AuthenticationPrincipal CustomUserDetails userDetails) {

		// 1. 로그인 상태 체크
		if (userDetails == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		try {
			// 2. UserService를 통해 PK(userId) 획득
			// UserService는 Long을 반환하므로 int로 변환 (BoardMapper가 int를 쓰기 때문)
			Long userIdLong = userService.getUserIdByProviderId(userDetails.getProviderId());
			int userId = userIdLong.intValue();

			// 3. 수정 서비스 호출
			boardService.updatePost(postId, userId, requestDTO);

			return ResponseEntity.ok("게시글이 수정되었습니다.");

		} catch (IllegalArgumentException e) {
			// 본인 글이 아니거나 글이 없는 경우 403 Forbidden 반환
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
		} catch (Exception e) {
			logger.error("게시글 수정 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
		}
	}
}
