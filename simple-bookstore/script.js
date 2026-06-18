// LibreriaOnline - Simple Bookstore MVP
// Vanilla JavaScript, HTML, CSS

document.addEventListener('DOMContentLoaded', () => {
    // Elements
    const loginBtn = document.getElementById('loginBtn');
    const loginModal = document.getElementById('loginModal');
    const closeLogin = loginModal.querySelector('.close');
    const loginForm = document.getElementById('loginForm');
    const loginError = document.getElementById('loginError');

    const cartBtn = document.getElementById('cartBtn');
    const cartModal = document.getElementById('cartModal');
    const closeCart = cartModal.querySelector('.close');
    const cartItemsContainer = document.getElementById('cartItems');
    const cartCount = document.getElementById('cartCount');
    const cartTotal = document.getElementById('cartTotal');
    const checkoutBtn = document.getElementById('checkoutBtn');

    const booksGrid = document.getElementById('booksGrid');

    // State
    let isLoggedIn = false;
    let cart = JSON.parse(localStorage.getItem('libreriaCart') || '[]');
    const USERNAME_KEY = 'libreriaUser';

    // Initialize
    init();

    function init() {
        checkLoginStatus();
        renderCart();
        fetchBooks();

        // Event listeners
        loginBtn.addEventListener('click', () => {
            loginModal.style.display = 'block';
        });

        closeLogin.addEventListener('click', () => {
            loginModal.style.display = 'none';
            loginError.textContent = '';
            loginForm.reset();
        });

        cartBtn.addEventListener('click', () => {
            cartModal.style.display = 'block';
        });

        closeCart.addEventListener('click', () => {
            cartModal.style.display = 'none';
        });

        loginForm.addEventListener('submit', handleLogin);
        checkoutBtn.addEventListener('click', handleCheckout);

        // Close modal when clicking outside
        window.addEventListener('click', (e) => {
            if (e.target === loginModal) {
                loginModal.style.display = 'none';
            }
            if (e.target === cartModal) {
                cartModal.style.display = 'none';
            }
        });
    }

    function checkLoginStatus() {
        const username = localStorage.getItem(USERNAME_KEY);
        if (username) {
            isLoggedIn = true;
            loginBtn.textContent = `Hola, ${username}`;
            loginBtn.style.backgroundColor = '#4caf50';
            loginBtn.style.color = 'white';
        } else {
            isLoggedIn = false;
            loginBtn.textContent = 'Login';
            loginBtn.style.backgroundColor = '';
            loginBtn.style.color = '';
        }
    }

    function handleLogin(e) {
        e.preventDefault();
        const username = document.getElementById('username').value.trim();
        const password = document.getElementById('password').value.trim();

        // Simple validation (in real app, use proper auth)
        if (username && password) {
            // For demo, accept any non-empty credentials
            localStorage.setItem(USERNAME_KEY, username);
            isLoggedIn = true;
            loginBtn.textContent = `Hola, ${username}`;
            loginBtn.style.backgroundColor = '#4caf50';
            loginBtn.style.color = 'white';
            loginModal.style.display = 'none';
            loginError.textContent = '';
            loginForm.reset();
        } else {
            loginError.textContent = 'Por favor ingrese usuario y contraseña';
        }
    }

    function fetchBooks() {
        // Show loading
        booksGrid.innerHTML = '<p class="loading">Cargando libros...</p>';

        // Fetch from Open Library API
        fetch('https://openlibrary.org/search.json?q=fiction&limit=12')
            .then(response => response.json())
            .then(data => {
                displayBooks(data.docs);
            })
            .catch(error => {
                console.error('Error fetching books:', error);
                booksGrid.innerHTML = '<p class="error">Error al cargar libros. Intente nuevamente más tarde.</p>';
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
            loginBtn.click();
            return;
        }

        // Simulate purchase
        alert(`¡Gracias por su compra, ${localStorage.getItem(USERNAME_KEY)}!\nTotal: ${cartTotal.textContent}\nSu pedido ha sido procesado.`);
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
});