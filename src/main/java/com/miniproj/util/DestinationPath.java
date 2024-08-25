package com.miniproj.util;

import javax.servlet.http.HttpServletRequest;


/**
 * @작성자 : 802-01
 * @작성일 : 2024. 8. 7.
 * @프로젝트명 : MiniProject
 * @패키지명 : com.miniproj.util
 * @파일명 : DestinationPath.java
 * @클래스명 : DestinationPath
 * @description : 
 * 로그인을 하지 않았을 때 로그인 페이지로 이동하기 전에, 원래 가려던 페이지 뎡로를 저장하는 객체
 */
public class DestinationPath {
	private String destPath; // 맴버 변수다 / 이걸 스태틱으로 하면 공유됨 즉 절대안됨 하나가지고 다같이 쓰니깐
	
	
	/**
	 * @작성자 : 802-01
	 * @작성일 : 2024. 8. 7.
	 * @메소드명 : setDestPath
	 * @param : HttpServletRequest req : request 객체
	 * @return : void
	 * @description : 
	 * request 객체에서 URI와 쿼리스트링을 얻어 목적지 경로(this.destPath) 변수에 할당
	 * 세션객체에 바인딩
	 */
	public void setDestPath(HttpServletRequest req) {
		// 글작성 : /hboard/saveBoard (쿼리스트링이 없다.)
		// 글수정(삭제) : /hboard/modifyBoard?boardNo=1234 (쿼리스트링이 있다.)
		String uri = req.getRequestURI();
		String queryString = req.getQueryString(); // "?"  가 빠진 상태로 반환됨 / 설명을 읽어보면! 없으면 null 을 반환함 이걸 고려해서 코딩 해야 한다.
		
//		if (StringUtils.isNullOrEmpty(queryString)) {
//			// 쿼리 스트링이 없다.
//			this.destPath = uri; 
//		} else {
//			// 쿼리 스트링이 있다.
//			this.destPath = uri + "?" + queryString;
//		}
		
		destPath = (queryString == null) ? uri : uri + "?" + queryString; //이런식으로 해야지 하수가 아님~
		req.getSession().setAttribute("destPath", this.destPath);
	}
	
	public void getDestPath() {
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
