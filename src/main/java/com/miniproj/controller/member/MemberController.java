package com.miniproj.controller.member;

import java.io.IOException;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.miniproj.model.LoginDTO;
import com.miniproj.model.MemberVO;
import com.miniproj.model.MyResponseWithoutData;
import com.miniproj.service.member.MemberService;
import com.miniproj.util.FileProcess;
import com.miniproj.util.SendMailService;
import com.mysql.cj.util.StringUtils;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor //생성자를 만들때 필요할때 받아온다. 스프링이 얘를 불러와서 mService; 하고나서 MemberController
public class MemberController {

	private final MemberService mService;
	private final FileProcess fp;
	@RequestMapping("/register")
	public void showRegisterForm() {
		
	}
	
	
	@RequestMapping(value="/register", method = RequestMethod.POST)
	   public String registerMember(MemberVO registMember, @RequestParam("userProfile") MultipartFile userProfile, 
			   RedirectAttributes rediAttributes, HttpServletRequest request) {
	      
	      System.out.println(userProfile.getOriginalFilename());
	      
	      String resultPage = "redirect:/"; // 성공했을 경우 index로
	      String realPath = request.getSession().getServletContext().getRealPath("/resources/userimg");
	      System.out.println("실제 파일 저장 경로 : " + realPath);
	      
	      //(프로필 파일이름 : 유저아이디.유저가 올린 파일의 확장자) - 유저가 프로필 파일을 업로드 했을 떄
	      String tmpUserProfileName = userProfile.getOriginalFilename();
	      if(!StringUtils.isNullOrEmpty(tmpUserProfileName)) {
	    	  String ext = tmpUserProfileName.substring(tmpUserProfileName.lastIndexOf(".") + 1);
		      registMember.setUserImg(registMember.getUserId() + "." + ext);
		      
	      }
	      
	      System.out.println("회원가입 진행~~~~~~~~~~~~~~~~" + registMember.toString());
	      
	      
	      try {
			if(mService.saveMember(registMember)) {
				rediAttributes.addAttribute("status","success");
				
				// 프로필을 올렸는지 확인
				if(!StringUtils.isNullOrEmpty(tmpUserProfileName)) { // - 유저가 프로필 파일을 업로드 했을 떄
					fp.saveUserProfileFile(userProfile.getBytes(), realPath, registMember.getUserImg());
			      }
				
				
			}
		} catch (Exception e) { //IOException(파일 처리시 발생한 예외) , SQLException ( DB 작업시 발생한 예외)
			
			e.printStackTrace();
			
			if(e instanceof IOException) {
				rediAttributes.addAttribute("status","fileFail");
				
				// DB에 방금전 회원 가입한 유저(registMember.getUserId()회원가입 취소 처리
				// service -> dao() 호출
				
			}else {
				rediAttributes.addAttribute("status","fail");
			}
			
			resultPage = "redirect:/member/register"; //실패한 경우 다시 회원 가입 페이지로 이동
			
		}
	      return resultPage;
	   }
	
	
	
	@RequestMapping(value="/isDuplicate", method = RequestMethod.POST, produces = "application/json; charset=UTF-8;")
	public ResponseEntity<MyResponseWithoutData> idIsDuplicate(@RequestParam("tmpUserId") String tmpUserId) {
		
		System.out.println(tmpUserId + "  가 중복되는지 확인");
		
		
		MyResponseWithoutData json = null;
		ResponseEntity<MyResponseWithoutData> result = null;
		
		try {
			
			if(mService.idIsDuplicate(tmpUserId)) {
				//아이디가 중복된다.
				json = new MyResponseWithoutData(200, tmpUserId, "duplicate");
				
				
			}else {
				// 아이디가 중복되지 않는다.
				json = new MyResponseWithoutData(200, tmpUserId, "not duplicate");
			}
			result = new ResponseEntity<MyResponseWithoutData>(json, HttpStatus.OK);
			
		} catch (Exception e) {
			
			e.printStackTrace();
			result = new ResponseEntity<>(json, HttpStatus.CONFLICT);
		}
		return result;
	}
	
	//스프링은 지가 혼자 알아서 싱글톤으로 객체 하나만 가지고 돌려 쓸 수 있도록 해줌 회사가서 스프링 같은거 안쓰는데서 new 다오 임플 이런거 하면 안됨
	@RequestMapping(value = "/callSendMail")
	public ResponseEntity<String> sendMailAuthCode(@RequestParam("tmpUserEmail") String tmpUserEmail, HttpSession session) { //리스폰스 엔티티는 담으면 제이슨으로 변하고 큰데이터를 주기 편하나 지금 같이 간단히 보낼때는 스트링으로
		String authCode = UUID.randomUUID().toString();
		System.out.println(tmpUserEmail + "로 " + authCode + "를 보내자~");
		
		String result = "";
		
		try {
			//new SendMailService().sendMail(tmpUserEmail, authCode); // 실제 메일 발송 이것만 주석하면 안보내짐
			session.setAttribute("authCode", authCode); // 인증 코드를 세션 객체에 저장
			
			result = "success";
			
			
		}  catch (Exception e) {
			
			e.printStackTrace();
			result = "fail";
		}
	
		return new ResponseEntity<String>(result, HttpStatus.OK);
		
	}
	
	@RequestMapping("/checkAuthCode")
	public ResponseEntity<String> checkAuthCode(@RequestParam("tmpUserAuthCode") String tmpUserAuthCode, HttpSession session){
		System.out.println(tmpUserAuthCode + "와 세션에 있는 인증 코드가 같은지 비교하자.");
		
		String result = "fail";
		
		if (session.getAttribute("authCode") != null ) {
			String sesAuthCode = (String)session.getAttribute("authCode");
			
			if(tmpUserAuthCode.equals(sesAuthCode)) {
				result = "success";
			}
		}
		return new ResponseEntity<String>(result, HttpStatus.OK);
	}
	@RequestMapping("/clearAuthCode")
	   public ResponseEntity<String> clearCode(HttpSession session) {
	      if (session.getAttribute("authCode") != null) {
	         session.removeAttribute("authCode");  // attribute 속성을 지운다...
	      }
	      
	      return new ResponseEntity<String>("success", HttpStatus.OK);
	   }
	@RequestMapping("/login")
	public void loginGET() {
		
		//로그인 기능 구현시 필요한 인터셉터
		// LoginInterceptor -> 직겁 로그인을 하는 동작과정을 인터셉터로 만듦 (유저가 로그인 페이지로 가서 로그인 할떄)
		// AuthInterceptor -> 로그인을 한 유저만 사용할 수 있도록 되어 있는 페이지에서... 현재 유저가 로그인 한 유저인지 아닌지 검사하는 인터셉터
		
		// /member/login.jsp 응답
		
	}
	
	
	@RequestMapping(value = "/loginPOST",  method = RequestMethod.POST)
	public void loginPOST(LoginDTO loginDTO, Model model) {
		System.out.println(loginDTO.toString() + "으로 로그인 한다.");
		
		
	
		try {
			MemberVO loginMember = mService.login(loginDTO);
			
			if(loginMember != null) {
				System.out.println("MemberController - 로그인 성공");
				model.addAttribute("loginMember", loginMember);
				// 홈으로 이동
				
				
			} 
			return; //loginPOST.jsp로 가지 않고, Logininterceptor의 posthandle이 수행되도록...  loginPost 여기서 메서드 끝내라 즉 포스트 핸들로해서 거기서 지정한 데스트 패스로 이동하라
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@RequestMapping("/logout")
	public String logoutMember(HttpSession session) {
		System.out.println("로그아웃 이전의 세션 " + session.getId());
		
		if(session.getAttribute("loginMember") != null) {
			//세션에 저장했던 값들을 지우고,
		session.removeAttribute("loginMember");
		session.removeAttribute("destPath");
		
			//세션 무효화
		session.invalidate();
		
		} 
		
		System.out.println("로그아웃 이후의 세션 " + session.getId());
		
		return "redirect:/";
	}
}
