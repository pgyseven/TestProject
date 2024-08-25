<%@ page language="java" contentType="text/html; charset=UTF-8"
   pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<script
   src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
   <script src="/resources/js/timer.js"></script>
<title>회원 가입 페이지</title>
<script>




   function outputError(msg, obj) {
        let errorTag = `<div class='error'>\${msg}</div>`;
      $(errorTag).insertAfter(obj);
      $(obj).css('border', '2px solid red'); // 에러가 난 태그의 선색상을 빨간색으로
    }
   
   // obj 다음 이웃 태그(에러메시지 div)를 지운다
   function clearError(obj) {
      $('.error').remove();
      $(obj).css('border', ''); // css를 원래 상태로
   }

   $(function(){
      //
      $('#userEmail').focus(function(){
         if ($('#emailValid').val() == 'checked'){
         return;

         }

      });

      // 이메일 주소 입력을 완료하고  blur 되었을 때
      $('#userEmail').blur(function(){
         emailValid();

      });


      
      // 패스워드1을 입력하고 blur 되었을때
      $('#userPwd1').blur(function (){
         let tmpPwd = $('#userPwd1').val();
         
         if (tmpPwd.length < 4 || tmpPwd.length > 8) {
            outputError('패스워드는 4~8자로 입력하세요.', $('#userPwd1'));
            $('#pwdValid').val('');
            $(this).val('');
         } else {
            setTimeout(()=> {
               $('.error').remove();
            }, 500);  // 0.5초 후에 에러메시지 사라짐
            $('#userPwd1').css('border', '');  // css 원상태로
         }
      });
      
      // 패스워드 확인을 입력하고 blur 되었을때
      $('#userPwd2').blur(function(){
         let tmpPwd1 = $('#userPwd1').val();
         if (tmpPwd1 != $(this).val()) {
            outputError('패스워드 다릅니다.', $('#userPwd1'));
            $('#userPwd1').val('');
            $(this).val('');
            $('#pwdValid').val('');
         } else {
            clearError($('#userPwd1'));
            $('#pwdValid').val('checked');
         }
      });
      
      
      // 아이디에 키보드가 눌려졌을때 발생하는 이벤트
      $('#userId').keyup(function(evt){
         let tmpUserId = $('#userId').val();
         if (tmpUserId.length < 4 || tmpUserId.length > 8) {
            outputError('아이디는 4~8자로 입력하세요.', $('#userId'));
            setTimeout(()=> {
               $('.error').remove();
            }, 500);
            $('#idValid').val('');
         } else {
            $.ajax({
               url : '/member/isDuplicate',             // 데이터가 송수신될 서버의 주소
               type : 'post',             // 통신 방식 : GET, POST, PUT, DELETE, PATCH   
               dataType : 'json',         // 수신 받을 데이터의 타입 (text, xml, json)
               data : {
                  "tmpUserId" : tmpUserId
               },
               success : function (data) {     // 비동기 통신에 성공하면 자동으로 호출될 callback function
                  console.log(data);
                  if (data.msg == 'duplicate') {
                     outputError('중복된 아이디입니다.', $('#userId'));
                     $('#idValid').val('');
                     $('#userId').focus();
                  }else if (data.msg == 'not duplicate') {
                     clearError($('#userId')); // error 메시지 클리어
                     $('#idValid').val('checked');
                  }
                  
               }, error : function (data) {
                  console.log(data);
               }

            });
         }
      });
   });

   function isValid() {
       // 아래의 조건에 만족할 때 회원가입이 진행 되도록(return true), 만족하지 않으면 회원가입이 되지 않도록 (return false)
       // 1) 아이디 : 필수이고, 4~8자, 아이디는 중복된 아이디가 없어야 함
       // 2) 비밀번호 : 필수이고, 4~8자, 비밀번호확인과 동일해야 한다.

       let idCheck = idValid();
       let pwdCheck = pwdValid();
       let genderCheck = genderValid();
       let emailCheck = emailValid();
       let mobileCheck = mobileValid();
       let imgCheck = imgValid();
       
       // 가입자 동의
       let agreeCheck = $('#agree').is(':checked');

       if (idCheck && pwdCheck && genderCheck && emailCheck && mobileCheck && imgCheck && agreeCheck) {
         return true;
       } else {
         return false;
       }
     }
     
     function imgValid() {
        let result = false;
        let userImg = $('#userImg').val();
        if ($('#imgCheck').val() == 'checked' || userImg == '' || userImg == null ) {
           result = true;
        }
        
        return result;
     }

     function mobileValid() {
       let result = false;
       let tmpUserMobile = $("#mobile").val();
       let mobileRegExp = /^(01[016789]{1})-?[0-9]{3,4}-?[0-9]{4}$/;
       if (!mobileRegExp.test(tmpUserMobile)) {
         outputError("휴대폰 번호 형식이 아입니다!", $("#mobile"));
       } else {
         clearError($("#mobile"));
         result = true;
       }

       return result;
     }

     function emailValid() {
       // 1) 이메일 주소 형식이면..(정규 표현식을 이용한다)
       // 2) 이메일 주소 형식이면..인증문자를 이메일로 보내고, 인증문자를 다시 입력받아 검증
       let result = false;

       let tmpUserEmail = $("#userEmail").val();
       let emailRegExp = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/i;
       if (!emailRegExp.test(tmpUserEmail)) {
         outputError("이메일 주소 형식이 아닙니다!", $("#userEmail"));
       } else {
         // 이메일 주소 형식이다...
         // 유저가 입력한 이메일 주소로 인증 코드 발송(back end) - timer(3분)
         // 인증코드를 유저에게 입력 받음
         // 유저가 입력한 인증코드와 백엔드에서 만든 인증코드가 같은지 비교
         // 같고, 인증시간 안에 인증 완료 통과...

         if ($('#emailValid').val() == 'checked') {
           result = true;
         } else {
           showAuthenticateDiv();  // 인증 코드를 입력하는 div창을 보여주기
           callSendMail();// 이메일 발송 하고
           startTimer(); // 타이머 동작 시키기..
           clearError($("#userEmail"));
         
           
         }
       }
       return result;
     }

     function callSendMail() {
       $.ajax({
             url: "/member/callSendMail", // 데이터가 송수신될 서버의 주소
             type: "post", // 통신 방식 : GET, POST, PUT, DELETE, PATCH
             dataType: "text", // 수신 받을 데이터의 타입 (text, xml, json)
             data: {
               "tmpUserEmail" : $("#userEmail").val()
             },
             success: function (data) {
               // 비동기 통신에 성공하면 자동으로 호출될 callback function
               console.log(data);
               if (data == 'success') {
                 alert("이메일로 인증코드를 발송했습니다..");
                 $('#userAuthCode').focus();
               }
             },
             error: function (data) {
               console.log(data);
             },
           });
     }

     function showAuthenticateDiv() {
       alert("이메일로 인증코드를 발송했습니다!\n 인증코드를 입력해주세요~");
       $('#userAuthCode').focus();
       let authDiv = "<div id='authenticateDiv'>";
       authDiv += `<input type="text" class="form-control" id="userAuthCode" placeholder="인증코드입력..." />`;
       authDiv += `<span class='timer'>3:00</span>`;
       authDiv += `<button type="button" id="authBtn" class="btn btn-primary" onclick="checkAuthCode()">인증</button>`;
       authDiv += "</div>";

       $(authDiv).insertAfter($("#userEmail"));
     }

     function checkAuthCode() {
       let userAuthCode = $("#userAuthCode").val();
       $.ajax({
         url: "/member/checkAuthCode", // 데이터가 송수신될 서버의 주소
         type: "post", // 통신 방식 : GET, POST, PUT, DELETE, PATCH
         dataType: "text", // 수신 받을 데이터의 타입 (text, xml, json)
         data: {
           "tmpUserAuthCode" : userAuthCode
         },    
         success: function (data) {
               // 비동기 통신에 성공하면 자동으로 호출될 callback function
               console.log(data);
               if (data == 'success') {
                 alert("인증 성공!");
                 $('#userEmail').attr("readonly", true);
                 $('#authenticateDiv').remove();
                 $('#emailValid').val("checked");

               } else if (data == 'fail')  {
                 alert("인증에 실패 했습니다!");
                 $('#emailValid').val("");
               }
         }
       });
     }

   function genderValid() { //자바 스크립트로도 더 간단히 가능 이건 각자 공부~~~
      // 성별을 남성, 여성 중 하나를 반드시 선택해야 한다.
      let genders = document.getElementsByName("gender");
      let result = false;
      
      for (let g of genders) {
         if (g.checked) {
            console.log("하나라도 체크 되었음");
            result = true;
         }
      }
      
      if (!result) {
         outputError('성별은 필수 입니다!', $('.genderDiv'));
      } else {
         clearError($('.genderDiv'));
      }
      
      return result;
   }

   function pwdValid() {
      // 비밀번호 : 필수이고, 4~8자, 비밀번호확인과 동일해야 한다.
      let result = false;
   
      if ($('#pwdValid').val() == 'checked') {
         result = true;
      }

      return result;
   }

   function idValid() {
      // 아이디 : 필수이고, 4~8자, 아이디는 중복된 아이디가 없어야 함
      let result = false;
      
      if ($('#idValid').val() == 'checked') {
         result = true;
      }

      return result;
   }
   
   function showPreview(obj) {
	      // 조건 : 이미지 파일이거나, 파일을 등록하지 않았다면 통과
	        
	      if (obj.files[0].size > 1024 * 1024 * 10) {
	         alert("10MB 이하의 파일만 업로드할 수 있습니다.");
	         obj.value = ""; // 선택한 파일 초기화
	            return;  // 10MB 이하의 파일만 업로드할 수 있도록 return
	      }
	      console.log(obj.files[0]);
	      let imageType = ["image/jpeg", "image/png", "image/gif", "image/jpg"];
	      // 파일 타입 확인
	      let fileType = obj.files[0].type;
	      console.log(fileType);  // file type : 

	      let fileName = obj.files[0].name;
	      if (imageType.indexOf(fileType) != -1) {  // 이미지 파일이다.
	         let reader = new FileReader();  // FileReader 객체 생성
	           reader.onload = function(e) { 
	               // reader객체에 의해 파일을 읽기 완료하면 실행되는 콜백함수
	            let imgTag = `<div style='padding : 6px;'><img src='\${e.target.result}' width='40px' /><span>\${fileName}</span></div>`;
	            $(imgTag).insertAfter(obj);
	           }
	           reader.readAsDataURL(obj.files[0]);  // 업로드된 파일을 읽어온다.
	           
	           clearError(obj);
	        
	           $('#imgCheck').val('checked');
	           
	      } else {
	         outputError("이미지 파일만 올릴 수 있습니다", obj);
	         $(obj).val('');
	         // $('#imgCheck').val('noImage');
	      }
	   }

   
   
   
   
</script>
<style>
   .error {
      color : #990000;
      font-size: .8em;
      padding : 5px;
      border : 1px solid #990000;
      border-radius: 5px;
      margin: 5px 0px;
     
   }
   .hobbies {
   display: flex;
   flex-direction: row;
   justify-content: space-between;
   }
   .timer {
      color : oranged;
      font-weight: bold;
      font-size: 0.8em;
   }
</style>


<script language="javascript">
function getAddr(){
	// 적용예 (api 호출 전에 검색어 체크) 	
	if (!checkSearchedWord(document.form.keyword)) {
		return ;
	}

	$.ajax({
		 url :"https://business.juso.go.kr/addrlink/addrLinkApiJsonp.do"  //인터넷망
		,type:"post"
		,data:$("#form").serialize()
		,dataType:"jsonp"
		,crossDomain:true
		,success:function(jsonStr){
			$("#list").html("");
			var errCode = jsonStr.results.common.errorCode;
			var errDesc = jsonStr.results.common.errorMessage;
			if(errCode != "0"){
				alert(errCode+"="+errDesc);
			}else{
				if(jsonStr != null){
					makeListJson(jsonStr);
					 $("#list").css('display', 'block');
				}
			}
		}
	    ,error: function(xhr,status, error){
	    	alert("에러발생");
	    }
	});
}

function makeListJson(jsonStr){
	var htmlStr = "";
	htmlStr += "<table><tr><th>도로명</th><th>지번주소</th><th>우편번호</th></tr>";
	$(jsonStr.results.juso).each(function(){
		htmlStr += "<tr onclick='selectAddress(\"" + this.roadAddr + "\")'>";
		htmlStr += "<td>"+this.roadAddr+"</td>";
		htmlStr += "<td>"+this.jibunAddr+"</td>";
		htmlStr += "<td>"+this.zipNo+"</td>";
		htmlStr += "</tr>";
	});
	htmlStr += "</table>";
	$("#list").html(htmlStr);
}

function selectAddress(address) {
    $("#address").val(address);
    $("#list").css('display', 'none')

}

//특수문자, 특정문자열(sql예약어의 앞뒤공백포함) 제거
function checkSearchedWord(obj){
	if(obj.value.length >0){
		//특수문자 제거
		var expText = /[%=><]/ ;
		if(expText.test(obj.value) == true){
			alert("특수문자를 입력 할수 없습니다.") ;
			obj.value = obj.value.split(expText).join(""); 
			return false;
		}
		
		//특정문자열(sql예약어의 앞뒤공백포함) 제거
		var sqlArray = new Array(
			//sql 예약어
			"OR", "SELECT", "INSERT", "DELETE", "UPDATE", "CREATE", "DROP", "EXEC",
             		 "UNION",  "FETCH", "DECLARE", "TRUNCATE" 
		);
		
		var regex;
		for(var i=0; i<sqlArray.length; i++){
			regex = new RegExp( sqlArray[i] ,"gi") ;
			
			if (regex.test(obj.value) ) {
			    alert("\"" + sqlArray[i]+"\"와(과) 같은 특정문자로 검색할 수 없습니다.");
				obj.value =obj.value.replace(regex, "");
				return false;
			}
		}
	}
	return true ;
}

function enterSearch() {
	var evt_code = (window.netscape) ? ev.which : event.keyCode;
	if (evt_code == 13) {    
		event.keyCode = 0;  
		getAddr(); //jsonp사용시 enter검색 
	} 
}
</script>
<style>
.error {
	color: #990000;
	font-size: .8em;
	padding: 5px;
	border: 1px solid black;
	border-radius: 5px;
	margin: 5px 0px;
}
</style>
</head>
<body>
   <c:import url="../header.jsp" />

   <div class="container">
      <h1>회원가입페이지</h1>

      <form method="post" action="/member/register" enctype="multipart/form-data">
      
         <div class="mb-3 mt-3">
            <label for="userId" class="form-label">아이디: </label> <input
               type="text" class="form-control" id="userId"
               placeholder="아이디를 입력하세요..." name="userId" />
            <input type="hidden" id="idValid"  />
         </div>

         <div class="mb-3 mt-3">
            <label for="userPwd1" class="form-label">패스워드: </label> <input
               type="password" class="form-control" id="userPwd1"
               placeholder="비밀번호를 입력하세요..." name="userPwd" />
         </div>

         <div class="mb-3 mt-3">
            <label for="userPwd2" class="form-label">패스워드 확인: </label> <input
               type="password" class="form-control" id="userPwd2"
               placeholder="비밀번호를 확인하세요..." />
               <input type="hidden" id="pwdValid"  />
         </div>
         
         
         <div class="mb-3 mt-3">
            <label for="userName" class="form-label">이름: </label> <input
               type="text" class="form-control" id="userName" name="userName"
               placeholder="이름을 입력하세요..." />
         </div>

         <!--  라디오 버튼 : 단일 선택 (input 태그의 name 속성 값을 반드시 동일하게 해야 한다)-->
         <div class="form-check genderDiv">
            <label
               class="form-check-label" for="female">
            <input type="radio" class="form-check-input" id="female"
               name="gender" value="F"  >여성</label>
         </div>
         <div class="form-check">
         <label
               class="form-check-label" for="male">
            <input type="radio" class="form-check-input" id="male"
               name="gender" value="M">남성</label>
         </div>
   
         <div class="mb-3 mt-3">
            <label for="userEmail" class="form-label">이메일: </label> <input
               type="text" class="form-control" id="userEmail" name="email" />
               <input type="hidden" id="emailValid"  />
         </div>

         <div class="mb-3 mt-3">
            <label for="mobile" class="form-label">휴대전화: </label> <input
               type="text" class="form-control" id="mobile"
               placeholder="전화번호를 입력하세요..." name="mobile" />
         </div>
         
         
         <div class="form-check">
     <div>취미 :</div>
     <div class="hobbies"> <!-- 베엔드단에서는 리퀘스트파람 벨류? 로 배열로 받아갈거다. -->
  	<span><input class="form-check-input" type="checkbox" name="hobby" value="sleep" checked>낮잠</span>
   
    <span><input class="form-check-input" type="checkbox" name="hobby" value="reading" >독서</span>

    <span><input class="form-check-input" type="checkbox" name="hobby" value="coding" >코딩</span>

    <span><input class="form-check-input" type="checkbox" name="hobby" value="game" >게임</span>
  </div>
</div>


         <div class="mb-3 mt-3">
            <label for="userImg" class="form-label">회원 프로필: </label> <input
               type="file" class="form-control" id="userImg"
               name="userProfile" onchange="showPreview(this);"/>
               <input type="hidden" id="imgCheck" />

         </div>


         <div class="form-check">
            <input class="form-check-input" type="checkbox" id="agree"
               name="agree" value="Y" /> <label class="form-check-label">회원
               가입 조항에 동의합니다</label>
         </div>

         <!-- form 태그는 항상 submit / reset 버튼과 함께 사용 -->
         <input type="submit" class="btn btn-success" value="회원가입" onclick="return isValid();" /> 
         <input type="reset" class="btn btn-danger" value="취소" />
      </form>
      
               <div class="form-check"> <!-- 온클릭 만들어서 신발 사이즈 옆에 스판 만들어서 온클릭마다 스판에 사이즈 줘서 알 수 있게 -->
            <label for="customRange" class="form-label">신발사이즈</label>
            <input type="range" class="form-range" id="customRange" min="210" max="300" step="5">

         </div>



		<form name="form" id="form" method="post">
			<input type="hidden" class="form-control" id="currentPage"
				placeholder="현재 페이지 번호를 입력하세요..." name="currentPage" value="1" /> <input
				type="hidden" class="form-control" id="countPerPage"
				placeholder="페이지당 출력할 개수를 입력하세요..." name="countPerPage" value="10" />


			<input type="hidden" class="form-control" id="resultType"
				placeholder="검색결과 형식을 입력하세요..." name="resultType" value="json" /> <input
				type="hidden" class="form-control" id="confmKey"
				placeholder="승인키를 입력하세요..." name="confmKey"
				value="devU01TX0FVVEgyMDI0MDcyOTE2MzQxMTExNDk3Mjg=" />

			<div class="mb-3 mt-3">
				<label for="keyword" class="form-label">주소: </label> <input
					type="text" class="form-control" id="keyword"
					placeholder="키워드를 입력하세요..." name="keyword"
					onkeydown="enterSearch();" />
			</div>



			<div class="mb-3 mt-3">
				<input type="button" onClick="getAddr();" value="주소검색하기" />
			</div>

			<div class="mb-3 mt-3">
				<label for="userAdress" class="form-label">상세 주소</label> <input
					type="text" class="form-control" id="address" name="address" />
			</div>

			<div id="list"></div>
			<!-- 검색 결과 리스트 출력 영역 -->
		</form>

 <form method="post" id="smsForm">
    <ul>
      <li>보낼사람 : <input type="text" name="from"/></li>
      <li>내용 : <textarea name="text"></textarea></li>
      <li><input type="button" onclick="sendSMS('sendSms')" value="전송하기" /></li>
    </ul>
  </form>
  
   <script>
    function sendSMS(pageName){

    	console.log("문자를 전송합니다.");
    	$("#smsForm").attr("action", "/coolsms/send-one");
    	$("#smsForm").submit();
    }
  </script>




   </div>

   <c:import url="../footer.jsp" />
</body>
</html>