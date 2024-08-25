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
	
	$(document).ready(function() {

	});

	function removeFile(fileId) {
		let removeFileArr = []; //배열 안써도 괜찮음

		$('.fileCheck').each(function(i, item) {
			if ($(item).is(':checked')) { // 파일을 삭제하겠다고 체크가 되어 있다면
				let tmp = $(item).attr('id'); //선택된 파일의 id 값을 얻어옴
				removeFileArr.push(tmp); // id 값을 removeFileArr에 저장
			}
		});

		console.log("삭제될 파일  " + removeFileArr)

		$.each(removeFileArr, function(i, item) {
			$.ajax({
				url : '/hboard/modifyRemoveFileCheck',
				type : 'post',
				dataType : 'json',
				data : {
					"removeFileNo" : item
				},
				async : false,
				success : function(data) {
					console.log(data);
					if (data.msg == 'success') {
						$('#' + item).parent().parent().css('opacity', 0.2);
					}

				},
				error : function(data) {
				}

			});
		});
	}
	function removeFileCheck(fileId) {
		// alert('check' + fileId); fileCheck
		let chkCount = isCheckBoxChecked();
		if (chkCount > 0) {
			$('.removeUpFileBtn').removeAttr('disabled'); // .removeAttr('disalbed')
			$('.removeUpFileBtn').val(chkCount + "개 파일을 삭제합니다!!!!");
		} else if (chkCount == 0) {
			$('.removeUpFileBtn').attr('disabled', true);
			$('.removeUpFileBtn').val("선택된 파일 없음");
		}

	}

	function isCheckBoxChecked() {
		let result = 0;
		$('.fileCheck').each(function(i, item) {
			if ($(item).is(':checked')) {
				result++;
			}
		});
		console.log(result);
		document.getElementsByClassName('fileCheck') // 클래스가 fileCheck인 것을 데려와서 배열로 만들어줌
		return result;
	}

	function cancelRemFile() {
		$.ajax({
			url : '/hboard/cancelRemoveFile',
			type : 'post',
			dataType : 'json',
			async : false,
			success : function(data) {
				console.log(data);
				if (data.msg == 'success') {
					$('.fileCheck').each(
							function(i, item) {

								$(item).prop('checked', false); //체크가 되지 않은 상태로 바꿈
								$('#' + $(item).attr('id')).parent().parent().css('opacity', 1);

							});

					$('.removeUpFileBtn').attr('disabled', true); //파일 삭제 버튼 비활성화
					$('.removeUpFileBtn').val("선택된 파일 없음");
				}

			},
			error : function(data) {
			}

		});
	}



	function addRows(obj) {
		let rowCnt = $('.fileListTable tr').length; // fileListTable (공백):뒤 자식을 가르킴 바디에 나온 tr의 숫자만 헤아림 즉 헤드껀 제외
		console.log(rowCnt);
		let row = $(obj).parent().parent();
        let inputFileTag = `<tr><td colspan='2'><input class='form-control' type='file' id='newFile_\${rowCnt}' name='modifyNewFile'  onchange='showPreview(this);' /></td>
                     <td><input type="button" class="btn btn-info cancelRemove" value="cancel" onclick="cancelAddFile(this);"/></td></tr>`; 
							// 자바에서는 여기서 실행시키지 마라 하면 백틱안에 든 변수 표시로 알아먹는다 이클립스에서 할때는 역슬레시 반드시 / 여기서  파일 올리는 기능은 여러개 한번에 올릴 수 있는 멀티플 쓸 수도 있어서 유저들이 잘 몰라서 안쓰는 편이다. 
							// <tr><td colspan='2'><input class='form-control' type='file' id='newFile_\${rowCnt}' onchange='showPreview(this)' multiple/>  이건 아래서 .each를 사용한다.
		$(inputFileTag).insertBefore(row); // cloneRow를  row의 위로 추가

	}


	function showPreview(obj) {
		if (obj.files[0].size > 1024*1024*10){ // 10MB
			alert("10MB 이하의 이미지만 업로드할 수 있습니다.");
			obj.value = ""; //선택한 파일 초기화
			return; // 10MB 이하가 아니면 return;


		} 

		console.log(obj.files[0]);
		//파일 타입 확인
		let imageType  =  ["image/jpeg", "image/png", "image/gif"];

		let fileType = obj.files[0].type;
        
		let fileName = obj.files[0].name;
		if (imageType.indexOf(fileType) != -1 ) {//incluede를 사용해도 된다.
		// 이미지 파일 이라면
		
		let reader = new FileReader(); //자바에만 있는데 아니다 FileReader 객체 생성
        reader.onload = function(e) { 
            // reader 객체에 의해 파일을 읽기 완료하면 실행되는 콜백 함수
			let imgTag =  `<div style='padding:6px;'><img src='\${e.target.result}' width='40px' /><span>\${fileName}</span></div>`; 
			// 자바에서 실행하지 말고 자바스크립트에서 실행되라는 의미로 역슬레시
			$(imgTag).insertAfter(obj); 
        }

		reader.readAsDataURL(obj.files[0]); //업로드된 파일을 읽어온다. 여기서 읽은 애가 위에 e에 들어간다.

		}else {
			//이미지 파일이 아닐때	
		let imgTag =   `<div style='padding:6px;'><img src='/resources/images/noimage.png' width='40px' /><span>\${fileName}</span></div>`;

		$(imgTag).insertAfter(obj);
		}
        
        

		
	}


function cancelAddFile(obj) {
	let fileTag = $(obj).parent().prev().children().eq(0); 
		$(fileTag).val(''); // 선택 파일 초기화
		$(fileTag).parent().parent().remove(); 

}
</script>
<style>
.fileBtns {
	display: flex;
	justify-content: flex-end;
}

.fileBtns input {
	margin-left: 5px;
}

.btns {
	display: flex;
	justify-content: center;
	margin-top: 20px; /* 버튼과 다른 요소들 사이의 간격을 조절 */
}
</style>
</head>
<body>

	<div class="container">
		<%-- <c:import url="../header.jsp"></c:import> --%>
		<jsp:include page="../header.jsp"></jsp:include>

		<div class="content">
			<h1>게시글 수정 페이지</h1>



			<c:forEach var="board" items="${boardDetailInfo}">
				<form action="/hboard/modifyBoardSave" method="post"
					enctype="multipart/form-data">

					<div class="boardInfo">
						<div class="mb-3">
							<label for="boardNo" class="form-label">글 번호</label> <input
								type="text" class="form-control" id="boardNo" name="boardNo"
								value="${board.boardNo}" readonly>
						</div>
						<div class="mb-3">
							<label for="title" class="form-label">글 제목</label> <input
								type="text" class="form-control" id="title" name="title"
								value="${board.title}">
						</div>
						<div class="mb-3">
							<label for="writer" class="form-label">작성자</label> <input
								type="text" class="form-control" id="writer"
								value= "${sessionScope.loginMember.userId}" readonly>
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
							<textarea class="form-control" id="content" rows="5"
								name="content">
						${board.content}
						</textarea>
						</div>


					</div>



					<div class="fileList" style="padding: 15px">
						<table class="table table-hover fileListTable">
							<thead>
								<tr>
									<th>#</th>
									<th>uploadedFiles</th>
									<th>fileName</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="file" items="${board.fileList}">
									<c:if test="${file.boardUpFileNo != '0' }">
										<tr>
											<td><input class="form-check-input fileCheck"
												type="checkbox" id="${file.boardUpFileNo}"
												<%-- "${file.boardUpFileNo"여기 따옴표 안해도 돌아가더라 --%>
											onclick="removeFileCheck(this.id);" /></td>
											<td><c:choose>
													<c:when test="${file.thumbFileName != null }">
														<!-- 이미지파일 이라면 -->
														<img src="/resources/boardUpFiles/${file.newFileName }"
															width="40px" />


													</c:when>

													<c:when test="${file.newFileName == null}">
														<a href="/resources/boardUpFiles/${file.newFileName }">
															<img src="/resources/images/noimage.png" />
															${file.newFileName }
														</a>
													</c:when>
												</c:choose></td>
											<td>${file.newFileName }</td>
										</tr>


									</c:if>
								</c:forEach>
								<tr>
									<td colspan="3" style="text-align: center"><img
										src="/resources/images/add.png" onclick="addRows(this);" /></td>
									<!-- 콜스판 병함 -->


								</tr>


							</tbody>
						</table>
						<div class="fileBtns">
							<input type="button" class="btn btn-danger removeUpFileBtn"
								disabled value="선택한 파일 삭제" onclick="removeFile();" /> <input
								type="button" class="btn btn-info cancelRemove" value="파일 삭제 취소"
								onclick="cancelRemFile();" />
						</div>


					</div>


					<div class="btns">
						<button type="submit" class="btn btn-primary">저장</button>
						<button type="button" class="btn btn-info" onclick="location.href='/hboard/viewBoard?boardNo=${board.boardNo}';">취소</button>
					</div>

				</form>
			</c:forEach>
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
		<jsp:include page="../footer.jsp"></jsp:include>
		<%-- <c:import url="../footer.jsp"></c:import> --%>
	</div>
</body>
</html>
