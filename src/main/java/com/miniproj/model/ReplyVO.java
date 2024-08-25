package com.miniproj.model;

import java.sql.Timestamp;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class ReplyVO {
	private int replyNo;
	private String replyer;
	private String content;
	private Timestamp regDate;
	private int boardNo;
	private String userImg;
	private String userName;
	private String email;
	
	
}
