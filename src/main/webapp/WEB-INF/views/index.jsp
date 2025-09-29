<%@ page contentType="text/html; charset=UTF-8" isELIgnored="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8" />
    <title>게시판</title>
    <link rel="icon" href="data:,">
    <meta name="viewport" content="width=device-width,initial-scale=1" />
    <!-- 외부 CSS -->
    <link rel="stylesheet" href="<c:url value='/css/board.css'/>">
</head>
<body>
<div class="container">
    <div class="hdr">
        <h1>게시판</h1>
        <span class="dot"></span>
        <div class="sub">더블클릭으로 본문·댓글을 열고(또는 접고) 매번 최신 댓글을 가져와요</div>
    </div>

    <div id="postList">
        <div class="skeleton"></div>
        <div class="skeleton"></div>
        <div class="skeleton"></div>
    </div>

    <!-- 글쓰기 버튼 바 -->
    <div class="write-bar">
        <button id="btnWrite" class="btn-primary">✍️ 글쓰기</button>
    </div>



</div>

<!-- 외부 JS -->
<script src="<c:url value='/js/board.js'/>"></script>
</body>
</html>
