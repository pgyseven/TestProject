package com.miniproj.controller;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.miniproj.model.HBoardVO;
import com.miniproj.model.MyResponseWithoutData;
import com.miniproj.service.hboard.HBoardService;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	@Autowired
	private HBoardService hbService;
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate );
		
		return "index"; //index.jsp
	}
	
	@RequestMapping("/weather")
	public void goWeatherPage() {
		
	}
	@RequestMapping("/movie")
	public void goMoviePage() {
		
	}
	@RequestMapping("/movieTwo")
	public void goMovieTwoPage() {
		
	}
	@RequestMapping("/news")
	public void goNewsPage() {
		
	}
	
	@RequestMapping("/saveCookie")
	public ResponseEntity<String> saveCookie(HttpServletResponse response) {
		System.out.println("쿠키를 저장하자.");
		Cookie myCookie = new Cookie("notice", "N");  //name, value    쿠키에서 도메인과 패스는 생략해도 자동으로 된다. 중요한건 만료일  import javax.servlet.http.Cookie;
		myCookie.setMaxAge(60*60*24); //쿠키 만료일 설정.. (만료일이 되면 자동으로 쿠키가 삭제된다.) / 초단위로 줘야함
		
		response.addCookie(myCookie); //쿠키를 응답 객체에 실어 보냄 보내면 응답하면서 저장이 되는거
		
		return new ResponseEntity<String>("success", HttpStatus.OK);
		
	}
	
	@RequestMapping(value="/readCookie", produces = "application/json; charset=UTF-8;")
	public ResponseEntity<MyResponseWithoutData> readCookie(HttpServletRequest request) {
		
		System.out.println("쿠키를 읽어보자");
		
		MyResponseWithoutData result = null;
		Cookie[] cookies = request.getCookies();
		// 이름이 notice 인 쿠키가 있고, 그값이 N 이다.
		for(int i = 0; i< cookies.length; i++) {
			if(cookies[i].getName().equals("notice") && cookies[i].getValue().equals("N")) {
			result = new MyResponseWithoutData(200, null, "success");
		}
		}
		if(result ==null) {
			result = new MyResponseWithoutData(400, null, "fail");
		}
		return new ResponseEntity<MyResponseWithoutData>(result, HttpStatus.OK);
		
	}
	
	@RequestMapping(value="/get5Boards", produces = "application/json; charset=UTF-8;")
	public ResponseEntity<List<HBoardVO>> get5Boards() {
		ResponseEntity<List<HBoardVO>> result = null;
		try {
			List<HBoardVO> popBoards = hbService.getPopularBoards();
			
			result = new ResponseEntity<List<HBoardVO>>(popBoards, HttpStatus.OK);
		} catch (Exception e) {
	
			e.printStackTrace();
			result = new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		return result;
	}
	
	
	@RequestMapping("/sampleInterceptor")
	public void sampleInterceptor() {
		//interceptor 의 prehandle 동작
		System.out.println("샘플 인터셉터 호출");
		// sampleInterceptor.jsp 를 찾아서 response 해주는게 얘의 역할임 근데 인터셉터가 가로채니깐~
	}
}
