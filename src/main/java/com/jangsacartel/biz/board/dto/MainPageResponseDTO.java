package com.jangsacartel.biz.board.dto;

import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@ApiModel(description = "메인 페이지 응답 DTO")
public class MainPageResponseDTO {

    @ApiModelProperty(value = "Hot 게시판 최신글")
    private List<BoardDTO> hot;

    @ApiModelProperty(value = "자유 게시판 최신글")
    private List<BoardDTO> free;

    @ApiModelProperty(value = "정보 게시판 최신글")
    private List<BoardDTO> info;

    @ApiModelProperty(value = "지역 게시판 최신글")
    private List<BoardDTO> local;
}
