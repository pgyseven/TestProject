package com.miniproj.persistence;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.miniproj.model.AutoLoginInfo;
import com.miniproj.model.LoginDTO;
import com.miniproj.model.MemberVO;
import com.miniproj.model.PointLogDTO;

@Repository
public class MemberDAOImpl implements MemberDAO {

	@Autowired
	private SqlSession ses;
	
	private static String NS = "com.miniproj.mapper.membermapper";
	//스태틱하지 않은건 객체가 있어야 호출해야함
	//static은 공유 그클래스에서 만들어진 객체들이 다 공유한다는 의미에서
	
	@Override
	public int updateUserPoint(PointLogDTO pointLogDTO) throws Exception {
		
		return ses.insert(NS + ".updateUserPoint", pointLogDTO);
	}

	@Override
	public int selectDuplicateId(String tmpUserId) throws Exception {
		
		return ses.selectOne(NS + ".selectUserId", tmpUserId);
	}

	@Override
	public int insertMember(MemberVO registMember) throws Exception {
		
		return ses.insert(NS + ".insertMember", registMember);
	}

	@Override
	public MemberVO login(LoginDTO loginDTO) throws Exception {
		
		return ses.selectOne(NS + ".loginWithLoginDTO", loginDTO);
	}

	@Override
	public int updatAutoLoginInfo(AutoLoginInfo autoLoginInfo) throws Exception {
		
		return ses.update(NS + ".updateAutoLoginInfo", autoLoginInfo);
	}

	@Override
	public MemberVO checkAutoLogin(String savedCookieSesId) throws Exception {
	
		return ses.selectOne(NS + ".checkAutoLoginUser", savedCookieSesId);
	}

}
