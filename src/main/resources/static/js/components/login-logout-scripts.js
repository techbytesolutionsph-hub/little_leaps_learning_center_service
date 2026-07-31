$(document).ready(function () {

    function closeSidebarIfNeeded() {
        const $sidenav = $("#mobile-sidenav");
        const $burger = $("#burgerToggle");
        const $body = $("body");

        const isOpen = $sidenav.hasClass("show");

        if (isOpen) {
            $sidenav.removeClass("show");
            $burger.removeClass("open");
            $body.removeClass("sidenav-open");
        }

        return isOpen;
    }

    function handleSignOut(ajaxFn) {

        const delayedLogout = () => ajaxFn();

        if (closeSidebarIfNeeded()) {
            setTimeout(delayedLogout, 300);
        } else {
            delayedLogout();
        }
    }

    $(document).on("click", "#seller-sign-out-page", function (e) {
        e.preventDefault();
        handleSignOut(sellerLogoutAndRedirect);
    });

    $(document).on("click", "#customer-sign-out-page", function (e) {
        e.preventDefault();
        handleSignOut(customerLogoutAndRedirect);
    });

});

/* =========================
   Common Sweet Loader
========================= */
function showLogoutLoader() {
    Swal.fire({
        title: 'Signing out',
        html: '<span style="font-size:14px; color:#555;">Please wait a moment</span>',
        background: '#ffffff',
        color: '#333',
        allowOutsideClick: false,
        allowEscapeKey: false,
        showConfirmButton: false,
        customClass: { popup: 'custom-logout-popup' },
        didOpen: () => Swal.showLoading()
    });
}

/* =========================
   Seller Logout
========================= */
function sellerLogoutAndRedirect() {

    showLogoutLoader();

    setTimeout(function () {
        fetch("/api/v1/auth/seller/logout", {
            method: "POST",
            credentials: "include"
        })
            .then(res => {
                if (!res.ok) console.error("Logout failed", res.statusText);
                else console.log("Logged out successfully");
            })
            .catch(err => console.error("Error during logout:", err))
            .finally(() => {
                window.location.replace("/telatak/seller/login");
            });
    }, 2000);
}

/* =========================
   Customer Logout
========================= */
function customerLogoutAndRedirect() {

    showLogoutLoader();

    setTimeout(function () {
        fetch("/api/v1/auth/customer/logout", {
            method: "POST",
            credentials: "include"
        })
            .then(res => {
                if (!res.ok) console.error("Logout failed", res.statusText);
                else console.log("Logged out successfully");
            })
            .catch(err => console.error("Error during logout:", err))
            .finally(() => {
                window.location.replace("/telatak/customer/login");
            });
    }, 2000);
}