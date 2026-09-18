/* =============================================================
   SOLUMECA S.A.S. — script.js
   Funciona en todas las páginas del sitio. Cada bloque verifica
   que sus elementos existan antes de engancharse, ya que no
   todas las páginas tienen los mismos componentes.
   ============================================================= */

document.addEventListener('DOMContentLoaded', () => {

  const prefersReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;

  /* ---------------------------------------------------------
     0.0 Selector de perfil del login
     --------------------------------------------------------- */
  const roleCards = document.querySelectorAll('.role-card');
  const usernameInput = document.getElementById('username');
  const passwordInput = document.getElementById('password');
  if (roleCards.length && usernameInput) {
    usernameInput.value = document.querySelector('.role-card.is-selected')?.dataset.user || usernameInput.value;
    roleCards.forEach((card) => {
      card.addEventListener('click', () => {
        roleCards.forEach((item) => {
          const selected = item === card;
          item.classList.toggle('is-selected', selected);
          item.setAttribute('aria-pressed', String(selected));
        });
        usernameInput.value = card.dataset.user || '';
        if (passwordInput) passwordInput.focus();
      });
    });
  }

  const queryParams = new URLSearchParams(window.location.search);
  const loginSuccess = document.getElementById('loginSuccess');
  const loginError = document.getElementById('loginError');
  if (loginSuccess && queryParams.get('registered') === 'true') {
    loginSuccess.hidden = false;
    loginSuccess.classList.add('is-active');
  }
  const errorParam = queryParams.get('error');
  if (loginError && errorParam) {
    if (errorParam === 'not_registered') {
      loginError.textContent = 'Este usuario o correo no está registrado en el sistema.';
    } else if (errorParam === 'bad_credentials') {
      loginError.textContent = 'Contraseña incorrecta. Verifica tus credenciales e intenta de nuevo.';
    } else {
      loginError.textContent = 'Usuario o contraseña incorrectos.';
    }
    loginError.classList.add('is-active');
  }

  /* ---------------------------------------------------------
     0.0.1 Ver / ocultar contraseña con el ojito (Universal)
     --------------------------------------------------------- */
  const initPasswordToggles = () => {
    // Manejar botones ya presentes en el marcado
    document.querySelectorAll('.btn-toggle-password').forEach((btn) => {
      if (btn.dataset.initialized) return;
      btn.dataset.initialized = 'true';

      const wrapper = btn.closest('.password-input-wrapper') || btn.parentElement;
      const input = wrapper ? wrapper.querySelector('input') : null;
      if (!input) return;

      const eyeShow = btn.querySelector('.eye-show');
      const eyeHide = btn.querySelector('.eye-hide');

      btn.addEventListener('click', (e) => {
        e.preventDefault();
        const isPassword = input.type === 'password';
        input.type = isPassword ? 'text' : 'password';

        if (eyeShow && eyeHide) {
          eyeShow.style.display = isPassword ? 'none' : 'block';
          eyeHide.style.display = isPassword ? 'block' : 'none';
        }
        btn.setAttribute('aria-label', isPassword ? 'Ocultar contraseña' : 'Ver contraseña');
        btn.setAttribute('title', isPassword ? 'Ocultar contraseña' : 'Ver contraseña');
        input.focus();
      });
    });

    // Auto-envolver cualquier input[type="password"] que no tenga aún botón de ojito
    document.querySelectorAll('input[type="password"]').forEach((input) => {
      if (input.closest('.password-input-wrapper') || input.dataset.hasEyeToggle) return;
      input.dataset.hasEyeToggle = 'true';

      const wrapper = document.createElement('div');
      wrapper.className = 'password-input-wrapper';
      input.parentNode.insertBefore(wrapper, input);
      wrapper.appendChild(input);

      const btn = document.createElement('button');
      btn.type = 'button';
      btn.className = 'btn-toggle-password';
      btn.setAttribute('aria-label', 'Ver contraseña');
      btn.setAttribute('title', 'Ver contraseña');
      btn.innerHTML = `
        <svg class="eye-show" viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
          <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
          <circle cx="12" cy="12" r="3"></circle>
        </svg>
        <svg class="eye-hide" viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true" style="display:none;">
          <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path>
          <line x1="1" y1="1" x2="23" y2="23"></line>
        </svg>
      `;
      wrapper.appendChild(btn);

      const eyeShow = btn.querySelector('.eye-show');
      const eyeHide = btn.querySelector('.eye-hide');

      btn.addEventListener('click', (e) => {
        e.preventDefault();
        const isPassword = input.type === 'password';
        input.type = isPassword ? 'text' : 'password';
        if (eyeShow && eyeHide) {
          eyeShow.style.display = isPassword ? 'none' : 'block';
          eyeHide.style.display = isPassword ? 'block' : 'none';
        }
        btn.setAttribute('aria-label', isPassword ? 'Ocultar contraseña' : 'Ver contraseña');
        btn.setAttribute('title', isPassword ? 'Ocultar contraseña' : 'Ver contraseña');
        input.focus();
      });
    });
  };
  initPasswordToggles();

  const carouselTrack = document.querySelector('[data-carousel-track]');
  const carouselCards = carouselTrack ? Array.from(carouselTrack.children) : [];
  const carouselPrev = document.querySelector('[data-carousel-prev]');
  const carouselNext = document.querySelector('[data-carousel-next]');
  let carouselIndex = 0;
  const updateCarousel = () => {
    if (!carouselTrack || !carouselCards.length) return;
    const gap = 12;
    const cardWidth = carouselCards[0].getBoundingClientRect().width + gap;
    carouselTrack.style.transform = `translateX(-${carouselIndex * cardWidth}px)`;
  };
  if (carouselTrack && carouselCards.length) {
    carouselNext?.addEventListener('click', () => {
      const visibleCards = window.innerWidth <= 520 ? 1 : window.innerWidth <= 800 ? 2 : 4;
      carouselIndex = (carouselIndex + 1) % Math.max(1, carouselCards.length - visibleCards + 1);
      updateCarousel();
    });
    carouselPrev?.addEventListener('click', () => {
      const visibleCards = window.innerWidth <= 520 ? 1 : window.innerWidth <= 800 ? 2 : 4;
      const maxIndex = Math.max(1, carouselCards.length - visibleCards + 1);
      carouselIndex = (carouselIndex - 1 + maxIndex) % maxIndex;
      updateCarousel();
    });
    window.addEventListener('resize', updateCarousel);
  }

  /* ---------------------------------------------------------
     0.1 Usuario autenticado en la navegación pública
     --------------------------------------------------------- */
  const sessionLink = document.querySelector('.navbar__link[href="login.html"]');
  if (sessionLink) {
    fetch('/api/session', { credentials: 'same-origin' })
      .then((response) => response.ok ? response.json() : null)
      .then((session) => {
        if (!session?.authenticated) return;
        const destinations = {
          ADMIN: '/admin/dashboard',
          CLIENTE: '/cliente/dashboard',
          TECNICO: '/tecnico/dashboard',
          SUPERVISOR: '/supervisor/dashboard',
          ENCARGADO: '/encargado/dashboard',
        };
        sessionLink.textContent = session.username;
        sessionLink.href = destinations[session.role] || '/index.html';
        sessionLink.setAttribute('aria-label', `Abrir panel de ${session.username}`);
      })
      .catch(() => {});
  }

  /* ---------------------------------------------------------
     0. Barra de progreso de lectura
     --------------------------------------------------------- */
  const progressBar = document.getElementById('progressBar');
  if (progressBar) {
    const updateProgress = () => {
      const scrollTop = window.scrollY;
      const docHeight = document.documentElement.scrollHeight - window.innerHeight;
      const pct = docHeight > 0 ? (scrollTop / docHeight) * 100 : 0;
      progressBar.style.width = `${pct}%`;
    };
    updateProgress();
    window.addEventListener('scroll', updateProgress, { passive: true });
  }

  /* ---------------------------------------------------------
     0.1 Parallax suave en imágenes de fondo
     --------------------------------------------------------- */
  const parallaxEls = Array.from(document.querySelectorAll('[data-parallax-img]')).map((el) => ({
    el,
    speed: parseFloat(el.getAttribute('data-parallax-img')) || 0.15,
  }));

  if (parallaxEls.length && !prefersReducedMotion) {
    const updateParallax = () => {
      parallaxEls.forEach(({ el, speed }) => {
        const rect = el.parentElement.getBoundingClientRect();
        if (rect.bottom > -200 && rect.top < window.innerHeight + 200) {
          el.style.transform = `translateY(${rect.top * -speed}px) scale(1.15)`;
        }
      });
    };
    updateParallax();
    window.addEventListener('scroll', updateParallax, { passive: true });
  }

  /* ---------------------------------------------------------
     0.2 Tilt 3D + spotlight de cursor en tarjetas
     --------------------------------------------------------- */
  const tiltCards = document.querySelectorAll('.service-card--tilt, .info-card--tilt, .project-card--tilt, .benefit--tilt, .explore-card');
  if (tiltCards.length && !prefersReducedMotion && window.matchMedia('(hover: hover)').matches) {
    tiltCards.forEach((card) => {
      card.addEventListener('mousemove', (e) => {
        const rect = card.getBoundingClientRect();
        const x = e.clientX - rect.left;
        const y = e.clientY - rect.top;
        const px = x / rect.width;
        const py = y / rect.height;
        const maxTilt = 5;
        card.style.setProperty('--rx', `${(0.5 - py) * maxTilt}deg`);
        card.style.setProperty('--ry', `${(px - 0.5) * maxTilt}deg`);
        card.style.setProperty('--mx', `${x}px`);
        card.style.setProperty('--my', `${y}px`);
      });
      card.addEventListener('mouseleave', () => {
        card.style.setProperty('--rx', '0deg');
        card.style.setProperty('--ry', '0deg');
      });
    });
  }

  /* ---------------------------------------------------------
     1. Navbar: cambia de apariencia al hacer scroll
     --------------------------------------------------------- */
  const navbar = document.getElementById('navbar');
  if (navbar) {
    const updateNavbar = () => {
      navbar.classList.toggle('is-scrolled', window.scrollY > 40);
    };
    updateNavbar();
    window.addEventListener('scroll', updateNavbar, { passive: true });
  }

  /* ---------------------------------------------------------
     1.1 Resalta el enlace de la página actual en el navbar
     --------------------------------------------------------- */
  const currentPage = (location.pathname.split('/').pop() || 'index.html').split('#')[0] || 'index.html';
  document.querySelectorAll('.navbar__link:not(.navbar__link--cta), .footer__links a').forEach((link) => {
    const href = (link.getAttribute('href') || '').split('#')[0];
    if (href === currentPage || (href === '' && currentPage === 'index.html')) {
      link.classList.add('is-current');
    }
  });

  /* ---------------------------------------------------------
     2. Menú hamburguesa (móvil)
     --------------------------------------------------------- */
  const navToggle = document.getElementById('navToggle');
  const navMenu = document.getElementById('navMenu');
  if (navToggle && navMenu) {
    navToggle.addEventListener('click', () => {
      const isOpen = navMenu.classList.toggle('is-open');
      navToggle.classList.toggle('is-active', isOpen);
      navToggle.setAttribute('aria-expanded', String(isOpen));
    });

    navMenu.querySelectorAll('.navbar__link').forEach((link) => {
      link.addEventListener('click', () => {
        navMenu.classList.remove('is-open');
        navToggle.classList.remove('is-active');
        navToggle.setAttribute('aria-expanded', 'false');
      });
    });
  }

  /* ---------------------------------------------------------
     3. Aparición progresiva al hacer scroll (Intersection Observer)
     --------------------------------------------------------- */
  const revealEls = document.querySelectorAll('.reveal');
  if (revealEls.length) {
    const revealObserver = new IntersectionObserver(
      (entries, observer) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            entry.target.classList.add('is-visible');
            observer.unobserve(entry.target);
          }
        });
      },
      { threshold: 0.15, rootMargin: '0px 0px -60px 0px' }
    );
    revealEls.forEach((el) => revealObserver.observe(el));
  }

  /* ---------------------------------------------------------
     4. Contador animado de cifras
     --------------------------------------------------------- */
  const counters = document.querySelectorAll('[data-counter]');
  if (counters.length) {
    const animateCounter = (el) => {
      const target = parseInt(el.getAttribute('data-counter'), 10);
      const duration = 1400;
      const start = performance.now();
      const step = (now) => {
        const progress = Math.min((now - start) / duration, 1);
        const eased = 1 - Math.pow(1 - progress, 3);
        el.textContent = Math.round(eased * target);
        if (progress < 1) requestAnimationFrame(step);
      };
      requestAnimationFrame(step);
    };

    const counterObserver = new IntersectionObserver(
      (entries, observer) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            animateCounter(entry.target);
            observer.unobserve(entry.target);
          }
        });
      },
      { threshold: 0.6 }
    );
    counters.forEach((el) => counterObserver.observe(el));
  }

  /* ---------------------------------------------------------
     5. Modales de servicios (solo en servicios.html)
     --------------------------------------------------------- */
  const modalOverlay = document.getElementById('modalOverlay');
  const serviceCards = document.querySelectorAll('.service-card');
  let lastFocusedEl = null;

  const closeModal = () => {
    document.querySelectorAll('.modal.is-active').forEach((m) => m.classList.remove('is-active'));
    if (modalOverlay) modalOverlay.classList.remove('is-active');
    document.body.style.overflow = '';
    if (lastFocusedEl) lastFocusedEl.focus();
  };

  if (modalOverlay && serviceCards.length) {
    const openModal = (modalId) => {
      const modal = document.getElementById(modalId);
      if (!modal) return;
      lastFocusedEl = document.activeElement;
      document.querySelectorAll('.modal.is-active').forEach((m) => m.classList.remove('is-active'));
      modal.classList.add('is-active');
      modalOverlay.classList.add('is-active');
      document.body.style.overflow = 'hidden';
      modal.querySelector('.modal__close').focus();
    };

    serviceCards.forEach((card) => {
      const moreBtn = card.querySelector('.service-card__more');
      const trigger = () => openModal(card.getAttribute('data-modal'));
      if (moreBtn) moreBtn.addEventListener('click', trigger);
      card.addEventListener('click', (e) => {
        if (e.target.closest('.service-card__more')) return;
        trigger();
      });
    });

    modalOverlay.addEventListener('click', (e) => {
      if (e.target === modalOverlay) closeModal();
    });

    document.querySelectorAll('[data-close]').forEach((btn) => {
      btn.addEventListener('click', closeModal);
    });
  }

  /* ---------------------------------------------------------
     6. Lightbox de galería (solo en proyectos.html)
     --------------------------------------------------------- */
  const lightbox = document.getElementById('lightbox');
  const galleryItems = Array.from(document.querySelectorAll('.gallery__item'));
  let currentGalleryIndex = 0;

  const closeLightbox = () => {
    if (!lightbox) return;
    const videoEl = document.getElementById('lightboxVideo');
    if (videoEl) { videoEl.pause(); videoEl.removeAttribute('src'); videoEl.load(); }
    lightbox.classList.remove('is-active');
    document.body.style.overflow = '';
  };

  if (lightbox && galleryItems.length) {
    const lightboxImg = document.getElementById('lightboxImg');
    const lightboxVideo = document.getElementById('lightboxVideo');
    const lightboxClose = document.getElementById('lightboxClose');
    const lightboxPrev = document.getElementById('lightboxPrev');
    const lightboxNext = document.getElementById('lightboxNext');

    const showGalleryImage = (index) => {
      currentGalleryIndex = (index + galleryItems.length) % galleryItems.length;
      const item = galleryItems[currentGalleryIndex];
      const videoSrc = item.getAttribute('data-video');

      if (videoSrc) {
        lightboxImg.style.display = 'none';
        if (lightboxVideo) {
          lightboxVideo.style.display = 'block';
          lightboxVideo.src = videoSrc;
          lightboxVideo.play().catch(() => {});
        }
      } else {
        if (lightboxVideo) {
          lightboxVideo.pause();
          lightboxVideo.removeAttribute('src');
          lightboxVideo.style.display = 'none';
        }
        lightboxImg.style.display = 'block';
        const src = item.getAttribute('data-full');
        const alt = item.querySelector('img').getAttribute('alt');
        lightboxImg.setAttribute('src', src);
        lightboxImg.setAttribute('alt', alt);
      }
    };

    galleryItems.forEach((item, index) => {
      item.addEventListener('click', () => {
        showGalleryImage(index);
        lightbox.classList.add('is-active');
        document.body.style.overflow = 'hidden';
      });
    });

    if (lightboxClose) lightboxClose.addEventListener('click', closeLightbox);
    if (lightboxPrev) lightboxPrev.addEventListener('click', () => showGalleryImage(currentGalleryIndex - 1));
    if (lightboxNext) lightboxNext.addEventListener('click', () => showGalleryImage(currentGalleryIndex + 1));

    lightbox.addEventListener('click', (e) => {
      if (e.target === lightbox) closeLightbox();
    });

    document.addEventListener('keydown', (e) => {
      if (!lightbox.classList.contains('is-active')) return;
      if (e.key === 'ArrowLeft') showGalleryImage(currentGalleryIndex - 1);
      if (e.key === 'ArrowRight') showGalleryImage(currentGalleryIndex + 1);
    });
  }

  /* ---------------------------------------------------------
     7. Cerrar modal / lightbox con tecla Escape
     --------------------------------------------------------- */
  document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') {
      closeModal();
      closeLightbox();
    }
  });

  /* ---------------------------------------------------------
     8. Botón volver arriba
     --------------------------------------------------------- */
  const backToTop = document.getElementById('backToTop');
  if (backToTop) {
    window.addEventListener(
      'scroll',
      () => backToTop.classList.toggle('is-visible', window.scrollY > 700),
      { passive: true }
    );
    backToTop.addEventListener('click', () => {
      window.scrollTo({ top: 0, behavior: 'smooth' });
    });
  }

  /* ---------------------------------------------------------
     9. Validación del formulario de contacto (solo en contacto.html)
     --------------------------------------------------------- */
  const form = document.getElementById('contactForm');
  const formSuccess = document.getElementById('formSuccess');

  if (form) {
    const validators = {
      name: (v) => v.trim().length >= 3 || 'Ingrese su nombre completo.',
      email: (v) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v.trim()) || 'Ingrese un correo electrónico válido.',
      message: (v) => v.trim().length >= 10 || 'Cuéntenos un poco más sobre su proyecto (mín. 10 caracteres).',
    };

    const showFieldError = (field, message) => {
      const row = document.getElementById(field).closest('.form-row');
      const errorEl = document.getElementById(`err-${field}`);
      row.classList.toggle('has-error', Boolean(message));
      errorEl.textContent = message || '';
    };

    Object.keys(validators).forEach((field) => {
      const input = document.getElementById(field);
      if (!input) return;
      input.addEventListener('blur', () => {
        const result = validators[field](input.value);
        showFieldError(field, result === true ? '' : result);
      });
    });

    form.addEventListener('submit', (e) => {
      e.preventDefault();
      let isValid = true;

      Object.keys(validators).forEach((field) => {
        const input = document.getElementById(field);
        if (!input) return;
        const result = validators[field](input.value);
        if (result !== true) {
          showFieldError(field, result);
          isValid = false;
        } else {
          showFieldError(field, '');
        }
      });

      if (!isValid) {
        if (formSuccess) formSuccess.textContent = '';
        return;
      }

      const submitButton = form.querySelector('button[type="submit"]');
      if (submitButton) submitButton.disabled = true;
      if (formSuccess) formSuccess.textContent = 'Enviando mensaje...';

      fetch('/api/contacto', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          name: document.getElementById('name').value.trim(),
          email: document.getElementById('email').value.trim(),
          phone: document.getElementById('phone').value.trim(),
          message: document.getElementById('message').value.trim(),
        }),
      })
        .then(async (response) => {
          const result = await response.json().catch(() => ({}));
          if (!response.ok) throw new Error(result.message || 'No fue posible enviar el mensaje.');
          return result;
        })
        .then((result) => {
          if (formSuccess) formSuccess.textContent = result.message || '¡Mensaje enviado! Nos pondremos en contacto pronto.';
          form.reset();
        })
        .catch((error) => {
          if (formSuccess) formSuccess.textContent = error.message;
        })
        .finally(() => {
          if (submitButton) submitButton.disabled = false;
          setTimeout(() => { if (formSuccess) formSuccess.textContent = ''; }, 6000);
        });
    });
  }

  /* ---------------------------------------------------------
     10. Año actual en el footer
     --------------------------------------------------------- */
  const yearEl = document.getElementById('year');
  if (yearEl) yearEl.textContent = new Date().getFullYear();

  /* ---------------------------------------------------------
     11. Estimador interactivo de servicio (servicios.html)
     --------------------------------------------------------- */
  const selectEquipo = document.getElementById('tipoEquipo');
  const selectServicio = document.getElementById('tipoServicio');
  const textoPresupuesto = document.getElementById('textoPresupuesto');
  const textoDias = document.getElementById('textoDias');
  const btnCotizarWa = document.getElementById('btnCotizarWa');

  if (selectEquipo && selectServicio && textoPresupuesto && textoDias) {
    const tarifas = {
      retroexcavadora: { preventivo: [1200000, 1800000, 2], hidraulico: [1800000, 3200000, 3], frenos: [950000, 1600000, 2], motor: [2800000, 5500000, 4], overhaul: [8500000, 15000000, 12] },
      montacargas_combustion: { preventivo: [850000, 1400000, 1], hidraulico: [1400000, 2500000, 2], frenos: [750000, 1300000, 1], motor: [2200000, 4200000, 3], overhaul: [6500000, 11000000, 8] },
      montacargas_electrico: { preventivo: [750000, 1200000, 1], hidraulico: [1300000, 2400000, 2], frenos: [800000, 1400000, 1], motor: [1900000, 3800000, 3], overhaul: [5800000, 9500000, 7] },
      minicargador: { preventivo: [900000, 1500000, 1], hidraulico: [1600000, 2800000, 2], frenos: [700000, 1200000, 1], motor: [2400000, 4500000, 3], overhaul: [7000000, 12500000, 9] },
      excavadora: { preventivo: [1500000, 2300000, 2], hidraulico: [2500000, 4800000, 4], frenos: [1200000, 2100000, 2], motor: [3500000, 7200000, 5], overhaul: [12000000, 22000000, 15] },
      plataforma: { preventivo: [800000, 1300000, 1], hidraulico: [1500000, 2600000, 2], frenos: [750000, 1300000, 1], motor: [2100000, 4000000, 3], overhaul: [6200000, 10500000, 8] },
    };

    const actualizarEstimacion = () => {
      const eq = selectEquipo.value;
      const serv = selectServicio.value;
      const eqText = selectEquipo.options[selectEquipo.selectedIndex]?.text || '';
      const servText = selectServicio.options[selectServicio.selectedIndex]?.text || '';

      const datos = (tarifas[eq] && tarifas[eq][serv]) || [1000000, 2000000, 2];
      const minCOP = datos[0].toLocaleString('es-CO');
      const maxCOP = datos[1].toLocaleString('es-CO');
      const dias = datos[2];

      textoPresupuesto.textContent = `$${minCOP} - $${maxCOP} COP`;
      textoDias.innerHTML = `⏱️ Tiempo estimado de ejecución: <strong>${dias} a ${dias + 1} días hábiles</strong>`;

      if (btnCotizarWa) {
        const mensaje = encodeURIComponent(`Hola SOLUMECA, deseo cotizar el servicio de "${servText}" para mi equipo "${eqText}". Presupuesto estimado: $${minCOP} - $${maxCOP} COP.`);
        btnCotizarWa.href = `https://wa.me/573002464311?text=${mensaje}`;
      }
    };

    selectEquipo.addEventListener('change', actualizarEstimacion);
    selectServicio.addEventListener('change', actualizarEstimacion);
    actualizarEstimacion();
  }
});
