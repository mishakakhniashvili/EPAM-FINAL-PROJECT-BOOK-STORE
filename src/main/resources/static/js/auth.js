// ---------- Auth box ----------
const authBox = document.getElementById('auth-box');
const btnLogin = document.getElementById('btn-login');
const btnLogout = document.getElementById('btn-logout');
const btnDeleteAccount = document.getElementById('btn-delete-account');

// confirm modal
const confirmModal = document.getElementById('confirm-modal');
const confirmText = document.getElementById('confirm-text');
const confirmCancel = document.getElementById('confirm-cancel');
const confirmOk = document.getElementById('confirm-ok');

let confirmAction = null;

function openConfirm(message, onOk) {
    confirmAction = onOk;
    if (confirmText) confirmText.textContent = message || t('confirm_delete_me');
    if (confirmModal) confirmModal.hidden = false;
}

function closeConfirm() {
    confirmAction = null;
    if (confirmModal) confirmModal.hidden = true;
}

confirmCancel?.addEventListener('click', closeConfirm);
confirmModal?.querySelector('.modal-backdrop')?.addEventListener('click', closeConfirm);
window.addEventListener('keydown', (e) => {
    if (e.key === 'Escape' && confirmModal && !confirmModal.hidden) closeConfirm();
});
confirmOk?.addEventListener('click', async () => {
    if (!confirmAction) return closeConfirm();
    const fn = confirmAction;
    closeConfirm();
    await fn();
});



function renderAuth(me) {
    if (!me) {
        authBox.textContent = t('auth_guest');
        btnLogin.style.display = '';
        btnLogout.style.display = 'none';
        btnDeleteAccount.style.display = 'none';
        return;
    }

    const rolesRaw = me.roles || [];
    const isClient = rolesRaw.includes('ROLE_CLIENT');

    const rolesText = rolesRaw.map(r => r.replace('ROLE_', '')).join(', ');
    authBox.textContent = t('auth_logged_as', { user: me.username || 'user', roles: rolesText });

    btnLogin.style.display = 'none';
    btnLogout.style.display = '';
    btnDeleteAccount.style.display = isClient ? '' : 'none';
}

btnLogin?.addEventListener('click', () => window.location.href = '/login.html');

btnLogout?.addEventListener('click', async () => {
    try {
        await fetch('/logout', { method: 'POST', credentials: 'same-origin' });
    } finally {
        renderAuth(null);
        applyUiAccess([]);
        applyOrdersUi(null);
        applyBooksUi(null);
        setActiveTab('books');
        show('Logged out.');
    }
});

btnDeleteAccount?.addEventListener('click', () => {
    openConfirm(t('confirm_delete_me'), async () => {
        try {
            await api('DELETE', '/profile');
            await api('POST', '/logout');
            show('Account deleted.');
        } catch (err) {
            show(err);
            return;
        } finally {
            renderAuth(null);
            applyUiAccess([]);
            applyOrdersUi(null);
            applyBooksUi(null);
            setActiveTab('books');
        }
    });
});
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