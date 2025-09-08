/* ===================== 전역 변수 ===================== */
const postInfo = {}; // key: boardId, value: post data
let currentImageIndex = 0;
let imageList = [];
let currentPage = 0;
const pageSize = 10;
const commentsInfo = {};
let userInfo = null;

/* ===================== URL에서 게시글 ID 가져오기 ===================== */
function getPostIdFromUrl() {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get('id');
}

/* ===================== 게시글 로딩 ===================== */
async function loadPost(postId) {
    if(postInfo[postId]) {
        renderPost(postInfo[postId])
        return;
    }

    try {
        const res = await fetch(`${API_BASE_URL}${API_ENDPOINTS.post}/${postId}`, {
            method: 'GET',
            credentials: 'include',
        });

        const json = await res.json();
        const post = json.data;
        postInfo[postId] = post;
        renderPost(post);

    } catch (err) {
        console.error('게시글 로딩 실패:', err);
    }
}

/* ===================== 게시글 렌더링 ===================== */
function renderPost(post) {
    document.title = `${post.title} - 커뮤니티`;
    document.getElementById('postNumber').textContent = `#${post.id}`;
    document.getElementById('postTitle').textContent = post.title;
    document.getElementById('authorName').textContent = post.writerNickname;
    document.getElementById('authorAvatar').src = post.writerProfileImage || getDefaultImage('profile');
    document.getElementById('postDate').textContent = formatDate(post.writeDatetime);
    document.getElementById('postContent').textContent = post.content;
    renderImages(post.imageList);
}

async function editPost() {
    if(!userInfo) return alert('로그인 필요');
    const postId = getPostIdFromUrl();
    const post = postInfo[postId];
    if(post.writerEmail != userInfo.email) return alert('권한 없음');
    writePost(postId);
}

async function deletePost() {
    if(!userInfo) return alert('로그인 필요');
    const postId = getPostIdFromUrl();
    const post = postInfo[postId];
    if(post.writerEmail != userInfo.email) return alert('권한 없음');

    await fetch(`${API_BASE_URL}${API_ENDPOINTS.post}/${postId}`, {
            method: 'DELETE',
            credentials: 'include'
            });

    home();
}

/* ===================== 이미지 렌더링 & 모달 ===================== */
function renderImages(images) {
    const container = document.getElementById('postImages');
    if (!images || images.length === 0) {
        container.style.display = 'none';
        imageList = [];
        return;
    }

    imageList = Array.isArray(images) ? images : [images];

    let galleryHTML = `<div class="image-gallery ${imageList.length === 1 ? 'single' : imageList.length === 2 ? 'double' : 'multiple'}">`;

    imageList.forEach((url, i) => {
        galleryHTML += `
            <div style="position: relative;">
                <img src="${url}" class="post-image" onclick="openImageModal(${i})" onerror="this.style.display='none'">
                ${imageList.length > 1 && i === 0 ? `<div class="image-counter">${imageList.length}장</div>` : ''}
            </div>
        `;
    });

    galleryHTML += '</div>';
    container.innerHTML = galleryHTML;
    container.style.display = 'block';
}

function openImageModal(index) {
    currentImageIndex = index;
    const modal = document.getElementById('imageModal');
    document.getElementById('modalImage').src = imageList[index];
    document.getElementById('modalIndicator').textContent = `${index + 1} / ${imageList.length}`;
    document.getElementById('prevBtn').disabled = index === 0;
    document.getElementById('nextBtn').disabled = index === imageList.length - 1;
    modal.style.display = 'flex';
    document.body.style.overflow = 'hidden';
}

function closeImageModal() {
    const modal = document.getElementById('imageModal');
    modal.style.display = 'none';
    document.body.style.overflow = 'auto';
}

function showPrevImage() { if (currentImageIndex > 0) openImageModal(currentImageIndex - 1); }
function showNextImage() { if (currentImageIndex < imageList.length - 1) openImageModal(currentImageIndex + 1); }

/* ===================== 좋아요 ===================== */
async function toggleLike() {
    if(!userInfo) return alert('로그인 필요');
    const postId = getPostIdFromUrl();
    const btn = document.getElementById('likeBtn');
    const text = document.getElementById('likeText');

    const liked = btn.classList.contains('liked');
    btn.classList.toggle('liked');
    text.textContent = liked ? '좋아요' : '좋아요 취소';

    try {
        await fetch(`${API_BASE_URL}${API_ENDPOINTS.like}/${postId}`, {
                    method: 'PUT',
                    credentials: 'include'
                });
    } catch (err) { console.error('좋아요 실패:', err); }
}

async function setToggleLike() {
    const postId = getPostIdFromUrl();
    const btn = document.getElementById('likeBtn');
    const text = document.getElementById('likeText');
    try {
        const res = await fetch(`${API_BASE_URL}${API_ENDPOINTS.like}/${postId}`, {
                    method: 'GET',
                    credentials: 'include'
                });
        const json = await res.json();
        const liked = json.data.liked;
        if(liked) btn.classList.toggle('liked');
        text.textContent = liked ? '좋아요 취소' : '좋아요';
    } catch (err) { console.error('좋아요 실패:', err); }
}

/* ===================== 댓글 로딩 ===================== */
async function loadComments(postId, page = 0) {
    try {
        const res = await fetch(`${API_BASE_URL}${API_ENDPOINTS.comment}/${postId}/comment-list?page=${page}&size=${pageSize}`, {
            method: 'GET',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        const result = await res.json();

        const commentList = result.data.commentList.content || [];

        if (page === 0) {
            // 첫 페이지면 새로 렌더
            renderComments(commentList, true);
        } else {
            // 다음 페이지면 기존에 이어 붙이기
            renderComments(commentList, false);
        }

        // 다음 페이지 로딩 버튼 상태
        const loadMoreBtn = document.getElementById('loadMoreCommentsBtn');
        loadMoreBtn.style.display = result.data.commentList.last ? 'none' : 'block';
        currentPage = result.data.commentList.number;

    } catch (err) { console.error('댓글 로딩 실패:', err); }
}

/* ===================== 댓글 렌더링 ===================== */
function renderComments(comments, reset = false) {
    const list = document.getElementById('commentsList');
    if (reset) list.innerHTML = '';

    comments.forEach(comment => {
        commentsInfo[comment.commentId] = comment;

        const html = `
            <div class="comment-item" data-comment-id="${comment.commentId}">
                <div class="comment-header">
                    <span class="comment-author-name">${comment.nickname}</span>
                    <span class="comment-date">${formatDate(comment.writeDatetime)}</span>
                    <div class="comment-actions">
                        <button class="comment-edit-btn" onclick="editComment(${comment.commentId})">수정</button>
                        <button class="comment-edit-btn" onclick="deleteComment(${comment.commentId})">삭제</button>
                    </div>
                </div>
                <div class="comment-text" id="commentText-${comment.commentId}">${comment.content}</div>
                <div class="comment-edit-form" id="editForm-${comment.commentId}" style="display:none;">
                    <textarea class="comment-edit-input" id="editInput-${comment.commentId}">${comment.content}</textarea>
                    <button class="comment-edit-btn save" onclick="saveCommentEdit(${comment.commentId})">저장</button>
                    <button class="comment-edit-btn cancel" onclick="cancelCommentEdit(${comment.commentId})">취소</button>
                </div>
                <div class="reply-actions">
                    <button class="reply-toggle-btn" onclick="loadReplies(${comment.commentId})">대댓글 보기</button>
                    <button class="reply-toggle-btn" onclick="toggleReplyForm(${comment.commentId})">대댓글 쓰기</button>
                </div>
                <div class="replies" id="replies-${comment.commentId}"></div>
                <div class="reply-form" id="replyForm-${comment.commentId}" style="display:none;">
                    <textarea class="reply-input" id="replyInput-${comment.commentId}" placeholder="답글 작성..."></textarea>
                    <button class="comment-edit-btn save" onclick="submitReply(${comment.commentId})">답글 작성</button>
                    <button class="comment-edit-btn cancel" onclick="closeReplyForm(${comment.commentId})">취소</button>
                </div>
            </div>
        `;
        list.insertAdjacentHTML('beforeend', html);
    });
}

/* ===================== 대댓글 로딩 (캐싱 적용) ===================== */
async function loadReplies(parentCommentId) {
    const container = document.getElementById(`replies-${parentCommentId}`);

    if (container.style.display === 'none' || container.style.display === '') {
            container.style.display = 'block';
        } else {
            container.style.display = 'none';
        }

    try {
        const res = await fetch(`${API_BASE_URL}${API_ENDPOINTS.comment}/${parentCommentId}/child-comment-list`, {
                                method: 'GET',
                                credentials: 'include'
                            });
        const json = await res.json();
        const replies = json.data.commentList.content;

        container.innerHTML = '';
        replies.forEach(reply => {
            commentsInfo[reply.commentId] = reply;

            const html = `
                <div class="comment-item reply" data-comment-id="${reply.commentId}">
                    <div class="comment-header">
                        <span class="comment-author-name">${reply.nickname}</span>
                        <span class="comment-date">${formatDate(reply.writeDatetime)}</span>
                        <div class="comment-actions">
                            <button class="comment-edit-btn save" onclick="editComment(${reply.commentId})">수정</button>
                            <button class="comment-edit-btn cansel" onclick="deleteComment(${reply.commentId})">삭제</button>
                        </div>
                    </div>
                    <div class="comment-text" id="commentText-${reply.commentId}">${reply.content}</div>
                    <div class="comment-edit-form" id="editForm-${reply.commentId}" style="display:none;">
                        <textarea class="comment-edit-input" id="editInput-${reply.commentId}">${reply.content}</textarea>
                        <button class="comment-edit-btn save" onclick="saveCommentEdit(${reply.commentId})">저장</button>
                        <button class="comment-edit-btn cancel" onclick="cancelCommentEdit(${reply.commentId})">취소</button>
                    </div>
                </div>
            `;
            container.insertAdjacentHTML('beforeend', html);
        });
    } catch (err) { console.error('대댓글 로딩 실패:', err); }
}

function closeReplyForm(parentCommentId) {
    const form = document.getElementById(`replyForm-${parentCommentId}`);
    form.style.display = 'none';
    document.getElementById(`replyInput-${parentCommentId}`).value = '';
}

async function submitComment() {
    if(!userInfo) return alert('로그인 필요');
    const input = document.getElementById('commentInput');
    const content = input.value.trim();
    if (!content) return alert('댓글 작성 필요');

    const postId = getPostIdFromUrl();
    await fetch(`${API_BASE_URL}${API_ENDPOINTS.comment}/${postId}/comment`, {
                method: 'POST',
                credentials: 'include',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({content})
            });
    input.value = '';
    loadComments(postId, 0);
}

function toggleReplyForm(commentId) {
    if(!userInfo) return alert('로그인 필요');
    const form = document.getElementById(`replyForm-${commentId}`);
    if (form.style.display === 'none' || form.style.display === '') {
        form.style.display = 'block';
    } else {
        form.style.display = 'none';
    }
}

/* ===================== 대댓글 작성  ===================== */
async function submitReply(parentCommentId) {
    if(!userInfo) return alert('로그인 필요');
    const input = document.getElementById(`replyInput-${parentCommentId}`);
    const content = input.value.trim();
    if (!content) return alert('대댓글 작성 필요');

    const bodyData = { content, parentCommentId };
    const postId = getPostIdFromUrl();

    await fetch(`${API_BASE_URL}${API_ENDPOINTS.comment}/${postId}/comment`, {
                method: 'POST',
                credentials: 'include',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(bodyData)
            });

    input.value = '';
    closeReplyForm(parentCommentId);
    loadReplies(parentCommentId);
}

function editComment(commentId) {
    if(!userInfo) return alert('로그인 필요');
    const comment = commentsInfo[commentId];
    const userNickname = userInfo.nickname;
    if(comment.nickname != userNickname) return alert('권한 없음');

    document.querySelectorAll('.comment-edit-form').forEach(f => f.style.display='none');
    document.querySelectorAll('.comment-text').forEach(t => t.style.display = 'block');
    document.querySelectorAll('.comment-item').forEach(i => i.classList.remove('editing'));

    const form = document.getElementById(`editForm-${commentId}`);
    const text = document.getElementById(`commentText-${commentId}`);
    if (form && text) {
        form.style.display = 'block';
        text.style.display = 'none';
        document.querySelector(`[data-comment-id="${commentId}"]`).classList.add('editing');
    }
}

function cancelCommentEdit(commentId) {
    const form = document.getElementById(`editForm-${commentId}`);
    const text = document.getElementById(`commentText-${commentId}`);
    if (form && text) {
        form.style.display = 'none';
        text.style.display = 'block';
        document.querySelector(`[data-comment-id="${commentId}"]`).classList.remove('editing');
    }
}

async function saveCommentEdit(commentId) {
    const input = document.getElementById(`editInput-${commentId}`);
    const content = input.value.trim();
    if (!content) return alert('댓글 내용 필요');

    const postId = getPostIdFromUrl();
    await fetch(`${API_BASE_URL}${API_ENDPOINTS.comment}/${postId}/${commentId}`, {
                method: 'PATCH',
                credentials: 'include',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({content})
            });
    cancelCommentEdit(commentId);
    loadComments(postId, 0);
}

async function deleteComment(commentId) {
    if(!userInfo) return alert('로그인 필요');
    const comment = commentsInfo[commentId];
    const userNickname = userInfo.nickname;
    if(comment.nickname != userNickname) return alert('권한 없음');

    const postId = getPostIdFromUrl();
    await fetch(`${API_BASE_URL}${API_ENDPOINTS.comment}/${postId}/${commentId}`, {
            method: 'DELETE',
            credentials: 'include'
            });
    loadComments(postId, 0);
}

function loadMoreComments() {
    const postId = getPostIdFromUrl();
    loadComments(postId, currentPage + 1);
}

document.getElementById('loginPage').onclick = function(e) {
             e.preventDefault(); // 기본 링크 이동 막기
             window.location.href = `${PAGE_ENDPOINTS.login}`;
        };

document.getElementById('signUpPage').onclick = function(e) {
             e.preventDefault(); // 기본 링크 이동 막기
             window.location.href = `${PAGE_ENDPOINTS.signUp}`;
        };

/* ===================== 초기 로드 ===================== */
document.addEventListener('DOMContentLoaded', async () => {
    const postId = getPostIdFromUrl();
    loadPost(postId);
    setToggleLike();
    loadComments(postId);
    userInfo = await getUserInfo();
    setHeader();
    const loadMoreBtn = document.getElementById('loadMoreCommentsBtn');
    if (loadMoreBtn) loadMoreBtn.style.display = 'none';
});
