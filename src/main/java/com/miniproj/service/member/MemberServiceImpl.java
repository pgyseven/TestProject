package com.miniproj.service.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.miniproj.model.AutoLoginInfo;
import com.miniproj.model.LoginDTO;
import com.miniproj.model.MemberVO;
import com.miniproj.model.PointLogDTO;
import com.miniproj.persistence.MemberDAO;
import com.miniproj.persistence.PointLogDAO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor // final한 멤버 mDao에게 생성자를 통해 객체를 주입하는 방식
public class MemberServiceImpl implements MemberService {
	//@Autowired //자동주입
	private final MemberDAO mDao; //final 상수 불변 private final MemberDAO mDao; 이렇게만 쓰면 초기화 안된다고 에러뜸 그래서 위에서 @RequiredArgsConstructor
	
	private final PointLogDAO pDao;
	
//	public void setMemberDAO(MemberDAO mdao) {
//		this.mDao = mdao; //세터를 이용해서 필요한걸 주입해서 DAO 단을 테스트 하기도 한다.
//	}
	
	@Override
	public boolean idIsDuplicate(String tmpUserId) throws Exception {
		boolean result = false;
		 if(mDao.selectDuplicateId(tmpUserId) == 1) {
			 result = true; // 중복된다.
		 }
		
		
		return result;
	}


	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
	public boolean saveMember(MemberVO registMember) throws Exception {
	    // 취미를 1:N로 만들어야 했는데, 취미를 저장할 테이블을 별도로 만들지 않기 위해
		// 여러개의 취미를 아래와 같이 하나의 문자열로 저장한다. 정석은 1:다 관계가 맞다 이렇게하면 나중에 신경써야할게 많다.
		
		boolean result = false;
		
		String tmpHobbies = "";
	    for (String hobby : registMember.getHobby()) {
	    	tmpHobbies += hobby + ", ";
	    }
		
	    registMember.setHobbies(tmpHobbies);
		// 1) 회원 데이터를 db에 저장
	    if(mDao.insertMember(registMember) == 1) {
	    	// 2) 가입한 회원에게 100포인트 부여 (로그 기록) 즉 업데이트만 할 필요가 없다.
	    	if(pDao.insertPointLog(new PointLogDTO(registMember.getUserId(), "회원가입")) ==1) {
	    		result = true;
	    	}
	    }
		
		return result;
	    
		

	}


	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
	public MemberVO login(LoginDTO loginDTO) throws Exception {
		PointLogDTO pointLogDTO = new PointLogDTO(loginDTO.getUserId(), "로그인");
		// 1) 로그인 시도 select
	 	MemberVO loginMember = mDao.login(loginDTO);
		if(loginMember != null ) {
			// 2) 1번에서 로그인 성공시 PointLog에 insert
			if(pDao.insertPointLog(pointLogDTO) == 1) {
				// 3) Member 테이블에 userpoin update
				mDao.updateUserPoint(pointLogDTO);
				
				
			}
			
			
			
			
		}
		
		
		return loginMember;
	}


	@Override
	public boolean saveAutoLoginInfo(AutoLoginInfo autoLoginInfo) throws Exception {
		boolean result = false;
		if(mDao.updatAutoLoginInfo(autoLoginInfo) == 1) {
			result =true;
		}
		return result;
	}


	@Override
	@Transactional(readOnly = true)
	public MemberVO checkAutoLogin(String savedCookieSesId) throws Exception {
		return mDao.checkAutoLogin(savedCookieSesId);
		
	}

}
