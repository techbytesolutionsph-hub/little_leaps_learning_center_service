/* Announcement Section - Start */
const announcements = [
    "Summer Sale: Up to 50% OFF!",
    "Free shipping on all orders!",
    '<a href="#">Track your order – Click here</a>'
];

function buildInfiniteTicker(selector, messages, speed = 0.3) {
    const $bar = $(selector);
    const $track = $bar.find('.announcement-track');
    $track.empty();

    // Duplicate messages for seamless infinite loop
    const items = [...messages, ...messages];
    items.forEach(msg => {
        $track.append(
            $('<span>', {
                class: 'announcement-item',
                html: msg
            })
        );
    });

    let offset = 0;
    const trackWidth = $track.width() / 4;
    let animationId;
    let isPaused = false;

    function animateTicker() {
        if (!isPaused) {
            offset -= speed;
            if (-offset >= trackWidth) offset = 0; // reset for infinite loop
            $track.css('transform', `translateX(${offset}px)`);
        }
        animationId = requestAnimationFrame(animateTicker);
    }

    // Start animation automatically
    animateTicker();

    // Pause/resume on hover
    $bar.on('mouseenter', () => { isPaused = true; });
    $bar.on('mouseleave', () => { isPaused = false; });
}

function sideNavigation() {
    const $burger = $("#burgerToggle");
    const $sidenav = $("#mobile-sidenav");
    const $body = $("body");

    // ✅ Always reset state on load
    $burger.removeClass("open");
    $sidenav.removeClass("show");
    $body.removeClass("sidenav-open");

    // Toggle mobile menu
    $burger.on("click", function () {
        $(this).toggleClass("open");
        $sidenav.toggleClass("show");

        // Toggle body scroll lock
        if ($sidenav.hasClass("show")) {
            $body.addClass("sidenav-open");
        } else {
            $body.removeClass("sidenav-open");
        }
    });

    // ✅ Close sidenav on desktop resize
    $(window).on("resize", function () {
        if ($(window).width() >= 768) {
            $sidenav.removeClass("show");
            $burger.removeClass("open");
            $body.removeClass("sidenav-open");
        }
    });

    // ✅ Reset on pageshow (bfcache support)
    $(window).on("pageshow", function (e) {
        if (e.originalEvent.persisted) {
            $burger.removeClass("open");
            $sidenav.removeClass("show");
            $body.removeClass("sidenav-open");
        }
    });
}

$(document).ready(function () {
    buildInfiniteTicker('.announcement-bar', announcements, 0.7);

    sideNavigation();
});