package com.miniproj.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.miniproj.model.BoardDetailInfo;
import com.miniproj.model.MemberVO;
import com.miniproj.persistence.HBoardDAO;
import com.miniproj.service.hboard.HBoardService;
import com.miniproj.service.hboard.RBoardService;
import com.miniproj.util.DestinationPath;

import lombok.RequiredArgsConstructor;

// 로그인 인증이 필요한 페이지에서 클라이언트가 현재 로그인 되어 있는지 아닌지 검사한다.\
// 로그인 인증이 필요한 페이지 (글작성, 글수정, 글삭제, 댓글작성, 답(댓)글작성/수정/삭제,관리자 페이지)
// 로그인 되어 있지 않으면, 로그인을 하도록 하고, 로그인이 되어 있다면... 
// 계속 원래 클라이언트가 하려던 작업을 수행 하도록 한다.

// *)로그인이 되어 있지 않아서, 로그인 페이지로 끌려갔다면, 로그인을 성공한 뒤에는 원래 하려던 기능의 
//페이지로 돌아가게 해야한다.

// *) 글수정, 글삭제, 댓글 수정, 댓글 삭제는 로그인 되어 있어야 할 뿐 아니라 그 글(댓글)의 주인인지 확인 해야한다.

/**
 * @작성자 : 802-01
 * @작성일 : 2024. 8. 7.
 * @프로젝트명 : MiniProject
 * @패키지명 : com.miniproj.interceptor
 * @파일명 : AuthInterceptor.java
 * @클래스명 : AuthInterceptor
 * @description : 로그인 인증이 필요한 페이지에서 클라이언트가 현재 로그인 되어 있는지 아닌지 검사한다.\ 로그인 인증이 필요한
 *              페이지 (글작성, 글수정, 글삭제, 댓글작성, 답(댓)글작성/수정/삭제,관리자 페이지) 로그인 되어 있지 않으면,
 *              로그인을 하도록 하고, 로그인이 되어 있다면... 계속 원래 클라이언트가 하려던 작업을 수행 하도록 한다.
 * 
 *              )로그인이 되어 있지 않아서, 로그인 페이지로 끌려갔다면, 로그인을 성공한 뒤에는 원래 하려던 기능의 페이지로
 *              돌아가게 해야한다. ) 글수정, 글삭제, 댓글 수정, 댓글 삭제는 로그인 되어 있어야 할 뿐 아니라 그 글(댓글)의
 *              주인인지 확인 해야한다.
 */

public class AuthInterceptor extends HandlerInterceptorAdapter {

	@Autowired // 생성자 없이 자동 주입해주기에 서블릿 다시 컨텍스트에 에러 없음 근데 final 하게 만들면 생성자를
	// 롬복에게 만들라고 즉 서비스 주입하라고 하는 생성자를 만듦 final로 하고 싶은면 생성자를 만들때 넣으라는 root-context에 있는
	// <constructor-arg ref="sqlSessionFactory"></constructor-arg> 이런식으로 서블릿 컨텍스트에도
	// 추가해줘야 한다.

	private HBoardService service;
	
	@Autowired
	private RBoardService rservice;

	/**
	 * @작성자 : 802-01
	 * @작성일 : 2024. 8. 7.
	 * @메소드명 : preHandle
	 * @param
	 * @param : @throws(예외) :
	 * @description :
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		boolean goOriginPath = false; // 원래의 목적지(기능)
		System.out.println("[AuthInterceptor]...... preHandle 작동중!!!");

		new DestinationPath().setDestPath(request); // 로그인하기 전 호출했던 페이지를 저장

		HttpSession ses = request.getSession();
		if (ses.getAttribute("loginMember") == null) { // 로그인을 하지 않았다.
			System.out.println("[AuthInterceptor] : 로그인 하지 않았다!");

			response.sendRedirect("/member/login"); // 로그인 페이지로 끌려감

		} else { // 로그인을 했다. 여기서 글의 주인을 확인 즉 db를 본다.
			System.out.println("[AuthInterceptor] : 로그인 OK 되어있다.![그 글에 대한 수정/삭제 권한(본인글)이 있는지 검사]");
			goOriginPath = true;

			// 만약 글(답글)수정, 글(답글) 삭제의 페이지에서 왔다면 그 글에 대한 수정/삭제 권한(본인글)이 있는지?
			String uri = request.getRequestURI();

			
			// 로그인한 유저의 정보
			MemberVO loginMember = (MemberVO) ses.getAttribute("loginMember");
			
			
			System.out.println("이전에 있다가 요청된 페이지 : " + uri);

			if (uri.contains("modify") || uri.contains("remove")) {
				int boardNo = Integer.parseInt(request.getParameter("boardNo"));
				System.out.println(boardNo + "이 글에 대한 수정/삭제 권한 (본인글) 이 있는지 검사하자");

				// 계층형 게시판의 글 수정이나 글 삭제의 경우 read  메서드가 List<BoardDetailInfo>를 반환
				// 그 글의 작성자는 배령의 0 번째에 있다.
				if (uri.contains("hboard")) { 
					

					List<BoardDetailInfo> board = service.read(boardNo);
					
					
					if (!board.get(0).getWriter().equals(loginMember.getUserId())) {
						System.out.println("작성자와 로그인한 유저의 아이디가 다르므로 돌려보냄");
						response.sendRedirect("/hboard/viewBoard?status=authFail&boardNo=" + boardNo);
					}
				} else if (uri.contains("rboard")) {
					// 댓글형 게시판의 글 수정이나 글 삭제의 경우 read  메서드가 List<BoardDetailInfo>를 반환
					BoardDetailInfo board = rservice.read(boardNo);
					if(!loginMember.getUserId().equals(board.getWriter())) {
						System.out.println("작성자와 로그인한 유저의 아이디가 다르므로 돌려보냄");
						response.sendRedirect("/rboard/viewBoard?status=authFail&boardNo=" + boardNo);
						
					}
					
				}
			}
			goOriginPath = true;
		}

		return goOriginPath;
	}

}
