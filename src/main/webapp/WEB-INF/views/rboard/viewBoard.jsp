<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html>
<head>
<meta charset="UTF-8">
<title>상세보기</title>
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>

<script>

let pageNo=1;

$(function(){ // 웹 문서가 로딩되면...
	getAllReplies();
	
	//모달창 닫기 버튼을 클릭하면...
	$('.modalCloseBtn').click(function(){
		$('#myModal').hide();
	});
	
});

function getAllReplies() {
	$.ajax({
		url : '/reply/all/${param.boardNo}/' + pageNo,
		type : 'get',
		dataType : 'json',
		async : false, // 여기에 대해서는 동기식으로 해라 cpu는 랜에 비해서 훨빠름 그래서 못 기다리고 다음 함수 시키지 않게
		success : function(data) {
			console.log(data);
			if (data.resultCode == 200 || data.resultMessage == "SUCCESS") {
				outputReplies(data);
			}
			

		},
		error : function(data) {
			console.log(data);
			alert("댓글을 불러오지 못했습니다.")
		}


});
	
}
function outputReplies(replies) {
	let output = `<div class="list-group">`;
	if(replies.data.replyList.length == 0){
		output += `<div class = "empty">`;
		output += `<img src= "/resources/userimg/empty.png">`;
		output += `<div class = "empty">텅~~ 댓글이 비어 있습니다.</div>`;
		output += `</div>`;
	}else{
	
	$.each(replies.data.replyList, function(i, reply) {
		output += `<a href="#" class="list-group-item list-group-item-action reply">`;

		output += `<div class='replyBody'>`; 
		
		output += `<div class='replyProfile'>`; 
		output += `<img src='/resources/userimg/\${reply.userImg}' />`; 
		
		
		
		
		output  +=`</div>`;
		
		output  +=`<div class='replyBodyArea'>`;
		output  +=`<div class='replyContent'>\${reply.content}</div>`;
		output  +=`<div class='replyInfo'>`;
		
		let betweenTime = processPostDate(reply.regDate);
		
		let rdate = new Date($(reply.regDate)[0]);

		let month = '';
		if (rdate.getMonth() + 1 < 10) {
			month = '0' + (rdate.getMonth() + 1);
		} else {
			month = rdate.getMonth() + 1;
		}

		rdate = rdate.getFullYear() + "-" + month + "-"+ rdate.getDate();
		
		
		
		output  +=`<div class='regDate'>\${betweenTime}</div>`;
		output  +=`<div class='replyer' onmouseover='showReplyInfo(this);' onmouseout='hideReplyInfo(this);'>`;
		output  +=`\${reply.replyer}</div>`;
		output  += `<div class = 'replyerInfo'>\${reply.userName}(\${reply.email})</div>`;
		output  +=`</div>`;
		
		output  +=`</div>`;
		output  +=`</div>`;
		output  +=`</a>`;
	
	});
	
	outputPagination(replies);
	
}
	
	  output += `</div>`;
	
	
	
	$(".replyList").html(output);
}


function showReplyInfo(obj){
	$(obj).next().show();
}
function hideReplyInfo(obj){
	$(obj).next().hide();
}


// 댓글 작성일시 방금전, 몇분전, 몇시간전...의 형식으로 출력
function processPostDate(writtenDate){

	const postDate = new Date(writtenDate); // 댓글 작성시간
	const now = new Date(); // 현재시간

	let diff = (now-postDate) / 1000; // 시간 차 (초단위)
	
	const times =[
		{name:"일", time : 60*60*24},
		{name:"시간", time : 60*60},
		{name:"분", time : 60}
		];
	
	for (let val of times) {
		let betweenTime = Math.floor(diff / val.time);
		console.log(diff, betweenTime);
		if (betweenTime > 0 && val.name != "일" ) { // 하루보다 크지 않다면...
			return betweenTime + val.name + "전";
		} else if (betweenTime > 0 && val.name == "일") { // 하루보다 큰 값이라면 그냥 작성일 출력
			return postDate.toLocaleDateString();
		}
		
	}
	return "방금전";
}


function outputPagination(replies){
	// 페이징
	let pagingInfo = replies.data.pagingInfo;

	let paging = $('.replyPagnation');

	let pagingHtml = `<ul class="pagination justify-content-center" style="margin: 20px 0">`;

	if (pagingInfo.pageNo > 1) {
		pagingHtml += '<li class="page-item"><a class="page-link" href="javascript:pagination('+ (pagingInfo.pageNo - 1) + ')" >Previous</a></li>';
	}
	for (let i = pagingInfo.startPageNoCurBlock; i <= pagingInfo.endPageNoCurBlock; i++) {
		if (pagingInfo.pageNo == i) {
			pagingHtml += '<li class="page-item active" id="'+i+'"><a class="page-link" href="javascript:pagination('+ pagingInfo.pageNo + ')"">' + i + '</a></li>';
		} else {
			pagingHtml += '<li class="page-item" id="'+i+'"><a class="page-link" href="javascript:pagination('+ i + ')">' + i + '</a></li>';
		}
	}
	if (pagingInfo.pageNo < pagingInfo.totalPageCnt) {
		pagingHtml += '<li class="page-item"><a class="page-link" href="javascript:pagination('+ (pagingInfo.pageNo + 1) + ')">Next</a></li>';
	}

	pagingHtml += "</ul>";
	
	paging.html(pagingHtml);
}




function showRemoveModal() {
	let boardNo = $('#boardNo').val();
	$('.modal-body').html(boardNo + '글을 삭제 할까요?')
	$('#myModal').show(500); /* 밀리세컨드 단위고 0.5초 속도로 천천히 보여줌 */
}



</script>
<style>
.content {
 margin-top: 10px;
 margin-bottom: 10px;
 padding:  10px;
 border: 1px solid #dee2e6;
 border-radius: 0.375rem;
}
.replyList{
 margin-top: 15px;
 padding: 10px;

 
}
.replyBody {
display: flex;
justify-content: space-between;
flex-direction: row;
align-items: center;
font-size: 0.8rem;
 color: rgba(0,0,0, 0.8);

}
.replyerProfile img{
width: 50px;
border-radius: 25px;
 border: 1px solid lightgray;

}
.replyBodyArea{
 flex:1; /* 비중 비율 */
 margin-left: 20px;
}
.replyInfo{
display: flex;
flex-direction: row;
justify-content: space-between;
font-size: 0.6rem;
 color: rgba(0,0,0, 0.4);
}
.replyerInfo{
display: none;
color: white;
background-color: #333;
padding: 5px;
/* width: 40; */
border-radius: 4px;

}
</style>
</head>
<body>

	<div class="container">
		<c:import url="../header.jsp"></c:import>

		<div class="content">
			<h1>게시글 상세 페이지</h1>





			<c:if test="${board.isDelete == 'Y'}">
				<c:redirect url="/hboard/listAll?status=wrongAccess" />
			</c:if>


			<div class="boardInfo">
				<div class="mb-3">
					<label for="boardNo" class="form-label">글 번호</label> <input
						type="text" class="form-control" id="boardNo"
						value="${board.boardNo}" readonly>
				</div>
				<div class="mb-3">
					<label for="title" class="form-label">글 제목</label> <input
						type="text" class="form-control" id="title" value="${board.title}"
						readonly>
				</div>
				<div class="mb-3">
					<label for="writer" class="form-label">작성자</label> <input
						type="text" class="form-control" id="writer"
						value="${board.writer}(${board.email})" readonly>
				</div>

				<div class="mb-3">
					<label for="writer" class="form-label">작성일</label> <input
						type="text" class="form-control" id="postDate"
						value="${board.postDate}" readonly>
				</div>

				<div class="mb-3">
					<label for="writer" class="form-label">조회수</label> <input
						type="text" class="form-control" id="readCount"
						value="${board.readCount}" readonly>
				</div>
				<!-- readonly는 수정 불가 -->


				<div class="mb-3">
					<label for="content" class="form-label">내용</label>
					<div class="form-control" id="content" rows="5" readonly>
						${board.content}</div>
				</div>


			</div>





			<div calss="btns">
				<button type="button" class="btn btn-primary"
					onclick="location.href='/rboard/modifyBoard?boardNo=${board.boardNo}';">글
					수정</button>


				<button type="button" class="btn btn-info"
					onclick="location.href='/rboard/listAll';">리스트페이지로</button>
			</div>


			<div class="replyList"></div>
				
			
		</div>

		<!-- The Modal -->
		<div class="modal" id="myModal" style="display: none;">
			<div class="modal-dialog">
				<div class="modal-content">

					<!-- Modal Header -->
					<div class="modal-header">
						<h4 class="modal-title">MiniProject</h4>
						<button type="button" class="btn-close modalCloseBtn"
							data-bs-dismiss="modal"></button>
					</div>

					<!-- Modal body -->
					<div class="modal-body"></div>

					<!-- Modal footer -->
					<div class="modal-footer">
						<button type="button" class="btn btn-info"
							onclick="location.href='/hboard/removeBoard?boardNo=${param.boardNo}';">삭제</button>
						<button type="button" class="btn btn-danger modalCloseBtn"
							data-bs-dismiss="modal">취소</button>
					</div>

				</div>
			</div>
		</div>

		<c:import url="../footer.jsp"></c:import>
	</div>
</body>
</html>
