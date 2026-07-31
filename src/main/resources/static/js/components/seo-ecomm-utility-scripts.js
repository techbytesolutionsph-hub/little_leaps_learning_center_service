function getCustomer(){
    const email = $("#current-user-email").text().trim();
    const isAnonymous = !email || email === "anonymousUser";

    if (!isAnonymous) {
        return email;
    } else {
        return f_getStoreValue("guestToken");
    }
}

function isAuthenticated() {
    const email = $("#current-user-email").text().trim();
    return !!email && email !== "anonymousUser";
}

function triggerConfetti() {
    const colors = ['#7a1113', '#ff8c00', '#ffd700'];

    confetti({
        particleCount: 100,
        angle: 120,
        spread: 60,
        origin: { x: 0.9, y: 1 },
        colors: colors,
        gravity: 0.2,
        scalar: 0.8,
        startVelocity: 40,
        ticks: 200,
        zIndex: 9999
    });

    confetti({
        particleCount: 100,
        angle: 60,
        spread: 60,
        origin: { x: 0.1, y: 1 },
        colors: colors,
        gravity: 0.2,
        scalar: 0.8,
        startVelocity: 40,
        ticks: 200,
        zIndex: 9999
    });
}

function showRedirectOverlay(message = "Redirecting to PayMongo checkout page...") {
    $("body").append(`
        <div id="redirect-overlay" style="
            position:fixed;
            top:0;
            left:0;
            width:100%;
            height:100%;
            background:rgba(255,255,255,0.4);
            display:flex;
            flex-direction:column;
            justify-content:center;
            align-items:center;
            color:#333;
            font-family:'Segoe UI',Tahoma,Geneva,Verdana,sans-serif;
            z-index:9999;">
            
            <img src="https://media.tenor.com/nfJNAjuNzE4AAAAj/cat-walking.gif"
                 style="width:100px; height:auto; margin-bottom:20px; filter: sepia(1) saturate(5) hue-rotate(-25deg) brightness(0.8);">
                 
            <div style="font-size:0.9rem;font-weight:700;text-align:center;">
                ${message}
            </div>
        </div>
    `);
}

function setPlaceOrderLoading(isLoading) {
    const btn = $("#desktop-place-order-btn, #mobile-place-order-btn");

    if (isLoading) {
        btn.prop("disabled", true);

        btn.each(function () {
            $(this).data("original-html", $(this).html());

            $(this).html(`
                <div class="spinner"></div>
                <span style="margin-left:8px;">Processing...</span>
            `);
        });

    } else {
        btn.prop("disabled", false);

        btn.each(function () {
            const original = $(this).data("original-html");
            if (original) {
                $(this).html(original);
            }
        });
    }
}