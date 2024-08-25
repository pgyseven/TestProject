<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%> <%@ taglib uri="http://java.sun.com/jsp/jstl/core"
prefix="c"%>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8" />
    <title>Insert title here</title>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
    <link
      href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
      rel="stylesheet"
    />
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
  </head>
  <body>
    <div class="container">
      <c:import url="./../header.jsp"></c:import>
      <h2>${param.boardNo}번 글에 대한 답글 작성 페이지</h2>
      <!-- multipart form-data : 데이터를 여러 조각으로 나누어서 전송하는 방식. 수신되는 곳에서는 재조립이 필요하다. -->
      <form action="saveReply" method="post">
        <!-- 패킷이란 단위로 데이터가 전송이됨 인터넷 선이 하나인데 대용량 업로드 중인데 하나만 하면 다른사람하고 통신을 못함 그래서 그걸 잘게 끊어서 날린다 그게 패킷단위고 이게 하나에 64kb 이다. 데이터가 이동할때 잘게 순서없이 보내지는데 그게 나중에 합쳐져야함 파일을 저장할땐 그래서 이속성을 꼭 써야함  4000바이트는 4kb /기존에 post였음 데이터 양이 많으니 포스트 방식으로 보낸다 만약 겟방식이면 url에 쿼리 스트링 형식으로 보내진다. 그러면 내용때문에 url의 길이 제한 때문에 문제 생긴다 정확히 2083자까지만 가능하다. -->
        <div class="mb-3">
          <label for="title" class="form-label">글제목</label>
          <input
            type="text"
            class="form-control"
            id="title"
            name="title"
            placeholder="글제목을 입력하세요"
          />
        </div>
        <div class="mb-3">
          <label for="author" class="form-label">작성자</label>
          <input
            type="text"
            class="form-control"
            id="writer"
            name="writer"
            placeholder="작성자를 입력하세요"
          />
        </div>
        <div class="mb-3">
          <label for="content" class="form-label">내용</label>
          <textarea
            class="form-control"
            id="content"
            name="content"
            rows="5"
            placeholder="내용을 입력하세요"
          ></textarea>
        </div>

        <div>
          <input type="hidden" name="ref" value="${param.ref}" />
          <input type="hidden" name="step" value="${param.step}" />
          <input type="hidden" name="refOrder" value="${param.refOrder}" />
        </div>

        <button type="submit" class="btn btn-primary" onclick="">
          답글 저장
        </button>
        <button type="button" class="btn btn-warning" onclick="">취소</button>
      </form>
      <!-- 네임속성이 있는 애들만 넘어간다 폼태그는 -->

      <c:import url="./../footer.jsp"></c:import>
    </div>
  </body>
</html>
