package com.jangsacartel.biz.policy.entity;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BizinfoPolicyResponseVO {

    private List<BizinfoPolicyVO> jsonArray;
}