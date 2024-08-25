package com.miniproj.persistence;

import com.miniproj.model.AutoLoginInfo;
import com.miniproj.model.LoginDTO;
import com.miniproj.model.MemberVO;
import com.miniproj.model.PointLogDTO;

public interface MemberDAO {
	// 유저의 userpoint를 수정하는 메서드
	int updateUserPoint(PointLogDTO pointLogDTO) throws Exception;
	
	
	// tmpUserId 아이디가 있는지 검사하는 메서드(아디디 중복 검사)
	int selectDuplicateId(String tmpUserId) throws Exception;

	// 회원을 저장하는 메서드
	int insertMember(MemberVO registMember) throws Exception;

	// 로그인 하는 메서드
	MemberVO login(LoginDTO loginDTO) throws Exception;

	// 자동로그인 정보를 저장하는 메서드
	int updatAutoLoginInfo(AutoLoginInfo autoLoginInfo) throws Exception;

	// 자동 로그인을 확인하는 메서드
	MemberVO checkAutoLogin(String savedCookieSesId) throws Exception;
	
	
	
}
