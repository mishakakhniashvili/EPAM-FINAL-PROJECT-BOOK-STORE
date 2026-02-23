function hasRole(me, role) {
    return Array.isArray(me?.roles) && me.roles.includes(role);
}

function getUserId(me) {
    // support both backends: {username,...} or {email,...}
    return me?.username || me?.email || '';
}

function renderAuth(me) {
    const authBox = document.getElementById('auth-box');
    const btnLogin = document.getElementById('btn-login');
    const btnLogout = document.getElementById('btn-logout');
    const btnDeleteAccount = document.getElementById('btn-delete-account');

    if (!authBox || !btnLogin || !btnLogout) return;

    const user = getUserId(me);
    const roles = Array.isArray(me?.roles) ? me.roles : [];
    const loggedIn = !!user;

    authBox.textContent = loggedIn
        ? t('auth_logged_as', {user, roles: roles.join(', ')})
        : t('auth_guest');

    btnLogin.style.display = loggedIn ? 'none' : '';
    btnLogout.style.display = loggedIn ? '' : 'none';

    // only CLIENT can delete own account
    if (btnDeleteAccount) {
        btnDeleteAccount.style.display = (loggedIn && roles.includes('ROLE_CLIENT')) ? '' : 'none';
    }
}

async function loadMeAndApplyAccess() {
    try {
        const me = await api('GET', '/me');

        applyUiAccess(me.roles || []);
        renderAuth(me);

        // also apply view-specific access if those modules are loaded
        if (typeof applyBooksUi === 'function') applyBooksUi(me);
        if (typeof applyOrdersUi === 'function') applyOrdersUi(me);

        return me;
    } catch (e) {
        applyUiAccess([]);
        renderAuth(null);

        if (typeof applyBooksUi === 'function') applyBooksUi(null);
        if (typeof applyOrdersUi === 'function') applyOrdersUi(null);

        return null;
    }
}

document.getElementById('btn-login')
    ?.addEventListener('click', () => window.location.href = '/login.html');

document.getElementById('btn-logout')
    ?.addEventListener('click', async () => {
        try {
            await api('POST', '/logout');
        } finally {
            window.location.href = '/index.html';
        }
    });

document.getElementById('btn-delete-account')
    ?.addEventListener('click', async () => {
        // If you don’t have confirmModal implemented, use plain confirm:
        const ok = window.confirm(t('confirm_delete_me'));
        if (!ok) return;

        await api('DELETE', '/profile');
        await api('POST', '/logout');
        window.location.href = '/index.html';
    });