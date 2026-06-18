// Sample book data for MVP
const books = [
  {
    id: 1,
    title: "El Alquimista",
    author: "Paulo Coelho",
    price: 12.99,
    cover: "https://covers.openlibrary.org/b/id/8225378-L.jpg",
    description: "Una historia mágica sobre seguir tus sueños y escuchar tu corazón."
  },
  {
    id: 2,
    title: "Cien años de soledad",
    author: "Gabriel García Márquez",
    price: 14.99,
    cover: "https://covers.openlibrary.org/b/id/8195541-L.jpg",
    description: "La historia épica de la familia Buendía en el pueblo de Macondo."
  },
  {
    id: 3,
    title: "Don Quijote de la Mancha",
    author: "Miguel de Cervantes",
    price: 9.99,
    cover: "https://covers.openlibrary.org/b/id/8190972-L.jpg",
    description: "Las aventuras del ingenioso hidalgo Don Quijote y su escudero Sancho Panza."
  },
  {
    id: 4,
    title: "1984",
    author: "George Orwell",
    price: 10.99,
    cover: "https://covers.openlibrary.org/b/id/8211072-L.jpg",
    description: "Una perturbadora visión de un futuro totalitario donde Big Brother te observa."
  },
  {
    id: 5,
    title: "Harry Potter y la Piedra Filosofal",
    author: "J.K. Rowling",
    price: 11.99,
    cover: "https://covers.openlibrary.org/b/id/8262982-L.jpg",
    description: "El inicio de las aventuras del joven mago Harry Potter en Hogwarts."
  },
  {
    id: 6,
    title: "El Principito",
    author: "Antoine de Saint-Exupéry",
    price: 8.99,
    cover: "https://covers.openlibrary.org/b/id/8244974-L.jpg",
    description: "Una poética historia sobre un niño que viaja entre planetas aprendiendo sobre la vida."
  }
];

// Make books available globally
window.books = books;