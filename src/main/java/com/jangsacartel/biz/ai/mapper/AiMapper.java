package com.jangsacartel.biz.ai.mapper;

import java.sql.Timestamp;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.jangsacartel.biz.ai.dto.PostLiteDTO;

public interface AiMapper {
	
    List<PostLiteDTO> selectPostsByDateRange(
        @Param("start") Timestamp start,
        @Param("end") Timestamp end,
        @Param("limit") int limit
    );
}