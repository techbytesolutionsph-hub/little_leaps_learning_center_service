$(document).ready(function () {

    /* Initialized Date Pickers */
    initializeMaxTodayDatePicker("#client-birth-date", "Select birth date");
    initializeMaxTodayDatePicker("#client-date-enrolled", "Select date enrolled");

    /* Initialized Image Upload */
    initializeImageUpload();

    $("#search-credentials-btn").click(function () {
        let username = $("#client-cred-username").val();

        if (!username) {
            showErrorPopup("Required Field", "Please enter username");
            return;
        }

        getUserByUsername(username);
    });

    $("#update-client-btn").on("click", function (e) {

        e.preventDefault();

        let isValid = true;
        let firstInvalidTab = null;
        let firstInvalidField = null;

        /* Remove previous validation styles */
        $("#update-client-form .form-control, #update-client-form .form-select")
            .removeClass("is-invalid");

        /* Validate all required fields except HMO fields */
        $("#update-client-form [required]")
            .each(function () {

                let field = $(this);

                /* Skip only disabled fields */
                if (field.prop("disabled")) {
                    return true;
                }

                let value = $.trim(field.val());

                if (!value) {
                    console.log("Invalid Field:", field.attr("id"));

                    isValid = false;
                    field.addClass("is-invalid");

                    if (!firstInvalidField) {
                        firstInvalidField = field;
                        firstInvalidTab = field.closest(".tab-pane");
                    }
                }
            });

        if (!isValid) {

            /* Open the tab containing the first invalid field */
            if (firstInvalidTab.length) {
                let tabId = "#" + firstInvalidTab.attr("id");
                $('.nav-link[data-bs-target="' + tabId + '"]').tab("show");
            }

            /* Focus the first invalid field */
            setTimeout(function () {
                firstInvalidField.trigger("focus");
            }, 300);

            showErrorPopup(
                "Required Field",
                "Please fill out all required fields before submitting."
            );

            return;
        }


        /* Build request */
        const clientRequest = buildClientRequest();
        console.log(clientRequest);

        /* update Client Record */
        updateClient(clientRequest);
    });

    /* Remove invalid state when user types/selects */
    function clearInvalidState($field) {
        if ($field.val() && $field.val().trim() !== "") {
            $field.removeClass("is-invalid");
        }
    }

    $("#update-client-form").on(
        "input change",
        ".form-control, .form-select",
        function () {
            clearInvalidState($(this));
        }
    );

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

function buildClientRequest() {
    /*
     * Get values
     */
    const firstName = $("#client-firstname").val()?.trim();
    const middleName = $("#client-middlename").val()?.trim();
    const lastName = $("#client-lastname").val()?.trim();
    const age = $("#client-age").val()
        ? parseInt($("#client-age").val(), 10)
        : null;
    const birthDate = $("#client-birth-date").val() || null;
    const gender = $("#client-gender").val() || null;
    const dateEnrolled = $("#client-date-enrolled").val() || null;
    const diagnosisConcern = $("#client-diagnosis").val() || null;
    const programType = $("#client-program-type").val() || null;
    const branch = $("#client-assign-branch").val() || null;
    const enrollmentStatus = $("#client-enrollment-status").val() || null;

    /*
     * Parent / Guardian
     */
    const parent = {
        firstName: $("#client-parent-firstname").val()?.trim(),
        middleName: $("#client-parent-middlename").val()?.trim(),
        lastName: $("#client-parent-lastname").val()?.trim(),
        contactNumber: $("#client-parent-contact-no").val()?.trim(),
        email: $("#client-parent-email").val()?.trim(),
        relationshipToClient: $("#client-parent-relationship").val() || null,
        gender: $("#client-parent-gender").val() || null,
        address: $("#client-parent-address").val()?.trim()
    };

    const accountAccess = {
        username: $('#client-cred-username').val(),
        password: $('#client-cred-password').val(),
        email: $('#client-cred-email').val(),
        status: $('#client-cred-status').val()
    };

    return {
        uuid: uuid,
        firstName: firstName,
        middleName: middleName,
        lastName: lastName,
        age: age,
        birthDate: birthDate,
        gender: gender,
        dateEnrolled: dateEnrolled,
        diagnosisConcern: diagnosisConcern,
        programType: programType,
        branch: branch,
        enrollmentStatus: enrollmentStatus,
        profileImageUrl: $('.image-preview').attr('src'),
        parents: [parent],
        accountAccess: accountAccess
    };
}

function formatStatus(role) {
    return role
        .toLowerCase()
        .replace(/_/g, " ")
        .replace(/\b\w/g, char => char.toUpperCase());
}

function getUserByUsername(username) {
    $.ajax({
        url: "/api/v1/account/admin/get-user/" + username,
        type: "GET",
        success: function(response) {
            console.log("User details:", response);

            $("#client-cred-password").val(response.lastPassword);
            $("#client-cred-email").val(response.email);
            $("#client-cred-status").val(formatStatus(response.status));
        },
        error: function(xhr, status, error) {
            console.error("Error fetching user:", xhr.responseText);
            showInfoPopup("Info", "User not found.");
        }
    });
}

function updateClient(clientRequest) {

    $.ajax({
        url: "/api/v1/client/update-client",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(clientRequest),

        success: function(response) {

            showSuccessThenRedirectPopup(
                "Success",
                response.returnMessage,
                () => {
                    window.location.href =
                        "/app/portal/client-management/registry";
                }
            );
        },
        error: function(xhr, status, error) {
            let message = "Unable to register client.";

            if (xhr.responseJSON) {

                if (xhr.responseJSON.message) {
                    message = xhr.responseJSON.message;
                } else if (xhr.responseJSON.returnMessage) {
                    message = xhr.responseJSON.returnMessage;
                }
            }

            showErrorPopup("Error", message);
        }
    });
}