function startTimer() {
  let timer = 20;

  let timerInterval = setInterval(displayTime, 1000); //함수를 변수에 할당 가능함 람다 표현식 객체를 안만들고 그안에 함수만 가져다 쓸때


  function displayTime() {
    //시간이 0 보다 작거나 인증이 성공 되었다면...


       if (timer < 0) {
           // alert('time is up!');
           clearInterval(timerInterval);
           $('#authBtn').prop('disabled', true);
      

           if($('#emailValid').val() != 'checked'){
           // 백엔드에 인증시간이 만료되었음을 알려야 한다!!!!
           $.ajax({
               url: "/member/clearAuthCode", // 데이터가 송수신될 서버의 주소
               type: "post", // 통신 방식 : GET, POST, PUT, DELETE, PATCH
               dataType: "text", // 수신 받을 데이터의 타입 (text, xml, json)
               success: function (data) {
                 // 비동기 통신에 성공하면 자동으로 호출될 callback function
                 console.log(data);
                 if (data == 'success') {
                  clearInterval(timerInterval);
                   alert("인증시간이 만료되었습니다. 이메일 주소를 다시 입력하시고, 재 인증 시도 하세요");
                   $('#authenticateDiv').remove();
                   $("#userEmail").val('');
                   $("#userEmail").focus();

                 }
               }
             });
            }
       } else {
           let min = Math.floor(timer / 60);
           let sec = String(timer % 60).padStart(2, '0');
           let remainTime = min + ":" + sec;
           $('.timer').html(remainTime);
           --timer; 
       }
   }

   


 


}