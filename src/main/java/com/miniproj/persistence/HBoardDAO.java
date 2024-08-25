package com.miniproj.persistence;

import java.util.List;

import com.miniproj.model.BoardDetailInfo;
import com.miniproj.model.BoardUpFilesVODTO;
import com.miniproj.model.HBoardDTO;
import com.miniproj.model.HBoardVO;
import com.miniproj.model.HReplyBoardDTO;
import com.miniproj.model.PagingInfo;
import com.miniproj.model.SearchCriteriaDTO;

public interface HBoardDAO  {
	
	//게시판의 전체 리스트를 가져오는 메서드
	List<HBoardVO>selectAllBoard(PagingInfo pi) throws Exception; //여기 퍼블릭 안쓴 이유 원래는 public abstrat 앱스트릭트는 생략되어도 그렇게 된다 없으면 퍼블릭이란것
    // 몸체가 없는 추상 메서드
	
	//게시글을 저장하는 메소드
	int insertNewBoard(HBoardDTO newBoard) throws Exception;
	
	//최근 저장된 글의 글번호를 얻어오는 메서드
	int getMaxBoardNo() throws Exception;
	
	// 업로드된 첨부파일을 저장하는 메서드
	int insertBoardUpFile(BoardUpFilesVODTO upFile) throws Exception;

	//BoardUpFilesMemberJoinVO selectBoardDetailInfoByBoardNo(int boardNo); //  이건 설명위해서 추가 했던건 첨부파일 중복으로 인해서 로우가 여러줄인 한개의 게시긆 정보를 불러올때 처리 방법 이걸로는 안된다는 아이러니 설명 위해서 추가 BoardUpFilesMemberJoinVO 이미 이안에서 첨부 파일은 리스트로 받아오기에 여기서 리스트로 할 필요는 없다.


	//게시글 상세정보를 읽어오는 메서드
	List<BoardDetailInfo> selectBoardByBoardNo(int BoardNo) throws Exception;

	//게시글의 조회수를 증가하는 메서드
	int updateReadCount(int boardNo) throws Exception;

	// ipAddr의 유저가 boardNo글을 언제 조회했는지 날짜 차이를 얻어온다.(조회한 적이 없다면 -1 반환)
	int selectDateDiff(int boardNo, String ipAddr) throws Exception;
	
	//ipAddr의 유저가 boardNo 글을 현재 시간에 조회한다고 기록
	int saveBoardReadLog(int boardNo, String ipAddr) throws Exception;

	//조회수 증가한 날짜로 update  
	int updateReadWhen(int boardNo, String ipAddr) throws Exception;

	//글 번호를 ref 컬럼에 update
	int updateBoardRef(int newBoardNo) throws Exception;
	
	//답글 데이터와 ref, step, refOrder 값을 저장
	int insertReplyBoard(HReplyBoardDTO replyBoard) throws Exception;

	// 자리 확보를 위해 기존 답글의 refOrder 값을 수정하는 메서드
	void updateRefOrder(int refOrder, int ref) throws Exception;
	
	// 게시글의 첨부 파일의 정보를 얻어오는 메서드
	List<BoardUpFilesVODTO> selectBoardUpFiles(int boardNo) throws Exception;
	
	
	// boardNo번 글의 첨부파일들을 모두 삭제하는 메서드
	void deleteAllBoardUpFiles(int boardNo) throws Exception;
	
	//boardNo 번 게시글의 삭제 처리 하는 메서드
	int deleteBoardByBoardNo(int boardNo) throws Exception;
	
	// 게시글 자체를 수정하는 메서드 
	int updateBoardByBoardNo(HBoardDTO modifyBoard) throws Exception;

	// boardUpfileNo번 첨부파일을 삭제하는 메서드
	void deleteBoardUpFile(int boardUpFileNo) throws Exception;
	
	// 인기글 5개 가져오기
	List<HBoardVO> selectPopBoards() throws Exception;
	
	
	// 게시판의 전체 글 수를 얻어오는 메서드(검색어가 없을 때)
	int getTotalPostCnt() throws Exception;
	
	//게시판의 전체 글 수를 얻어오는 메서드(검색어가 있을 때) 오버로드
	int getTotalPostCnt(SearchCriteriaDTO sc) throws Exception;
	
	// 검색어가 있는 경우 검색된 글을 페이징 하여 가져오는 메서드
	List<HBoardVO> selectAllBoard(PagingInfo pi, SearchCriteriaDTO searchCriteria) throws Exception;
}
