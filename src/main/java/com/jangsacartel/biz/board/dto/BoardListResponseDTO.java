package com.jangsacartel.biz.board.dto;

import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@ApiModel(description = "게시판 목록 응답 DTO")
public class BoardListResponseDTO {

    @ApiModelProperty(value = "게시글 목록")
    private List<BoardDTO> posts;

    @ApiModelProperty(value = "전체 게시글 수")
    private int totalCount;
}
