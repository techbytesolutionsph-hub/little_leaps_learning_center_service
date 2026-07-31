const $container = $("#cartItems");
const $subtotal = $("#cart-subtotal");
const $cartFooter = $(".cart-footer");
let cartData = null;
let parsedBody = null;
let itemCount = 0;

function loadSideCart() {
    const customerId = getCustomer();

    getShoppingCart(customerId)
        .then((cartJson) => {
            if (!cartJson) {
                showEmptyCart();
                return;
            }

            parsedBody = JSON.parse(cartJson);
            cartData = parsedBody.addToCartItems;

            renderCart();
        })
        .catch((err) => {
            console.error(err);
            showEmptyCart();
        });
}

function renderCart() {
    $container.empty();

    let subtotal = 0;
    let itemCount = 0;

    if (!cartData || !cartData.length) {
        showEmptyCart();
        return;
    }

    $cartFooter.show();

    /* Cart Details */
    cartData.forEach((product) => {
        product.variants?.forEach((variant) => {
            variant.options?.forEach((option) => {

                itemCount += option.quantity;
                subtotal += option.finalPrice * option.quantity;

                /* Cart Title */
                $(".cart-header-title").text(`Your cart (${itemCount})`);

                const minusButton = option.quantity === 1
                        ? `<button class="qty-btn minus trash-btn"><i class="fas fa-trash"></i></button>`
                        : `<button class="qty-btn minus">−</button>`;

                const html = `
                    <div class="cart-item"
                         data-product-id="${product.productId}"
                         data-variant-id="${variant.variantId}"
                         data-option-id="${option.variantOptionId}">

                        <img src="${variant.image}" class="product-image" alt="${product.title}">

                        <div class="item-details">
                            <h5 class="sidecart-product-title">
                                ${product.title}
                            </h5>

                            <div class="product-options">
                                <span>${variant.color} / ${option.size.toUpperCase()}</span>
                                <span class="option-price">
                                    ${option.finalPrice < option.price ? 
                                        `
                                            <span class="price">
                                                ₱${option.finalPrice.toFixed(2)}
                                            </span>
                                            <span class="base-price">
                                                ₱${option.price.toFixed(2)}
                                            </span>
                                        `
                                            : 
                                        `
                                            <span class="price">
                                                ₱${option.price.toFixed(2)}
                                            </span>
                                        `
                                    }
                                </span>
                            </div>

                            <div class="item-actions">
                                <div class="qty">
                                    ${minusButton}
                                    <input type="number" min="1" value="${option.quantity}" class="qty-input">
                                    <button class="qty-btn plus">+</button>
                                </div>

                                ${option.discountType === 'PERCENT' && option.discountPercent > 0 ? 
                                    `
                                        <span class="discount-badge percent">
                                            ${Math.round(option.discountPercent)}% OFF
                                        </span>
                                    `
                                        : option.discountType === 'FIXED' && option.discountAmount > 0 ? 
                                    `
                                        <span class="discount-badge fixed">
                                            ₱${option.discountAmount.toFixed(0)} OFF
                                        </span>
                                    `
                                        : ''
                                }
                            </div>
                        </div>
                    </div>
                `;

                $container.append(html);
            });
        });
    });

    loadCartCount(itemCount);

    $("#reward-bar").remove();

    /* Reward Bar */
    if (itemCount > 0) {
        const rewardStepperHtml = `
            <div id="reward-bar" class="wizz-rewardbar-container">
                <div class="reward-inner-container">
                    <p class="reward-message">
                        Your cart total of <span class="reward-subtotal">₱${subtotal.toFixed(2)}</span> qualifies for <strong>FREE Shipping</strong>!
                    </p>

                    <div class="reward-stepper">
                        <div class="reward-line"></div>

                        <div class="reward-circle-wrapper">
                            <div class="reward-circle">
                                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="#fff" class="reward-car-icon"> <path fill-rule="evenodd" clip-rule="evenodd" d="M4 5.25C4 4.83579 4.33579 4.5 4.75 4.5H11.7414C12.9692 4.5 14.0483 5.31394 14.3856 6.49452L14.8125 7.98862C14.837 8.07452 14.9055 8.1408 14.9922 8.16247L16.6744 8.58303C17.4535 8.77779 18 9.47776 18 10.2808V11.5C18 12.2108 17.5763 12.8226 16.9676 13.0966C16.9889 13.2279 17 13.3627 17 13.5C17 14.8807 15.8807 16 14.5 16C13.1193 16 12 14.8807 12 13.5C12 13.4156 12.0042 13.3322 12.0123 13.25H8.98766C8.99582 13.3322 9 13.4156 9 13.5C9 14.8807 7.88071 16 6.5 16C5.11929 16 4 14.8807 4 13.5C4 13.1444 4.07422 12.8062 4.20802 12.5H3.75C3.33579 12.5 3 12.1642 3 11.75C3 11.3358 3.33579 11 3.75 11H6.25C6.27988 11 6.30935 11.0017 6.33831 11.0051C6.39177 11.0017 6.44568 11 6.5 11C7.19935 11 7.83163 11.2872 8.28536 11.75H12.7146C13.1684 11.2872 13.8007 11 14.5 11C15.1982 11 15.8296 11.2863 16.2832 11.7478C16.4056 11.7316 16.5 11.6268 16.5 11.5V10.2808C16.5 10.1661 16.4219 10.0661 16.3106 10.0382L14.6284 9.61769C14.0217 9.466 13.542 9.00205 13.3702 8.4007L12.9433 6.9066C12.79 6.36997 12.2995 6 11.7414 6H4.75C4.33579 6 4 5.66421 4 5.25ZM6.5 14.5C7.05228 14.5 7.5 14.0523 7.5 13.5C7.5 12.9477 7.05228 12.5 6.5 12.5C5.94772 12.5 5.5 12.9477 5.5 13.5C5.5 14.0523 5.94772 14.5 6.5 14.5ZM14.5 14.5C15.0523 14.5 15.5 14.0523 15.5 13.5C15.5 12.9477 15.0523 12.5 14.5 12.5C13.9477 12.5 13.5 12.9477 13.5 13.5C13.5 14.0523 13.9477 14.5 14.5 14.5Z"></path> </svg> 
                            </div>

                            <span class="reward-label">
                                <span>Free</span>
                                <span>Shipping</span>
                            </span>
                        </div>
                    </div>
                </div>
            </div>
        `;

        $(".cart-header").after(rewardStepperHtml);
    }

    /* Cart Footer */
    $cartFooter
        .html(`
            <button class="btn btn-secondary d-flex justify-content-center align-items-center mt-2 mb-2" id="checkout-btn">
                <i class="far fa-credit-card mr-1" style="font-size:15px"></i> &nbsp;Checkout 
                <strong id="cart-subtotal">
                    &nbsp;-&nbsp;₱${subtotal.toLocaleString(undefined, {
                        minimumFractionDigits: 2,
                        maximumFractionDigits: 2
                    })}
                </strong>
            </button>
        
            <a href="/telatak/shop/home" class="btn btn-secondary d-flex justify-content-center align-items-center mt-2 mb-2" id="continue-shopping-btn">Continue Shopping</a>
        `).show();
}

function loadCartCount(cartCount) {
    const waitForBadge = setInterval(() => {
        const badge = $('.cart-badge');

        if (badge.length) {
            if (cartCount > 0) {
                badge
                    .text(cartCount)
                    .css({
                        display: 'flex',
                        visibility: 'visible',
                        opacity: 1,
                        bottom: 4
                    })
                    .hover(
                        function () {
                            $(this).css({
                                color: '#7a1113',
                                background: '#fff'
                            });
                        },
                        function () {
                            $(this).css({
                                color: '',
                                background: ''
                            });
                        }
                    );
            } else {
                badge.hide();
            }
            clearInterval(waitForBadge);
        }
    }, 50);
}

function showEmptyCart() {
    $container.empty();
    $container.append(`
        <div class="cart-empty text-center" style="padding: 40px 20px;">
            <h4 style="font-weight: bold; color: rgb(48, 48, 48);">Your cart is empty</h4>
            <p style="font-size: 12px; color: rgb(48, 48, 48);">Start shopping to fill your cart with great finds.</p>
            <a href="/telatak/shop/home" class="btn btn-secondary d-flex justify-content-center align-items-center mt-2 mb-2" id="homepage-btn">Go to Homepage</a>
        </div>
    `);
    $subtotal.text(`₱0`);
    $cartFooter.hide();

    $("#reward-bar").remove();

    /* Cart Footer */
    $cartFooter
        .html(
            `
                <div style="width: 100%; display: flex; justify-content: center; align-items: center; padding: 10px 0;">
                    <a href="/telatak/shop/home" style="color: #303030; font-weight: bold; font-size: 12px; text-decoration: none;">
                        Continue Shopping
                    </a>
                </div>
            `,
        )
        .show();
}

$(document).ready(function () {
    const $sidebar = $('.mobile-sidenav');
    const $cart = $('.shopping-cart-panel');
    const $mobileCartButton = $('#mobileCartButton');
    const $desktopCartButton = $('#desktopCartButton');
    const $burgerButton = $('#burgerToggle');

    $mobileCartButton.on('click', function() {

        if ($sidebar.hasClass('show')) {
            $sidebar.removeClass('show');
            $('body').removeClass('sidenav-open');
        }

        $cart.toggleClass('show');
        $burgerButton.removeClass('open');
    });

    $desktopCartButton.on('click', function() {
        if ($sidebar.hasClass('show')) {
            $sidebar.removeClass('show');
            $('body').removeClass('sidenav-open');
        }

        $cart.toggleClass('show');
        $burgerButton.removeClass('open');
    });

    window.toggleCart = function () {
        const $cart = $("#cartSidebar");
        const $overlay = $("#cartOverlay");
        const isOpening = !$cart.hasClass("active");

        $cart.toggleClass("active");
        $overlay.toggleClass("active");
        $("body").toggleClass("cart-open", isOpening);
    };

    $("#cartOverlay").on("click", toggleCart);
    loadSideCart();

    $(document).on("click", ".qty-btn.minus", function () {
        const $item = $(this).closest(".cart-item");
        const productId = Number($item.data("product-id"));
        const variantId = Number($item.data("variant-id"));
        const optionId = Number($item.data("option-id"));

        const product = cartData.find(p => p.productId === productId);
        if (!product) return;

        const variant = product.variants?.find(v => v.variantId === variantId);
        if (!variant) return;

        const option = variant.options?.find(o => o.variantOptionId === optionId);
        if (!option) return;

        /* CASE 1: Decrease quantity */
        if (option.quantity > 1) {
            option.quantity -= 1;
        }
        /* CASE 2: Delete item when 1 */
        else {
            variant.options = variant.options.filter(
                o => o.variantOptionId !== optionId
            );

            product.variants = product.variants.filter(
                v => v.options.length > 0
            );

            cartData = cartData.filter(
                p => p.variants.length > 0
            );
        }

        saveCart();
        renderCart();
    });

    $(document).on("click", ".qty-btn.plus", function () {
        const $item = $(this).closest(".cart-item");
        const productId = Number($item.data("product-id"));
        const variantId = Number($item.data("variant-id"));
        const optionId = Number($item.data("option-id"));

        const product = cartData.find(p => p.productId === productId);
        if (!product) return;

        const variant = product.variants?.find(v => v.variantId === variantId);
        if (!variant) return;

        const option = variant.options?.find(o => o.variantOptionId === optionId);
        if (!option) return;

        /* Increase quantity */
        option.quantity += 1;

        saveCart();

        renderCart();
    });

    $(document).on("change", ".qty-input", function () {
        const $item = $(this).closest(".cart-item");
        const productId = Number($item.data("product-id"));
        const variantId = Number($item.data("variant-id"));
        const optionId = Number($item.data("option-id"));

        let qty = parseInt($(this).val()) || 1;

        qty = Math.max(1, qty);

        const product = cartData.find(
            p => p.productId === productId
        );

        if (!product) return;

        const variant = product.variants?.find(
            v => v.variantId === variantId
        );

        if (!variant) return;

        const option = variant.options?.find(
            o => o.variantOptionId === optionId
        );

        if (!option) return;

        option.quantity = qty;

        saveCart();
        renderCart();
    });

    function saveCart() {
        let request = buildShoppingCartRequest();
        addOrUpdateShoppingCart(request);
    }

    $(document).on("click", "#checkout-btn", function (e) {
        e.preventDefault();

        let request = buildShoppingCartRequest();

        checkoutSelectedItems(request)
            .then((response) => {
                console.log("Checkout success:", response);
                const sessionId = response.responseBody.sessionId;

                setTimeout(() => {
                    window.location.href = `/telatak/shop/checkout/${sessionId}`;
                }, 1000);
            })
            .catch((error) => {
                console.error("Checkout failed:", error);
            });
    });

    function buildShoppingCartRequest() {

        const email = $("#current-user-email").text().trim();
        const isAnonymous = !email || email === "anonymousUser";

        const customerId = !isAnonymous ? email : undefined;
        const guestToken = isAnonymous ? f_getStoreValue("guestToken") : undefined;

        return {
            customerId: customerId,
            guestToken: guestToken,
            cartItemsJson: JSON.stringify(
                {
                    customerInfo: parsedBody.customerInfo || null,
                    addressInfo: parsedBody.addressInfo || null,
                    addToCartItems: cartData
                }
            )
        };
    }
});