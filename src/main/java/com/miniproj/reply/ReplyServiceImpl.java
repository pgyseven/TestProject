package com.miniproj.reply;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.miniproj.model.PagingInfo;
import com.miniproj.model.PagingInfoDTO;
import com.miniproj.model.ReplyVO;
import com.mysql.cj.util.StringUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReplyServiceImpl implements ReplyService {


	private final ReplyDAO rDao;
	@Override
	@Transactional(readOnly = true)
	public Map<String, Object> getAllReplies(int boardNo, PagingInfoDTO pagingInfoDTO) throws Exception {
		
		Map<String, Object> result =  new HashMap<String, Object>();
		PagingInfo pi = makePaingInfo(boardNo, pagingInfoDTO);
		List<ReplyVO> list = rDao.getAllReplies(boardNo, pi);
		
		result.put("pagingInfo", pi);
		result.put("replyList", list);
		return result;
	}
	
	private PagingInfo makePaingInfo(int boardNo, PagingInfoDTO pagingInfoDTO) throws Exception {
		PagingInfo pi = new PagingInfo(pagingInfoDTO);
		
		
		
		
			
		pi.setTotalPostCnt(rDao.getTotalPostCnt(boardNo));
		pi.setTotalPageCnt(); 
		pi.setStartRowIndex(); 
		
		//페이징 블럭 만들기
		pi.setPageBlockNoCurPage();
		pi.setStartPageNoCurBlock();
		pi.setEndPageNoCurBlock();
		
		
		System.out.println(pi.toString());
		return pi;
	}
		
	}


