// LibreriaOnline - Simple Bookstore MVP with Supabase auth
// Vanilla JavaScript, HTML, CSS

const SUPABASE_URL = 'https://phztuootrngnsjffdadh.supabase.co';
const SUPABASE_ANON_KEY = 'sb_publishable_7Os8eGty-0SD_mQgG_PyAw_eM8ztvLs';

let isLoggedIn = false;
let cart = JSON.parse(localStorage.getItem('libreriaCart') || '[]');
const USERNAME_KEY = 'libreriaUser';
const HARDCODED_BOOKS = [
  { id: '1', title: 'El Aleph', author: 'Jorge Luis Borges', price: 15, cover: 'https://via.placeholder.com/180x260?text=El+Aleph' },
  { id: '2', title: '1984', author: 'George Orwell', price: 12, cover: 'https://via.placeholder.com/180x260?text=1984' },
  { id: '3', title: 'Cien años de soledad', author: 'Gabriel García Márquez', price: 18, cover: 'https://via.placeholder.com/180x260?text=Cien+años+de+soledad' }
];

let loginBtn;
let loginModal;
let authForm;
let authError;
let tabButtons;
let loginPanel;
let registerPanel;
let cartBtn;
let cartModal;
let cartItemsContainer;
let cartCount;
let cartTotal;
let checkoutBtn;
let booksGrid;

document.addEventListener('DOMContentLoaded', async () => {


    // Elements
    loginBtn = document.getElementById('loginBtn');
    loginModal = document.getElementById('loginModal');
    const closeLogin = loginModal.querySelector('.close');
    authForm = document.getElementById('authForm');
    authError = document.getElementById('authError');
    tabButtons = loginModal.querySelectorAll('.tab-btn');
    loginPanel = document.getElementById('login-panel');
    registerPanel = document.getElementById('register-panel');

    cartBtn = document.getElementById('cartBtn');
    cartModal = document.getElementById('cartModal');
    const closeCart = cartModal.querySelector('.close');
    cartItemsContainer = document.getElementById('cartItems');
    cartCount = document.getElementById('cartCount');
    cartTotal = document.getElementById('cartTotal');
    checkoutBtn = document.getElementById('checkoutBtn');

    booksGrid = document.getElementById('booksGrid');

    // Initialize
    await init();

    // Event listeners
    loginBtn.addEventListener('click', () => {
        if (!isLoggedIn) {
            openLoginModal();
        } else {
            // If logged in, clicking button could show user info or logout
            alert(`Has iniciado sesión como ${localStorage.getItem(USERNAME_KEY) || ''}`);
        }
    });

    closeLogin.addEventListener('click', closeLoginModal);

    tabButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            const tab = btn.dataset.tab;
            // Update active tab
            tabButtons.forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            // Show corresponding panel
            if (tab === 'login') {
                loginPanel.classList.add('active');
                registerPanel.classList.remove('active');
            } else {
                registerPanel.classList.add('active');
                loginPanel.classList.remove('active');
            }
            authError.textContent = '';
            authForm.reset();
        });
    });

    authForm.addEventListener('submit', handleAuthSubmit);

    cartBtn.addEventListener('click', () => {
        cartModal.style.display = 'block';
    });

    closeCart.addEventListener('click', () => {
        cartModal.style.display = 'none';
    });

    checkoutBtn.addEventListener('click', handleCheckout);

    // Close modal when clicking outside
    window.addEventListener('click', (e) => {
        if (e.target === loginModal) {
            closeLoginModal();
        }
        if (e.target === cartModal) {
            cartModal.style.display = 'none';
        }
    });
});

async function init() {
    try {
        if (!window.supabase) {
            throw new Error('Supabase library not loaded');
        }
        window.libreriaSupabase = window.supabase.createClient(SUPABASE_URL, SUPABASE_ANON_KEY);
        const { data: { session } } = await window.libreriaSupabase.auth.getSession();
        if (session) {
            setLoggedIn(session.user);
        } else {
            // No session, show login modal automatically
            openLoginModal();
        }
    } catch (error) {
        console.error('Error initializing Supabase:', error);
        authError.textContent = 'Error al inicializar el servicio de autenticación';
        // If we can't verify session, still show login modal
        openLoginModal();
    }
    renderCart();
    fetchBooks();
}

function setLoggedIn(user) {
    isLoggedIn = true;
    const username = user.email?.split('@')[0] || user.id;
    localStorage.setItem(USERNAME_KEY, username);
    loginBtn.textContent = `Hola, ${username}`;
    loginBtn.style.backgroundColor = '#4caf50';
    loginBtn.style.color = 'white';
    // Hide login modal if open
    closeLoginModal();
}

function setLoggedOut() {
    isLoggedIn = false;
    localStorage.removeItem(USERNAME_KEY);
    loginBtn.textContent = 'Login';
    loginBtn.style.backgroundColor = '';
    loginBtn.style.color = '';
    // Optionally clear cart? Keep cart as is.
    // Show login modal
    openLoginModal();
}

function openLoginModal() {
    loginModal.style.display = 'block';
    authError.textContent = '';
    authForm.reset();
    // Ensure login tab is active by default
    document.querySelector('.tab-btn[data-tab="login"]').classList.add('active');
    document.querySelector('.tab-btn[data-tab="register"]').classList.remove('active');
    loginPanel.classList.add('active');
    registerPanel.classList.remove('active');
}

function closeLoginModal() {
    loginModal.style.display = 'none';
    authError.textContent = '';
    authForm.reset();
}

async function handleAuthSubmit(e) {
    console.log('handleAuthSubmit called');
    e.preventDefault();
    if (!window.libreriaSupabase) {
        authError.textContent = 'Servicio de autenticación no disponible. Por favor recarga la página.';
        return;
    }
    authError.textContent = '';

    const isLogin = document.querySelector('.tab-btn.active').dataset.tab === 'login';

    if (isLogin) {
        const email = document.getElementById('loginEmail').value.trim();
        const password = document.getElementById('loginPassword').value.trim();

        if (!email || !password) {
            authError.textContent = 'Por favor ingrese correo y contraseña';
            return;
        }

        const { data, error } = await window.libreriaSupabase.auth.signInWithPassword({
            email,
            password
        });

        if (error) {
            authError.textContent = error.message || 'Error al iniciar sesión';
            return;
        }

        setLoggedIn(data.user);
    } else {
        const email = document.getElementById('registerEmail').value.trim();
        const password = document.getElementById('registerPassword').value.trim();
        const username = document.getElementById('registerUsername').value.trim();

        if (!email || !password || !username) {
            authError.textContent = 'Por favor complete todos los campos';
            return;
        }

        const { data, error } = await window.libreriaSupabase.auth.signUp({
            email,
            password,
            options: {
                data: {
                    username
                }
            }
        });

        if (error) {
            authError.textContent = error.message || 'Error al registrarse';
            return;
        }

        // After sign up, we can either auto-login or ask to check email
        authError.textContent = 'Registro exitoso. Por favor verifica tu correo e inicia sesión.';
        // Switch to login tab
        document.querySelector('.tab-btn[data-tab="login"]').click();
    }
}

async function handleLogout() {
    await window.libreriaSupabase.auth.signOut();
    setLoggedOut();
}

// Rest of the functions (fetchBooks, displayBooks, cart handling, etc.) remain same as before
// but we need to adjust loginBtn inner text update in setLoggedOut (already done)
// Also ensure that cart/checkout requires login (already handled)

function fetchBooks() {
    // Show loading
    booksGrid.innerHTML = '<p class="loading">Cargando libros...</p>';

    // Fetch from Open Library API
    fetch('https://openlibrary.org/search.json?q=fiction&limit=12')
        .then(response => response.json())
        .then(data => {
            if (data && data.docs && data.docs.length > 0) {
                displayBooks(data.docs);
            } else {
                // No data from API, use hardcoded books
                displayBooks(HARDCODED_BOOKS);
            }
        })
        .catch(error => {
            console.error('Error fetching books:', error);
            // Fallback to hardcoded books on network error
            displayBooks(HARDCODED_BOOKS);
        });
}

function displayBooks(books) {
    booksGrid.innerHTML = ''; // Clear loading

    if (books.length === 0) {
        booksGrid.innerHTML = '<p>No se encontraron libros.</p>';
        return;
    }

    books.forEach(book => {
        const bookCard = document.createElement('div');
        bookCard.className = 'book-card';

        // Cover image
        const coverId = book.cover_i;
        const coverUrl = coverId
            ? `https://covers.openlibrary.org/b/id/${coverId}-M.jpg`
            : 'https://via.placeholder.com/180x260?text=Sin+imagen';

        const coverImg = document.createElement('img');
        coverImg.src = coverUrl;
        coverImg.alt = book.title || 'Portada del libro';
        coverImg.className = 'book-cover';
        coverImg.onerror = () => {
            coverImg.src = 'https://via.placeholder.com/180x260?text=Sin+imagen';
        };

        // Info
        const infoDiv = document.createElement('div');
        infoDiv.className = 'book-info';

        const title = document.createElement('h3');
        title.className = 'book-title';
        title.textContent = book.title || 'Título desconocido';

        const author = document.createElement('p');
        author.className = 'book-author';
        author.textContent = book.author_name ? book.author_name[0] : 'Autor desconocido';

        const actionsDiv = document.createElement('div');
        actionsDiv.className = 'book-actions';

        const addBtn = document.createElement('button');
        addBtn.className = 'add-btn';
        addBtn.textContent = 'Agregar';
        addBtn.addEventListener('click', () => {
            addToCart({
                id: book.olid ? book.olid.replace('/ol/', '') : Math.random().toString(36).substr(2, 9),
                title: book.title,
                author: book.author_name ? book.author_name[0] : 'Desconocido',
                price: Math.floor(Math.random() * 20) + 10, // Random price between 10-30
                cover: coverUrl
            });
        });

        actionsDiv.appendChild(addBtn);

        infoDiv.appendChild(title);
        infoDiv.appendChild(author);
        infoDiv.appendChild(actionsDiv);

        bookCard.appendChild(coverImg);
        bookCard.appendChild(infoDiv);

        booksGrid.appendChild(bookCard);
    });
}

function addToCart(book) {
    // Check if already in cart
    const existingIndex = cart.findIndex(item => item.id === book.id);
    if (existingIndex > -1) {
        cart[existingIndex].quantity += 1;
    } else {
        cart.push({ ...book, quantity: 1 });
    }

    saveCart();
    renderCart();
    showNotification('¡Libro añadido al carrito!');
}

function removeFromCart(id) {
    cart = cart.filter(item => item.id !== id);
    saveCart();
    renderCart();
}

function updateQuantity(id, delta) {
    const item = cart.find(item => item.id === id);
    if (item) {
        item.quantity += delta;
        if (item.quantity <= 0) {
            removeFromCart(id);
        } else {
            saveCart();
            renderCart();
        }
    }
}

function saveCart() {
    localStorage.setItem('libreriaCart', JSON.stringify(cart));
}

function renderCart() {
    // Update cart count
    const totalItems = cart.reduce((sum, item) => sum + item.quantity, 0);
    cartCount.textContent = totalItems;
    cartCount.style.display = totalItems > 0 ? 'block' : 'none';

    // Render cart items
    if (cart.length === 0) {
        cartItemsContainer.innerHTML = '<p class="empty-cart">Tu carrito está vacío</p>';
        cartTotal.textContent = '$0.00';
        return;
    }

    cartItemsContainer.innerHTML = '';
    let total = 0;

    cart.forEach(item => {
        const itemTotal = item.price * item.quantity;
        total += itemTotal;

        const cartItem = document.createElement('div');
        cartItem.className = 'cart-item';

        const img = document.createElement('img');
        img.src = item.cover;
        img.alt = item.title;
        img.className = 'cart-item-img';

        const details = document.createElement('div');
        details.className = 'cart-item-details';

        const title = document.createElement('div');
        title.className = 'cart-item-title';
        title.textContent = item.title;

        const author = document.createElement('div');
        author.textContent = `por ${item.author}`;
        author.style.fontSize = '0.85rem';
        author.style.color = '#b0b0b0';

        const price = document.createElement('div');
        price.className = 'cart-item-price';
        price.textContent = `$${item.price.toFixed(2)}`;

        const quantityControl = document.createElement('div');
        quantityControl.style.display = 'flex';
        quantityControl.style.alignItems = 'center';
        quantityControl.style.gap = '0.5rem';
        quantityControl.style.marginTop = '0.5rem';

        const minusBtn = document.createElement('button');
        minusBtn.textContent = '-';
        minusBtn.style.width = '25px';
        minusBtn.style.height = '25px';
        minusBtn.addEventListener('click', () => updateQuantity(item.id, -1));

        const quantityDisplay = document.createElement('span');
        quantityDisplay.className = 'cart-item-quantity';
        quantityDisplay.textContent = item.quantity;

        const plusBtn = document.createElement('button');
        plusBtn.textContent = '+';
        plusBtn.style.width = '25px';
        plusBtn.style.height = '25px';
        plusBtn.addEventListener('click', () => updateQuantity(item.id, 1));

        quantityControl.appendChild(minusBtn);
        quantityControl.appendChild(quantityDisplay);
        quantityControl.appendChild(plusBtn);

        const removeBtn = document.createElement('button');
        removeBtn.className = 'remove-btn';
        removeBtn.textContent = '×';
        removeBtn.addEventListener('click', () => removeFromCart(item.id));

        details.appendChild(title);
        details.appendChild(author);
        details.appendChild(price);
        details.appendChild(quantityControl);

        cartItem.appendChild(img);
        cartItem.appendChild(details);
        cartItem.appendChild(removeBtn);

        cartItemsContainer.appendChild(cartItem);
    });

    cartTotal.textContent = `$${total.toFixed(2)}`;
}

function handleCheckout() {
    if (cart.length === 0) {
        alert('Tu carrito está vacío');
        return;
    }

    if (!isLoggedIn) {
        alert('Por favor inicie sesión para proceder al pago');
        openLoginModal();
        return;
    }

    // Simulate purchase
    alert(`¡Gracias por su compra, ${localStorage.getItem(USERNAME_KEY) || ''}!\nTotal: ${cartTotal.textContent}\nSu pedido ha sido procesado.`);
    cart = [];
    saveCart();
    renderCart();
}

function showNotification(message) {
    // Simple toast notification
    const notification = document.createElement('div');
    notification.style.position = 'fixed';
    notification.style.bottom = '20px';
    notification.style.right = '20px';
    notification.style.backgroundColor = 'var(--accent)';
    notification.style.color = 'var(--bg-dark)';
    notification.style.padding = '1rem';
    notification.style.borderRadius = 'var(--border-radius)';
    notification.style.boxShadow = '0 4px 12px rgba(0,0,0,0.3)';
    notification.style.zIndex = '1000';
    notification.style.animation = 'slideIn 0.3s, fadeOut 0.3s 2.7s forwards';

    notification.textContent = message;
    document.body.appendChild(notification);

    // Remove after animation
    setTimeout(() => {
        notification.remove();
    }, 3100);
}

// Add CSS for notification animation
const style = document.createElement('style');
style.textContent = `
    @keyframes slideIn {
        from { transform: translateX(100%); opacity: 0; }
        to { transform: translateX(0); opacity: 1; }
    }
    @keyframes fadeOut {
        to { opacity: 0; }
    }
`;
document.head.appendChild(style);