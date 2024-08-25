package com.miniproj.model;

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
public class BoardUpFilesVODTO {
	private int boardUpFileNo;
	private String newFileName;
	private String originalFileName;
	private String thumbFileName;
	private String ext;
	private long size;
	private int boardNo;
	private String base64Img; //대용량을 빠르게 처리하기 위해서 지금 큰 이미지의 경우 오류가 나거나 파일 명이 안뜸
	private BoardUpFileStatus fileStatus; // 게시물 수정시 첨부 파일의 상태를 기록하는 변수(ex : insert ->  새로 저장된 파일, delete->삭제될 파일)
    // 이넘 타입도 널 가능하다.
}
