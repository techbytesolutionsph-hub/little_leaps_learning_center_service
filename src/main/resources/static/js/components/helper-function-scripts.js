function getDtoForUser() {
    return new Promise((resolve, reject) => {
        const token = f_getStoreValue("accessToken");
        const isLoggedIn = isUserLoggedIn();

        if (isLoggedIn) {
            const decoded = decodeJWT(token);
            const userId = decoded?.userId;
            if (!userId) {
                return console.error("userId is null or undefined!");
            }

            getCustomerAccount(userId, function(response) {
                const body = response.body || {};

                fetchShoppingCart(userId)
                    .then(cart => {
                        let dto;

                        if (cart.body && cart.body.trim()) {
                            const parsedBody = JSON.parse(cart.body);
                            dto = CustomerPayloadDto.fromJSON(parsedBody);
                            // dto = new CustomerPayloadDto(body.customerInfo, body.shippingAddressInfo, [parsedBody.orderItems]);
                            dto = new CustomerPayloadDto(body.customerInfo, body.shippingAddressInfo, dto.orderItems);
                        } else {
                            dto = new CustomerPayloadDto(body.customerInfo, body.shippingAddressInfo, []);
                        }

                        resolve(dto);
                    })
                    .catch(err => {
                        console.error(err);
                    });

            }, function(xhr, status, error) {
                console.error("Error fetching account:", error);
                reject(error);
            });
        } else {
            // Guest user
            let userId = f_getStoreValue("userId");
            if (!userId || userId.trim() === "") {
                userId = generateUUID();
                f_setStoreValue("userId", userId);
            }

            // ✅ Prepare user info
            const customerInfoData = new CustomerInfo({ id: userId, isActive: true });

            // ✅ Load existing DTO or create a new one
            fetchShoppingCart(userId)
                .then(cart => {
                    let dto;

                    if (cart.body && cart.body.trim()) {
                        const parsedBody = JSON.parse(cart.body);
                        dto = CustomerPayloadDto.fromJSON(parsedBody);
                    } else {
                        dto = new CustomerPayloadDto(customerInfoData, new ShippingAddressInfo(), []);
                    }

                    resolve(dto);
                })
                .catch(err => {
                    showError("Error", err.message);
                });
        }
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

// Subtract purchased items from cart
function subtractPurchasedItems(cartItems) {
    let userId = getUserIdByDecodedTokenOrLocalStorage();

    fetchCheckoutCart(userId)
        .then(cart => {
            const purchaseItems = CustomerPayloadDto.fromJSON(JSON.parse(cart.body));

            if (!purchaseItems.orderItems || !Array.isArray(cartItems)) return;

            // Keep only the items from cartItems that exist in purchaseItems.orderItems
            const matchedItems = cartItems.filter(pItem =>
                !purchaseItems.orderItems.some(cartItem =>
                    cartItem.id === pItem.id &&
                    cartItem.color === pItem.color &&
                    cartItem.size === pItem.size
                )
            );

            const updatedCart = {
                ...purchaseItems,
                orderItems: matchedItems
            };

            console.log("Filtered items:", updatedCart);

            addOrUpdateShoppingCart(userId, JSON.stringify(updatedCart))
        })
}