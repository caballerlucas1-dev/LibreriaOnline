// Simple Bookstore MVP - JavaScript Logic

// Cart state (in a real app, this would be in localStorage or backend)
let cart = [];

// DOM Elements
const booksGrid = document.getElementById('booksGrid');
const cartIndicator = document.getElementById('cartIndicator');

// Initialize the app
function init() {
  renderBooks();
  updateCartIndicator();
}

// Render all books to the grid
function renderBooks() {
  booksGrid.innerHTML = ''; // Clear existing content

  books.forEach(book => {
    const bookCard = createBookCard(book);
    booksGrid.appendChild(bookCard);
  });
}

// Create a book card element
function createBookCard(book) {
  const card = document.createElement('div');
  card.className = 'book-card';

  card.innerHTML = `
    <div class="book-cover">
      <img src="${book.cover}" alt="${book.title}" onerror="this.onerror=null;this.src='https://via.placeholder.com/300x400?text=Sin+Portada'">
    </div>
    <div class="book-info">
      <h2>${book.title}</h2>
      <p class="author">por ${book.author}</p>
      <p class="price">$${book.price.toFixed(2)}</p>
      <p class="book-description">${book.description}</p>
      <button class="buy-btn" data-book-id="${book.id}">
        Comprar Ahora
      </button>
    </div>
  `;

  // Add event listener to the buy button
  const buyBtn = card.querySelector('.buy-btn');
  buyBtn.addEventListener('click', () => handlePurchase(book));

  return card;
}

// Handle book purchase
function handlePurchase(book) {
  // Add to cart
  cart.push({
    ...book,
    purchasedAt: new Date().toISOString()
  });

  // Update UI
  updateCartIndicator();

  // Show feedback
  showPurchaseFeedback(book.title);

  // In a real app, we might redirect to cart or checkout here
}

// Update the cart indicator badge
function updateCartIndicator() {
  cartIndicator.textContent = cart.length;
  if (cart.length === 0) {
    cartIndicator.style.display = 'none';
  } else {
    cartIndicator.style.display = 'flex';
  }
}

// Show a simple purchase feedback
function showPurchaseFeedback(title) {
  // Create a temporary notification
  const notification = document.createElement('div');
  notification.style.cssText = `
    position: fixed;
    top: 20px;
    left: 50%;
    transform: translateX(-50%);
    background: #27ae60;
    color: white;
    padding: 12px 24px;
    border-radius: 30px;
    font-size: 1rem;
    box-shadow: 0 4px 12px rgba(0,0,0,0.15);
    z-index: 1000;
    opacity: 0;
    transition: opacity 0.3s ease;
  `;
  notification.textContent = `¡${title} agregado al carrito!`;

  document.body.appendChild(notification);

  // Trigger reflow for animation
  void notification.offsetWidth;

  // Fade in
  notification.style.opacity = '1';

  // Remove after 2 seconds
  setTimeout(() => {
    notification.style.opacity = '0';
    setTimeout(() => {
      document.body.removeChild(notification);
    }, 300);
  }, 2000);
}

// Initialize the app when DOM is loaded
document.addEventListener('DOMContentLoaded', init);

// For debugging - expose cart to window
window.cart = cart;