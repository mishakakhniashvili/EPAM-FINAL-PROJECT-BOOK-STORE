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
        // header / auth
        header_title: 'Book Store – Simple UI',
        auth_guest: 'Auth: guest',
        auth_logged_as: 'Auth: {user} ({roles})',
        btn_login: 'Login',
        btn_logout: 'Logout',
        btn_delete_me: 'Delete my account',

        // tabs
        tab_books: 'Books',
        tab_clients: 'Clients',
        tab_employees: 'Employees',
        tab_orders: 'Orders',

        // books view
        books_refresh: 'Refresh books',
        books_hint: 'GET /books is public. Employees can POST/PUT/DELETE /books.',
        books_admin_title: 'Employee-only book CRUD',
        books_create_title: 'Create book',
        books_update_title: 'Update book',
        books_delete_title: 'Delete book',
        books_table_title: 'Books',
        books_view_btn: 'View',

        // clients view
        clients_hint: 'Employees only',
        clients_refresh: 'Refresh clients',
        clients_create_title: 'Create client',
        clients_update_title: 'Update client',
        clients_delete_title: 'Delete client',
        clients_table_title: 'Clients',
        clients_balance_note: 'Balance as number',

        // employees view
        employees_hint: 'Employees only',
        employees_refresh: 'Refresh employees',
        employees_create_title: 'Create employee',
        employees_update_title: 'Update employee',
        employees_delete_title: 'Delete employee',
        employees_table_title: 'Employees',

        // orders view
        orders_hint: 'GET by client/employee + POST /orders (price computed server-side)',
        orders_find_title: 'Find orders',
        orders_my_btn: 'Get my orders',
        orders_by_employee_btn: 'Get by employee',
        orders_delete_btn: 'Delete',
        orders_delete_note: 'Employees only',
        orders_create_title: 'Create order',
        orders_create_btn: 'Create order',
        orders_items_title: 'Book items',
        orders_add_item: '+ Add item',
        orders_items_note: 'Book must exist (backend searches by bookName)',
        orders_last_title: 'Orders (last query)',

        // response panel
        resp_title: 'Response',
        btn_clear: 'Clear',
        tip_401: 'If you get 401, login first.',
        tip_403: 'If you get 403 on POST/PUT/DELETE/PATCH, it’s usually CSRF.',

        // generic output messages
        out_loading_books: 'Loading books…',
        out_loading_clients: 'Loading clients…',
        out_loading_employees: 'Loading employees…',
        out_loading_orders: 'Loading orders…',

        // confirm modal
        confirm_delete_me: 'Are you sure you want to delete your account?',
        confirm_cancel: 'Cancel',
        confirm_ok: 'Delete'
    },

    ka: {
        header_title: 'წიგნის მაღაზია – მარტივი UI',
        auth_guest: 'ავტორიზაცია: სტუმარი',
        auth_logged_as: 'ავტორიზაცია: {user} ({roles})',
        btn_login: 'შესვლა',
        btn_logout: 'გასვლა',
        btn_delete_me: 'ჩემი ანგარიშის წაშლა',

        tab_books: 'წიგნები',
        tab_clients: 'კლიენტები',
        tab_employees: 'თანამშრომლები',
        tab_orders: 'შეკვეთები',

        books_refresh: 'წიგნების განახლება',
        books_hint: 'GET /books არის საჯარო. თანამშრომლებს შეუძლიათ POST/PUT/DELETE /books.',
        books_admin_title: 'თანამშრომლებისთვის – წიგნების CRUD',
        books_create_title: 'წიგნის შექმნა',
        books_update_title: 'წიგნის განახლება',
        books_delete_title: 'წიგნის წაშლა',
        books_table_title: 'წიგნები',
        books_view_btn: 'ნახვა',

        clients_hint: 'მხოლოდ თანამშრომლები',
        clients_refresh: 'კლიენტების განახლება',
        clients_create_title: 'კლიენტის შექმნა',
        clients_update_title: 'კლიენტის განახლება',
        clients_delete_title: 'კლიენტის წაშლა',
        clients_table_title: 'კლიენტები',
        clients_balance_note: 'ბალანსი რიცხვად',

        employees_hint: 'მხოლოდ თანამშრომლები',
        employees_refresh: 'თანამშრომლების განახლება',
        employees_create_title: 'თანამშრომლის შექმნა',
        employees_update_title: 'თანამშრომლის განახლება',
        employees_delete_title: 'თანამშრომლის წაშლა',
        employees_table_title: 'თანამშრომლები',

        orders_hint: 'GET კლიენტით/თანამშრომლით + POST /orders (ფასი ითვლება სერვერზე)',
        orders_find_title: 'შეკვეთების ძებნა',
        orders_my_btn: 'ჩემი შეკვეთები',
        orders_by_employee_btn: 'თანამშრომლით',
        orders_delete_btn: 'წაშლა',
        orders_delete_note: 'მხოლოდ თანამშრომლები',
        orders_create_title: 'შეკვეთის შექმნა',
        orders_create_btn: 'შეკვეთის შექმნა',
        orders_items_title: 'წიგნები',
        orders_add_item: '+ დამატება',
        orders_items_note: 'წიგნი უნდა არსებობდეს (backend ეძებს bookName-ით)',
        orders_last_title: 'შეკვეთები (ბოლო მოთხოვნა)',

        resp_title: 'პასუხი',
        btn_clear: 'გასუფთავება',
        tip_401: 'თუ მიიღე 401, ჯერ გაიარე ავტორიზაცია.',
        tip_403: 'თუ POST/PUT/DELETE/PATCH-ზე 403ა, ხშირად CSRF არის.',

        out_loading_books: 'წიგნების ჩატვირთვა…',
        out_loading_clients: 'კლიენტების ჩატვირთვა…',
        out_loading_employees: 'თანამშრომლების ჩატვირთვა…',
        out_loading_orders: 'შეკვეთების ჩატვირთვა…',

        confirm_delete_me: 'დარწმუნებული ხარ, რომ გინდა ანგარიშის წაშლა?',
        confirm_cancel: 'გაუქმება',
        confirm_ok: 'წაშლა'
    }
};

let LANG = 'en';

function t(key, vars = {}) {
    const dict = I18N[LANG] || I18N.en;
    let s = dict[key] || (I18N.en[key] || key);
    for (const [k, v] of Object.entries(vars)) {
        s = s.replaceAll(`{${k}}`, v);
    }
    return s;
}

function applyI18n() {
    LANG = getLang();
    document.documentElement.lang = LANG;

    // text nodes by data-i18n
    document.querySelectorAll('[data-i18n]').forEach(el => {
        const key = el.getAttribute('data-i18n');
        el.textContent = t(key);
    });

    // placeholders by data-i18n-ph
    document.querySelectorAll('[data-i18n-ph]').forEach(el => {
        const key = el.getAttribute('data-i18n-ph');
        el.setAttribute('placeholder', t(key));
    });

    // header title
    document.querySelector('header h1') && (document.querySelector('header h1').textContent = t('header_title'));

    highlightLangButtons();
}

// ---------- i18n helpers ----------
function setLangAndReload(lang) {
    const url = new URL(window.location.href);
    url.searchParams.set('lang', lang);
    window.location.href = url.toString(); // reload so Spring sets LOCALE cookie too
}

function highlightLangButtons() {
    document.getElementById('lang-en')?.classList.toggle('active', LANG === 'en');
    document.getElementById('lang-ka')?.classList.toggle('active', LANG === 'ka');
}