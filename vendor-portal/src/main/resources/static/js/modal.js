// Powers the "Uploaded Documents" preview modal on the summary page.
// Document data comes live from DocumentController's JSON endpoint,
// so this list always reflects what's actually stored server-side.

const modalOverlay = document.getElementById('preview-modal-overlay');
const modalWindow = document.getElementById('preview-modal');
const modalPoText = document.getElementById('modal-po-reference');
const modalDocList = document.getElementById('modal-document-list');

const DOC_ICONS = {
  INVOICE: 'file-text',
  DELIVERY_ORDER: 'image',
  COA: 'file-text',
  OTHER: 'file'
};

async function openPreviewModal(poReference) {
  modalPoText.textContent = `Reference: ${poReference}`;
  modalDocList.innerHTML = '<p class="text-sm text-slate-400">Loading...</p>';
  modalOverlay.classList.remove('hidden');
  modalWindow.classList.remove('hidden');
  document.body.style.overflow = 'hidden';

  try {
    const res = await fetch(`/api/po/${encodeURIComponent(poReference)}/documents`);
    if (!res.ok) throw new Error('Failed to load documents');
    const docs = await res.json();

    if (docs.length === 0) {
      modalDocList.innerHTML = '<p class="text-sm text-slate-400">No documents uploaded yet.</p>';
      return;
    }

    modalDocList.innerHTML = docs.map(doc => `
      <div class="flex items-center justify-between p-3 border border-slate-200 rounded-lg hover:border-blue-300 transition-colors group">
        <div class="flex items-center gap-3">
          <div class="bg-blue-100 text-blue-600 p-2 rounded-lg">
            <i data-lucide="${DOC_ICONS[doc.documentType] || 'file'}" class="w-5 h-5"></i>
          </div>
          <div>
            <p class="text-sm font-semibold text-slate-800 group-hover:text-blue-700">${escapeHtml(doc.fileName)}</p>
            <p class="text-xs text-slate-500">Uploaded ${new Date(doc.uploadedAt).toLocaleDateString()} • ${doc.sizeMb} MB</p>
          </div>
        </div>
        <a href="/documents/${doc.id}/download" class="text-slate-400 hover:text-blue-600 px-2 py-1 flex items-center gap-1 text-xs font-medium">
          <i data-lucide="download" class="w-4 h-4"></i> Download
        </a>
      </div>
    `).join('');

    lucide.createIcons();
  } catch (err) {
    modalDocList.innerHTML = '<p class="text-sm text-red-500">Could not load documents. Please try again.</p>';
    console.error(err);
  }
}

function closePreviewModal() {
  modalOverlay.classList.add('hidden');
  modalWindow.classList.add('hidden');
  document.body.style.overflow = 'auto';
}

function escapeHtml(str) {
  const div = document.createElement('div');
  div.textContent = str;
  return div.innerHTML;
}

modalOverlay.addEventListener('click', closePreviewModal);

// Wire up every "View Uploads" button on the summary page.
// (Buttons carry their PO reference in a data-po-reference attribute
// rather than an inline onclick, since Thymeleaf disallows string
// concatenation inside event-handler attributes for security reasons.)
document.querySelectorAll('.view-uploads-btn').forEach(btn => {
  btn.addEventListener('click', () => {
    openPreviewModal(btn.getAttribute('data-po-reference'));
  });
});
