$(document).ready(function () {

    /* Image Upload Preview */
    $(document).on('change', '.image-upload', async function (event) {

        const file = event.target.files[0];

        const $wrapper = $(this)
            .closest('.mb-4')
            .find('.color-upload-wrapper');

        const $img = $wrapper.find('.image-preview');

        $img.hide();
        $wrapper.find('.remove-image').remove();

        if (!file) return;

        if (!file.type.startsWith('image/')) {
            alert('Please select a valid image file.');
            $(this).val('');
            return;
        }

        const url = await uploadToCloudinary(file);

        if (url) {
            $img.attr('src', url).show();

            const $removeBtn = $(`
                        <button type="button"
                            class="remove-image btn btn-sm btn-danger position-absolute d-flex justify-content-center align-items-center"
                            style="top:8px; right:8px; width:28px; height:28px; border-radius:50%;">
                            &times;
                        </button>
                    `);

            $wrapper.css('position', 'relative').append($removeBtn);
        }
    });

    /* Remove image */
    $(document).on('click', '.remove-image', function () {
        const $wrapper = $(this).closest('.color-upload-wrapper');
        $wrapper.find('.image-preview').attr('src', '').hide();
        $wrapper.siblings('.custom-file-upload').find('.image-upload').val('');
        $(this).remove();
    });

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

    $('#generate-password-btn').on('click', function () {
        $.get('/api/v1/account/admin/generate-temp-password', function (response) {
            $('#user-password').val(response);
        });
    });

    $("#update-user-btn").on("click", function () {

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
            url: "/api/v1/account/admin/update",
            type: "PUT",
            contentType: "application/json",
            data: JSON.stringify(request),
            beforeSend: function () {
                $("#update-user-btn")
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
                $("#update-user-btn")
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