  // 전역 변수
  let boardImageList = [];
  const uploadSection = document.getElementById('uploadSection');
  const fileInput = document.getElementById('fileInput');
  const uploadedImages = document.getElementById('uploadedImages');
  const loadingSpinner = document.getElementById('loadingSpinner');
  const alertBox = document.getElementById('alertBox');
  const submitBtn = document.getElementById('submitBtn');
  let isEditMode = false;
  let currentPostId = null;

  function getPostIdFromUrl() {
      const urlParams = new URLSearchParams(window.location.search);
      return urlParams.get('id');
  }

  // 알림 표시 함수
  function showAlert(message, type) {
      alertBox.textContent = message;
      alertBox.className = `alert ${type}`;
      alertBox.style.display = 'block';
      setTimeout(() => {
          alertBox.style.display = 'none';
      }, 5000);
  }

  // 파일 업로드 영역 클릭 이벤트
  uploadSection.addEventListener('click', () => {
      fileInput.click();
  });

  // 드래그 앤 드롭 이벤트
  uploadSection.addEventListener('dragover', (e) => {
      e.preventDefault();
      uploadSection.classList.add('dragover');
  });

  uploadSection.addEventListener('dragleave', (e) => {
      e.preventDefault();
      uploadSection.classList.remove('dragover');
  });

  uploadSection.addEventListener('drop', (e) => {
      e.preventDefault();
      uploadSection.classList.remove('dragover');
      const files = Array.from(e.dataTransfer.files);
      handleFiles(files);
  });

  // 파일 선택 이벤트
  fileInput.addEventListener('change', (e) => {
      const files = Array.from(e.target.files);
      handleFiles(files);
  });

  // 파일 처리 함수
  function handleFiles(files) {
      const imageFiles = files.filter(file => file.type.startsWith('image/'));

      if (imageFiles.length === 0) {
          showAlert('이미지 파일만 업로드 가능합니다.', 'error');
          return;
      }

      imageFiles.forEach(file => {
          if (file.size > 5 * 1024 * 1024) { // 5MB 제한
              showAlert(`${file.name}은(는) 파일 크기가 너무 큽니다. (최대 5MB)`, 'error');
              return;
          }
          uploadImage(file);
      });
  }

  // 이미지 업로드 함수
  async function uploadImage(file) {
      const imageId = Date.now() + Math.random();
      const imageItem = createImageItem(file, imageId);
      uploadedImages.appendChild(imageItem);

      // 로딩 스피너 표시
      loadingSpinner.style.display = 'block';

      try {
           const formData = new FormData();
           formData.append('file', file);
           const response = await fetch(`${API_BASE_URL}${API_ENDPOINTS.file}`, {
               method: 'POST',
               body: formData
           });
           const url = await response.text();

          // 업로드 성공
          boardImageList.push(url);
          imageItem.dataset.url = url;
          imageItem.querySelector('.image-info').innerHTML = `
              <div>업로드 완료</div>
              <div style="color: #4CAF50; font-size: 10px;">✓</div>
          `;
      } catch (error) {
          // 업로드 실패
          imageItem.remove();
          showAlert(`${file.name} 업로드에 실패했습니다.`, 'error');
          console.error('Upload error:', error);
      } finally {
          loadingSpinner.style.display = 'none';
      }
  }

  // 이미지 아이템 생성 함수
  function createImageItem(file, imageId) {
      const imageItem = document.createElement('div');
      imageItem.className = 'image-item';
      imageItem.dataset.id = imageId;

      const img = document.createElement('img');
      img.className = 'image-preview';
      img.src = URL.createObjectURL(file);
      img.onload = () => URL.revokeObjectURL(img.src);

      const removeBtn = document.createElement('button');
      removeBtn.className = 'remove-image';
      removeBtn.innerHTML = '×';
      removeBtn.onclick = (e) => {
          e.stopPropagation();
          removeImage(imageItem);
      };

      const imageInfo = document.createElement('div');
      imageInfo.className = 'image-info';
      imageInfo.innerHTML = `
          <div>${file.name}</div>
          <div style="color: #ff9800;">업로드 중...</div>
      `;

      imageItem.appendChild(img);
      imageItem.appendChild(removeBtn);
      imageItem.appendChild(imageInfo);

      return imageItem;
  }

  // 이미지 제거 함수
  function removeImage(imageItem) {
      const url = imageItem.dataset.url;
      if (url) {
          const index = boardImageList.indexOf(url);
          if (index > -1) {
              boardImageList.splice(index, 1);
          }
      }
      imageItem.remove();
      showAlert('이미지가 제거되었습니다.', 'success');
  }

  // 폼 제출 처리
  document.getElementById('postForm').addEventListener('submit', async (e) => {
      e.preventDefault();

      const title = document.getElementById('title').value.trim();
      const content = document.getElementById('content').value.trim();

      // 유효성 검사
      if (!title) {
          showAlert('제목을 입력해주세요.', 'error');
          document.getElementById('title').classList.add('error');
          return;
      }

      if (!content) {
          showAlert('내용을 입력해주세요.', 'error');
          document.getElementById('content').classList.add('error');
          return;
      }

      // 로딩 상태
      submitBtn.disabled = true;
      submitBtn.textContent = isEditMode ? '게시글 수정 중...' : '게시글 작성 중...';

      try {
          const postData = {
              title: title,
              content: content,
              boardImageList: boardImageList
          };
          let response;
        if (isEditMode) {
            // 수정 모드
            response = await fetch(`${API_BASE_URL}${API_ENDPOINTS.post}/${currentPostId}`, {
                method: 'PATCH',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'include',
                body: JSON.stringify(postData)
            });
        } else {
            // 작성 모드
            response = await fetch(`${API_BASE_URL}${API_ENDPOINTS.post}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'include',
                body: JSON.stringify(postData)
            });
        }

           if (!response.ok) {
               throw new Error('오류');
               document.getElementById('postForm').reset();
               boardImageList = [];
               uploadedImages.innerHTML = '';
           }

            myPage();

      } catch (error) {
          showAlert('요청에 실패했습니다.', 'error');
          console.error('Submit error:', error);
      } finally {
          submitBtn.disabled = false;
          submitBtn.textContent = isEditMode ? '게시글 수정하기' : '게시글 작성하기';
      }
  });

  // 입력 필드 에러 상태 제거
  document.getElementById('title').addEventListener('input', () => {
      document.getElementById('title').classList.remove('error');
  });

  document.getElementById('content').addEventListener('input', () => {
      document.getElementById('content').classList.remove('error');
  });

  document.addEventListener('DOMContentLoaded', async () => {
      const topTitle = document.getElementById('top-title');

      const urlParams = new URLSearchParams(window.location.search);
      const postId = urlParams.get('id');

      if(postId) {
            isEditMode = true;
            currentPostId = postId;
            submitBtn.textContent = '게시글 수정하기';
            topTitle.textContent = '게시글 수정하기';
      }
  });