$(document).ready(function () {

    const emailModal = new bootstrap.Modal(
        document.getElementById("emailCouponModal")
    );

    const couponModal = new bootstrap.Modal(
        document.getElementById("couponCodeModal")
    );

    setTimeout(function () {
        emailModal.show();
    }, 2500);

    $("#couponForm").on("submit", function (e) {

        e.preventDefault();

        const email = $("#couponEmail");

        if (!email.val().trim()) {
            email[0].reportValidity();
            return;
        }

        $("#emailCouponModal").one("hidden.bs.modal", function () {
            couponModal.show();
        });

        emailModal.hide();
    });

    // Copy coupon
    $(".coupon-copy-btn").on("click", function () {
        copyCoupon();
    });

});

function showCopyTooltip() {

    const tooltip = $(".copy-tooltip");
    tooltip.addClass("show");

    setTimeout(function () {
        tooltip.removeClass("show");
    }, 1500);
}

function copyCoupon() {

    const code = $("#generatedCoupon").text().trim();

    if (navigator.clipboard) {
        navigator.clipboard.writeText(code).then(function () {
            showCopyTooltip();
        });

    } else {
        const temp = $("<input>");
        $("body").append(temp);
        temp.val(code).select();
        document.execCommand("copy");
        temp.remove();

        showCopyTooltip();
    }
}