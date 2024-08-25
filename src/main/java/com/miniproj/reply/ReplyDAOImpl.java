package com.miniproj.reply;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.miniproj.model.PagingInfo;
import com.miniproj.model.ReplyVO;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReplyDAOImpl implements ReplyDAO {

	
	private static final String NS = "com.miniproj.replyMapper";
	private final SqlSession ses;
	
	@Override
	public List<ReplyVO> getAllReplies(int boardNo, PagingInfo pi) throws Exception {
		
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("boardNo", boardNo);
		param.put("startRowIndex", pi.getStartRowIndex());
		param.put("viewPostCntPerPage", pi.getViewPostCntPerPage());
		return ses.selectList(NS + ".getReplies", param);
	}

	@Override
	public int getTotalPostCnt(int boardNo) throws Exception {
		
		return ses.selectOne(NS + ".getReplyCount", boardNo);
	}

	

	

}
