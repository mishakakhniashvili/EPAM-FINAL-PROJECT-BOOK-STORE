// ---------- Books UI (access) ----------
function applyBooksUi(me) {
    const roles = me?.roles || [];
    const isEmployee = roles.includes('ROLE_EMPLOYEE');

    const booksAdmin = document.getElementById('books-admin');
    if (!booksAdmin) return;

    booksAdmin.style.display = isEmployee ? '' : 'none';
    booksAdmin.hidden = !isEmployee;
}

// ---------- BOOKS ----------
const booksTbody = document.getElementById('books-tbody');

let booksCache = [];

const bookModal = document.getElementById('book-modal');
const bookModalTitle = document.getElementById('book-modal-title');
const bookModalBody = document.getElementById('book-modal-body');
const bookModalClose = document.getElementById('book-modal-close');

function openBookModal(book) {
    if (!bookModal || !bookModalBody || !bookModalTitle) {
        // fallback if modal markup is missing
        alert(`Characteristics: ${book?.characteristics ?? ''}\n\nDescription: ${book?.description ?? ''}`);
        return;
    }

    bookModalTitle.textContent = book?.name ? `Book: ${book.name}` : 'Book details';

    const safe = (v) => escapeHtml(v ?? '');
    bookModalBody.innerHTML = `
    <div class="kv"><div class="k">Author</div><div class="v">${safe(book.author)}</div></div>
    <div class="kv"><div class="k">Genre</div><div class="v">${safe(book.genre)}</div></div>
    <div class="kv"><div class="k">Price</div><div class="v">${safe(book.price)}</div></div>
    <div class="kv"><div class="k">Language</div><div class="v">${safe(book.language)}</div></div>
    <div class="kv"><div class="k">Age group</div><div class="v">${safe(book.ageGroup)}</div></div>
    <div class="kv"><div class="k">Publication date</div><div class="v">${safe(book.publicationDate)}</div></div>
    <div class="kv"><div class="k">Pages</div><div class="v">${safe(book.pages)}</div></div>
    <hr style="border:0;border-top:1px solid #1c2632" />
    <div class="kv"><div class="k">Characteristics</div><div class="v">${safe(book.characteristics)}</div></div>
    <div class="kv"><div class="k">Description</div><div class="v">${safe(book.description)}</div></div>
  `;

    bookModal.hidden = false;
}

function closeBookModal() {
    if (bookModal) bookModal.hidden = true;
}

bookModalClose?.addEventListener('click', closeBookModal);
bookModal?.querySelector('.modal-backdrop')?.addEventListener('click', closeBookModal);
window.addEventListener('keydown', (e) => {
    if (e.key === 'Escape' && bookModal && !bookModal.hidden) closeBookModal();
});

async function loadBooks() {
    show(t('out_loading_books'));
    const data = await api('GET', '/books');
    booksCache = Array.isArray(data) ? data : [];

    booksTbody.innerHTML = booksCache.map((b, i) => `
    <tr>
      <td>${escapeHtml(b.name ?? '')}</td>
      <td>${escapeHtml(b.author ?? '')}</td>
      <td>${escapeHtml(b.genre ?? '')}</td>
      <td>${escapeHtml(b.price ?? '')}</td>
      <td>${escapeHtml(b.language ?? '')}</td>
      <td>${escapeHtml(b.ageGroup ?? '')}</td>
      <td>${escapeHtml(b.publicationDate ?? '')}</td>
      <td>${escapeHtml(b.pages ?? '')}</td>
      <td><button type="button" class="btn-book-details" data-idx="${i}">View</button></td>
    </tr>
  `).join('');

    show({ok: true, count: booksCache.length, endpoint: 'GET /books'});
}

booksTbody?.addEventListener('click', (e) => {
    const btn = e.target.closest('.btn-book-details');
    if (!btn) return;

    const idx = Number(btn.dataset.idx);
    const book = booksCache[idx];
    if (book) openBookModal(book);
});

document.getElementById('btn-books-refresh')
    ?.addEventListener('click', () => loadBooks().catch(show));