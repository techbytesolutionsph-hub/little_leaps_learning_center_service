$(document).ready(function () {
    /* Initialized Date Pickers */
    initializeMaxTodayDatePicker("#client-birth-date", "Select birth date");
    initializeMaxTodayDatePicker("#client-date-enrolled", "Select date enrolled");

    initializeClientPhotoUpload();

    const $wrapper = $('#diagnosis-concern-wrapper');
    const $button = $('#assign-client-diagnosis-concern');
    const $dropdown = $('#diagnosis-concern-dropdown');

    $button.on('click', function (e) {
        e.stopPropagation();
        $wrapper.toggleClass('open');
    });

    $dropdown.on('change', 'input[type="checkbox"]', function () {
        updateDiagnosisConcernText();
    });

    $(document).on('click', function (e) {
        if (!$(e.target).closest('#diagnosis-concern-wrapper').length) {
            $wrapper.removeClass('open');
        }
    });

    function updateDiagnosisConcernText() {

        const selected = [];

        $dropdown
            .find('input[type="checkbox"]:checked')
            .each(function () {
                selected.push(
                    $(this)
                        .siblings('span')
                        .text()
                        .trim()
                );
            });

        const $placeholder =
            $button.find('.multi-select-placeholder');

        if (selected.length === 0) {
            $placeholder.text('Select diagnosis concern');
        } else if (selected.length <= 2) {
            $placeholder.text(selected.join(', '));
        } else {
            $placeholder.text(selected.length + ' concerns selected');
        }
    }

    function getSelectedDiagnosisConcerns() {
        return $dropdown
            .find('input[name="diagnosisConcerns"]:checked')
            .map(function () {
                return $(this).val();
            })
            .get();
    }

    $dropdown.on('change', 'input[name="diagnosisConcerns"]', function () {
        updateDiagnosisConcernText();

        const selected = getSelectedDiagnosisConcerns();
        const $diagnosisError = $wrapper.siblings(".field-error");

        if (selected.length > 0) {
            $button.removeClass("is-invalid");
            $diagnosisError.removeClass("show");
        } else {
            $button.addClass("is-invalid");
            $diagnosisError.addClass("show");
        }
    });

    function validateForm(){
        let valid = true;
        let firstInvalid = null;

        $(".required-field").each(function () {

            const $field = $(this);
            const value = $.trim($field.val());

            if (!value) {
                $field.addClass("field-invalid");
                $field
                    .closest("[class*='col-']")
                    .find(".field-error")
                    .first()
                    .addClass("show");

                valid = false;

                if (!firstInvalid) {
                    firstInvalid = $field;
                }
            } else {
                $field.removeClass("field-invalid");
                $field
                    .closest("[class*='col-']")
                    .find(".field-error")
                    .first()
                    .removeClass("show");
            }
        });

        $(".required-field").on("input change", function () {
            const $field = $(this);
            const value = $.trim($field.val());
            const $visibleField = $field.hasClass("flatpickr-input")
                ? $field.next(".form-control")
                : $field;

            const $error = $visibleField
                .closest("[class*='col-']")
                .find(".field-error")
                .first();

            if (value) {
                $visibleField.removeClass("field-invalid");
                $error.removeClass("show");
            }
        });

        /* DIAGNOSIS CONCERN */
        const diagnosisCount = $('input[name="diagnosisConcerns"]:checked').length;
        const $diagnosis = $("#diagnosis-concern-wrapper");
        const $diagnosisError = $diagnosis
            .closest("[class*='col-']")
            .find(".field-error")
            .first();

        if (diagnosisCount === 0) {
            $diagnosis.addClass("field-invalid");
            $diagnosisError.addClass("show");

            valid = false;

            if (!firstInvalid) {
                firstInvalid =
                    $("#assign-client-diagnosis-concern");
            }
        } else {
            $diagnosis.removeClass("field-invalid");
            $diagnosisError.removeClass("show");
        }

        const $email = $("#client-parent-email");

        if ($email.val().trim()) {
            const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

            if (!emailPattern.test($email.val().trim())) {
                $email.addClass("field-invalid");

                $email
                    .closest("[class*='col-']")
                    .find(".field-error")
                    .first()
                    .text("Please enter a valid email address.")
                    .addClass("show");

                valid = false;

                if (!firstInvalid) {
                    firstInvalid = $email;
                }
            }
        }

        if (!valid) {
            if (firstInvalid && firstInvalid.length) {
                $("html, body").animate({
                    scrollTop: firstInvalid.offset().top - 100
                }, 300);
            }
            return false;
        }
        return true;
    }


    /* REMOVE ERROR WHILE USER IS FIXING FIELD */
    $(".required-field").on("input change", function () {
        if ($.trim($(this).val())) {
            $(this).removeClass("field-invalid");
            $(this)
                .closest("[class*='col-']")
                .find(".field-error")
                .first()
                .removeClass("show");
        }
    });

    /* DIAGNOSIS CONCERN ERROR CLEAR */
    $('input[name="diagnosisConcerns"]').on("change", function () {
        if ($('input[name="diagnosisConcerns"]:checked').length > 0) {
            $("#diagnosis-concern-wrapper")
                .removeClass("field-invalid");

            $("#diagnosis-concern-wrapper")
                .closest("[class*='col-']")
                .find(".field-error")
                .first()
                .removeClass("show");
        }
    });

    $("#search-credentials-btn").click(function () {
        let username = $("#client-cred-username").val();

        if (!username) {
            showErrorPopup("Required Field", "Please enter username");
            return;
        }

        getUserByUsername(username);
    });

    $("#add-client-btn").on("click", function (e) {
        e.preventDefault();

        const isValid = validateForm();

        if (!isValid) {
            console.log("Client form is invalid. Submission stopped.");
            return;
        }

        console.log("Client form is valid.");
        const clientRequest = buildClientRequest();
        console.log(clientRequest);
        createClient(clientRequest);
    });

    $('#back-client-btn').on('click', function (e) {
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

    function buildClientRequest() {
        const firstName = $("#client-firstname").val()?.trim();
        const middleName = $("#client-middlename").val()?.trim();
        const lastName = $("#client-lastname").val()?.trim();
        const age = $("#client-age").val()
            ? parseInt($("#client-age").val(), 10)
            : null;
        const birthDate = $("#client-birth-date").val() || null;
        const gender = $("#client-gender").val() || null;
        const dateEnrolled = $("#client-date-enrolled").val() || null;
        const diagnosisConcerns = getSelectedDiagnosisConcerns();
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
            firstName: firstName,
            middleName: middleName,
            lastName: lastName,
            age: age,
            birthDate: birthDate,
            gender: gender,
            dateEnrolled: dateEnrolled,
            diagnosisConcerns: diagnosisConcerns,
            programType: programType,
            branch: branch,
            enrollmentStatus: enrollmentStatus,
            profileImageUrl: $("#clientPhotoPreview").attr("src") || "",
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

    function createClient(clientRequest) {

        $.ajax({
            url: "/api/v1/client/register-client",
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
});