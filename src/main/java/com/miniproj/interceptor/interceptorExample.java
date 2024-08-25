//패키지 명. 클래스 명 => class full name 즉 이게 정확한 이름이다. 즉 이름이 같은 클래스가 있다.date 라는 유틸이 자바.유틸과 자바.에슼큐엘에 있는것 과 같이~
package com.miniproj.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;

//Interceptor는 컨트롤러에 들어오는 요청 request와 컨트롤러가 응답하는 response를 가로채는 역할을 합니다.
public class interceptorExample extends HandlerInterceptorAdapter {

	//가이드 역할을 하는게 아답터 객체이고 해당 클래스 호출할때 C 아이콘에 A 붙어있으면 추상메서드임 HandlerInterceptorAdapter 이걸로 인해서 우리 객체가 인터셉터가 된거다.
	//참고로 임플리먼트스는 implements 인터페이스 상속 추상의 정도가 제일 높은게 인터페이스고 그걸 다 구현 못하면 객체로 만들수 없다 그래서 지금 이창에서도 임플만하면 에러가 날것이다 인터페이스의 기능을 다 구현해야함
	//그러나 추상 클래스를 상속 받으면 일반 메서드나 일반 멤버를 가지고 있음 그래서 이것 만으로도 객체가 만들어진다.
	// 그러나 니가 원한다면 특별 기능을 원한다면 추상메서드를 오버라이딩 해도 좋다~ 이거다 이게 인터페이스와 다른거다. 
	
	
	
	//오버라이드 할수 있는것 우클릭 소스에 오버라이드에 HandlerInterceptorAdapter의 목록 보면 된다.
	
	// mapping되는 컨트롤러단의 메서드가 호출되기 이전에 request와 response를 빼앗아 와서 동작
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		//서블릿단에서 <mapping path="/sampleInterceptor"/> 이거에 의해 컨트롤단에 실행되기 전에 실행될거임
		System.out.println("인터셉터 prehandle 동작!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		
		return super.preHandle(request, response, handler); //return false; // 해당 컨트롤러 단의 메서드로 제어가 돌아가지 않는다.
		//return true; //해당 컨트롤러 단의 메서드로 제어가 돌아간다.
	}
	// mapping되는 컨트롤러단의 메서드가 호출되어 실행된 후에 request와 response를 빼앗아 와서 동작
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
		//컨트롤단 실행 종료후 찍힐거임
		System.out.println("인터셉터 postHandle 동작!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		super.postHandle(request, response, handler, modelAndView);
	}
	// 해당 interceptor의 preHandle, postHandle 의 전 과정이 끝난 후에(view 단이 렌더링된 후) request와 response를 빼앗아 와서 동작
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception { //이건 대부분 익셉션 객체를 공동으로 처리할때 많이 쓴다 그래서 우린 거의 쓸 일 없다.

		// 위의 두개가 끝난 후(view 단이 렌더링된 후) 여기가 작동함
		System.out.println("인터셉터 afterCompletion 동작!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		super.afterCompletion(request, response, handler, ex);
	}

	
	
	
	
	
	
}
