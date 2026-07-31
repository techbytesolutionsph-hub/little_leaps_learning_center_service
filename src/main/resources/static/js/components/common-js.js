(function() {
	'use strict';

	var tinyslider = function() {
		var el = document.querySelectorAll('.testimonial-slider');

		if (el.length > 0) {
			var slider = tns({
				container: '.testimonial-slider',
				items: 1,
				axis: "horizontal",
				controlsContainer: "#testimonial-nav",
				swipeAngle: false,
				speed: 700,
				nav: true,
				controls: true,
				autoplay: true,
				autoplayHoverPause: true,
				autoplayTimeout: 3500,
				autoplayButtonOutput: false
			});
		}
	};
	tinyslider();

	


	var sitePlusMinus = function() {

		var value,
    		quantity = document.getElementsByClassName('quantity-container');

		function createBindings(quantityContainer) {
	      var quantityAmount = quantityContainer.getElementsByClassName('quantity-amount')[0];
	      var increase = quantityContainer.getElementsByClassName('increase')[0];
	      var decrease = quantityContainer.getElementsByClassName('decrease')[0];
	      increase.addEventListener('click', function (e) { increaseValue(e, quantityAmount); });
	      decrease.addEventListener('click', function (e) { decreaseValue(e, quantityAmount); });
	    }

	    function init() {
	        for (var i = 0; i < quantity.length; i++ ) {
						createBindings(quantity[i]);
	        }
	    };

	    function increaseValue(event, quantityAmount) {
	        value = parseInt(quantityAmount.value, 10);

	        console.log(quantityAmount, quantityAmount.value);

	        value = isNaN(value) ? 0 : value;
	        value++;
	        quantityAmount.value = value;
	    }

	    function decreaseValue(event, quantityAmount) {
	        value = parseInt(quantityAmount.value, 10);

	        value = isNaN(value) ? 0 : value;
	        if (value > 0) value--;

	        quantityAmount.value = value;
	    }
	    
	    init();
		
	};
	sitePlusMinus();


})()

  /**
   * Dynamically generates a Bootstrap carousel’s items
   *
   * @param {string} carouselSelector - e.g. "#homepage-carousel"
   * @param {Array} images - array of { src, alt }
   */
  function createCarousel(carouselSelector, images) {
    const $carousel = $(carouselSelector);
    const $carouselInner = $carousel.find('.carousel-inner');
    
    $carouselInner.empty();

    images.forEach((img, index) => {
      const isActive = index === 0 ? 'active' : '';
      const item = `
        <div class="carousel-item ${isActive}">
          <img src="${img.src}" class="d-block w-100" alt="hero-img">
        </div>
      `;
      $carouselInner.append(item);
    });
  }

$(document).ready(function () {
      const $burger = $('#burgerToggle');
      const $sidenav = $('#mobile-sidenav');

      // ✅ Always reset state on load (even after back/forward navigation)
      $burger.removeClass('open');
      $sidenav.removeClass('show');

      // Toggle mobile menu
      $burger.on('click', function () {
         $(this).toggleClass('open');
         $sidenav.toggleClass('show');
      });

      // Fix: Close sidenav on desktop resize
      $(window).on('resize', function () {
         if ($(window).width() >= 768) {
            $sidenav.removeClass('show');
            $burger.removeClass('open');
         }
      });

      // Extra: reset on pageshow (for bfcache in Safari, Firefox)
      $(window).on('pageshow', function (e) {
         if (e.originalEvent.persisted) {
            $burger.removeClass('open');
            $sidenav.removeClass('show');
         }
      });
   });