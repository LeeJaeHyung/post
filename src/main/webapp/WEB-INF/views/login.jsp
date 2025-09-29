<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>로그인</title>
    <style>
        body {
            margin: 0;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea, #764ba2);
            display: flex;
            align-items: center;
            justify-content: center;
            height: 100vh;
        }

        .login-container {
            background: #fff;
            padding: 40px 30px;
            border-radius: 12px;
            box-shadow: 0 6px 20px rgba(0,0,0,0.15);
            width: 350px;
            text-align: center;
        }

        .login-container h1 {
            margin-bottom: 24px;
            font-size: 24px;
            color: #333;
        }

        .form-group {
            margin-bottom: 18px;
            text-align: left;
        }

        .form-group label {
            display: block;
            margin-bottom: 6px;
            font-size: 14px;
            color: #555;
        }

        .form-group input {
            width: 100%;
            padding: 12px;
            border: 1px solid #ddd;
            border-radius: 8px;
            font-size: 15px;
            box-sizing: border-box;
            transition: border-color 0.2s;
        }

        .form-group input:focus {
            border-color: #667eea;
            outline: none;
        }

        button {
            width: 100%;
            padding: 12px;
            background: #667eea;
            border: none;
            border-radius: 8px;
            font-size: 16px;
            color: #fff;
            font-weight: bold;
            cursor: pointer;
            transition: background 0.3s;
        }

        button:hover {
            background: #5563d6;
        }

        .error-msg {
            margin-top: 10px;
            color: #e63946;
            font-size: 14px;
        }
    </style>
</head>
<body>
<div class="login-container">
    <h1>로그인</h1>
    <form id="loginForm">
        <div class="form-group">
            <label for="username">아이디</label>
            <input type="text" name="username" id="username" placeholder="아이디를 입력하세요" required />
        </div>
        <div class="form-group">
            <label for="password">비밀번호</label>
            <input type="password" name="password" id="password" placeholder="비밀번호를 입력하세요" required />
        </div>
        <button type="submit">로그인</button>
        <p id="error" class="error-msg" style="display:none;">로그인에 실패했습니다.</p>
    </form>
</div>

<script>
    document.querySelector("#loginForm").addEventListener("submit", async (e) => {
        e.preventDefault();

        const formData = new FormData(e.target);
        const res = await fetch("/users/login", {
            method: "POST",
            body: formData
        });

        if (res.ok) {
            window.location.href = "/index"; // 성공하면 바로 이동
        } else {
            document.getElementById("error").style.display = "block";
        }
    });
</script>
</body>
</html>
