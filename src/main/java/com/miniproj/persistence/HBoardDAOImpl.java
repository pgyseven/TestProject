package com.miniproj.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.miniproj.model.BoardDetailInfo;
import com.miniproj.model.BoardUpFilesVODTO;
import com.miniproj.model.HBoardDTO;
import com.miniproj.model.HBoardVO;
import com.miniproj.model.HReplyBoardDTO;
import com.miniproj.model.PagingInfo;
import com.miniproj.model.SearchCriteriaDTO;

@Repository //아래의 클래스가 DAO 객체임을 명시 빈스그래프에 뜨는지 확인 안뜨면 우클릭 스프링 들어가서 빈스그래프 뜨게 하면됨
public class HBoardDAOImpl implements HBoardDAO {
// Impl 구현하다 implement
	
	//루트다시 컨텍스트 아래 소스에 써둔 sqlsession 불러오는것
	@Autowired
	private SqlSession ses;
	
	private static String NS = "com.miniproj.mapper.hboardmapper";
	
	
	// throws : 현재 메서드에서 예외가 발생하면 현재 메서드를 호출한 곳에서 예외처리를 하도록 미뤄두는 키워드
	@Override
	public List<HBoardVO> selectAllBoard(PagingInfo pi) throws Exception {
		System.out.println("여기는 HBoard DaoImpl ...................");
		
		List<HBoardVO> list = ses.selectList(NS + ".getAllHBoard", pi);
		
//		for(HBoardVO b : list) {
//			System.out.println(b.toString());
//		}
		return list;
		
		
		
		
	}


	@Override
	public int insertNewBoard(HBoardDTO newBoard) {
		
		return ses.insert(NS + ".saveNewBoard", newBoard);
	}


	@Override
	public int getMaxBoardNo() throws Exception {
		
		return ses.selectOne(NS + ".getMaxNo");
	}


	@Override
	public int insertBoardUpFile(BoardUpFilesVODTO upFile) throws Exception {
		
		return ses.insert(NS + ".saveUpFile", upFile);
	}


	@Override
	public List<BoardDetailInfo> selectBoardByBoardNo(int BoardNo) throws Exception {
		return ses.selectList(NS + ".selectBoardDetailInfoByBoardNo", BoardNo);
	}


	@Override
	public int updateReadCount(int boardNo) throws Exception {
		return ses.update(NS + ".updateReadCount", boardNo);   
	}


	@Override
	public int selectDateDiff(int boardNo, String ipAddr) throws Exception {
		
		// SqlSession temlplate의 메서드가 파라메터를 한개만 받을 수 있다.
		// 지금 넘겨줘야 할 파라메터가 2개 이상이면.. Map을 사용하여 파라메터를 매핑하여 넘겨준다.
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("readWho", ipAddr);
		params.put("boardNo", boardNo);
		return ses.selectOne(NS + ".selectBoardDateDiff", params);
	}


	@Override
	public int saveBoardReadLog(int boardNo, String ipAddr) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("readWho", ipAddr);
		params.put("boardNo", boardNo);
		return ses.insert(NS + ".saveBoardReadLog", params);
	}


	@Override
	public int updateReadWhen(int boardNo, String ipAddr) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("readWho", ipAddr);
		params.put("boardNo", boardNo);
		return ses.update(NS + ".updateBoardReadLog", params);
	}


	@Override
	public int updateBoardRef(int newBoardNo) throws Exception {

		return ses.update(NS + ".updateBoardRef", newBoardNo);
	}


	@Override
	public int insertReplyBoard(HReplyBoardDTO replyBoard) throws Exception {
		
		return ses.insert(NS + ".insertReplyBoard", replyBoard);
	}


	@Override
	public void updateRefOrder(int refOrder, int ref) throws Exception {
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("refOrder", refOrder);
		params.put("ref", ref);
		
		ses.update(NS + ".updateBoardRefOrder", params);
	}


	@Override
	public List<BoardUpFilesVODTO> selectBoardUpFiles(int boardNo) throws Exception {
		
		return ses.selectList(NS + ".selectBoardUpFiles", boardNo);
	}


	@Override
	public void deleteAllBoardUpFiles(int boardNo) throws Exception {
		ses.delete(NS + ".deleteAllBoardFiles", boardNo);
		
	}


	@Override
	public int deleteBoardByBoardNo(int boardNo) throws Exception {
		return ses.update(NS + ".deleteBoardByBoardNo", boardNo);
		
	}


	@Override
	public int updateBoardByBoardNo(HBoardDTO modifyBoard) throws Exception {
		
		return ses.update(NS + ".updateBoardByBoardNo", modifyBoard);
	}


	@Override
	public void deleteBoardUpFile(int boardUpFileNo) throws Exception {
		 ses.delete(NS + ".deleteBoardUpFileByPK", boardUpFileNo);
		
	}


	@Override
	public List<HBoardVO> selectPopBoards() throws Exception {
		return ses.selectList(NS + ".selectPopBoards");
		 
	}


	@Override
	public int getTotalPostCnt() throws Exception {
		
		return ses.selectOne(NS + ".selectTotalCount");
	}


	@Override // 지금 여기서는 오버로딩 안써도 된다 이미 오버라이딩으로 알아서 하지만 인터페이스에 있으니 오버 라이드 했다 치는거
	public int getTotalPostCnt(SearchCriteriaDTO sc) throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put("searchType",sc.getSearchType());
		params.put("searchWord","%" + sc.getSearchWord() + "%");
		
		return ses.selectOne(NS + ".selectTotalCountWithSearchCriteria", params);
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
