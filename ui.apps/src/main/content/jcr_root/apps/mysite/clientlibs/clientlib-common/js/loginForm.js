(function () {
    const overlay = document.querySelector('.outer-container');
    const loginBox = document.querySelector('.login-box');
    const loginTrigger = document.querySelector('.login-btn a, .login-btn button');

    if (!overlay || !loginBox || !loginTrigger) return;

    function toggleOverlay() {
        overlay.classList.toggle('show');
    }

    loginTrigger.addEventListener('click', function (e) {
        e.preventDefault();
        toggleOverlay();
    });

    overlay.addEventListener('click', function () {
        overlay.classList.remove('show');
    });

    loginBox.addEventListener('click', function (e) {
        e.stopPropagation();
    });
})();