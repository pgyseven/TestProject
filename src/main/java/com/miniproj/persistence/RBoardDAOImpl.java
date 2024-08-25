package com.miniproj.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.miniproj.model.BoardDetailInfo;
import com.miniproj.model.BoardUpFilesVODTO;
import com.miniproj.model.HBoardDTO;
import com.miniproj.model.HBoardVO;
import com.miniproj.model.HReplyBoardDTO;
import com.miniproj.model.PagingInfo;
import com.miniproj.model.SearchCriteriaDTO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class RBoardDAOImpl implements RBoardDAO {
	private static final String NS = "com.miniproj.mapper.rboardmapper";
	private final SqlSession ses;
	
	@Override
	public List<HBoardVO> selectAllBoard(PagingInfo pi) throws Exception {
		
		return ses.selectList(NS + ".getAllHBoard", pi);
	}

	@Override
	public int insertNewBoard(HBoardDTO newBoard) throws Exception {
		
		return ses.insert(NS + ".saveNewBoard", newBoard);
	}

	@Override
	public int getMaxBoardNo() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public BoardDetailInfo selectBoardByBoardNo(int BoardNo) throws Exception {
		
		return ses.selectOne(NS + ".selectBoardDetailInfoByBoardNo", BoardNo);
	}

	@Override
	public int deleteBoardByBoardNo(int boardNo) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public List<HBoardVO> selectPopBoards() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getTotalPostCnt() throws Exception {
		
		return ses.selectOne(NS + ".selectTotalCount");
	}

	@Override
	public int getTotalPostCnt(SearchCriteriaDTO sc) throws Exception {
		
		return ses.selectOne(NS + ".selectTotalCountWithSearchCriteria", sc);
	}

	@Override
	public List<HBoardVO> selectAllBoard(PagingInfo pi, SearchCriteriaDTO searchCriteria) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("searchType", searchCriteria.getSearchType());
		params.put("searchWord","%" + searchCriteria.getSearchWord() + "%");
		params.put("startRowIndex", pi.getStartRowIndex());
		params.put("viewPostCntPerPage", pi.getViewPostCntPerPage());
		
		return ses.selectList(NS + ".getSeasrchBoardWithPaging", params);
	}

}
