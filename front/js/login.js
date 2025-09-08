  function togglePassword() {
      const passwordInput = document.getElementById('password');
      const eyeIcon = document.getElementById('eyeIcon');

      if (passwordInput.type === 'password') {
          passwordInput.type = 'text';
          eyeIcon.innerHTML = `
              <path d="M12 7c2.76 0 5 2.24 5 5 0 .65-.13 1.26-.36 1.83l2.92 2.92c1.51-1.26 2.7-2.89 3.43-4.75-1.73-4.39-6-7.5-11-7.5-1.4 0-2.74.25-3.98.7l2.16 2.16C10.74 7.13 11.35 7 12 7zM2 4.27l2.28 2.28.46.46C3.08 8.3 1.78 10.02 1 12c1.73 4.39 6 7.5 11 7.5 1.55 0 3.03-.3 4.38-.84l.42.42L19.73 22 21 20.73 3.27 3 2 4.27zM7.53 9.8l1.55 1.55c-.05.21-.08.43-.08.65 0 1.66 1.34 3 3 3 .22 0 .44-.03.65-.08l1.55 1.55c-.67.33-1.41.53-2.2.53-2.76 0-5-2.24-5-5 0-.79.2-1.53.53-2.2zm4.31-.78l3.15 3.15.02-.16c0-1.66-1.34-3-3-3l-.17.01z"/>
          `;
      } else {
          passwordInput.type = 'password';
          eyeIcon.innerHTML = `
              <path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z"/>
          `;
      }
  }

  function showAlert(message, type = 'error') {
      const alertElement = document.getElementById('alertMessage');
      alertElement.textContent = message;
      alertElement.className = `alert ${type}`;
      alertElement.style.display = 'block';

      // 3초 후 자동으로 숨기기
      setTimeout(() => {
          alertElement.style.display = 'none';
      }, 3000);
  }

  function setLoading(loading) {
      const loginBtn = document.getElementById('loginBtn');
      const btnText = document.getElementById('btnText');

      if (loading && loading !== "undefined") {
          loginBtn.disabled = true;
          btnText.textContent = '이미 로그인 중...';
      } else {
          loginBtn.disabled = false;
          btnText.textContent = '로그인';
      }
  }

  async function handleLogin(email, password) {
      try {
          const url = `${API_BASE_URL}${API_ENDPOINTS.login}`;

          const response = await fetch(url, {
              method: 'POST',
              credentials: 'include',
              headers: {
                  'Content-Type': 'application/json',
              },
              body: JSON.stringify({
                  email: email,
                  password: password,
              })
          });

          const data = await response.json();

          if (response.ok) {
                  const userInfo = await getUserInfo();
                  localStorage.setItem('userInfo', JSON.stringify(userInfo));

                  // 1초 후 메인 페이지로 리다이렉트
                  setTimeout(() => {
                      window.location.href = `${PAGE_ENDPOINTS.main}`;
                  }, 1000);

          } else {
              // 실패 시 서버에서 받은 에러 메시지 표시
              showAlert(data.message || '로그인에 실패했습니다.', 'error');
          }
      } catch (error) {
          console.error('Login error:', error);
      }
  }

  document.getElementById('loginForm').addEventListener('submit', async function(e) {
      e.preventDefault();

      const email = document.getElementById('email').value.trim();
      const password = document.getElementById('password').value;

      await handleLogin(email, password);
      setLoading(JSON.parse(localStorage.getItem('userInfo')));
  });

  document.getElementById('signUpPage').onclick = function(e) {
               e.preventDefault(); // 기본 링크 이동 막기
               window.location.href = `${PAGE_ENDPOINTS.signUp}`;
          };

  // 페이지 로드 시 로그인 유무 표시
  document.addEventListener('DOMContentLoaded', function() {
      setLoading(JSON.parse(localStorage.getItem('userInfo')));
  });