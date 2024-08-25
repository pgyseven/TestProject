package com.miniproj.persistence;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.miniproj.model.PointLogDTO;

@Repository
public class PointLogDAOImpl implements PointLogDAO { // 임플리먼트 상속 받는것 뉴클래스로 만들때 add 하는거 포인트 로그 다오는 임플과 같은 객체다 프로그램에서는 부모 이콜 자식
	@Autowired
	private SqlSession ses;
	
	private static String NS = "com.miniproj.mapper.pointlogmapper";
	
	@Override
	public int insertPointLog(PointLogDTO pointLogDTO) throws Exception {
		
		return ses.insert(NS + ".insertPointLog", pointLogDTO);
	}
	

	
	
}
