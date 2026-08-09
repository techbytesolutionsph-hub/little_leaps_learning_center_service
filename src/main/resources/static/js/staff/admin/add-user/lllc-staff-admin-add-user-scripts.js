$(document).ready(function () {

    /* Initialized Image Upload */
    initializeImageUpload();

    $('#generate-password-btn').on('click', function () {
        $.get('/api/v1/account/admin/generate-temp-password', function (response) {
            $('#user-password').val(response);
        });
    });

    $("#add-user-btn").on("click", function () {

        let request = {
            username: $("#user-username").val(),
            password: $("#user-password").val(),
            email: $("#user-email").val(),
            role: $("#user-role").val(),
            status: $("#user-status").val(),
            profileImageUrl: $(".image-preview").attr("src") || ""
        }

        console.log(request);

        $.ajax({
            url: "/api/v1/account/admin/create",
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(request),
            beforeSend: function () {
                $("#add-user-btn")
                    .prop("disabled", true)
                    .html('<span class="spinner-border spinner-border-sm me-2"></span> Creating...');
            },

            success: function (response) {

                showSuccessThenRedirectPopup(
                    "Success",
                    response.returnMessage,
                    () => {
                        window.location.href = "/app/portal/admin/user-account";
                    }
                );
            },

            error: function (xhr) {

                let message = "Unable to create user.";

                if (xhr.responseJSON && xhr.responseJSON.message) {
                    message = xhr.responseJSON.message;
                }

                showErrorPopup("Error", message);
            },

            complete: function () {
                $("#add-user-btn")
                    .prop("disabled", false)
                    .html('<i class="fa-solid fa-user-plus mr-2"></i> Add User');
            }
        });

    });

    $('#back-btn').on('click', function (e) {
        e.preventDefault();

        const url = $(this).data('url');
        console.log(url);

        showConfirmPopup(
            'Leave this page?',
            'Your unsaved product data will be lost.',
            function () {
                window.location.href = url;
            }
        );
    });
});