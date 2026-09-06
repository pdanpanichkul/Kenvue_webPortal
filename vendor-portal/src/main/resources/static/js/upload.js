// Handles drag-and-drop + click-to-upload UI feedback on the upload form.
// The actual file submission is a normal multipart form POST handled by
// PurchaseOrderController on the Java side - this file is purely cosmetic/UX.

document.addEventListener('DOMContentLoaded', () => {
  const fileInputs = document.querySelectorAll('.file-input');
  const dropzones = document.querySelectorAll('.dropzone');

  function handleFileSelection(inputElement, file) {
    if (!file) return;

    const container = inputElement.closest('.bg-white');
    const fileNameDisplay = container.querySelector('.file-name');
    const statusBadge = container.querySelector('.status-badge');

    fileNameDisplay.textContent = `Attached: ${file.name} (${(file.size / (1024 * 1024)).toFixed(2)} MB)`;
    fileNameDisplay.classList.remove('text-slate-400');
    fileNameDisplay.classList.add('text-emerald-600', 'font-semibold');

    if (statusBadge) {
      statusBadge.textContent = 'Status: Attached';
      statusBadge.className = 'status-badge inline-flex items-center px-2.5 py-1 rounded-full text-xs font-medium bg-emerald-100 text-emerald-800 self-start';
    }
  }

  fileInputs.forEach(input => {
    input.addEventListener('change', (e) => {
      handleFileSelection(input, e.target.files[0]);
    });
  });

  dropzones.forEach(zone => {
    const input = zone.querySelector('.file-input');

    ['dragenter', 'dragover'].forEach(eventName => {
      zone.addEventListener(eventName, (e) => {
        e.preventDefault();
        zone.classList.add('border-blue-500', 'bg-blue-50');
      });
    });

    ['dragleave', 'drop'].forEach(eventName => {
      zone.addEventListener(eventName, (e) => {
        e.preventDefault();
        zone.classList.remove('border-blue-500', 'bg-blue-50');
      });
    });

    zone.addEventListener('drop', (e) => {
      const files = e.dataTransfer.files;
      if (files.length > 0) {
        input.files = files;
        handleFileSelection(input, files[0]);
      }
    });
  });
});
