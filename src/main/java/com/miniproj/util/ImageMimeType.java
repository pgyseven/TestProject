package com.miniproj.util;

import java.util.HashMap;
import java.util.Map;

public class ImageMimeType { // 필요할때만 로드해서 쓸테니깐 쓰레드 안터짐 주구장창 올라가면 안되니깐 

	private static Map<String, String> imageMimeType;
	{
		// instance 멤버를 초기화 하는 초기화 블럭
	}
	
	static {
		// static 멤버를 초기화 하는 블럭
		
		imageMimeType = new HashMap<String, String>();
		imageMimeType.put("jpg", "image/jpeg");
		imageMimeType.put("jpeg", "image/jpeg");
		imageMimeType.put("gif", "image/gif");
		imageMimeType.put("png", "image/png");
		
	}
	// ext 가 이미지(true) 인지 아닌지(false)
	public static boolean isImage(String ext) {
		return imageMimeType.containsKey(ext);
		
		
	}
	
	
	
	
	
	
	
	
}
