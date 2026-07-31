function f_getStoreValue(v_key){
    return localStorage.getItem(v_key);
}

function f_setStoreValue(v_key, v_value){
    localStorage.setItem(v_key, v_value);
}

function removeValue(v_key){
    localStorage.removeItem(v_key);
}

function redirectPage(path){
    // Redirect to log out or login page
    window.location.href = path;
}

function redirectToLogoutPage(){
    // Redirect to log out or login page
    window.location.href = '/sms/ui/login';
}

function syncCartToLocalStorage(updatedItems) {
  const stored = f_getStoreValue("orderDetails");
  if (!stored) return;

  stored.purchaseDetails = updatedItems;
  f_setStoreValue("orderDetails", dto.toJSON());
}

function generateUUID() {
    return "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g, function (c) {
        const r = (Math.random() * 16) | 0,
            v = c === "x" ? r : (r & 0x3) | 0x8;
        return v.toString(16);
    });
}

/**
 * Checks if a string is null, undefined, or empty (including whitespace)
 * @param {string} str - The string to check
 * @returns {boolean} - true if null/empty/whitespace, false otherwise
 */
function isNullOrEmpty(str) {
    return !str || str.trim() === "";
}

function isUserLoggedIn(){
    const token = f_getStoreValue("accessToken");
    return token && token !== "null" && token !== "";
}

function decodeJWT(token) {
    if (!token) return null;

    try {
        // JWT format: header.payload.signature
        const payloadBase64 = token.split('.')[1];
        if (!payloadBase64) return null;

        // Decode Base64 (URL-safe)
        const payloadJson = atob(payloadBase64.replace(/-/g, '+').replace(/_/g, '/'));
        return JSON.parse(payloadJson);
    } catch (err) {
        console.error("Invalid JWT:", err);
        return null;
    }
}