package com.miniproj.model;

import org.springframework.http.ResponseEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

//@Builder //클래스 위에 사용 하는 @Builder 어노테이션은 이 클래스가 가지는 모든 멤버변수를 builder 패턴으로 만들어 준다.
//@AllArgsConstructor //이게 있어야 클래스에서는 뷰일더 어노테이션 가능

@Getter
public class MyResponseWithData<T> {
	private int resultCode;
	private String resultMessage;
	private T data; // t 제너릭, 클래스<T>도 그래야함 
	
	@Builder // 생성자 위에 사용하는 @Bulider 어노테이션은 아래의 생성자가 가지고 있는 변수를 builder 패턴으로 만들어 준다.
	public MyResponseWithData(ResponseType responseType, T data) {
		this.resultCode = responseType.getResultCode();
		this.resultMessage = responseType.getResultMessage();
		this.data = data;
	}
	
	
	/**
	 * @작성자 : 802-01
	 * @작성일 : 2024. 8. 16.
	 * @메소드명 : success
	 * @return : MyResponseWithData
	 * @throws(예외) :
	 * @description : data 없이 성공했다는 코드(200)와 "success"만 전송
	 */
	public static MyResponseWithData success() {
		return MyResponseWithData.builder()
				.responseType(ResponseType.SUCCESS)
				.build();
	}
	
	
	/**
	 * @작성자 : 802-01
	 * @작성일 : 2024. 8. 16.
	 * @메소드명 : success
	 * @param : 제너릭 타입의 json 으로 만들어 줄 data
	 * @return : MyResponseWithData<T>
	 * @throws(예외) :
	 * @description : data + 성공했다는 코드(200)와 "success" 전송
	 */
	public static <T> MyResponseWithData<T> success(T data) { //제너릭 타입을 사용하는 메서드 제너릭 메서드
		return new MyResponseWithData<>(ResponseType.SUCCESS, data);
		
	}
	/**
	 * @작성자 : 802-01
	 * @작성일 : 2024. 8. 16.
	 * @메소드명 : fail
	 * @return : MyResponseWithData
	 * @throws(예외) :
	 * @description : 실패했다는 코드 (400)과 "fail" 전송
	 */
	public static MyResponseWithData fail() {
		return MyResponseWithData.builder()
				.responseType(ResponseType.FAIL)
				.build();
	}
	
}
