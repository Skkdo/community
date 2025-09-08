        // 페이징 관련 변수
        let currentPage = 0; // API는 0부터 시작
        let totalPages = 0;
        let totalElements = 0;
        const pageSize = 5; // 한 페이지당 게시글 수
        let allPosts = []; // 현재 페이지의 게시글들
        let userInfo = null;
        let isSearching = false; // 검색 모드 여부
        let searchQuery = '';    // 현재 검색어

        function truncateContent(content, maxLength = 150) {
            if (content.length <= maxLength) return content;
            return content.substring(0, maxLength) + '...';
        }

        async function fetchLatestPosts(page = 0, size = pageSize) {
            try {
                const url = `${API_BASE_URL}${API_ENDPOINTS.latestList}?page=${page}&size=${size}`;

                const response = await fetch(url, {
                    method: 'GET',
                    credentials: 'include',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                });

                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }

                const result = await response.json();

                if (result.message === 'Success' && result.data && result.data.boardList) {
                    return result.data.boardList;
                } else {
                    throw new Error('Invalid response format');
                }
            } catch (error) {
                console.error('API Error:', error);
                throw error;
            }
        }

        async function fetchSearchPosts(query, page = 0, size = pageSize) {
            try {
                const url = `${API_BASE_URL}${API_ENDPOINTS.post}/search-list/${query}?page=${page}&size=${size}`;
                const response = await fetch(url, {
                    method: 'GET',
                    credentials: 'include',
                    headers: { 'Content-Type': 'application/json' },
                });

                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }

                const result = await response.json();

                if (result.message === 'Success' && result.data && result.data.boardList) {
                    return result.data.boardList;
                } else {
                    throw new Error('Invalid response format');
                }
            } catch (error) {
                console.error('검색 API Error:', error);
                throw error;
            }
        }

        async function handleSearch() {
            const searchInput = document.getElementById('searchInput');
            searchQuery = searchInput.value.trim();

            if (!searchQuery) {
                // 검색어가 없으면 전체 게시글 로딩
                isSearching = false;
                currentPage = 0;
                loadPosts();
                return;
            }

            isSearching = true;
            currentPage = 0; // 검색 시작은 항상 첫 페이지
            await loadSearchPosts();
        }

        async function loadSearchPosts(page = currentPage) {
            const loading = document.getElementById('loading');
            const postList = document.getElementById('postList');
            const pagination = document.getElementById('pagination');

            loading.style.display = 'flex';
            pagination.style.display = 'none';

            try {
                const boardData = await fetchSearchPosts(searchQuery, page, pageSize);

                allPosts = boardData.content.map(transformApiDataToPost);
                totalPages = boardData.totalPages;
                totalElements = boardData.totalElements;

                const postsHTML = allPosts.map(post => createPostCard(post)).join('');
                postList.innerHTML = postsHTML;

                if (totalPages > 1) {
                    createPagination();
                    pagination.style.display = 'flex';
                }

                loading.style.display = 'none';
            } catch (error) {
                console.error('검색 게시글 로딩 실패:', error);
                loading.style.display = 'none';
                postList.innerHTML = `
                    <div style="text-align: center; padding: 60px 20px; color: rgba(255, 255, 255, 0.8);">
                        <h3 style="margin-bottom: 20px;">검색 결과를 불러올 수 없습니다</h3>
                        <p style="margin-bottom: 20px;">서버 연결을 확인해주세요.</p>
                        <button onclick="handleSearch()" style="padding: 10px 20px; border: 2px solid white; background: transparent; color: white; border-radius: 8px; cursor: pointer;">
                            다시 시도
                        </button>
                    </div>
                `;
            }
        }

        function transformApiDataToPost(apiPost) {
            return {
                id: apiPost.boardId,
                title: apiPost.title,
                content: truncateContent(apiPost.content),
                author: apiPost.writerNickname,
                authorAvatar: apiPost.writerProfileImage || getDefaultImage('profile'),
                postImage: apiPost.boardTitleImage || getDefaultImage('post'),
                likes: apiPost.favoriteCount,
                views: apiPost.viewCount,
                comments: apiPost.commentCount,
                writeDate: formatDate(apiPost.writeDatetime)
            };
        }

        function formatNumber(num) {
            if (num >= 1000) {
                return (num / 1000).toFixed(1) + 'k';
            }
            return num.toString();
        }

        function createPostCard(post) {
            return `
                <div class="post-card">
                    <div class="post-header">
                        <div class="post-number">#${post.id}</div>
                        <div class="author-info">
                            <img src="${post.authorAvatar}" alt="${post.author}" class="author-avatar"
                                 onerror="this.src='${getDefaultImage('profile')}'">
                            <div style="display: flex; flex-direction: column; align-items: flex-start;">
                                <span class="author-name">${post.author}</span>
                                <span style="font-size: 12px; color: #999; margin-top: 2px;">${post.writeDate}</span>
                            </div>
                        </div>
                    </div>

                    <div class="post-content">
                        <img src="${post.postImage}" alt="게시글 이미지" class="post-image"
                             onerror="this.src='${getDefaultImage('post')}'">
                        <div class="post-text">
                            <h3 class="post-title" onclick="viewPost(${post.id})">${post.title}</h3>
                            <p class="post-preview">${post.content}</p>
                        </div>
                    </div>

                    <div class="post-stats">
                        <div class="stat-item">
                            <svg class="stat-icon" viewBox="0 0 24 24" fill="currentColor">
                                <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
                            </svg>
                            <span>${formatNumber(post.likes)}</span>
                        </div>
                        <div class="stat-item">
                            <svg class="stat-icon" viewBox="0 0 24 24" fill="currentColor">
                                <path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z"/>
                            </svg>
                            <span>${formatNumber(post.views)}</span>
                        </div>
                        <div class="stat-item">
                            <svg class="stat-icon" viewBox="0 0 24 24" fill="currentColor">
                                <path d="M21.99 4c0-1.1-.89-2-2-2H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h14l4 4-.01-18zM18 14H6v-2h12v2zm0-3H6V9h12v2zm0-3H6V6h12v2z"/>
                            </svg>
                            <span>${post.comments}</span>
                        </div>
                    </div>
                </div>
            `;
        }

        function getCurrentPagePosts() {
            return allPosts; // 현재는 API에서 받은 페이지 데이터를 그대로 사용
        }

        function createPagination() {
            const pagination = document.getElementById('pagination');
            let paginationHTML = '';

            // 이전 버튼
            paginationHTML += `
                <button class="pagination-btn" ${currentPage === 0 ? 'disabled' : ''} onclick="changePage(${currentPage - 1})">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                        <path d="M15.41 7.41L14 6l-6 6 6 6 1.41-1.41L10.83 12z"/>
                    </svg>
                </button>
            `;

            // 페이지 번호들 (API 페이지는 0부터 시작하지만 UI는 1부터 표시)
            const maxVisiblePages = 5;
            let startPage = Math.max(0, currentPage - Math.floor(maxVisiblePages / 2));
            let endPage = Math.min(totalPages - 1, startPage + maxVisiblePages - 1);

            // startPage 조정
            if (endPage - startPage + 1 < maxVisiblePages) {
                startPage = Math.max(0, endPage - maxVisiblePages + 1);
            }

            // 첫 페이지가 보이지 않으면 첫 페이지와 ... 추가
            if (startPage > 0) {
                paginationHTML += `<button class="pagination-btn" onclick="changePage(0)">1</button>`;
                if (startPage > 1) {
                    paginationHTML += `<span class="pagination-dots">...</span>`;
                }
            }

            // 페이지 번호 버튼들
            for (let i = startPage; i <= endPage; i++) {
                paginationHTML += `
                    <button class="pagination-btn ${i === currentPage ? 'active' : ''}" onclick="changePage(${i})">
                        ${i + 1}
                    </button>
                `;
            }

            // 마지막 페이지가 보이지 않으면 ... 과 마지막 페이지 추가
            if (endPage < totalPages - 1) {
                if (endPage < totalPages - 2) {
                    paginationHTML += `<span class="pagination-dots">...</span>`;
                }
                paginationHTML += `<button class="pagination-btn" onclick="changePage(${totalPages - 1})">${totalPages}</button>`;
            }

            // 다음 버튼
            paginationHTML += `
                <button class="pagination-btn" ${currentPage === totalPages - 1 ? 'disabled' : ''} onclick="changePage(${currentPage + 1})">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                        <path d="M10 6L8.59 7.41 13.17 12l-4.58 4.59L10 18l6-6z"/>
                    </svg>
                </button>
            `;

            // 페이지 정보
            const startItem = currentPage * pageSize + 1;
            const endItem = Math.min((currentPage + 1) * pageSize, totalElements);
            paginationHTML += `
                <div class="pagination-info">
                    ${startItem}-${endItem} / ${totalElements}
                </div>
            `;

            pagination.innerHTML = paginationHTML;
        }

        function changePage(page) {
            if (page < 0 || page >= totalPages || page === currentPage) return;

            currentPage = page;
                if (isSearching) {
                    loadSearchPosts();
                } else {
                    loadPosts();
                }

            // 페이지 변경 시 상단으로 부드럽게 스크롤
            window.scrollTo({
                top: 0,
                behavior: 'smooth'
            });
        }

        async function loadPosts() {
            const loading = document.getElementById('loading');
            const postList = document.getElementById('postList');
            const pagination = document.getElementById('pagination');

            loading.style.display = 'flex';
            pagination.style.display = 'none';

            try {
                const boardData = await fetchLatestPosts(currentPage, pageSize);

                // API 응답 데이터 처리
                allPosts = boardData.content.map(transformApiDataToPost);
                totalPages = boardData.totalPages;
                totalElements = boardData.totalElements;

                // 게시글 HTML 생성
                const postsHTML = allPosts.map(post => createPostCard(post)).join('');
                postList.innerHTML = postsHTML;

                // 페이징 생성 (게시글이 있을 때만)
                if (totalPages > 1) {
                    createPagination();
                    pagination.style.display = 'flex';
                }

                loading.style.display = 'none';

            } catch (error) {
                console.error('게시글 로딩 실패:', error);
                loading.style.display = 'none';

                // 에러 처리 - 사용자에게 알림
                postList.innerHTML = `
                    <div style="text-align: center; padding: 60px 20px; color: rgba(255, 255, 255, 0.8);">
                        <h3 style="margin-bottom: 20px;">게시글을 불러올 수 없습니다</h3>
                        <p style="margin-bottom: 20px;">서버 연결을 확인해주세요.</p>
                        <button onclick="loadPosts()" style="padding: 10px 20px; border: 2px solid white; background: transparent; color: white; border-radius: 8px; cursor: pointer;">
                            다시 시도
                        </button>
                    </div>
                `;
            }
        }

        function viewPost(postId) {
            const url = `${PAGE_ENDPOINTS.postDetail}?id=${postId}`;
            window.location = url;
        }

        // Enter 키로 검색
        document.getElementById('searchInput').addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                handleSearch();
            }
        });

document.getElementById('loginPage').onclick = function(e) {
             e.preventDefault(); // 기본 링크 이동 막기
             window.location.href = `${PAGE_ENDPOINTS.login}`;
        };

document.getElementById('signUpPage').onclick = function(e) {
             e.preventDefault(); // 기본 링크 이동 막기
             window.location.href = `${PAGE_ENDPOINTS.signUp}`;
        };

// 페이지 로드 시
document.addEventListener('DOMContentLoaded', async () => {
    loadPosts();
    userInfo = await getUserInfo();
    setHeader();
});


