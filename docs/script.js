/**
 * I.S.D.I Interactive Logic
 * Espacial / Multidispositivo
 */

document.addEventListener('DOMContentLoaded', () => {
    
    // 1. Navbar Scroll Effect
    const navbar = document.querySelector('.navbar');
    window.addEventListener('scroll', () => {
        if (window.scrollY > 50) {
            navbar.classList.add('scrolled');
        } else {
            navbar.classList.remove('scrolled');
        }
    });

    // 2. Parallax Effect on Hero & Mouse Glow
    const parallaxElements = document.querySelectorAll('.parallax-element');
    const mouseGlow = document.querySelector('.mouse-glow');
    
    let targetX = window.innerWidth / 2;
    let targetY = window.innerHeight / 2;
    let glowX = targetX;
    let glowY = targetY;

    window.addEventListener('mousemove', (e) => {
        targetX = e.clientX;
        targetY = e.clientY;
        
        if (mouseGlow && (mouseGlow.style.opacity === '0' || mouseGlow.style.opacity === '')) {
            mouseGlow.style.opacity = '1';
        }

        const px = (window.innerWidth - e.pageX * 2) / 100;
        const py = (window.innerHeight - e.pageY * 2) / 100;

        parallaxElements.forEach(el => {
            const speed = el.getAttribute('data-speed');
            el.style.transform = `translateX(${px * speed}px) translateY(${py * speed}px)`;
        });
    });

    // Smooth animation for mouse glow
    function animateGlow() {
        if (mouseGlow) {
            glowX += (targetX - glowX) * 0.08;
            glowY += (targetY - glowY) * 0.08;
            mouseGlow.style.transform = `translate(${glowX}px, ${glowY}px)`;
        }
        requestAnimationFrame(animateGlow);
    }
    animateGlow();

    // 3. Init VanillaTilt for 3D Cards (Only on Desktop)
    if (window.innerWidth > 900) {
        VanillaTilt.init(document.querySelectorAll(".tilt-card"), {
            max: 5,               // max tilt rotation (deg)
            speed: 400,           // Speed of the enter/exit transition
            glare: true,          // Add a glare effect
            "max-glare": 0.1,     // maximum "glare" opacity
            perspective: 1000,    // Transform perspective, the lower the more extreme the tilt gets
            scale: 1.02           // 2% scale on hover
        });
    }

    // 4. Staggered Entrance Animation (Observer)
    const observerOptions = {
        threshold: 0.1,
        rootMargin: "0px 0px -50px 0px"
    };

    const entranceObserver = new IntersectionObserver((entries) => {
        entries.forEach((entry, index) => {
            if (entry.isIntersecting) {
                setTimeout(() => {
                    entry.target.style.opacity = '1';
                    entry.target.style.transform = 'translateY(0) translateZ(0)'; // Reset translations but keep Z context
                }, 100 * index);
                entranceObserver.unobserve(entry.target);
            }
        });
    }, observerOptions);

    // Initial state for bento cards
    const cards = document.querySelectorAll('.bento-card');
    cards.forEach((card) => {
        card.style.opacity = '0';
        // Add translating Y for entrance effect without breaking 3D context
        card.style.transform = 'translateY(40px)';
        card.style.transition = 'opacity 0.8s cubic-bezier(0.16, 1, 0.3, 1), transform 0.8s cubic-bezier(0.16, 1, 0.3, 1)';
        entranceObserver.observe(card);
    });

    // Hero entrance
    const heroElements = document.querySelectorAll('.hero > *');
    heroElements.forEach((el, idx) => {
        if(!el.classList.contains('main-sun') && !el.classList.contains('scroll-indicator')) {
            el.style.opacity = '0';
            el.style.transform = 'translateY(20px)';
            el.style.transition = 'opacity 0.8s cubic-bezier(0.16, 1, 0.3, 1), transform 0.8s cubic-bezier(0.16, 1, 0.3, 1)';
            
            setTimeout(() => {
                el.style.opacity = '1';
                el.style.transform = 'translateY(0)';
            }, 100 * idx + 200);
        }
    });

    // 5. Mobile Menu Toggle (Basic implementation)
    const mobileBtn = document.querySelector('.mobile-menu-btn');
    const navLinks = document.querySelector('.nav-links');
    
    if(mobileBtn) {
        mobileBtn.addEventListener('click', () => {
            if(navLinks.style.display === 'flex') {
                navLinks.style.display = 'none';
            } else {
                navLinks.style.display = 'flex';
                navLinks.style.flexDirection = 'column';
                navLinks.style.position = 'absolute';
                navLinks.style.top = '100%';
                navLinks.style.left = '0';
                navLinks.style.width = '100%';
                navLinks.style.background = 'rgba(1, 1, 4, 0.95)';
                navLinks.style.padding = '2rem';
                navLinks.style.backdropFilter = 'blur(20px)';
                navLinks.style.borderBottom = '1px solid var(--glass-border)';
            }
        });
    }

});
