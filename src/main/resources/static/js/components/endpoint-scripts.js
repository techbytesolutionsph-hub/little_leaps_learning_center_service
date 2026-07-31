/**
 * Fetch customer account by user ID.
 *
 * @function getCustomerAccount
 * @param {string} userId - Unique identifier of the user
 * @param {Function} [onSuccess] - Callback executed on successful request
 * @param {Object} onSuccess.response - Server response object
 * @param {Function} [onError] - Callback executed on request failure
 * @param {jqXHR} onError.xhr - jQuery XHR object
 * @param {string} onError.status - Error status
 * @param {string} onError.error - Error message
 *
 * @example
 * getCustomerAccount("123", res => console.log(res), err => console.error(err));
 */
function getCustomerAccount(userId, onSuccess, onError) {
    $.ajax({
        url: `/api/v1/customer/account/get-account/${encodeURIComponent(userId)}`,
        method: "POST",
        contentType: "application/json",
        success: function(response) {
            if (onSuccess) onSuccess(response);
        },
        error: function(xhr, status, error) {
            if (onError) onError(xhr, status, error);
        }
    });
}

/**
 * Fetch a product by its ID.
 *
 * @function fetchProductById
 * @param {string|number} productId - Product unique identifier
 * @param {Function} callback - Callback function with fetched product data
 * @param {Object|null} callback.response - Product response or null on failure
 *
 * @example
 * fetchProductById(10, product => console.log(product));
 */
function fetchProductById(productId, callback) {
    if (!productId) {
        console.error("Product ID is required");
        return;
    }

    $.ajax({
        url: `/api/v1/products/getProduct/${productId}`,
        method: "GET",
        dataType: "json",
        success: function (response) {
            if (response && response.body) {
                callback(response);
            } else {
                console.error("Invalid product response:", response);
                callback(null);
            }
        },
        error: function (xhr, status, error) {
            console.error("Failed to fetch product:", error);
            callback(null);
        },
    });
}

/**
 * Add or update shopping cart items.
 *
 * @async
 * @function addOrUpdateShoppingCart
 * @param {string} userId - User identifier
 * @param {Array<Object>} cartItems - List of cart items
 * @returns {Promise<Object>} Server response JSON
 *
 * @throws {Error} When the request fails
 *
 * @example
 * await addOrUpdateShoppingCart("123", [{ productId: 1, qty: 2 }]);
 */
async function addOrUpdateShoppingCart(userId, cartItems) {
    console.log(cartItems);
    const requestPayload = {
        userId,
        cartItemsJson: JSON.stringify(cartItems)
    };

    const response = await fetch('/api/v1/cart/add-update-shopping-cart', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(requestPayload)
    });

    if (!response.ok) {
        const text = await response.text();
        throw new Error(text);
    }

    return response.json();
}

/**
 * Checkout shopping cart for a user.
 *
 * @function checkoutCart
 * @param {string} userId - User identifier
 * @param {Array<Object>} cartItems - Cart items to checkout
 *
 * @example
 * checkoutCart("123", cartItems);
 */
function checkoutCart(userId, cartItems){
    const requestPayload = {
        userId: userId,
        cartItemsJson: JSON.stringify(cartItems)
    };

    fetch('/api/v1/cart/checkout-cart', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(requestPayload)
    })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => { throw new Error(text) });
            }
            return response.json();
        })
        .then(data => {
            redirectPage("/telatak/shop/checkout-page");
        })
        .catch(error => {
            showError("Error", error.message);
        });
}

/**
 * Fetch shopping cart data.
 *
 * @function fetchShoppingCart
 * @param {string} userId - User identifier
 * @returns {Promise<Object>} Shopping cart JSON data
 *
 * @example
 * fetchShoppingCart("123").then(cart => console.log(cart));
 */
function fetchShoppingCart(userId) {
    return fetch(`/api/v1/cart/get-shopping-cart/${encodeURIComponent(userId)}`, {
        method: "GET",
        headers: {
            "Accept": "application/json"
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}`);
            }
            return response.json();
        });
}

/**
 * Fetch checkout cart data.
 *
 * @function fetchCheckoutCart
 * @param {string} userId - User identifier
 * @returns {Promise<Object>} Checkout cart JSON data
 */
function fetchCheckoutCart(userId) {
    return fetch(`/api/v1/cart/get-checkout-cart/${encodeURIComponent(userId)}`, {
        method: "GET",
        headers: {
            "Accept": "application/json"
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}`);
            }
            return response.json();
        });
}

/**
 * Fetch customer billing address and update UI.
 *
 * @function fetchCustomerBillingAddress
 * @param {string} userId - User identifier
 *
 * @example
 * fetchCustomerBillingAddress("123");
*/
function fetchCustomerBillingAddress(userId) {
    // Optional: show loading state
    $("#customer-fullname").text("Loading...");
    $("#customer-address").text("Loading...");
    $("#customer-phone").text("Loading...");
    $("#customer-email").text("Loading...");

    fetch(`/api/v1/customer/account/get-billing-address/${userId}`, {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        }
    })
        .then(response => {
            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
            return response.json();
        })
        .then(data => {
            if (data.code === 200 && data.body) {
                const customer = data.body;
                $("#customer-fullname").text(customer.fullName);
                $("#customer-address").text(customer.address);
                $("#customer-phone").text(customer.phone);
                $("#customer-email").text(customer.email);
            } else {
                console.error("Failed to fetch billing address:", data.message);
                $("#customer-fullname").text("Unable to load customer details");
                $("#customer-address").text("");
                $("#customer-phone").text("");
                $("#customer-email").text("");
            }
        })
        .catch(error => {
            console.error("Fetch error:", error);
            $("#customer-fullname").text("Unable to load customer details");
            $("#customer-address").text("");
            $("#customer-phone").text("");
            $("#customer-email").text("");
        });
}

/* Cloudinary upload function */
async function uploadToCloudinary(file) {
    const formData = new FormData();
    formData.append("file", file);
    formData.append("upload_preset", "telatak");

    try {
        const res = await fetch("https://api.cloudinary.com/v1_1/dx8es7rbm/image/upload", {
            method: "POST",
            body: formData
        });
        const data = await res.json();
        return data.secure_url;
    } catch (err) {
        console.error("Cloudinary upload error:", err);
        alert("Upload failed. Try again.");
        return null;
    }
}