document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("newPostForm");
    const title = document.getElementById("title");
    const content = document.getElementById("content");
    const btnSubmit = document.getElementById("btnSubmit");
    const btnCancel = document.getElementById("btnCancel");
    const alertBox = document.getElementById("formAlert");

    function showAlert(msg, type="error"){
        alertBox.textContent = msg;
        alertBox.className = `alert ${type}`;
        alertBox.hidden = false;
    }
    function clearAlert(){ alertBox.hidden = true; }

    // 취소 버튼 → 목록으로 이동
    btnCancel.addEventListener("click", () => {
        window.location.href = "/index";
    });

    // 폼 제출
    form.addEventListener("submit", async (e) => {
        e.preventDefault();
        clearAlert();

        const payload = {
            title: title.value.trim(),
            content: content.value.trim()
        };

        if (!payload.title){
            showAlert("제목을 입력해주세요."); title.focus(); return;
        }
        if (!payload.content){
            showAlert("내용을 입력해주세요."); content.focus(); return;
        }

        btnSubmit.disabled = true;

        try {
            const res = await fetch("/posts", {
                method: "POST",
                credentials: "include",
                headers: { "Content-Type": "application/json", "Accept": "application/json" },
                body: JSON.stringify(payload)
            });

            if (!res.ok){
                let data;
                try { data = await res.json(); } catch {}
                showAlert(data?.message || "글 등록에 실패했습니다.");
                return;
            }

            // 성공 시 → index.jsp로 이동
            window.location.href = "/index";

        } catch(err){
            console.error(err);
            showAlert("네트워크 오류가 발생했습니다.");
        } finally {
            btnSubmit.disabled = false;
        }
    });
});
