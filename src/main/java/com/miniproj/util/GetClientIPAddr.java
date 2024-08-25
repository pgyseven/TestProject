package com.miniproj.util;

import javax.servlet.http.HttpServletRequest;

public class GetClientIPAddr {
	
	public static String getClientIp(HttpServletRequest request) { //이거 http 임포트 할때 버전에 따라 다를 수 있다.
        

        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.length() == 0 || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("Proxy-Client-IP");
        }
        if (clientIp == null || clientIp.length() == 0 || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("WL-Proxy-Client-IP");
        }
        if (clientIp == null || clientIp.length() == 0 || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (clientIp  == null || clientIp .length() == 0 || "unknown".equalsIgnoreCase(clientIp )) {
            clientIp  = request.getHeader("X-Real-IP");
        }
        if (clientIp  == null || clientIp .length() == 0 || "unknown".equalsIgnoreCase(clientIp )) {
            clientIp  = request.getHeader("X-RealIP");
        }
        if (clientIp  == null || clientIp .length() == 0 || "unknown".equalsIgnoreCase(clientIp )) {
            clientIp  = request.getHeader("REMOTE_ADDR");
        }
        if (clientIp == null || clientIp.length() == 0 || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getRemoteAddr();
        }
       
        return clientIp; 

}

}
