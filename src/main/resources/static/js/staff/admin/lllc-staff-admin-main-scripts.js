$(document).ready(function () {

    $(document).on("click", ".delete-user-btn", function () {

        const username = $(this).data("id");

        console.log(username);

        showConfirmPopup(
            "Disable User Account?",
            "This user will no longer be able to sign in.",
            function () {
                softDeleteUser(username);
            }
        );
    });

    function softDeleteUser(username) {

        $.ajax({
            url: "/api/v1/account/admin/delete/" + encodeURIComponent(username),
            type: "DELETE",

            success: function (response) {
                showSuccessThenRedirectPopup(
                    "Success",
                    response.returnMessage,
                    function () {
                        window.location.href = "/app/portal/admin/user-account";
                    }
                );
            },

            error: function (xhr) {
                let message = "Unable to disable the user account.";
                if (xhr.responseJSON) {
                    message = xhr.responseJSON.returnMessage
                        || xhr.responseJSON.message
                        || message;
                }
                showErrorPopup("Error", message);
            }
        });
    }
});