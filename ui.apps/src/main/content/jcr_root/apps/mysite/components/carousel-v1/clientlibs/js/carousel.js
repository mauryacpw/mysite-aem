document.addEventListener("DOMContentLoaded", function() {
    const carousels = document.querySelectorAll('.cmp-custom-carousel');

    carousels.forEach(carousel => {
        const inner = carousel.querySelector('.cmp-custom-carousel__inner');
        const items = carousel.querySelectorAll('.cmp-custom-carousel__item');
        const indicators = carousel.querySelectorAll('.cmp-custom-carousel__indicator');
        const prevBtn = carousel.querySelector('.cmp-custom-carousel__control--prev');
        const nextBtn = carousel.querySelector('.cmp-custom-carousel__control--next');

        if (!inner || items.length === 0) return;

        let currentIndex = 0;
        const totalItems = items.length;

        function updateCarousel(index) {
            // Handle bounds
            if (index < 0) {
                currentIndex = totalItems - 1;
            } else if (index >= totalItems) {
                currentIndex = 0;
            } else {
                currentIndex = index;
            }

            // Slide
            const offset = -currentIndex * 100;
            inner.style.transform = `translateX(${offset}%)`;

            // Update Indicators
            indicators.forEach((ind, i) => {
                if (i === currentIndex) {
                    ind.classList.add('cmp-custom-carousel__indicator--active');
                } else {
                    ind.classList.remove('cmp-custom-carousel__indicator--active');
                }
            });
        }

        if (prevBtn) {
            prevBtn.addEventListener('click', function() {
                updateCarousel(currentIndex - 1);
            });
        }

        if (nextBtn) {
            nextBtn.addEventListener('click', function() {
                updateCarousel(currentIndex + 1);
            });
        }

        indicators.forEach((ind, i) => {
            ind.addEventListener('click', function() {
                updateCarousel(i);
            });
        });

        // Initialize state
        updateCarousel(0);
    });
});
