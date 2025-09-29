<%@ page contentType="text/html; charset=UTF-8" isELIgnored="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8" />
    <title>새 글 작성</title>
    <meta name="viewport" content="width=device-width,initial-scale=1" />
    <link rel="icon" href="data:,">
    <link rel="stylesheet" href="<c:url value='/css/new-post.css'/>">
</head>
<body>
<div class="container">
    <header class="hdr">
        <h1>새 글 작성</h1>
        <p class="sub">제목과 내용을 입력하고 등록하세요</p>
    </header>

    <section class="card">
        <form id="newPostForm" novalidate>
            <!-- 제목 -->
            <div class="field">
                <label for="title">제목</label>
                <input type="text" id="title" name="title" maxlength="100" placeholder="제목을 입력하세요" required />
                <div class="hint">
                    <span id="titleCount">0</span>/100
                </div>
            </div>

            <!-- 공개 여부 -->
            <div class="field">
                <label>공개 설정</label>
                <div class="radio-group">
                    <label class="radio"><input type="radio" name="status" value="PUBLIC" checked> 공개</label>
                    <label class="radio"><input type="radio" name="status" value="DELETED"> 비공개(임시)</label>
                </div>
            </div>

            <!-- 내용 -->
            <div class="field">
                <label for="content">내용</label>
                <textarea id="content" name="content" rows="10" maxlength="5000" placeholder="내용을 입력하세요" required></textarea>
                <div class="hint">
                    <span id="contentCount">0</span>/5000
                </div>
            </div>

            <!-- 에러/알림 -->
            <div id="formAlert" class="alert" hidden></div>

            <!-- 액션 -->
            <div class="actions">
                <button type="button" id="btnCancel" class="btn secondary">취소</button>
                <button type="submit" id="btnSubmit" class="btn primary">등록</button>
            </div>
        </form>
    </section>
</div>

<script src="<c:url value='/js/new-post.js'/>"></script>
</body>
</html>
