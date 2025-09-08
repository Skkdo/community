        const API_BASE_URL = 'community-alb-1159771291.ap-northeast-2.elb.amazonaws.com';
        const API_ENDPOINTS = {
            latestList: '/api/board/latest-list',
            post: '/api/board',
            login: '/api/user/auth/sign-in',
            signUp: '/api/user/auth/sign-up',
            like: '/api/favorite',
            comment: '/api/comment',
            user: '/api/user',
            file: '/file/upload-file',
        };
        const PAGE_ENDPOINTS = {
            main: 'index.html',
            postDetail: 'boardDetail.html',
            login: 'login.html',
            signUp: 'signUp.html',
            myPage: 'myPage.html',
            writePost: "writePost.html"
        };

        function formatDate(dateString) {
            const date = new Date(dateString);
            const now = new Date();
            const diffTime = now - date;
            const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24));
            const diffHours = Math.floor(diffTime / (1000 * 60 * 60));
            const diffMinutes = Math.floor(diffTime / (1000 * 60));

            if (diffDays > 0) {
                return `${diffDays}일 전`;
            } else if (diffHours > 0) {
                return `${diffHours}시간 전`;
            } else if (diffMinutes > 0) {
                return `${diffMinutes}분 전`;
            } else {
                return '방금 전';
            }
        }

        function getDefaultImage(type) {
            const defaultImages = {
                profile: '../assets/profile.png',
                post: '../assets/board.png'
            };
            return defaultImages[type];
        }

        function home() {
            const url = `${PAGE_ENDPOINTS.main}`;
            window.location = url;
        }

        function myPage() {
            const url = `${PAGE_ENDPOINTS.myPage}`;
            window.location = url;
        }

        function writePost(postId) {
            const url = `${PAGE_ENDPOINTS.writePost}?id=${postId}`;
            window.location = url;
        }

        async function getUserInfo() {
            try{
                const res = await fetch(`${API_BASE_URL}${API_ENDPOINTS.user}/`, {
                    method: 'GET',
                    credentials: 'include',
                });
                const json = await res.json();
                localStorage.setItem('userInfo', JSON.stringify(json.data));
                return json.data;
            }catch(err) {
                console.error('로그인 정보 로딩 실패:', err);
                return null;
            }
        }

        function setHeader() {
            const myPageSection = document.getElementById('myPageSection');
            const authSection = document.getElementById('authSection');
            if(!userInfo) {
                myPageSection.style.display = 'none';
                authSection.style.display = 'flex';
            } else {
                myPageSection.style.display = 'flex';
                authSection.style.display = 'none';

                document.getElementById('userAvatar').src = userInfo.profileImage || getDefaultImage('profile');
                document.getElementById('userNickname').textContent = userInfo.nickname;
            }
        }
