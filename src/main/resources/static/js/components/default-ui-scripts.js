/* Announcement Section - Start */
const announcements = [
    "🔥 Summer Sale: Up to 50% OFF!",
    "🚚 Free shipping on all orders ₱999 and up!",
    '<a href="#">📦 Track your order – Click here</a>'
];

function createAnnouncementCarousel(selector, messages) {
    const $carouselInner = $(selector).find('.carousel-inner');

    $carouselInner.empty();

    $.each(messages, function (index, msg) {
        const isActive = index === 0 ? 'active' : '';
        const $item = $('<div>', {class: 'carousel-item ' + isActive});
        const $container = $('<div>', {class: 'container py-3'});
        const $row = $('<div>', {class: 'row g-3'});
        const $col = $('<div>', {class: 'col-12 col-lg-12'});
        const $card = $('<div>', {class: 'announcement-card', id: 'carousel-slider-msg', html: msg});

        $col.append($card);
        $row.append($col);
        $container.append($row);
        $item.append($container);
        $carouselInner.append($item);
    });
}
/* Announcement Section - End */

/* Hero Section - Start */

/**
 * Dynamically generates a Bootstrap carousel’s items
 *
 * @param {string} carouselSelector - e.g. "#homepage-carousel"
 * @param {Array} images - array of { src, alt }
 * 
 */
function createCarousel(carouselSelector, images) {
    const $carousel = $(carouselSelector);
    const $carouselInner = $carousel.find('.carousel-inner');
    $carouselInner.empty();

    if (!images.length) {
        $('#homepage-hero-title').text('Create Your Custom Tee Now');
        $('#homepage-hero-message').text('Design your own shirt and bring your ideas to life. Fast, easy, and uniquely yours.');

        const fallbackItem = `
            <div class="carousel-item active">
                <div class="d-flex justify-content-center align-items-center" style="height: 300px;">
                    <div class="text-muted">No images available</div>
                </div>
            </div>
        `;
        $carouselInner.append(fallbackItem);
    } else {
        images.forEach((img, index) => {
            const isActive = index === 0 ? 'active' : '';
            const item = `
                <div class="carousel-item ${isActive}">
                    <img src="${img.src}" class="d-block hero-carousel-img" alt="Hero Image">
                </div>
            `;
            $carouselInner.append(item);
        });
    }
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

/* Hero Section - End */
$(document).ready(function () {
    // disableRightClick();

    createAnnouncementCarousel('.announcement-slider', announcements);

    createCarousel("#promo-carousel", images);

    sideNavigation();

    AOS.init({
      duration: 1500, // animation duration
      easing: "ease-in-out",
      once: true, // only animate once
    });
    
});

function disableRightClick(){
    $(document).on("contextmenu", function(e) {
        e.preventDefault();
        alert("Right-click is disabled on this page!");
    });
}

function updateDesktopLoginLink() {
    const $loginLink = $('a.nav-link[href="/telatak/customer/login"]');
    let token = f_getStoreValue("accessToken");

    // Normalize token: treat null, undefined, or "null" as missing
    if (token === "null" || token === null || token === undefined || token === "") {
        token = null;
    }

    if (token) {
        console.log("token exists");
        $loginLink.attr("href", "/telatak/customer/dashboard");
    } else {
        console.log("token deleted");
        $loginLink.attr("href", "/telatak/customer/login");
    }
}

function updateMobileLoginLink() {
    const $mobileLoginLink = $('a[href="/telatak/customer/login"]');
    const token = f_getStoreValue("accessToken");

    if (token && token !== "null" && token !== "") {
        $mobileLoginLink.attr("href", "/telatak/customer/dashboard");
    } else {
        $mobileLoginLink.attr("href", "/telatak/customer/login");
    }
}


