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
document.getElementById('form-book-update')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const f = e.target;

    const pathName = f.pathName.value.trim();
    const body = {};

    if (f.name.value.trim()) body.name = f.name.value.trim();
    if (f.author.value.trim()) body.author = f.author.value.trim();
    if (f.genre.value.trim()) body.genre = f.genre.value.trim();
    if (f.price.value !== '') body.price = Number(f.price.value);
    if (f.publicationDate.value) body.publicationDate = f.publicationDate.value;
    if (f.pages.value !== '') body.pages = Number(f.pages.value);
    if (f.language.value) body.language = f.language.value;
    if (f.ageGroup.value) body.ageGroup = f.ageGroup.value;
    if (f.characteristics.value.trim()) body.characteristics = f.characteristics.value.trim();
    if (f.description.value.trim()) body.description = f.description.value.trim();

    if (!pathName) { show('Enter book name to update.'); return; }
    if (Object.keys(body).length === 0) { show('Fill at least one field to update.'); return; }

    try {
        const updated = await api('PATCH', '/books/' + encodePath(pathName), body);
        show(updated);
        await loadBooks();
    } catch (err) { show(err); }
});
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
        const res = await api('POST', '/clients', body);
        show(res);
        await loadClients();
    } catch (err) {
        show(err);
    }
});

document.getElementById('form-client-update')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const f = e.target;

    // support either name="email" or name="pathEmail"
    const email = (f.email?.value || f.pathEmail?.value || '').trim();

    const body = {};
    if (f.password?.value?.trim()) body.password = f.password.value;
    if (f.name?.value?.trim()) body.name = f.name.value.trim();
    if (f.balance?.value !== undefined && f.balance.value !== '') body.balance = Number(f.balance.value);

    if (!email) { show('Enter client email to update.'); return; }
    if (Object.keys(body).length === 0) { show('Fill at least one field to update.'); return; }

    try {
        const res = await api('PATCH', '/clients/' + encodePath(email), body);
        show(res);
        await loadClients();
    } catch (err) {
        show(err);
    }
});

document.getElementById('form-client-delete')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const f = e.target;
    const email = f.email.value.trim();
    try {
        const res = await api('DELETE', '/clients/' + encodePath(email));
        show(res);
        await loadClients();
    } catch (err) {
        show(err);
    }
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
        const res = await api('POST', '/employees', body);
        show(res);
        await loadEmployees();
    } catch (err) {
        show(err);
    }
});

document.getElementById('form-employee-update')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const f = e.target;

    const pathEmail = f.pathEmail.value.trim();
    const body = {};

    if (f.password.value.trim()) body.password = f.password.value;
    if (f.name.value.trim()) body.name = f.name.value.trim();
    if (f.birthDate.value) body.birthDate = f.birthDate.value;
    if (f.phone.value.trim()) body.phone = f.phone.value.trim();

    if (!pathEmail) { show('Enter employee email to update.'); return; }
    if (Object.keys(body).length === 0) { show('Fill at least one field to update.'); return; }

    try {
        const updated = await api('PATCH', '/employees/' + encodePath(pathEmail), body);
        show(updated);
        await loadEmployees();
    } catch (err) { show(err); }
});
document.getElementById('form-employee-delete')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const f = e.target;
    const email = f.email.value.trim();
    try {
        const res = await api('DELETE', '/employees/' + encodePath(email));
        show(res);
        await loadEmployees();
    } catch (err) {
        show(err);
    }
});
// ---------- BOOKS (employee CRUD) ----------
document.getElementById('form-book-create')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const f = e.target;

    try {
        const body = {
            name: f.elements['name'].value.trim(),
            author: f.elements['author'].value.trim(),
            genre: f.elements['genre'].value.trim(),
            price: Number(f.elements['price'].value),
            publicationDate: f.elements['publicationDate'].value, // YYYY-MM-DD
            pages: Number(f.elements['pages'].value),
            language: f.elements['language'].value,
            ageGroup: f.elements['ageGroup'].value,
            characteristics: f.elements['characteristics'].value.trim(),
            description: f.elements['description'].value.trim()
        };

        const res = await api('POST', '/books', body);
        show(res);
        f.reset();
        await loadBooks(); // from books.js
    } catch (err) {
        show(err);
    }
});

document.getElementById('form-book-get')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const name = e.target.elements['name'].value.trim();
    try {
        const res = await api('GET', '/books/' + encodePath(name));
        show(res);
    } catch (err) {
        show(err);
    }
});

document.getElementById('form-book-update')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const f = e.target;

    const pathName = f.elements['pathName'].value.trim();

    try {
        const body = {
            name: f.elements['name'].value.trim(),
            author: f.elements['author'].value.trim(),
            genre: f.elements['genre'].value.trim(),
            price: Number(f.elements['price'].value),
            publicationDate: f.elements['publicationDate'].value,
            pages: Number(f.elements['pages'].value),
            language: f.elements['language'].value,
            ageGroup: f.elements['ageGroup'].value,
            characteristics: f.elements['characteristics'].value.trim(),
            description: f.elements['description'].value.trim()
        };

        const res = await api('PUT', '/books/' + encodePath(pathName), body);
        show(res);
        await loadBooks();
    } catch (err) {
        show(err);
    }
});

document.getElementById('form-book-delete')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const name = e.target.elements['name'].value.trim();
    try {
        await api('DELETE', '/books/' + encodePath(name));
        show({ ok: true, deleted: name, endpoint: 'DELETE /books/' + name });
        await loadBooks();
    } catch (err) {
        show(err);
    }
});