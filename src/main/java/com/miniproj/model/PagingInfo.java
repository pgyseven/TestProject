package com.miniproj.model;

import lombok.Getter;
import lombok.ToString;

// view 에서 페이징 처리를 하기 위한 모든정보를 담고 있는 객체
@ToString
@Getter
public class PagingInfo { // 모두 디비에서 가져온 정보만은 아니라서 VO 라고 안 붙였다
	// -------------------기본 페이징 출력 필요한 변수들 ------------------------
	private int pageNo; // 현재 페이지 번호
	private int viewPostCntPerPage; // 1페이지당 보여줄 글의 갯수
	
	private int totalPostCnt; // 전체 글(데이터)의 갯수
	
	private int totalPageCnt; //전체 페이지 수
	private int startRowIndex; //현재 페이지에서 보여주기 시잘할 글의 index 번호 / 토탈 페이지를 알아야함
	// -----------------------------
	
	// ------------------- 페이징 블럭을 만들 때 필요한 변수들 ----------------------
	private int pageCntPerBlock = 10; // 1개의 페이징 블럭에 몇 페이지를 보여줄것인가?
	private int pageBlockNoCurPage; // 현재 페이지가 속한 페이징 블럭 번호
	private int startPageNoCurBlock; //현재 페이징 블럭에서 출력 시작할 페이지 번호
	private int endPageNoCurBlock; // 현재 페이징 블럭에서 출력 끝 페이지 번호
	
	// ------------------------------------------------
	
	public PagingInfo(PagingInfoDTO dto) {
		this.pageNo = dto.getPageNo();
		this.viewPostCntPerPage = dto.getPagingSize();
		
	}
	public void setTotalPostCnt(int totalPostCnt) {
		this.totalPostCnt = totalPostCnt;
	}
	
	public void setTotalPageCnt() {
		
		//전체 페이지 수 : 전체 데이터 수 / 1페이당 보여줄 글의 개수 = > 나누어 떨어진다면 몫 , 나누어 떨어지지 않는다면 +1
		if(this.totalPostCnt % this.viewPostCntPerPage == 0) {
			this.totalPageCnt = this.totalPostCnt / this.viewPostCntPerPage;
		}else {
			this.totalPageCnt = (this.totalPostCnt / this.viewPostCntPerPage) + 1 ;
		}
	}
	public void setStartRowIndex() {
		// ( 현재페이지 번호 -1 ) * 1페이지당 보여줄 글의 갯수 => ?번 페이지에서 보여주기 시작할 글의 index 번호
		this.startRowIndex = (this.pageNo - 1) * this.viewPostCntPerPage;
	}
	
	public void setPageBlockNoCurPage() {
//		 -- 현재 페이지 번호/ 1개 페이징 블럭에서 보여줄 페이지 수
//		 -- 나누어 떨어지지 않으면 올림(+1)
//		 -- 나누어 떨어지면 그 값.
		if(this.pageNo % this.pageCntPerBlock == 0) {
			this.pageBlockNoCurPage = this.pageNo / this.pageCntPerBlock;
		}else {
			this.pageBlockNoCurPage =( this.pageNo / this.pageCntPerBlock ) + 1;
		}
	}
	
	public void setStartPageNoCurBlock() {
		//(현재 페이징 블럭 번호 -1 ) * 1개 페이징 블럭에서 보여줄 페이지 수;
		this.startPageNoCurBlock = (this.pageBlockNoCurPage - 1) * this.pageCntPerBlock + 1;
	}

	
	public void setEndPageNoCurBlock() {
		// startPageNoCurBlock + (1 페이지 블럭에서 보여줄 페이지 수 -1)
		this.endPageNoCurBlock = this.startPageNoCurBlock + (this.pageCntPerBlock - 1);
		
		//데이터가 없는 페이지가 나오지 않도록 처리
		if (this.totalPageCnt < this.endPageNoCurBlock) {
			this.endPageNoCurBlock = this.totalPageCnt;
		}
		
	}
	
}
