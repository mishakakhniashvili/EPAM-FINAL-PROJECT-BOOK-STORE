// ---------- init ----------
(async function init() {
    applyI18n();
    document.getElementById('lang-en')?.addEventListener('click', () => setLangAndReload('en'));
    document.getElementById('lang-ka')?.addEventListener('click', () => setLangAndReload('ka'));

    if (typeof addItemRow === 'function') addItemRow('', 1);
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