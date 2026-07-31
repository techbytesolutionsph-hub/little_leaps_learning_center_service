$(document).ready(function () {
    loadCartCount();
});

function loadCartCount(){
    const customerId = getCustomer();

    getShoppingCart(customerId)
        .then((cartJson) => {
            const parsedBody = JSON.parse(cartJson);
            const cartData = parsedBody.addToCartItems;

            let cartCount = 0;

            cartData.forEach((product) => {
                product.variants?.forEach((variant) => {
                    variant.options?.forEach((option) => {
                        cartCount += option.quantity;

                        const waitForBadge = setInterval(() => {
                            const badge = $('.cart-badge');

                            if (badge.length) {
                                if (cartCount > 0) {
                                    badge
                                        .text(cartCount)
                                        .css({
                                            display: 'flex',
                                            visibility: 'visible',
                                            opacity: 1
                                        });
                                } else {
                                    badge.hide();
                                }
                                clearInterval(waitForBadge);
                            }
                        }, 50);
                    });
                });
            });
        })
        .catch(err => {
            console.error('Cart fetch error:', err);
        });
}

function getUserIdByDecodedTokenOrLocalStorage(){
    const token = f_getStoreValue("accessToken");
    const isLoggedIn = isUserLoggedIn();

    if(isLoggedIn){
        const decoded = decodeJWT(token);
        return decoded?.userId;
    }else{
        return f_getStoreValue("userId");
    }
}