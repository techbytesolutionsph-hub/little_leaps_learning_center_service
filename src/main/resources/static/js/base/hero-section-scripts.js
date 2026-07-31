$(document).ready(function () {
    AOS.init({
        duration: 1200,
        easing: "ease-in-out",
        once: false
    });

    const $slides = $(".slides img");
    let index = 0;
    const totalSlides = $slides.length;

    $slides.addClass('diffuse');

    $slides.hide().eq(0).show().addClass('aos-animate');

    function showNextSlide() {
        $slides.eq(index).removeClass('aos-animate').fadeOut(600);
        index = (index + 1) % totalSlides;

        $slides.eq(index).fadeIn(600, function () {
            const $this = $(this);

            $this.removeClass('aos-animate');

            void $this[0].offsetWidth;

            $this.addClass('aos-animate');

            AOS.refresh();
        });
    }

    setInterval(showNextSlide, 3500);

});