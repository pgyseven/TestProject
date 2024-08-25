package com.miniproj.service.hboard;

import java.util.List;
import java.util.Map;

import com.miniproj.model.BoardDetailInfo;
import com.miniproj.model.BoardUpFilesVODTO;
import com.miniproj.model.HBoardDTO;
import com.miniproj.model.HBoardVO;
import com.miniproj.model.HReplyBoardDTO;
import com.miniproj.model.PagingInfoDTO;
import com.miniproj.model.SearchCriteriaDTO;

public interface HBoardService   //클래스와 동급이다 클래스를 디자인하기 위해서 먼저 만드는것 중괄호 없는 추상메서드만 만들수 있음 추상클래스와 인터페이스도 상속 가능 
{ // 추상 형체가 없다 이것도 저것도 될 수 있다. 자바의 부모는 object다 이것도 형체가 없다.  여기를 상속 받아서 bean grahp 처럼 구현 부모가 뭐냐에 따라 ! 부모가 동물이냐 식물이냐에 따라 자식이 결정되듯
   // 게시판 전체 리스트 조회
   public Map<String, Object> getAllBoard(PagingInfoDTO dto, SearchCriteriaDTO searchCriteria) throws Exception;
   
   // 게시판 글 작성 
   boolean saveBoard(HBoardDTO newBoard) throws Exception;
   
   // 게시판 상세 보기
   public List<BoardDetailInfo> read(int boardNo, String ipAddr) throws Exception;
   
   /**
    * @작성자 : 802-02
    * @작성일 : 2024. 8. 8.
    * @메소드명 : read
    * @parameter : boardNo - 조회하고 싶은 글의 글번호
    * @returType : List<BoardDetailInfo> - 글과 첨부파일, 글의 작성자 정보를 함께 불러온다.
    * @throwsException : dao단 다녀오는거라 예외 생길 수 있음
    */
   //read(int boardNo, String ipAddr) 오버로딩 했다
   public List<BoardDetailInfo> read(int boardNo) throws Exception; // 게시글 수정을 위해 게시글을 불러오는 메서드

   
   // 게시글 답글 달기
   public boolean saveReply(HReplyBoardDTO replyBoard) throws Exception;
   
   // 게시판 글 삭제
   public List<BoardUpFilesVODTO> removeBoard(int boardNo) throws Exception;
   
   // 게시판 글 수정
   public boolean modifyBoard(HBoardDTO modifyBoard) throws Exception;
 
   
   //인기글 5개 가져오기
   public List<HBoardVO> getPopularBoards() throws Exception;

   // 게시판 페이징
   
   // 게시글 검색
   
   
  
   
   
 
}
