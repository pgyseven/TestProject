package com.miniproj.model;

public enum ResponseType {
	
	// 이안에 있는게 static final 이다 근데 내부에서는 int로 처리됨 첫번째 값은 0 다음은 1 이런식 그값을 임의로 줄 수도 있다.

	 SUCCESS(200), FAIL(400);
	
	private int resultCode;
	
	ResponseType(int resultCode) { // enum의 접근제한자는 only private /  이넘 타입은 접근제어자가 생략되어도 기본이 private 임 class 와 다르게 dafault 가 아님
		this.resultCode = resultCode;
		
	}
	
	public int getResultCode() {
		return this.resultCode;
		
	}
	public String getResultMessage() {
		return this.name(); // SUCCESS,FAIL 이 String 으로 반환/ SUCCESS,FAIL 인트값을 가진 이름 이게 자동으로 네임에 들어감
	}
	
	
}

