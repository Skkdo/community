    function togglePassword(inputId) {
        const passwordInput = document.getElementById(inputId);
        const eyeIcon = inputId === 'password' ? document.getElementById('eyeIcon') : document.getElementById('eyeIcon2');

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

        setTimeout(() => {
            alertElement.style.display = 'none';
        }, 5000);
    }

    async function handleSignup(formData) {
        try {
            const response = await fetch(`${API_BASE_URL}${API_ENDPOINTS.signUp}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(formData)
            });

            const data = await response.json();

            if (response.ok) {
                showAlert('회원가입이 완료되었습니다! 로그인 페이지로 이동합니다.', 'success');

                setTimeout(() => {
                    window.location.href = `${PAGE_ENDPOINTS.login}`;
                }, 2000);
            } else {
                showAlert(data.message || '회원가입에 실패했습니다.', 'error');
            }
        } catch (error) {
            console.error('Signup error:', error);
        }
    }

    document.getElementById('loginPage').onclick = function(e) {
                 e.preventDefault(); // 기본 링크 이동 막기
                 window.location.href = `${PAGE_ENDPOINTS.login}`;
            };

  document.getElementById('signupForm').addEventListener('submit', async function(e) {
      e.preventDefault();
      const form = e.target;
          const formData = {
            email: form.email.value.trim(),
            nickname: form.nickname.value.trim(),
            password: form.password.value,
          };

      await handleSignup(formData);
  });