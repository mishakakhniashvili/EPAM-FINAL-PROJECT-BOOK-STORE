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

function show(obj) {
    if (obj == null) { out.textContent = ''; return; }
    if (typeof obj === 'string') { out.textContent = obj; return; }

    if (obj.ok === true && obj.endpoint) {
        const count = (obj.count !== undefined && obj.count !== null) ? ` (count: ${obj.count})` : '';
        out.textContent = `OK: ${obj.endpoint}${count}`;
        return;
    }

    if (obj.status && (obj.error || obj.message)) {
        out.textContent = `Error ${obj.status}: ${obj.message || obj.error}`;
        return;
    }

    out.textContent = JSON.stringify(obj, null, 2);
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

    const unsafe = !['GET', 'HEAD', 'OPTIONS'].includes(upper);
    if (unsafe) {
        const csrf = await ensureCsrf();
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

// ---------- UI Access (tabs) ----------
function setTabVisible(view, visible) {
    const tab = document.querySelector(`.tab[data-view="${view}"]`);
    const viewEl = document.getElementById('view-' + view);
    if (tab) tab.style.display = visible ? '' : 'none';
    if (viewEl && !visible) viewEl.hidden = true;
}

// roles from /me
function applyUiAccess(roles) {
    const isEmployee = roles.includes('ROLE_EMPLOYEE');
    const isClient = roles.includes('ROLE_CLIENT');

    // default: Books visible to all
    setTabVisible('books', true);

    // admin tabs:
    setTabVisible('clients', isEmployee);
    setTabVisible('employees', isEmployee);

    // orders tab visible for employee/client
    setTabVisible('orders', isEmployee || isClient);

    // if current tab hidden, move to books
    const active = document.querySelector('.tab.active')?.dataset.view;
    if (active && document.querySelector(`.tab[data-view="${active}"]`)?.style.display === 'none') {
        setActiveTab('books');
    }
}

// ---------- helpers (shared) ----------
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