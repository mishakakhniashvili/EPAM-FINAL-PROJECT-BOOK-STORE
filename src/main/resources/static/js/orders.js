// js/orders.js

// ---------- Orders UI (access) ----------
function applyOrdersUi(me) {
    const roles = me?.roles || [];
    const isEmployee = roles.includes('ROLE_EMPLOYEE');
    const isClient = roles.includes('ROLE_CLIENT');

    const byClientForm = document.getElementById('form-orders-by-client');
    const byEmployeeForm = document.getElementById('form-orders-by-employee');
    const deleteForm = document.getElementById('form-order-delete');
    const createForm = document.getElementById('form-order-create');

    // by employee -> employees only
    if (byEmployeeForm) byEmployeeForm.style.display = isEmployee ? '' : 'none';

    // delete -> employee OR client (client: only own orders, enforced backend)
    if (deleteForm) {
        deleteForm.style.display = (isEmployee || isClient) ? '' : 'none';
        const note = deleteForm.querySelector('.muted');
        if (note) note.textContent = isEmployee ? 'Employees: can delete any order' : 'Client: can delete only your orders';
    }

    // by client form:
    // - client -> no email input, use /orders/my
    // - employee -> email input visible, uses /orders/by_client/{email}
    if (byClientForm) {
        const emailInput = byClientForm.querySelector('input[name="email"]');
        const emailWrap = emailInput?.closest('div');

        if (isClient) {
            if (emailInput) {
                emailInput.required = false;
                emailInput.value = '';
            }
            if (emailWrap) emailWrap.style.display = 'none';
        } else {
            if (emailInput) emailInput.required = true;
            if (emailWrap) emailWrap.style.display = '';
        }
    }

    // create order form:
    // - employee: clientEmail required, employeeEmail hidden (server fills from auth)
    // - client: both email inputs hidden (server fills both)
    if (createForm) {
        const clientInput = createForm.querySelector('input[name="clientEmail"]');
        const empInput = createForm.querySelector('input[name="employeeEmail"]');
        const clientWrap = clientInput?.closest('div');
        const empWrap = empInput?.closest('div');

        if (isEmployee) {
            if (clientInput) clientInput.required = true;
            if (clientWrap) clientWrap.style.display = '';

            if (empInput) {
                empInput.required = false;
                empInput.disabled = true;
                empInput.value = '';
            }
            if (empWrap) empWrap.style.display = 'none';
        } else if (isClient) {
            if (clientInput) {
                clientInput.required = false;
                clientInput.disabled = true;
                clientInput.value = '';
            }
            if (clientWrap) clientWrap.style.display = 'none';

            if (empInput) {
                empInput.required = false;
                empInput.disabled = true;
                empInput.value = '';
            }
            if (empWrap) empWrap.style.display = 'none';
        } else {
            // guest -> hide whole create form
            const card = createForm.closest('.card');
            if (card) card.style.display = 'none';
        }
    }
}

// ---------- Items UI ----------
const itemsWrap = document.getElementById('items-wrap');

function addItemRow(bookName = '', quantity = 1) {
    if (!itemsWrap) return;

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
    row.querySelector('.btn-remove')?.addEventListener('click', () => row.remove());
    itemsWrap.appendChild(row);
}

document.getElementById('btn-add-item')?.addEventListener('click', () => addItemRow());

// ---------- Orders logic ----------
async function showOrders(data, endpoint) {
    const arr = Array.isArray(data) ? data : [data];
    if (ordersPre) ordersPre.textContent = arr.map(o => JSON.stringify(o, null, 2)).join('\n\n---\n\n');
    show({ok: true, count: arr.length, endpoint});
}

document.getElementById('form-orders-by-client')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const email = e.target.querySelector('input[name="email"]')?.value?.trim();

    try {
        const data = email ? await api('GET', '/orders/by_client/' + encodePath(email))
            : await api('GET', '/orders/my');
        await showOrders(data, email ? `GET /orders/by_client/${email}` : 'GET /orders/my');
    } catch (err) {
        show(err);
    }
});

document.getElementById('form-orders-by-employee')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const email = e.target.querySelector('input[name="email"]')?.value?.trim();
    if (!email) return show('Enter employee email.');

    try {
        const data = await api('GET', '/orders/by_employee/' + encodePath(email));
        await showOrders(data, `GET /orders/by_employee/${email}`);
    } catch (err) {
        show(err);
    }
});

document.getElementById('form-order-delete')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const id = e.target.querySelector('input[name="id"]')?.value?.trim();
    if (!id) return show('Enter order ID.');

    try {
        await api('DELETE', '/orders/' + encodePath(id));
        show({ok: true, deletedOrderId: id, endpoint: `DELETE /orders/${id}`});
    } catch (err) {
        show(err);
    }
});

document.getElementById('form-order-create')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const f = e.target;

    try {
        const rows = itemsWrap ? Array.from(itemsWrap.querySelectorAll('.grid')) : [];
        const items = rows.map(row => ({
            bookName: row.querySelector('.item-book')?.value?.trim(),
            quantity: Number(row.querySelector('.item-qty')?.value)
        })).filter(i => i.bookName);

        const body = {
            orderDate: toLocalDateTime(f.orderDate?.value),
            bookItems: items
        };

        // employee may supply clientEmail; client should not
        const clientEmail = f.clientEmail?.value?.trim();
        if (clientEmail) body.clientEmail = clientEmail;

        const created = await api('POST', '/orders', body);
        await showOrders(created, 'POST /orders');
    } catch (err) {
        show(err);
    }
});