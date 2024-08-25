package com.miniproj.util;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

public class SendMailService {
	
	private String username; //네이버 아이디
	private String password; // 네이버 2단계 인증에서 받아온 것
	
	@Autowired
	ResourceLoader resourceLoader;
	

	
	public void sendMail(String eamilAddr, String activationCode) throws AddressException, MessagingException, IOException {
		String subject = "miniproject.com 에서 보내는 회원가입 이메일 인증번호 입니다.!";
		String message = "회원 가입을 환영 인증번호 :" + activationCode + "를 입력하시고 인증하세요";
		
		//naver 이메일서버의 메일 서버 환경 설정
		//prop 키와 발류로 설정한다. properties 부모가  map(dictionary) 인터페이스임 / 쉽게 파일로 저장하거나 읽을 수 있다.
		Properties props = new Properties();
		
		props.put("mail.smtp.host", "smtp.naver.com");  // smtp 호스트 주소 등록
		props.put("mail.smtp.port", "465"); // naver smtp의 포트번호
		props.put("mail.smtp.starttls.required", "true"); //동기식 전송을 위해 설정
		props.put("mail.smtp.ssl.protocols", "TLSv1.2");
		props.put("mail.smtp.ssl.enable", "true");  // SSL 사용
		
		props.put("mail.smtp.auth", "true"); // 인증 과정을 거치겠다.
		
		
		getAccount();
		
		Session mailSession = Session.getInstance(props, new Authenticator() { //Authenticator추상메서드라 내가 구체화 해야함

			@Override //source 에 오버라이드 해서 만든다.
			protected PasswordAuthentication getPasswordAuthentication() {
				
				return new PasswordAuthentication(username,password);
			}
		
		
		
		}); //javax.mail 임포트함
		
		
		System.out.println(mailSession.toString());
		
		if(mailSession != null) {
			MimeMessage mime = new MimeMessage(mailSession);
			mime.setFrom(new InternetAddress("pgyseven@naver.com")); // 보내는 주소 / 이게 이메일이 아닐수도 있어서 예외 처리함 트로우 익셉션 / 주소 객체 InternetAddress
			mime.addRecipient(RecipientType.TO, new InternetAddress(eamilAddr)); //  받는사람 / 임포트 할때 배열 형태로 하면 여러사람한테 쭉 보낼때 이메일 배열로 받아서 같은거 보낼때 유용
			// 메일 제목과 , 본문 세팅
			mime.setSubject(subject);
			mime.setText(message);
			
			//위에서는 접속 객체만 가져온거고 여기서 부터 메일을 보낸다.
			Transport trans = mailSession.getTransport("smtp"); // 메일 보내는 객체 Transport
			trans.connect(username,password); //연결
			
			trans.send(mime);
			trans.close();// 통신 종료
		
		}
	}
		private void getAccount() throws IOException {
		
		
			
		 Properties account = new Properties();
				 
				 account.load(new FileReader("D:\\lecture\\spring\\MiniProject\\src\\main\\webapp\\WEB-INF\\config.properties"));
				 this.username = (String)account.get("username");
				 this.password = (String)account.get("password");
			
	}
}
