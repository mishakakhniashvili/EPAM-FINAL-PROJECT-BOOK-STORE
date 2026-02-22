// ---------- Orders UI rules ----------
function applyOrdersUi(me) {
    const roles = me?.roles || [];
    const username = me?.username || '';
    const isEmployee = roles.includes('ROLE_EMPLOYEE');
    const isClient = roles.includes('ROLE_CLIENT');

    const byEmployeeForm = document.getElementById('form-orders-by-employee');
    const deleteOrderForm = document.getElementById('form-order-delete');
    const createOrderForm = document.getElementById('form-order-create');

    // employee-only controls:
    if (byEmployeeForm) byEmployeeForm.style.display = isEmployee ? '' : 'none';
    if (deleteOrderForm) deleteOrderForm.style.display = isEmployee ? '' : 'none';

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