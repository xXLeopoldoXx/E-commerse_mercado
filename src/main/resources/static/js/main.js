/* ============================================
   MERCADO YULI ONLINE - JavaScript Principal
   ============================================ */

document.addEventListener('DOMContentLoaded', function () {

    // ==========================================
    // NOTA: la validacion de login y registro se realiza
    // en el servidor con Spring Validator (Bean Validation).
    // Aqui solo se limpian los mensajes al reabrir los modales.
    // ==========================================

    // ==========================================
    // HELPER: LIMPIAR ERRORES INLINE
    // ==========================================
    function limpiarErrores(form) {
        form.querySelectorAll('.my-input').forEach(el => {
            el.classList.remove('is-invalid');
        });
        form.querySelectorAll('.invalid-feedback').forEach(el => {
            el.textContent = '';
        });
    }

    // ==========================================
    // LIMPIAR MODALS AL CERRAR
    // ==========================================
    ['loginModal', 'registerModal'].forEach(modalId => {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.addEventListener('hidden.bs.modal', function () {
                const form = this.querySelector('form');
                if (form) {
                    form.reset();
                    limpiarErrores(form);
                }
            });
        }
    });

    // ==========================================
    // TOAST FLASH MESSAGES
    // ==========================================
    const toastEl = document.getElementById('toastCarrito');
    const toastMsg = document.getElementById('toastMensaje');
    if (toastEl && toastEl.dataset.mensaje) {
        toastMsg.textContent = toastEl.dataset.mensaje;
        new bootstrap.Toast(toastEl, { delay: 3000 }).show();
    }

    // ==========================================
    // NAVBAR SCROLL EFFECT
    // ==========================================
    const navbar = document.querySelector('.my-navbar');
    if (navbar) {
        window.addEventListener('scroll', function () {
            if (window.scrollY > 50) {
                navbar.style.boxShadow = '0 4px 30px rgba(26, 92, 42, 0.35)';
            } else {
                navbar.style.boxShadow = '0 4px 20px rgba(26, 92, 42, 0.25)';
            }
        }, { passive: true });
    }

    // ==========================================
    // SMOOTH SCROLL PARA ANCLAS
    // ==========================================
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                e.preventDefault();
                target.scrollIntoView({ behavior: 'smooth', block: 'start' });
            }
        });
    });

    // ==========================================
    // ANIMACION DE ENTRADA PARA CARDS
    // ==========================================
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.opacity = '1';
                entry.target.style.transform = 'translateY(0)';
                observer.unobserve(entry.target);
            }
        });
    }, { threshold: 0.1 });

    document.querySelectorAll('.product-card, .cat-card, .confirm-info-card').forEach(card => {
        card.style.opacity = '0';
        card.style.transform = 'translateY(20px)';
        card.style.transition = 'opacity 0.5s ease, transform 0.5s ease';
        observer.observe(card);
    });

});

// ==========================================
// MODAL BIENVENIDA (una sola vez por sesion)
// ==========================================
window.addEventListener('load', function () {
    const visto = sessionStorage.getItem('bienvenidaVisto');
    const modal = document.getElementById('bienvenidaModal');
    if (!visto && modal) {
        setTimeout(() => {
            new bootstrap.Modal(modal).show();
            sessionStorage.setItem('bienvenidaVisto', 'true');
        }, 1200);
    }
});

// ==========================================
// COPIAR CODIGO DE DESCUENTO
// ==========================================
function copiarCodigo() {
    const codigo = document.getElementById('codigoDescuento').textContent;
    navigator.clipboard.writeText(codigo).then(() => {
        const icono = document.getElementById('iconoCopiar');
        icono.className = 'bi bi-clipboard-check';
        setTimeout(() => { icono.className = 'bi bi-clipboard'; }, 2000);
    });
}

// ==========================================
// MODAL CONFIRMAR ELIMINAR CARRITO
// ==========================================
document.addEventListener('DOMContentLoaded', function () {
    const eliminarModal = document.getElementById('eliminarModal');
    if (eliminarModal) {
        eliminarModal.addEventListener('show.bs.modal', function (e) {
            const btn = e.relatedTarget;
            const nombre = btn.getAttribute('data-nombre');
            const id = btn.getAttribute('data-id');
            document.getElementById('nombreProductoEliminar').textContent = nombre;
            document.getElementById('productoIdEliminar').value = id;
            document.getElementById('formEliminar').action = '/carrito/eliminar';
        });
    }
});

// ==========================================
// VALIDACION FORMULARIO CONTACTO
// ==========================================
document.addEventListener('DOMContentLoaded', function () {
    const contactoForm = document.getElementById('contactoForm');
    if (contactoForm) {
        contactoForm.addEventListener('submit', function (e) {
            e.preventDefault();
            let valido = true;

            const nombre  = document.getElementById('contactoNombre');
            const email   = document.getElementById('contactoEmail');
            const asunto  = document.getElementById('contactoAsunto');
            const mensaje = document.getElementById('contactoMensaje');

            ['errorContactoNombre','errorContactoEmail','errorContactoAsunto','errorContactoMensaje']
                .forEach(id => { document.getElementById(id).textContent = ''; });
            [nombre, email, asunto, mensaje].forEach(el => el.classList.remove('is-invalid'));

            if (!nombre.value.trim()) {
                nombre.classList.add('is-invalid');
                document.getElementById('errorContactoNombre').textContent = 'Ingresa tu nombre.';
                valido = false;
            }
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(email.value)) {
                email.classList.add('is-invalid');
                document.getElementById('errorContactoEmail').textContent = 'Ingresa un correo valido.';
                valido = false;
            }
            if (!asunto.value) {
                asunto.classList.add('is-invalid');
                document.getElementById('errorContactoAsunto').textContent = 'Selecciona el motivo.';
                valido = false;
            }
            if (!mensaje.value.trim() || mensaje.value.trim().length < 10) {
                mensaje.classList.add('is-invalid');
                document.getElementById('errorContactoMensaje').textContent = 'Escribe tu mensaje (minimo 10 caracteres).';
                valido = false;
            }

            if (valido) {
                contactoForm.reset();
                document.getElementById('mensajeExito').textContent = 'Mensaje enviado correctamente!';
                const contactModal = bootstrap.Modal.getInstance(document.getElementById('contactoModal'));
                if (contactModal) contactModal.hide();
                setTimeout(() => new bootstrap.Modal(document.getElementById('exitoModal')).show(), 300);
            }
        });
    }
});

// ==========================================
// CARRUSELES DE PRODUCTOS
// ==========================================
document.addEventListener('DOMContentLoaded', function () {
    // Carousel destacados
    const carousel1 = document.getElementById('carouselDestacados');
    document.getElementById('prevDestacados')?.addEventListener('click', () => {
        carousel1.scrollBy({ left: -420, behavior: 'smooth' });
    });
    document.getElementById('nextDestacados')?.addEventListener('click', () => {
        carousel1.scrollBy({ left: 420, behavior: 'smooth' });
    });

    // Carousel ofertas
    const carousel2 = document.getElementById('carouselOfertas');
    document.getElementById('prevOfertas')?.addEventListener('click', () => {
        carousel2.scrollBy({ left: -420, behavior: 'smooth' });
    });
    document.getElementById('nextOfertas')?.addEventListener('click', () => {
        carousel2.scrollBy({ left: 420, behavior: 'smooth' });
    });

    // Filtros por categoria
    document.querySelectorAll('.filtro-btn').forEach(btn => {
        btn.addEventListener('click', function () {
            document.querySelectorAll('.filtro-btn').forEach(b => b.classList.remove('active'));
            this.classList.add('active');

            const cat = this.dataset.cat;
            document.querySelectorAll('.product-card-carousel').forEach(card => {
                if (cat === 'todos' || card.dataset.cat === cat) {
                    card.style.display = 'block';
                } else {
                    card.style.display = 'none';
                }
            });
        });
    });
});
