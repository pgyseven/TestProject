<%@ page language="java" contentType="text/html; charset=UTF-8"
   pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html>
<head>
<meta charset="UTF-8">
<title>INDEX</title>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
<script>

   function checkCookie() {
      $.ajax({
           url : '/readCookie',             
           type : 'GET',                                        
           dataType : 'json',                                 
           success : function (data) {                       
           console.log(data);

           if (data.msg == 'fail') {
              $('#myModal').show();
         }
          }
        });
   }

   function getTop5Board() {
      $.ajax({
           url : '/get5Boards',             
           type : 'GET',                                        
           dataType : 'json',                                 
           success : function (data) {                       
           console.log(data);
         outputPopBoards(data);
          }
        });
   }

   function outputPopBoards(data) {
      let output = '<table class="table table-hover popBoards">';
      
      $.each(data, function(i, e){
         output += "<tr>";
         output += "<td><a href='/hboard/viewBoard?boardNo=" + e.boardNo + "'>";
         output += `\${e.title}</a></td>`;
         let postDate = new Date(e.postDate).toLocaleDateString();
         output += `<td>\${postDate}</td>`;
         output += '</tr>';
      });

      output += '</table>';

      $('.top5Board').html(output);
   }

   $(function() { // 웹 문서가 로딩되면..

      checkCookie(); // 쿠키를 읽어봐서 쿠키가 없다면 모달창을 띄운다.

      getTop5Board();
   
      // 클래스가 modalCloseBtn인 태그를 클릭하면 실행되는 함수
         $('.modalCloseBtn').click(function(){
             // 유저가 체크박스에 체크를 했는지 검사
          if($('#ch_agree').is(':checked')) {
            // 쿠키 저장
            $.ajax({
                  url : '/saveCookie',             
                  type : 'GET',                                        
                  dataType : 'text',                                 
                  success : function (data) {                       
                  console.log(data);
                  }
               });
                
          } else {
            alert('쿠키 저장 안함');
          }

            $("#myModal").hide(); // 태그를 화면에서 감춤
         });
   }); 
</script>
<style>
   .modalFooter{
      padding : 1rem;
      display : flex;
      justify-content : space-between;
      justify-items : center;
   }
   .popBoards a{
      text-decoration: none; /* 하이퍼 밑줄 제거 */


   }

   .popBoards a:any-link {
      color : black;

   }
</style>
</head>
<body>
   <div class="container">  <!-- 여백에 조정 될거임 -- header 좌우 여백 생김 --> 
      <%-- <c:import url="./header.jsp" /> <!-- 경로에 있는 파일을 가져와서 넣어라 그리고 상대적 경로니깐 같은 위치에 있는 헤더 가져오는거라 --> --%>
		<jsp:include page="./header.jsp"></jsp:include>
		
      <div class="content">
         <h1>귀멸의 칼날의 홈</h1>
         <div>
            <img src="data:image/png;base64,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" /></div>
</div>

         <div class = "top5Board"></div>
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
               <div class="modal-body">
                  <div>빠른 시일 안에 개편을 완료하도록 하겠습니다.</div>
                     <dir>블라 블라</dir>
               </div>
               <!-- Modal footer -->
               <div class="modalFooter">
                  <span><input class="form-check-input" type="checkbox" id="ch_agree" />하루동안 열지 않기</span>
                  <button type="button" class="btn btn-danger modalCloseBtn"
                     data-bs-dismiss="modal">Close</button>
               </div>

            </div>
         </div>
      </div>

      <%-- <c:import url="./footer.jsp" /> <!--  ./ 현재 경로의  --> --%>
      <jsp:include page="./footer.jsp"></jsp:include>
   </div>
</body>
</html>
