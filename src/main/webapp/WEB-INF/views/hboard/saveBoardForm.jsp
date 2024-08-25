<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
	rel="stylesheet">
<script
	src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<script>
	let upfiles = new Array(); // 업로드 드되는 파일들을 저장하는 배열 /[]; 이것도 빈 배열 생성하는 것. 자바에서는 크기가 고정된 정적 배열이라 쓸모가 크게 없다 그러나 자바스크립트는 크기가 가변이다 즉 지금 () 안에 크기 안넣으면 사이즈가 무한데이다. 

	$(function() {
		// 업로드 파일 영역에 drag$drop 과 관련된 이벤트 (파일의 경우 파일이 웹브라우저에서 실행되는 등)를 방지 해야한다. -> 이벤트 캔슬링
			$('.fileUploadArea').on("dragenter dragover", function(evt) {
				//파일을 올리기만 하면 오버고 파일을 드라그해서 두면 그게 엔터
			evt.preventDefault(); // 기본 이벤트 캔슬
			});
		
		// 유저가 fileUploadArea에 파일을 드래그&드랍하면...
		$('.fileUploadArea').on("drop", function(evt) {
			evt.preventDefault();
			// console.log(evt.originalEvent.dataTransfer.files); //업로드 되는 파일 객체의 정보	
			for (let file of evt.originalEvent.dataTransfer.files) { //읽기 전용 속성임 즉 세터가 없다.
				
				
				// 파일 사이즈 검사 하여 10MB 가 넘게되면 파일 업로드가 안되도록...
				if (file.size > 10485760){
					// 알림창은 alert는 쓰는게 사실 아니다 유저가 불편한게 있어서
					alert("파일 용량이 너무 큽니다! 업로드한 파일을 확인해 주세요.") //원래는 이거 모달로 해야한다.
					
				} else {
					upfiles.push(file); // 배열에 담기
					console.log(upfiles);
					
					
					//해당 파일 업로드
					fileUpload(file)
					
				
				}
				
				
				
				
			}
		});
	});
	
	//실제로 유저가 업로드한 파일을 컨트롤러단에 전송하여 저장하도록 하는 함수
	//
	function fileUpload(file) {
		let result = false;
		let fd = new FormData() //FormData 객체를 생성 : 폼태그와 같은 역할의 객체
		fd.append("file", file); // 폼데이터 안에 키와 밸류 형태로 넣어준다.
		
		/* 비동기 방식과 동기 방식
		동기방식은 카페에서 각각 주문을 받아서 한번에 두개를 처리 못하니깐 잠깐 기다려 달라고 다음 사람한테 한다. 이게 동기 방식이다.
		아이스아메리카노 가져가세요 한다. 그런데도 가지갈때까지 기다린다 이게 동기식
		비동기식은 가지가던 말던 테이블에 둔다. 이게 비동기식임
		자기꺼 아닌데 누가 잘못 가져가면 다시 만들어줌
		일단 접수 쭉 받고 받던 안받던 만들어줌 
		자바스크립트는 함수를 비동기로 실행한다. 빠르게 하기 위해서 */
		
		
		$.ajax({
	         url : '/hboard/upfiles',             // 데이터가 송수신될 서버의 주소
	         type : 'post',             // 통신 방식 : GET, POST, PUT, DELETE, PATCH 대소문자 상관없음  
	         dataType : 'json',         // 수신 받을 데이터의 타입 (text, xml, json)
	    	 data : fd,					// 보낼 데이터
	         // processData : false -> 데이터를 쿼리 스트링 형태로 보내지 안겠다는 설정
	         // contentType 의 디폴트 값이 "application/x-www-form-urlencoded"(get과 같다 url에 붙여서 보내는것)인데 그렇기에 프로세스도 같이 폴스로 설정, 파일을 전송하는 방식이기에 " multipart/form-data로 되어야 하므로
	         processData : false,
	         contentType : false,
	         async : false, // 비동기 통신 : false 응답이 오기를 기다렸다가 아래꺼 실행 비동기는 응답이 없어도 아래꺼를 실행 할 수도 있어서!
	         success : function (data) {     // 비동기 통신에 성공하면 자동으로 호출될 callback function
	             
	        	 console.log(data);
	         
	         	if(data.msg ==  'success'){
	         		
	         		// 미리 보기
					showPreview(file, data.newFileName);
	         	}
	         }, error : function (data){
	        	 console.log(data);
	        	 if(data == 'fail') {
	        		 alert('파일을 업로드 하지 못 했습니다.');
	        		 
	        		 for(let i = 0; i < upfiles.length; i++)  {
	        			 
	        			 if(upfiles[i].name == file.name) {
	        				 
	        				 upfiles.splice(i, 1); //배열에서 삭제
	        			 }
	        		 }
	        	 }
	         }
	            
				//여러개의 데이터는 아작스가 편하다. 폼태그 형식도 가능은 하지만....
	         

	      });
		
	}

	// 넘겨진 file 이 이미지 파일이라면 미리보기 하여 출력한다.
	function showPreview(file, newFileName) {
		let imageType = ["image/jpeg", "image/png", "image/gif"];  //콘솔에 뜨는 파일의 타입의 형태 아래 콘솔로 찍어보면 볼 수 있는 타입이 뜬다. 서버탭에 웹.xml dp mime-type을 보면된다.
		console.log(file);
		let fileType = file.type.toLowerCase(); // 대문자가 있을 수 있으니 소문자로 바꾸라는 함수 투로이어 케이스
		if(imageType.indexOf(fileType) != -1){
			// 이미지 파일이라면...
			
			// 업로드 했던 이미지를 reader객체로 읽어와 출력 합시다. 내일~~~
			
			
			
		     let output=`<div><img src='/resources/boardUpFiles\${newFileName}' /><span>\${file.name}</span>`;
	          output += `<span><img src='/resources/images/remove.png' width='20px' onclick="remFile(this);" id="\${newFileName}"></span></div>`;
	          $('.preview').append(output);

			
			
			
		}else{
			//이미지 파일이 아니다...
			
			let output = `<div><img src='/resources/images/noimage.png' /><span>\${file.name}</span>`;
			
			output += `<span><img src='/resources/images/remove.png' width='20px' onclick="remFile(this);" id="\${newFileName}" /></span></div>`; //디스는 현재의 파일을 나타내니깐 해당 객체 즉 그 해당하는 이미지의 객체가 됌
			$('.preview').append(output); //어펜드 끝에다가 추가
		}
	}
	// 업로드한 파일을 지운다. (화면, front배열, 백엔드)
	function remFile(obj) {
		let removedFileName = $(obj).attr('id'); //attr 어트리뷰트 속성을 까보는것  / obj가 위에서 this 니깐img 태크를 보낸것, 콘솔에서 경로를 보면 된다. 
		console.log('지워야 할 파일 이름 : ' + removedFileName)
		for(let i = 0; i < upfiles.length; i++) { 
			if(upfiles[i].name == $(obj).parent().prev().html()) {
				// 파일 삭제(백엔드 단에서도 삭제 해야함)
				// 파일 삭제(백엔드 단에서 삭제가 성공하면 fornt 단에서도 배여르, 화면에서 삭제 해야함 )
						$.ajax({
	         url : '/hboard/removefile',             // 데이터가 송수신될 서버의 주소
	         type : 'post',             // 통신 방식 : GET, POST, PUT, DELETE, PATCH 대소문자 상관없음  
	         
	         dataType : 'json',         // 수신 받을 데이터의 타입 (text, xml, json)
	         data : {					//보낼데이터
	        	 "removedFileName" : removedFileName
	         },
						async : false, // 비동기 통신 : false 응답이 오기를 기다렸다가 아래꺼 실행 비동기는 응답이 없어도 아래꺼를 실행 할 수도 있어서!
	         success : function (data) {     // 비동기 통신에 성공하면 자동으로 호출될 callback function
	             
	        	 console.log(data);
	         	if (data.msg == 'success'){
	         		upfiles.splice(i, 1); //배열에서 삭제 i번째에서 1개 삭제
					console.log(upfiles);
					$(obj).parent().parent().remove(); //태그 삭제 / 화면에서 삭제된거
	         	}
	        
	         }

	      });
				
				
				
				
			}
		}
		
	}
	
	function validBoard() { //$function은 로드가 다되고 실행되는데 여기서 사용을 안하는 이유는 페이지가 다 뜨고 클릭을 해야 함수가 작동하기에 $function이 필요 없다.
		
		console.log("validBoard에 들어옴!!!!!!!!!!!!!!!!!!!!!!!!!");
		let result = false;
		let title = $('#title').val();
		console.log(title);

		if (title == '' || title.length < 1 || title == null) { // || or
			// 제목을 입력하지 않았을때
			alert("제목은 반드시 입력하셔야 합니다.");
			$('#title').focus();
		} else {

			// 제목을 입력했을 때
			result = true;
		}

		// 유효성 검사 하는 자바스크립트에서는 마지막에 boolean 타입의 값을 반환하여
		// 데이터가 백엔드 단으로 넘어갈지 말지늘 결정해줘야 한다.
		return result;
	}
	 // 유저가 취소 버튼을 클릭하면 서버에 저장한 모든 업로드된 파일을 지우고, 게시판 전체 조회 페이지로 돌아간다.
    function cancelBoard() {
       
       $.ajax({
           url : '/hboard/cancelBoard',
           type : 'GET',
           dataType : 'json',
           async : false, 
           success : function (data) {
              console.log(data); 
              if (data.msg == 'success') {
                 location.href='/hboard/listAll';
              }
           }
        });   
    }
</script>
<style>
.fileUploadArea {
	width: 100%;
	height: 300px;
	background-color: lightgray;
	text-align: center;
	line-height: 300px; /* 팁인데 이렇게 하면 위에 하이트랑 맞추면 세로에서 중앙정렬된다 */
}
</style>
</head>
<body>
	<div class="container">
		<c:import url="./../header.jsp"></c:import>
		<h2>게시글 작성</h2>
		<!-- multipart form-data : 데이터를 여러 조각으로 나누어서 전송하는 방식. 수신되는 곳에서는 재조립이 필요하다. -->
		<form action="saveBoard" method="post">
			<!-- 패킷이란 단위로 데이터가 전송이됨 인터넷 선이 하나인데 대용량 업로드 중인데 하나만 하면 다른사람하고 통신을 못함 그래서 그걸 잘게 끊어서 날린다 그게 패킷단위고 이게 하나에 64kb 이다. 데이터가 이동할때 잘게 순서없이 보내지는데 그게 나중에 합쳐져야함 파일을 저장할땐 그래서 이속성을 꼭 써야함  4000바이트는 4kb /기존에 post였음 데이터 양이 많으니 포스트 방식으로 보낸다 만약 겟방식이면 url에 쿼리 스트링 형식으로 보내진다. 그러면 내용때문에 url의 길이 제한 때문에 문제 생긴다 정확히 2083자까지만 가능하다. -->
			<div class="mb-3">
				<label for="title" class="form-label">글제목</label> <input type="text"
					class="form-control" id="title" name="title"
					placeholder="글제목을 입력하세요">
			</div>
			<div class="mb-3">
				<label for="author" class="form-label">작성자</label> <input
					type="text" class="form-control" id="writer" name="writer" value= "${sessionScope.loginMember.userId}" readOnly
					placeholder="작성자를 입력하세요">
			</div>
			<div class="mb-3">
				<label for="content" class="form-label">내용</label>
				<textarea class="form-control" id="content" name="content" rows="5"
					placeholder="내용을 입력하세요"></textarea>
			</div>

			<div class="fileUploadArea mb-3">
				<!-- mb-3 가로가 꽉 찰거다 -->
				<p>업로드할 파일을 이곳에...</p>
			</div>
			<div class="preview"></div>
			
			<button type="submit" class="btn btn-primary"   
				onclick="return validBoard();">저장</button>  <!-- 서밋은 폼태그 날아가는데 폼은 인풋타입의 네임을 가지고 간다. 인풋!!! -->
			<!-- 이벤트 캔슬링과 같다 리턴해오는 값에 따라서 서밋을 할지 말지!!!return validBoard()뒤에서 가저온 함수를 리턴으로 보낸다 위에서 return 값을 false로하면 결국 false 가 가니깐 즉 서밋하면 false가 가서 너 그기능 하지마! 이게 되는 거임 true 면 할 행동을 하게됨 -->
		<button type="button" class="btn btn-warning" onclick="cancelBoard();">취소</button>  <!-- 버튼 타입 리셋으로 하면 새로고침만 되는거라 서버나 파일에 담아뒀던게 삭제 안된다. 이럴때는 일반 버튼으로 해라 -->
		</form>

		<c:import url="./../footer.jsp"></c:import>
	</div>

</body>
</html>