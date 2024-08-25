package com.miniproj.interceptor;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.WebUtils;

import com.miniproj.model.AutoLoginInfo;
import com.miniproj.model.MemberVO;
import com.miniproj.service.member.MemberService;
import com.mysql.cj.util.StringUtils;

//직겁 로그인을 하는 동작과정을 인터셉터로 구현 / 지금 우린 리퀘스트 매핑의 벨류는 같고 전송 방식만 다른경우 있는데 인터셉터는 포스트인지 겟인지 구분하는 다른 기능은 없다 그러나
// get 방식으로 요청된건지, post 방식으로 요청되어서 인터셉터가 동작하는지를 구분해야한다.
public class LoginInterceptor extends HandlerInterceptorAdapter {
	@Autowired
	private MemberService service;
	
	@Override
	   public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
	         throws Exception {
	      
	      boolean isLoginPageShow = false;
	      
	      if (request.getMethod().toUpperCase().equals("GET")) { // 요청이 GET 방식일때만 수행한다.
	         System.out.println("[LoginInterceptor...preHandle() 호출]");
	         
	         // 이미 로그인이 되어있는 경우에는 로그인 페이지를 보여줄 필요가 없다.
	         // 로그인이 되어있지 않은 경우에만 로그인 페이지를 보여줘야 한다.
	         
	         Cookie autoLoginCookie = WebUtils.getCookie(request, "al");
	         
	         // 쿠키를 검사하여 자동로그인 쿠키가 존재한다면
	         if (autoLoginCookie != null) {
	        	 System.out.println("로그인 인터셉 쿠키 있을때");
	            // 쿠키가 있을 때
	            String savedCookieSesId = autoLoginCookie.getValue();
	            
	            // -> DB에 다녀와서 자동로그인을 체크한 유저를 자동로그인 시켜야 한다. -> 로그인 페이지X
	            MemberVO autoLoginUser = service.checkAutoLogin(savedCookieSesId);
	            
	            HttpSession ses = request.getSession();
	            
	            ses.setAttribute("loginMember", autoLoginUser);
	            
	            Object dp = ses.getAttribute("destPath");
	            response.sendRedirect((dp != null) ? (String)dp : "/");
	            
	         } else {//쿠키가 없고, 로그인 하지 않은 경우 로그인 페이지를 보여준다.
	            if (request.getSession().getAttribute("loginMember") == null) {
	        	 System.out.println("로그인 인터셉 쿠키 없을때");
	            isLoginPageShow = true;
	            }else {//쿠키가 없고, 로그인 한 경우 페이지를 보여주지 않는다.
	            	isLoginPageShow = false;
	            }
	         }
	         
	         
	         // 쿠키가 존재하지 않는다면 수동으로 로그인할 수 있도록 로그인 페이지를 보여줘야 한다.
	      }else  if (request.getMethod().toUpperCase().equals("POST")) {
	    	  isLoginPageShow = true;
	      }
	      
	      return isLoginPageShow;
	   }

	/*이렇게 하면 loginPost도 그냥 login으로 바꿔도 될듯
	 * @Override public boolean preHandle(HttpServletRequest request,
	 * HttpServletResponse response, Object handler) throws Exception { if
	 * (request.getMethod().toUpperCase().equals("GET") ||
	 * request.getMethod().toUpperCase().equals("POST")) { // 기존 로직 유지 boolean
	 * isLoginPageShow = false; Cookie autoLoginCookie = WebUtils.getCookie(request,
	 * "al"); if (autoLoginCookie != null) { String savedCookieSesId =
	 * autoLoginCookie.getValue(); MemberVO autoLoginUser =
	 * service.checkAutoLogin(savedCookieSesId); if (autoLoginUser != null) {
	 * HttpSession ses = request.getSession(); ses.setAttribute("loginMember",
	 * autoLoginUser); Object dp = ses.getAttribute("destPath");
	 * response.sendRedirect((dp != null) ? (String) dp : "/"); return false; // 요청
	 * 처리를 중단합니다. } } else { isLoginPageShow = true; } return isLoginPageShow; }
	 * return true; // GET과 POST 외의 요청에 대해 계속 처리합니다. }
	 */
	
	
	
	/*
	 * 포스트에서 검증
	 * 
	 * @Override public void postHandle(HttpServletRequest request,
	 * HttpServletResponse response, Object handler, ModelAndView modelAndView)
	 * throws Exception { if (request.getMethod().toUpperCase().equals("POST")) {
	 * Map<String, Object> model = modelAndView.getModel(); MemberVO loginMember =
	 * (MemberVO) model.get("loginMember"); if (loginMember != null) { HttpSession
	 * ses = request.getSession(); ses.setAttribute("loginMember", loginMember); if
	 * (request.getParameter("remember") != null) { saveAutoLoginInfo(request,
	 * response); } Object tmp = ses.getAttribute("destPath");
	 * response.sendRedirect((tmp == null) ? "/" : (String) tmp); } else {
	 * response.sendRedirect("/member/login?status=fail"); } } }
	 */
	
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
		
		
		

		if (request.getMethod().toUpperCase().equals("POST")) { // POST 방식으로 호출 했을 때만 실행되도록
			System.out.println("loginIntercepror 의 posthandle호출 ~~~~~~~~~~~~~");
			super.postHandle(request, response, handler, modelAndView);
			Map<String, Object> model = modelAndView.getModel();
			MemberVO loginMember = (MemberVO) model.get("loginMember");
			if (loginMember != null) {
				System.out.println("[loginIntercepror... postHandle() : 로그인 성공]");
				// 세션에 로그인한 유저의 정보를 넣어주었다..
				HttpSession ses = request.getSession();
				ses.setAttribute("loginMember", loginMember);
				//만약 자동 로그인을 체크한 유저라면...
				
				/*
				 * 1)login.jsp에서 체크박스 클릭시 : alert() 띄운다.
				 * 2) 로그인 성공 했다면, 자동 로그인 체크 한 유저인지 검사한다.
				 * 3) 
				 */
				if(request.getParameter("remember") !=null ) { //스프링 프레임워크가 해주는게 갯파람해서 있으면 트로 없으면false 준다.
					saveAutoLoginInfo(request, response);
					
				}
				//홈
				//response.sendRedirect("/"); // 이건 그닥 좋은 방법이 아니다. 이건 뷰만 호출하는 형식이다 즉 리다이렉트라는 이름과 다르게 포워딩이 아니라 컨트롤러단을 거치지 않고 이동시킴 그럼 c:이 문법 자체를 못이해해서 에러남

//				if(ses.getAttribute("destPath") != null) { //사실 무조건 넣어서 널일 가능성 거의 없다 하지만 이렇게 한다고 하신다.
//					response.sendRedirect((String)ses.getAttribute("destPath")); 
//				}else {
//					response.sendRedirect("/");
//				}
				
				Object tmp = ses.getAttribute("destPath");
				System.out.println((String)tmp + "이건 데스트 패스 입니다.");
				response.sendRedirect((tmp == null) ? "/" : (String)tmp);
			} else {
				System.out.println("[loginIntercepror... postHandle() : 로그인 실패]");
				response.sendRedirect("/member/login?status=fail"); // 시스템 아웃보다 우선 순위가 높다
			} 
		}
	}
	
	
	

	private void saveAutoLoginInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 자동 로그인을 체크한 유저의 컬럼에 세션값과 만료일 DB에 저장
		String sesId = request.getSession().getId();
		MemberVO loginMember = (MemberVO)request.getSession().getAttribute("loginMember");
		String loginUserId = loginMember.getUserId();
		Timestamp allimit = new Timestamp(System.currentTimeMillis()+(1000*60*60*24*7)); //롱타입임 그러면  db는 타임스탭프니깐 타입스탬프 객체로 바꿔야함 자바api 에서 타임스탭프 검사하면 생성자에 있는거 쓸거임 / 현재 날짜 시간을 밀리 세컨드 단위로 가져옴
		
		Instant instant = allimit.toInstant(); //Instant 추상 클래스 객체임
		ZonedDateTime gmpDateTime = instant.atZone(ZoneId.of("GMT"));
		Timestamp gmtAlLimit = Timestamp.from(gmpDateTime.toInstant());
		
		
		//SimpleDateFormat sd = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z"); //EEE, d MMM yyyy HH:mm:ss Z 자바api에서 다른것도 볼 수 있다.
		//System.out.println(sd.format(new java.sql.Date(System.currentTimeMillis())));
		
		
		if (service.saveAutoLoginInfo(new AutoLoginInfo(loginUserId, sesId, allimit))) {
			// 쿠키가 gmt 인지 utc 인지 체크한다. 지금같은 경우 크롬 저장된거 보니깐gmp 시간같음 mysql은 기본 utc 속성임
			// 자동 로그인을 체크 했을때의 세션을 쿠키에 넣어둠 
			// 우리의 문제는 한컴터를 두명이상이 쓸때 첫번재 사람은 남은 쿠키로 인해서 로그인이 불가능하다. 이런건 구글은
			//쿠키에 암호화된 아이디를 넣둬서 해결한다. 우리는 이건 안한다.
			Cookie autoLoginCookie = new Cookie("al", sesId); 
			autoLoginCookie.setMaxAge(60*60*24*7); //일주일동안 쿠키 유지 (자동 로그인 쿠키)
			autoLoginCookie.setPath("/"); //쿠키가 저장될 경로 설정(해당 경로일때 쿠키 확인이 가능)
			response.addCookie(autoLoginCookie);
		}
		
		
		
		
		
	}
	
}
