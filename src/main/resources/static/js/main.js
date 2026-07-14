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
    // (si el ancla esta dentro de un modal, se cierra el modal ANTES de
    //  hacer scroll; de lo contrario el scroll queda bloqueado y "no pasa nada")
    // ==========================================
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        const href = anchor.getAttribute('href');
        if (href === '#' || href.length < 2) return;
        anchor.addEventListener('click', function (e) {
            const target = document.querySelector(href);
            if (!target) return;
            e.preventDefault();
            const modalEl = this.closest('.modal');
            const modal = modalEl ? bootstrap.Modal.getInstance(modalEl) : null;
            if (modal) {
                modalEl.addEventListener('hidden.bs.modal',
                    () => target.scrollIntoView({ behavior: 'smooth', block: 'start' }), { once: true });
                modal.hide();
            } else {
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

// ==========================================
// CARRITO FLOTANTE (AJAX, sin recargar) + sonido + animacion
// ==========================================
document.addEventListener('DOMContentLoaded', function () {

    // Sonido corto al agregar (Web Audio, sin archivo externo)
    function sonidoCarrito() {
        try {
            const ctx = new (window.AudioContext || window.webkitAudioContext)();
            const o = ctx.createOscillator(), g = ctx.createGain();
            o.connect(g); g.connect(ctx.destination);
            o.type = 'sine';
            o.frequency.setValueAtTime(660, ctx.currentTime);
            o.frequency.exponentialRampToValueAtTime(990, ctx.currentTime + 0.12);
            g.gain.setValueAtTime(0.0001, ctx.currentTime);
            g.gain.exponentialRampToValueAtTime(0.25, ctx.currentTime + 0.03);
            g.gain.exponentialRampToValueAtTime(0.0001, ctx.currentTime + 0.3);
            o.start(); o.stop(ctx.currentTime + 0.3);
        } catch (e) { /* navegador sin audio: se ignora */ }
    }

    // Actualiza el contador del carrito con animacion
    function actualizarBadge(count) {
        document.querySelectorAll('.badge-cart').forEach(b => {
            b.textContent = count;
            b.style.display = count > 0 ? '' : 'none';
            b.classList.remove('cart-bump'); void b.offsetWidth; b.classList.add('cart-bump');
        });
        document.querySelectorAll('.btn-cart-nav, .btn-cart-mobile').forEach(i => {
            i.classList.remove('cart-shake'); void i.offsetWidth; i.classList.add('cart-shake');
        });
    }

    // Dibuja el contenido del offcanvas
    function renderCarrito(data) {
        const cont = document.getElementById('cartItems');
        const footer = document.getElementById('cartFooter');
        if (!cont) return;
        const items = data.items || [];
        if (!items.length) {
            cont.innerHTML = '<div class="text-center text-muted py-5"><i class="bi bi-cart-x d-block" style="font-size:2.5rem"></i><p class="mt-2 mb-0">Tu carrito esta vacio</p></div>';
            if (footer) footer.style.display = 'none';
            return;
        }
        let html = '';
        items.forEach(it => {
            html +=
                '<div class="d-flex align-items-center gap-2 py-2 border-bottom">' +
                '<img src="' + it.imagenUrl + '" onerror="this.src=\'/images/placeholder.svg\'" style="width:52px;height:52px;object-fit:cover;border-radius:8px">' +
                '<div class="flex-grow-1"><div class="fw-semibold small">' + it.nombre + '</div>' +
                '<div class="text-muted" style="font-size:.8rem">' + it.cantidad + ' x S/ ' + Number(it.precioUnitario).toFixed(2) + '</div></div>' +
                '<div class="text-end"><div class="fw-bold small">S/ ' + Number(it.subtotal).toFixed(2) + '</div>' +
                '<button class="btn btn-link text-danger p-0 btn-quitar-item" data-id="' + it.productoId + '" style="font-size:.75rem">Quitar</button></div>' +
                '</div>';
        });
        cont.innerHTML = html;
        const totalEl = document.getElementById('cartTotal');
        if (totalEl) totalEl.textContent = 'S/ ' + Number(data.total).toFixed(2);
        if (footer) footer.style.display = 'block';

        cont.querySelectorAll('.btn-quitar-item').forEach(btn => {
            btn.addEventListener('click', function () {
                fetch('/carrito/api/eliminar', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: 'productoId=' + this.dataset.id
                }).then(r => r.json()).then(d => { renderCarrito(d); actualizarBadge(d.count); });
            });
        });
    }

    function cargarCarrito() { return fetch('/carrito/api/items').then(r => r.json()); }

    const offcanvas = document.getElementById('offcanvasCarrito');
    if (offcanvas) {
        offcanvas.addEventListener('show.bs.offcanvas', () => cargarCarrito().then(renderCarrito));
    }

    // Intercepta los formularios de "agregar al carrito" para no recargar
    document.querySelectorAll('form[action*="carrito/agregar"]').forEach(form => {
        form.addEventListener('submit', function (e) {
            e.preventDefault();
            const pid = form.querySelector('[name="productoId"]');
            if (!pid) { form.submit(); return; }
            const cantEl = form.querySelector('[name="cantidad"]');
            const cantidad = cantEl ? cantEl.value : 1;
            fetch('/carrito/api/agregar', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: 'productoId=' + pid.value + '&cantidad=' + cantidad
            }).then(r => r.json()).then(d => {
                actualizarBadge(d.count);
                sonidoCarrito();
                cargarCarrito().then(data => {
                    renderCarrito(data);
                    if (offcanvas) bootstrap.Offcanvas.getOrCreateInstance(offcanvas).show();
                });
            }).catch(() => form.submit());   // si el AJAX falla, envio normal
        });
    });
});
