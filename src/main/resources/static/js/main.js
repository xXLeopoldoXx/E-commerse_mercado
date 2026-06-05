/* ============================================
   MERCADO YULI ONLINE - JavaScript Principal
   ============================================ */

document.addEventListener('DOMContentLoaded', function () {

    // ==========================================
    // VALIDACION FORMULARIO LOGIN
    // ==========================================
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', function (e) {
            e.preventDefault();
            let valido = true;

            // Limpiar errores
            limpiarErrores(loginForm);

            // Validar email
            const email = document.getElementById('loginEmail');
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!email.value.trim() || !emailRegex.test(email.value)) {
                mostrarError(email, 'errorLoginEmail', 'Ingresa un correo electronico valido.');
                valido = false;
            }

            // Validar contrasena
            const password = document.getElementById('loginPassword');
            if (!password.value || password.value.length < 6) {
                mostrarError(password, 'errorLoginPassword', 'La contrasena debe tener al menos 6 caracteres.');
                valido = false;
            }

            if (valido) {
                loginForm.submit();
            }
        });
    }

    // ==========================================
    // VALIDACION FORMULARIO REGISTRO
    // ==========================================
    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        registerForm.addEventListener('submit', function (e) {
            e.preventDefault();
            let valido = true;

            limpiarErrores(registerForm);

            // Validar DNI
            const dni = document.getElementById('regDni');
            if (dni && (!dni.value.match(/^[0-9]{8}$/))) {
                mostrarError(dni, 'errorRegDni', 'El DNI debe tener exactamente 8 digitos.');
                valido = false;
            }

            // Validar telefono
            const telefono = document.getElementById('regTelefono');
            if (telefono && (!telefono.value.match(/^[0-9]{9}$/))) {
                mostrarError(telefono, 'errorRegTelefono', 'El telefono debe tener 9 digitos.');
                valido = false;
            }

            // Validar nombre
            const nombre = document.getElementById('regNombre');
            if (!nombre.value.trim() || nombre.value.trim().length < 3) {
                mostrarError(nombre, 'errorRegNombre', 'Ingresa tu nombre completo (minimo 3 caracteres).');
                valido = false;
            }

            // Validar email
            const email = document.getElementById('regEmail');
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!email.value.trim() || !emailRegex.test(email.value)) {
                mostrarError(email, 'errorRegEmail', 'Ingresa un correo electronico valido.');
                valido = false;
            }

            // Validar contrasena: min 8 chars, mayuscula, minuscula, numero
            const password = document.getElementById('regPassword');
            const passRegex = /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d).{8,}$/;
            if (!passRegex.test(password.value)) {
                mostrarError(password, 'errorRegPassword',
                    'Minimo 8 caracteres, una mayuscula, una minuscula y un numero.');
                valido = false;
            }

            if (valido) {
                registerForm.submit();
            }
        });
    }

    // ==========================================
    // HELPER: MOSTRAR ERROR INLINE
    // ==========================================
    function mostrarError(input, errorId, mensaje) {
        input.classList.add('is-invalid');
        const errorDiv = document.getElementById(errorId);
        if (errorDiv) errorDiv.textContent = mensaje;
    }

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
// FLASH MESSAGES DEL SERVIDOR (login/registro)
// Abre el modal correcto con el error o exito
// ==========================================
window.addEventListener('load', function () {
    let hayFlash = false;

    const flashLogin    = document.getElementById('flashMensajeLogin');
    const flashRegistro = document.getElementById('flashMensajeRegistro');

    function parsearMensaje(raw) {
        const idx = raw.indexOf(':');
        if (idx === -1) return null;
        return { tipo: raw.substring(0, idx), texto: raw.substring(idx + 1) };
    }

    // — Flash de LOGIN —
    if (flashLogin && flashLogin.dataset.mensaje) {
        const m = parsearMensaje(flashLogin.dataset.mensaje);
        if (m) {
            hayFlash = true;
            if (m.tipo === 'ERROR') {
                const alerta = document.getElementById('alertaLoginServer');
                const texto  = document.getElementById('alertaLoginTexto');
                if (alerta && texto) {
                    texto.textContent = m.texto;
                    alerta.classList.remove('d-none');
                }
                setTimeout(() => {
                    const modal = document.getElementById('loginModal');
                    if (modal) new bootstrap.Modal(modal).show();
                }, 150);
            } else if (m.tipo === 'OK') {
                // Login exitoso: mostrar toast de bienvenida
                const toastEl  = document.getElementById('toastCarrito');
                const toastMsg = document.getElementById('toastMensaje');
                if (toastEl && toastMsg) {
                    toastMsg.textContent = m.texto;
                    toastEl.querySelector('.bi')?.classList.replace('bi-cart-check-fill', 'bi-person-check-fill');
                    new bootstrap.Toast(toastEl, { delay: 4000 }).show();
                }
            }
        }
    }

    // — Flash de REGISTRO —
    if (flashRegistro && flashRegistro.dataset.mensaje) {
        const m = parsearMensaje(flashRegistro.dataset.mensaje);
        if (m) {
            hayFlash = true;
            if (m.tipo === 'ERROR') {
                const alerta = document.getElementById('alertaRegistroServer');
                const texto  = document.getElementById('alertaRegistroTexto');
                if (alerta && texto) {
                    texto.textContent = m.texto;
                    alerta.classList.remove('d-none');
                }
                setTimeout(() => {
                    const modal = document.getElementById('registerModal');
                    if (modal) new bootstrap.Modal(modal).show();
                }, 150);
            } else if (m.tipo === 'OK') {
                // Registro exitoso: mostrar modal de exito
                const exito = document.getElementById('exitoModal');
                const msg   = document.getElementById('mensajeExito');
                if (exito && msg) {
                    msg.textContent = 'Cuenta creada correctamente. Ahora puedes iniciar sesion.';
                    setTimeout(() => new bootstrap.Modal(exito).show(), 150);
                }
            }
        }
    }

    // — Modal de bienvenida: solo si no hay flash messages —
    if (!hayFlash) {
        const visto = sessionStorage.getItem('bienvenidaVisto');
        const modal = document.getElementById('bienvenidaModal');
        if (!visto && modal) {
            setTimeout(() => {
                new bootstrap.Modal(modal).show();
                sessionStorage.setItem('bienvenidaVisto', 'true');
            }, 1200);
        }
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
