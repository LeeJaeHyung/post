// ====== 유틸 ======
const esc = (s) => String(s ?? "")
    .replaceAll("&","&amp;")
    .replaceAll("<","&lt;")
    .replaceAll(">","&gt;");

const fmtNum = (n) => {
    const v = (typeof n === "number" && !isNaN(n)) ? n : (Number(n) || 0);
    return v.toLocaleString();
};

const toLocal = (v) => {
    if (!v) return "";
    try {
        const s = String(v).replace(" ", "T");
        const d = new Date(s);
        if (isNaN(d.getTime())) return esc(v);
        return d.toLocaleString();
    } catch { return esc(v); }
};

const pickPosts = (data) => {
    if (Array.isArray(data)) return data;
    if (Array.isArray(data?.content)) return data.content;
    if (Array.isArray(data?.postList)) return data.postList;
    return [];
};

// ====== API ======
async function fetchComments(postId){
    const res = await fetch(`/posts/${postId}/comments`, {
        credentials: "include",
        headers: { "Accept": "application/json" }
    });

    let data;
    try { data = await res.json(); }
    catch { throw new Error("응답을 해석할 수 없습니다."); }

    if (!res.ok) {
        const msg = data?.message || "댓글 조회 실패";
        throw new Error(msg);
    }

    if (!Array.isArray(data)) return [];
    return data;
}

async function postComment(postId, payload){
    // payload: { comment: string, parentId?: number }
    const res = await fetch(`/posts/${postId}/comments`, {
        method: "POST",
        credentials: "include",
        headers: {
            "Accept": "application/json",
            "Content-Type": "application/json"
        },
        body: JSON.stringify(payload)
    });

    let data;
    try { data = await res.json(); }
    catch { throw new Error("응답을 해석할 수 없습니다."); }

    if (!res.ok) {
        const msg = data?.message || "댓글 작성 실패";
        throw new Error(msg);
    }

    // 기대: CommentDto
    return data;
}

// ====== 렌더링: 게시글 ======
function renderPosts(posts){
    const wrap = document.getElementById("postList");
    const html = posts.map(p => {
        const title   = esc(p.title);
        const author  = esc(p.author ?? p.username ?? p.authorName ?? "익명");
        const content = esc(p.content);
        const created = toLocal(p.createdAt);

        const likes   = fmtNum(p.likeCount);
        const cmts    = fmtNum(p.commentCount);
        const views   = fmtNum(p.viewCount);

        return `
      <article class="post-card" data-post-id="${p.id}">
        <h2 class="post-title dbl-target">${title}</h2>
        <div class="post-meta">
          <span>작성자: ${author}</span>
          ${created ? `<span>·</span><span>${created}</span>` : ""}
        </div>
        <div class="metrics">
          <span class="badge like">👍 <span class="num">${likes}</span></span>
          <span class="badge comment">💬 <span class="num">${cmts}</span></span>
          <span class="badge view">👁️ <span class="num">${views}</span></span>
        </div>

        <div class="hint dbl-target">본문/댓글을 보려면 이 카드를 더블클릭하세요</div>

        <div class="post-content is-hidden">${content}</div>

        <section class="comments" hidden>
          <div class="cmt-header">💬 댓글</div>
          <div class="cmt-list"></div>
          <!-- 댓글 폼은 JS가 동적으로 붙임 -->
        </section>
      </article>`;
    }).join("");

    wrap.innerHTML = html;

    // 더블클릭 이벤트 위임: 매번 AJAX 요청
    wrap.addEventListener("dblclick", async (e) => {
        const card = e.target.closest(".post-card");
        if (!card) return;

        const postId = Number(card.getAttribute("data-post-id"));
        const contentEl = card.querySelector(".post-content");
        const hintEl    = card.querySelector(".hint");
        const comments  = card.querySelector(".comments");
        const listEl    = card.querySelector(".cmt-list");

        // 토글
        const nowHidden = contentEl.classList.toggle("is-hidden");
        hintEl.style.display = nowHidden ? "" : "none";
        comments.hidden = nowHidden ? true : false;

        // 더블클릭 때마다 항상 서버 요청
        listEl.innerHTML = `<div class="loading">댓글을 불러오는 중…</div>`;
        try{
            const items = await fetchComments(postId);
            if (!nowHidden){
                renderCommentsThread(card, listEl, items);       // 리스트 렌더
                ensureCommentForm(card, postId);                 // 폼 삽입/바인딩
                attachCommentSelectHandler(card);                // 댓글 클릭 선택(대댓글 대상)
            }
        }catch(err){
            console.error(err);
            if (!nowHidden){
                listEl.innerHTML = `<div class="loading">댓글을 불러올 수 없습니다.</div>`;
            }
        }
    }, { passive: true });
}

// ====== 렌더링: 댓글 트리 ======
function renderCommentsThread(card, listEl, items){
    if (!Array.isArray(items) || items.length === 0){
        listEl.innerHTML = `<div class="loading">아직 댓글이 없습니다.</div>`;
        return;
    }

    // id -> comment, parent_id -> children[] (서버 정렬 유지)
    const byId = new Map();
    items.forEach(c => byId.set(c.id, c));
    const children = new Map();
    items.forEach(c => {
        const key = c.parent_id ?? 0; // 최상위는 0 키
        if (!children.has(key)) children.set(key, []);
        children.get(key).push(c);
    });

    // 깊이 계산
    const depthCache = new Map();
    const getDepth = (c) => {
        if (depthCache.has(c.id)) return depthCache.get(c.id);
        let d = 0, cur = c;
        const visited = new Set();
        while (cur && cur.parent_id){
            if (visited.has(cur.id)) break;
            visited.add(cur.id);
            const p = byId.get(cur.parent_id);
            if (!p) break;
            d++; cur = p;
        }
        depthCache.set(c.id, d);
        return d;
    };

    // 재귀 렌더
    const renderNodeList = (arr) => {
        return arr.map(c => {
            const d = getDepth(c);
            const pad = 22 * d;
            const author  = esc(c.author ?? "익명");
            const content = esc(c.content);
            const created = toLocal(c.createdAt);
            const likeCnt = fmtNum(c.likeCount);

            const row = `
        <div class="cmt-row depth-${d}" style="padding-left:${pad}px">
          <div class="cmt-item" data-cmt-id="${c.id}" data-position="${Number.isInteger(c.position)? c.position : ''}">
            <div class="cmt-meta">
              <span>${author}</span>
              ${created ? `<span>·</span><span>${created}</span>` : ""}
              <span>·</span><span>👍 ${likeCnt}</span>
              ${Number.isInteger(c.position) ? `<span>·</span><span>#${c.position}</span>` : ""}
            </div>
            <div class="cmt-body">${content}</div>
          </div>
        </div>
      `;

            const kids = children.get(c.id) || [];
            return row + (kids.length ? renderNodeList(kids) : "");
        }).join("");
    };

    const roots = children.get(0) || [];
    listEl.innerHTML = renderNodeList(roots);

    // 선택 상태 초기화
    clearReplySelection(card);
}

// ====== 댓글 폼 생성/바인딩 ======
function ensureCommentForm(card, postId){
    // 이미 있으면 스킵
    let formWrap = card.querySelector(".cmt-form");
    if (!formWrap){
        const comments = card.querySelector(".comments");
        formWrap = document.createElement("div");
        formWrap.className = "cmt-form";
        formWrap.innerHTML = `
      <div class="replying" data-replying="off" style="display:none"></div>
      <textarea placeholder="댓글을 입력하세요"></textarea>
      <div class="row">
        <button type="button" class="btn-submit" disabled>등록</button>
        <span class="cancel-reply" style="display:none">답글 대상 취소</span>
      </div>
      <input type="hidden" class="parent-id" value="">
    `;
        comments.appendChild(formWrap);

        // 입력 감지해 버튼 활성화
        const textarea = formWrap.querySelector("textarea");
        const submitBtn = formWrap.querySelector(".btn-submit");

        textarea.addEventListener("input", () => {
            submitBtn.disabled = textarea.value.trim().length === 0;
        });

        // 전송
        submitBtn.addEventListener("click", async () => {
            const comment = textarea.value.trim();
            if (!comment) return;

            const parentIdRaw = formWrap.querySelector(".parent-id").value;
            const payload = { comment };
            if (parentIdRaw) payload.parentId = Number(parentIdRaw);

            // 비활성화 & 전송
            submitBtn.disabled = true;
            try{
                await postComment(postId, payload);
                // 성공: 입력 비우고 선택 해제, 댓글 다시 로드
                textarea.value = "";
                clearReplySelection(card);

                const listEl = card.querySelector(".cmt-list");
                listEl.innerHTML = `<div class="loading">댓글을 불러오는 중…</div>`;
                const items = await fetchComments(postId);
                renderCommentsThread(card, listEl, items);
            }catch(err){
                console.error(err);
                alert(err.message || "댓글 작성에 실패했습니다.");
            }finally{
                submitBtn.disabled = textarea.value.trim().length === 0;
            }
        });

        // 답글 대상 취소
        const cancel = formWrap.querySelector(".cancel-reply");
        cancel.addEventListener("click", () => {
            clearReplySelection(card);
        });
    }
}

// ====== 댓글 선택(대댓글 대상 설정) ======
function attachCommentSelectHandler(card){
    const listEl = card.querySelector(".cmt-list");
    if (!listEl) return;

    // 이미 등록되어 있으면 중복 방지
    if (listEl.__selectHandlerBound) return;
    listEl.__selectHandlerBound = true;

    listEl.addEventListener("click", (e) => {
        const item = e.target.closest(".cmt-item");
        if (!item) return;

        // 선택 토글: 같은 걸 또 누르면 해제, 다른 걸 누르면 변경
        const currentlySelected = card.querySelector(".cmt-item.is-selected");
        if (currentlySelected === item){
            clearReplySelection(card);
            return;
        }

        if (currentlySelected) currentlySelected.classList.remove("is-selected");
        item.classList.add("is-selected");

        const parentId = item.getAttribute("data-cmt-id");
        const position = item.getAttribute("data-position");
        setReplySelection(card, parentId, position);
    });
}

function setReplySelection(card, parentId, position){
    const form = card.querySelector(".cmt-form");
    if (!form) return;

    form.querySelector(".parent-id").value = parentId;
    const info = form.querySelector(".replying");
    info.style.display = "";
    info.dataset.replying = "on";
    info.textContent = position ? `답글 대상: #${position}` : `답글 대상 선택됨`;

    const cancel = form.querySelector(".cancel-reply");
    cancel.style.display = "";
}

function clearReplySelection(card){
    const form = card.querySelector(".cmt-form");
    if (!form) return;

    form.querySelector(".parent-id").value = "";
    const info = form.querySelector(".replying");
    info.style.display = "none";
    info.dataset.replying = "off";
    info.textContent = "";

    const cancel = form.querySelector(".cancel-reply");
    cancel.style.display = "none";

    const sel = card.querySelector(".cmt-item.is-selected");
    if (sel) sel.classList.remove("is-selected");
}

// ====== 초기 로딩 ======
async function loadPosts(){
    const wrap = (html) => { document.getElementById("postList").innerHTML = html; };

    try{
        const res = await fetch("/posts", {
            credentials: "include",
            headers: { "Accept":"application/json" }
        });

        let data;
        try { data = await res.json(); }
        catch { wrap(`<div class="empty">응답을 해석할 수 없습니다.</div>`); return; }

        if (!res.ok){
            const msg = (data && data.message) ? esc(data.message) : "게시물을 불러올 수 없습니다.";
            wrap(`<div class="empty">${msg}</div>`);
            return;
        }

        const posts = pickPosts(data);
        if (posts.length === 0){
            wrap(`<div class="empty">등록된 게시물이 없습니다.</div>`);
            return;
        }

        renderPosts(posts);
    } catch (e){
        console.error(e);
        document.getElementById("postList").innerHTML = `<div class="empty">에러가 발생했습니다.</div>`;
    }
}

document.addEventListener("DOMContentLoaded", loadPosts);

document.addEventListener("DOMContentLoaded", () => {
    const btn = document.getElementById("btnWrite");
    if (btn){
        btn.addEventListener("click", () => {
            // 글쓰기 페이지 라우트에 맞게 수정하세요.
            // 일반적으로 /posts/new 또는 /posts/write 등을 사용합니다.
            window.location.href = "/posts/new";
        });
    }
});
