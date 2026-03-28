(function () {
    const header = document.querySelector('.global-header');
    const hamburger = document.querySelector('.hamburger');

    if (!header || !hamburger) return;

    function openMenu() {
        header.classList.add('open');
        hamburger.setAttribute('aria-expanded', 'true');
    }

    function closeMenu() {
        header.classList.remove('open');
        hamburger.setAttribute('aria-expanded', 'false');
    }

    function toggleMenu() {
        const isOpen = header.classList.contains('open');
        if (isOpen) {
            closeMenu();
        } else {
            openMenu();
        }
    }

    hamburger.addEventListener('click', function (e) {
        e.stopPropagation();
        toggleMenu();
    });

    document.addEventListener('click', function (e) {
        if (!header.contains(e.target)) {
            closeMenu();
        }
    });

    header.addEventListener('click', function (e) {
        e.stopPropagation();
    });
})();