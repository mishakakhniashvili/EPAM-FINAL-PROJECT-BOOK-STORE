const out = document.getElementById('out');
const ordersPre = document.getElementById('orders-pre');

function show(obj) {
    out.textContent = typeof obj === 'string' ? obj : JSON.stringify(obj, null, 2);
}
function encodePath(value) {
    return encodeURIComponent(value);
}
function toLocalDateTime(dtLocalValue) {
    // datetime-local -> "YYYY-MM-DDTHH:mm" (no seconds), add ":00"
    if (!dtLocalValue) return null;
    return dtLocalValue.length === 16 ? (dtLocalValue + ':00') : dtLocalValue;
}

async function api(method, url, body) {
    const opts = {
        method,
        credentials: 'same-origin',
        headers: { 'Accept': 'application/json' }
    };
    if (body !== undefined) {
        opts.headers['Content-Type'] = 'application/json';
        opts.body = JSON.stringify(body);
    }

    const res = await fetch(url, opts);
    const ct = (res.headers.get('content-type') || '').toLowerCase();

    let payload;
    if (ct.includes('application/json')) payload = await res.json();
    else payload = await res.text();

    if (!res.ok) {
        // Your GlobalExceptionHandler returns JSON with {timestamp,status,error,message}:contentReference[oaicite:11]{index=11}
        throw payload;
    }
    return payload;
}

function setActiveTab(view) {
    document.querySelectorAll('.tab')
        .forEach(b => b.classList.toggle('active', b.dataset.view === view));
    document.querySelectorAll('.view').forEach(v => v.hidden = true);
    document.getElementById('view-' + view).hidden = false;
}

document.querySelectorAll('.tab').forEach(btn => {
    btn.addEventListener('click', () => setActiveTab(btn.dataset.view));
});

document.getElementById('btn-clear').addEventListener('click', () => show('Ready.'));

// ---------- BOOKS ----------
const booksTbody = document.getElementById('books-tbody');
async function loadBooks() {
    show('Loading books...');
    const data = await api('GET', '/books');
    booksTbody.innerHTML = data.map(b => `
    <tr>
      <td>${escapeHtml(b.name ?? '')}</td>
      <td>${escapeHtml(b.author ?? '')}</td>
      <td>${escapeHtml(b.genre ?? '')}</td>
      <td>${escapeHtml(b.price ?? '')}</td>
      <td>${escapeHtml(b.language ?? '')}</td>
      <td>${escapeHtml(b.ageGroup ?? '')}</td>
      <td>${escapeHtml(b.publicationDate ?? '')}</td>
      <td>${escapeHtml(b.pages ?? '')}</td>
    </tr>
  `).join('');
    show({ ok: true, count: data.length, endpoint: 'GET /books' });
}
document.getElementById('btn-books-refresh')
    .addEventListener('click', () => loadBooks().catch(show));

// ---------- CLIENTS ----------
const clientsTbody = document.getElementById('clients-tbody');
async function loadClients() {
    show('Loading clients...');
    const data = await api('GET', '/clients');
    clientsTbody.innerHTML = data.map(c => `
    <tr>
      <td>${escapeHtml(c.email ?? '')}</td>
      <td>${escapeHtml(c.name ?? '')}</td>
      <td>${escapeHtml(c.balance ?? '')}</td>
    </tr>
  `).join('');
    show({ ok: true, count: data.length, endpoint: 'GET /clients' });
}
document.getElementById('btn-clients-refresh')
    .addEventListener('click', () => loadClients().catch(show));

document.getElementById('form-client-create').addEventListener('submit', async (e) => {
    e.preventDefault();
    const f = e.target;
    try {
        const body = {
            email: f.email.value.trim(),
            password: f.password.value,
            name: f.name.value.trim(),
            balance: Number(f.balance.value)
        };
        const created = await api('POST', '/clients', body);
        show(created);
        await loadClients();
    } catch (err) { show(err); }
});

document.getElementById('form-client-get').addEventListener('submit', async (e) => {
    e.preventDefault();
    const email = e.target.email.value.trim();
    try {
        const client = await api('GET', '/clients/' + encodePath(email));
        show(client);
    } catch (err) { show(err); }
});

document.getElementById('form-client-update').addEventListener('submit', async (e) => {
    e.preventDefault();
    const f = e.target;
    try {
        const pathEmail = f.pathEmail.value.trim();
        const body = {
            email: pathEmail, // backend sets email to path anyway:contentReference[oaicite:12]{index=12}
            password: f.password.value,
            name: f.name.value.trim(),
            balance: Number(f.balance.value)
        };
        const updated = await api('PUT', '/clients/' + encodePath(pathEmail), body);
        show(updated);
        await loadClients();
    } catch (err) { show(err); }
});

document.getElementById('form-client-delete').addEventListener('submit', async (e) => {
    e.preventDefault();
    const email = e.target.email.value.trim();
    try {
        await api('DELETE', '/clients/' + encodePath(email));
        show({ ok: true, deleted: email });
        await loadClients();
    } catch (err) { show(err); }
});

// ---------- EMPLOYEES ----------
const employeesTbody = document.getElementById('employees-tbody');
async function loadEmployees() {
    show('Loading employees...');
    const data = await api('GET', '/employees');
    employeesTbody.innerHTML = data.map(emp => `
    <tr>
      <td>${escapeHtml(emp.email ?? '')}</td>
      <td>${escapeHtml(emp.name ?? '')}</td>
      <td>${escapeHtml(emp.birthDate ?? '')}</td>
      <td>${escapeHtml(emp.phone ?? '')}</td>
    </tr>
  `).join('');
    show({ ok: true, count: data.length, endpoint: 'GET /employees' });
}
document.getElementById('btn-employees-refresh')
    .addEventListener('click', () => loadEmployees().catch(show));

document.getElementById('form-employee-create').addEventListener('submit', async (e) => {
    e.preventDefault();
    const f = e.target;
    try {
        const body = {
            email: f.email.value.trim(),
            password: f.password.value,
            name: f.name.value.trim(),
            birthDate: f.birthDate.value, // LocalDate
            phone: f.phone.value.trim()
        };
        const created = await api('POST', '/employees', body);
        show(created);
        await loadEmployees();
    } catch (err) { show(err); }
});

document.getElementById('form-employee-get').addEventListener('submit', async (e) => {
    e.preventDefault();
    const email = e.target.email.value.trim();
    try {
        const emp = await api('GET', '/employees/' + encodePath(email));
        show(emp);
    } catch (err) { show(err); }
});

document.getElementById('form-employee-update').addEventListener('submit', async (e) => {
    e.preventDefault();
    const f = e.target;
    try {
        const pathEmail = f.pathEmail.value.trim();
        const body = {
            email: pathEmail,
            password: f.password.value,
            name: f.name.value.trim(),
            birthDate: f.birthDate.value,
            phone: f.phone.value.trim()
        };
        const updated = await api('PUT', '/employees/' + encodePath(pathEmail), body);
        show(updated);
        await loadEmployees();
    } catch (err) { show(err); }
});

document.getElementById('form-employee-delete').addEventListener('submit', async (e) => {
    e.preventDefault();
    const email = e.target.email.value.trim();
    try {
        await api('DELETE', '/employees/' + encodePath(email));
        show({ ok: true, deleted: email });
        await loadEmployees();
    } catch (err) { show(err); }
});

// ---------- ORDERS ----------
async function loadOrdersBy(url) {
    show('Loading orders...');
    const data = await api('GET', url);
    ordersPre.textContent = JSON.stringify(data, null, 2);
    show({ ok: true, count: data.length, endpoint: url });
}

document.getElementById('form-orders-by-client').addEventListener('submit', async (e) => {
    e.preventDefault();
    const email = e.target.email.value.trim();
    loadOrdersBy('/orders/by_client/' + encodePath(email)).catch(show);
});

document.getElementById('form-orders-by-employee').addEventListener('submit', async (e) => {
    e.preventDefault();
    const email = e.target.email.value.trim();
    loadOrdersBy('/orders/by_employee/' + encodePath(email)).catch(show);
});

const itemsWrap = document.getElementById('items-wrap');
function addItemRow(bookName = '', quantity = 1) {
    const row = document.createElement('div');
    row.className = 'grid cols3';
    row.innerHTML = `
    <div>
      <label>Book name</label>
      <input class="item-book" type="text" required value="${escapeAttr(bookName)}" placeholder="Exact book name"/>
    </div>
    <div>
      <label>Quantity</label>
      <input class="item-qty" type="number" min="1" step="1" required value="${quantity}"/>
    </div>
    <div class="actions" style="align-items:end">
      <button type="button" class="danger btn-remove">Remove</button>
    </div>
  `;
    row.querySelector('.btn-remove').addEventListener('click', () => row.remove());
    itemsWrap.appendChild(row);
}
document.getElementById('btn-add-item').addEventListener('click', () => addItemRow());

document.getElementById('form-order-create').addEventListener('submit', async (e) => {
    e.preventDefault();
    const f = e.target;

    try {
        const items = Array.from(itemsWrap.querySelectorAll('.grid')).map(row => ({
            bookName: row.querySelector('.item-book').value.trim(),
            quantity: Number(row.querySelector('.item-qty').value)
        })).filter(i => i.bookName);

        const body = {
            clientEmail: f.clientEmail.value.trim(),
            employeeEmail: f.employeeEmail.value.trim(),
            orderDate: toLocalDateTime(f.orderDate.value),
            bookItems: items
            // price is calculated on backend:contentReference[oaicite:13]{index=13}
        };

        const created = await api('POST', '/orders', body);
        show(created);
        ordersPre.textContent = JSON.stringify([created], null, 2);
    } catch (err) { show(err); }
});

// helpers
function escapeHtml(s) {
    return String(s)
        .replaceAll('&', '&amp;').replaceAll('<', '&lt;').replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;').replaceAll("'", '&#039;');
}
function escapeAttr(s) { return escapeHtml(s).replaceAll('\n', ' '); }

// init defaults
(function init() {
    // add 1 default item row
    addItemRow('', 1);
    // default datetime-local = now
    const dt = new Date();
    const pad = n => String(n).padStart(2,'0');
    const local = dt.getFullYear() + '-' + pad(dt.getMonth()+1) + '-' + pad(dt.getDate()) +
        'T' + pad(dt.getHours()) + ':' + pad(dt.getMinutes());
    document.querySelector('[name="orderDate"]').value = local;

    // load books first
    loadBooks().catch(show);
})();
