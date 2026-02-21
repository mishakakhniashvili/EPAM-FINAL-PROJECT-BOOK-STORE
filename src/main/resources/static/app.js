// app.js (full, with CSRF fix via /csrf response token)

const out = document.getElementById('out');
const ordersPre = document.getElementById('orders-pre');
let CSRF = null;

async function ensureCsrf() {
    if (CSRF) return CSRF;
    const res = await fetch('/csrf', { credentials: 'same-origin' });
    CSRF = await res.json(); // { token, headerName, parameterName }
    return CSRF;
}

function applyBooksUi(me) {
    const roles = me?.roles || [];
    const isEmployee = roles.includes('ROLE_EMPLOYEE');

    const booksAdmin = document.getElementById('books-admin');
    if (!booksAdmin) return;

    booksAdmin.style.display = isEmployee ? '' : 'none';
    booksAdmin.hidden = !isEmployee;
}


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
    const upper = method.toUpperCase();

    const opts = {
        method: upper,
        credentials: 'same-origin',
        headers: { 'Accept': 'application/json' }
    };

    // ✅ CSRF for unsafe methods (POST/PUT/PATCH/DELETE)
    const unsafe = !['GET', 'HEAD', 'OPTIONS'].includes(upper);
    if (unsafe) {
        const csrf = await ensureCsrf();
        // headerName is usually "X-XSRF-TOKEN", but we use what backend says
        opts.headers[csrf.headerName] = csrf.token;
    }

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
        if (payload === '' || payload == null) {
            throw { status: res.status, statusText: res.statusText, url };
        }
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

// ---------- i18n (front-end) ----------
function normalizeLang(code) {
    const c = (code || '').toLowerCase();
    if (c === 'ge') return 'ka'; // common mistake: ge -> ka (Georgian)
    return c || 'en';
}

function getLang() {
    const qs = new URLSearchParams(window.location.search);
    const fromParam = qs.get('lang');
    if (fromParam) return normalizeLang(fromParam);

    // fallback to LOCALE cookie set by Spring (CookieLocaleResolver)
    const cookieLocale = getCookie('LOCALE');
    if (cookieLocale) return normalizeLang(cookieLocale);

    return 'en';
}

const I18N = {
    en: {
        title: 'Book Store – Simple UI',
        h1: 'Book Store – Simple UI',
        tab_books: 'Books',
        tab_clients: 'Clients',
        tab_employees: 'Employees',
        tab_orders: 'Orders',
        btn_login: 'Login',
        btn_logout: 'Logout',
        btn_clear: 'Clear',
        btn_refresh_books: 'Refresh books',
        btn_list_clients: 'List clients',
        btn_list_employees: 'List employees',
        auth_guest: 'Auth: guest',
        out_ready: 'Ready.',
        out_loading_books: 'Loading books...',
        out_loading_clients: 'Loading clients...',
        out_loading_employees: 'Loading employees...',
        out_loading_orders: 'Loading orders...'
    },
    ka: {
        title: 'წიგნების მაღაზია – მარტივი UI',
        h1: 'წიგნების მაღაზია – მარტივი UI',
        tab_books: 'წიგნები',
        tab_clients: 'კლიენტები',
        tab_employees: 'თანამშრომლები',
        tab_orders: 'შეკვეთები',
        btn_login: 'შესვლა',
        btn_logout: 'გამოსვლა',
        btn_clear: 'გასუფთავება',
        btn_refresh_books: 'წიგნების განახლება',
        btn_list_clients: 'კლიენტების სია',
        btn_list_employees: 'თანამშრომლების სია',
        auth_guest: 'ავტორიზაცია: სტუმარი',
        out_ready: 'მზადაა.',
        out_loading_books: 'იტვირთება წიგნები...',
        out_loading_clients: 'იტვირთება კლიენტები...',
        out_loading_employees: 'იტვირთება თანამშრომლები...',
        out_loading_orders: 'იტვირთება შეკვეთები...'
    }
};

let LANG = 'en';

function t(key) {
    return (I18N[LANG] && I18N[LANG][key]) || I18N.en[key] || key;
}

function applyI18n() {
    LANG = getLang();
    document.documentElement.lang = LANG;

    document.title = t('title');
    const h1 = document.querySelector('header h1');
    if (h1) h1.textContent = t('h1');

    const tab = (view, textKey) => {
        const el = document.querySelector(`.tab[data-view="${view}"]`);
        if (el) el.textContent = t(textKey);
    };
    tab('books', 'tab_books');
    tab('clients', 'tab_clients');
    tab('employees', 'tab_employees');
    tab('orders', 'tab_orders');

    const setText = (id, key) => {
        const el = document.getElementById(id);
        if (el) el.textContent = t(key);
    };

    setText('btn-login', 'btn_login');
    setText('btn-logout', 'btn_logout');
    setText('btn-clear', 'btn_clear');
    setText('btn-books-refresh', 'btn_refresh_books');
    setText('btn-clients-refresh', 'btn_list_clients');
    setText('btn-employees-refresh', 'btn_list_employees');
    highlightLangButtons();
}

// ---------- UI Access (tabs) ----------
function setTabVisible(view, visible) {
    const tab = document.querySelector(`.tab[data-view="${view}"]`);
    const panel = document.getElementById('view-' + view);

    if (tab) tab.style.display = visible ? '' : 'none';
    if (panel && !visible) panel.hidden = true;
}

function applyUiAccess(roles) {
    const isEmployee = roles.includes('ROLE_EMPLOYEE');

    // Clients/Employees pages only for employees
    setTabVisible('clients', isEmployee);
    setTabVisible('employees', isEmployee);

    // If user is on hidden view -> go to Books
    const activeTab = document.querySelector('.tab.active')?.dataset?.view;
    if (!isEmployee && (activeTab === 'clients' || activeTab === 'employees')) {
        setActiveTab('books');
    }
}

// ---------- Auth box ----------
const authBox = document.getElementById('auth-box');
const btnLogin = document.getElementById('btn-login');
const btnLogout = document.getElementById('btn-logout');

function renderAuth(me) {
    if (!authBox || !btnLogin || !btnLogout) return;

    if (!me || !me.username) {
        authBox.textContent = t('auth_guest');
        btnLogin.style.display = '';
        btnLogout.style.display = 'none';
        return;
    }
    const roles = (me.roles || []).join(',');
    authBox.textContent = `Auth: ${me.username} (${roles})`;
    btnLogin.style.display = 'none';
    btnLogout.style.display = '';
}

btnLogin?.addEventListener('click', () => window.location.href = '/login');

btnLogout?.addEventListener('click', async () => {
    try {
        // optional: ensure CSRF is loaded before POST /logout
        await ensureCsrf().catch(() => {});
        await api('POST', '/logout');
    } catch (e) {
        show(e);
        return;
    }

    // hard redirect back to UI (keeps ?lang=...)
    const url = new URL(window.location.href);
    url.pathname = '/index.html';
    window.location.replace(url.toString());
});

// ---------- Orders UI rules ----------
function applyOrdersUi(me) {
    const roles = me?.roles || [];
    const username = me?.username || '';
    const isEmployee = roles.includes('ROLE_EMPLOYEE');
    const isClient = roles.includes('ROLE_CLIENT');

    const byEmployeeForm = document.getElementById('form-orders-by-employee');
    const createOrderForm = document.getElementById('form-order-create');
    const deleteForm = document.getElementById('form-order-delete');

    if (deleteForm) deleteForm.style.display = isEmployee ? '' : 'none';
    if (byEmployeeForm) byEmployeeForm.style.display = isEmployee ? '' : 'none';

    // Both employee and client can create orders
    if (createOrderForm) {
        const card = createOrderForm.closest('.card');
        if (card) card.style.display = (isEmployee || isClient) ? '' : 'none';

        // Hide/disable employeeEmail field (backend sets it)
        const empInput = createOrderForm.querySelector('input[name="employeeEmail"]');
        const empWrap = empInput?.closest('div');
        if (empInput) {
            empInput.required = false;
            empInput.disabled = true;
            empInput.value = '';
        }
        if (empWrap) empWrap.style.display = 'none';

        // Client email behavior:
        const clientInput = createOrderForm.querySelector('input[name="clientEmail"]');
        const clientWrap = clientInput?.closest('div');

        if (isClient && clientInput) {
            clientInput.value = username;
            clientInput.readOnly = true;
            clientInput.required = false;
        }
        if (isEmployee && clientInput) {
            clientInput.readOnly = false;
            clientInput.required = true;
            if (!clientInput.value) clientInput.value = '';
            if (clientWrap) clientWrap.style.display = '';
        }
    }
}

async function loadMeAndApplyAccess() {
    try {
        const me = await api('GET', '/me');
        renderAuth(me);
        applyUiAccess(me.roles || []);
        applyOrdersUi(me);
        applyBooksUi(me);
    } catch (e) {
        renderAuth(null);
        applyUiAccess([]);
        applyOrdersUi(null);
        applyBooksUi(null);
    }
}
// ---------- BOOKS ----------
const booksTbody = document.getElementById('books-tbody');

async function loadBooks() {
    show(t('out_loading_books'));
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
    ?.addEventListener('click', () => loadBooks().catch(show));

// ---------- CLIENTS ----------
const clientsTbody = document.getElementById('clients-tbody');

async function loadClients() {
    show(t('out_loading_clients'));
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
    ?.addEventListener('click', () => loadClients().catch(show));

document.getElementById('form-client-create')?.addEventListener('submit', async (e) => {
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

document.getElementById('form-client-get')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const email = e.target.email.value.trim();
    try {
        const client = await api('GET', '/clients/' + encodePath(email));
        show(client);
    } catch (err) { show(err); }
});

document.getElementById('form-client-update')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const f = e.target;
    try {
        const pathEmail = f.pathEmail.value.trim();
        const body = {
            email: pathEmail,
            password: f.password.value,
            name: f.name.value.trim(),
            balance: Number(f.balance.value)
        };
        const updated = await api('PUT', '/clients/' + encodePath(pathEmail), body);
        show(updated);
        await loadClients();
    } catch (err) { show(err); }
});

document.getElementById('form-client-delete')?.addEventListener('submit', async (e) => {
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
    show(t('out_loading_employees'));
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
    ?.addEventListener('click', () => loadEmployees().catch(show));

document.getElementById('form-employee-create')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const f = e.target;
    try {
        const body = {
            email: f.email.value.trim(),
            password: f.password.value,
            name: f.name.value.trim(),
            birthDate: f.birthDate.value,
            phone: f.phone.value.trim()
        };
        const created = await api('POST', '/employees', body);
        show(created);
        await loadEmployees();
    } catch (err) { show(err); }
});

document.getElementById('form-employee-get')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const email = e.target.email.value.trim();
    try {
        const emp = await api('GET', '/employees/' + encodePath(email));
        show(emp);
    } catch (err) { show(err); }
});

document.getElementById('form-employee-update')?.addEventListener('submit', async (e) => {
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

document.getElementById('form-employee-delete')?.addEventListener('submit', async (e) => {
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
    show(t('out_loading_orders'));
    const data = await api('GET', url);

    const arr = Array.isArray(data) ? data : [data];

    ordersPre.textContent = arr.map(o => {
        const idLine = (o.id !== undefined && o.id !== null) ? `ID: ${o.id}\n` : '';
        return idLine + JSON.stringify(o, null, 2);
    }).join('\n\n---\n\n');

    show({ ok: true, count: arr.length, endpoint: url });
}

document.getElementById('form-orders-by-client')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    loadOrdersBy('/orders/my').catch(show);
});

document.getElementById('form-orders-by-employee')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const email = e.target.email.value.trim();
    loadOrdersBy('/orders/by_employee/' + encodePath(email)).catch(show);
});

document.getElementById('form-order-delete')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const id = e.target.elements['id'].value.trim();
    try {
        await api('DELETE', '/orders/' + encodePath(id));
        show({ ok: true, deletedOrderId: id });
        ordersPre.textContent = '';
    } catch (err) {
        show(err);
    }
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

document.getElementById('btn-add-item')?.addEventListener('click', () => addItemRow());

document.getElementById('form-order-create')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const f = e.target;

    try {
        const items = Array.from(itemsWrap.querySelectorAll('.grid')).map(row => ({
            bookName: row.querySelector('.item-book').value.trim(),
            quantity: Number(row.querySelector('.item-qty').value)
        })).filter(i => i.bookName);

        const body = {
            clientEmail: f.clientEmail?.value?.trim() || '',
            orderDate: toLocalDateTime(f.orderDate.value),
            bookItems: items
        };
        if (!body.clientEmail) delete body.clientEmail;

        const created = await api('POST', '/orders', body);
        show(created);
        ordersPre.textContent = JSON.stringify([created], null, 2);
    } catch (err) { show(err); }
});

// ---------- helpers ----------
function escapeHtml(s) {
    return String(s)
        .replaceAll('&', '&amp;').replaceAll('<', '&lt;').replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;').replaceAll("'", '&#039;');
}
function escapeAttr(s) {
    return escapeHtml(s).replaceAll('\n', ' ');
}
function getCookie(name) {
    const m = document.cookie.match('(^|;)\\s*' + name + '\\s*=\\s*([^;]+)');
    return m ? decodeURIComponent(m.pop()) : '';
}
function setLangAndReload(lang) {
    const url = new URL(window.location.href);
    url.searchParams.set('lang', lang);
    window.location.href = url.toString(); // reload so Spring sets LOCALE cookie too
}

function highlightLangButtons() {
    document.getElementById('lang-en')?.classList.toggle('active', LANG === 'en');
    document.getElementById('lang-ka')?.classList.toggle('active', LANG === 'ka');
}

// ---------- init ----------
(async function init() {
    applyI18n();
    document.getElementById('lang-en')?.addEventListener('click', () => setLangAndReload('en'));
    document.getElementById('lang-ka')?.addEventListener('click', () => setLangAndReload('ka'));

    addItemRow('', 1);

    // preload CSRF (so first POST works immediately)
    ensureCsrf().catch(() => {});

    // default datetime-local = now
    const dt = new Date();
    const pad = n => String(n).padStart(2, '0');
    const local = dt.getFullYear() + '-' + pad(dt.getMonth() + 1) + '-' + pad(dt.getDate()) +
        'T' + pad(dt.getHours()) + ':' + pad(dt.getMinutes());
    const orderDateInput = document.querySelector('[name="orderDate"]');
    if (orderDateInput) orderDateInput.value = local;

    loadBooks().catch(show);
    loadMeAndApplyAccess().catch(() => {});
})();