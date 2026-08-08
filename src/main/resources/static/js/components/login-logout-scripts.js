$(document).ready(function () {

    function handleSidebarResize() {
        const $sidenav = $("#mobile-sidenav");
        const $burger = $("#burgerToggle");
        const $body = $("body");

        if ($(window).width() > 767) {
            $sidenav.removeClass("show");
            $burger.removeClass("open");
            $body.removeClass("sidenav-open");
        }
    }

    handleSidebarResize();

    $(window).on("resize", function () {
        handleSidebarResize();
    });

    $("#burgerToggle").on("click", function () {
        const $sidenav = $("#mobile-sidenav");
        const $burger = $(this);
        const $body = $("body");

        $sidenav.toggleClass("show");
        $burger.toggleClass("open");
        $body.toggleClass("sidenav-open");
    });


    function closeSidebarIfNeeded() {
        const $sidenav = $("#mobile-sidenav");
        const $burger = $("#burgerToggle");
        const $body = $("body");

        const isOpen = $sidenav.hasClass("show");

        console.log("closeSidebarIfNeeded()");
        console.log("Sidebar currently open:", isOpen);

        if (isOpen) {
            $sidenav.removeClass("show");
            $burger.removeClass("open");
            $body.removeClass("sidenav-open");

            console.log("Sidebar closed");
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

    $(document).on("click", "#staff-sign-out-page", function (e) {
        e.preventDefault();
        handleSignOut(staffLogoutAndRedirect);
    });

    $(document).on("click", "#client-sign-out-page", function (e) {
        e.preventDefault();
        handleSignOut(clientLogoutAndRedirect);
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
   Staff Logout
========================= */
function staffLogoutAndRedirect() {

    showLogoutLoader();

    setTimeout(function () {
        fetch("/api/v1/auth/portal/logout", {
            method: "POST",
            credentials: "include"
        })
            .then(res => {
                if (!res.ok) console.error("Logout failed", res.statusText);
                else console.log("Logged out successfully");
            })
            .catch(err => console.error("Error during logout:", err))
            .finally(() => {
                window.location.replace("/app/portal/login");
            });
    }, 2000);
}

/* =========================
   Client Logout
========================= */
function clientLogoutAndRedirect() {

    showLogoutLoader();

    setTimeout(function () {
        fetch("/api/v1/auth/client/logout", {
            method: "POST",
            credentials: "include"
        })
            .then(res => {
                if (!res.ok) console.error("Logout failed", res.statusText);
                else console.log("Logged out successfully");
            })
            .catch(err => console.error("Error during logout:", err))
            .finally(() => {
                window.location.replace("/lllc/client/login");
            });
    }, 2000);
}