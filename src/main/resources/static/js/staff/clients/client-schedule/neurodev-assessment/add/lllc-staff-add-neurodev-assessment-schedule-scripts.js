const $button = $("#search-client-btn");
const originalHtml = $button.html();

$(document).ready(function () {

    initializeDatePicker("#neurodev-client-initial-assessment-date", "Select neurodev assessment date");

    $("#neurodev-client-notes").on("input", function () {
        $("#charCount").text($(this).val().length);
    });

    $("#neurodev-client-assessment-status").on("change", function () {
        if ($(this).val()) {
            $("#step2").addClass("active");
        }
    });

    // Client selection
    $("#clientCard").on("click", function () {
        $(this).toggleClass("selected");
    });

    $(".required-field").on("change", function () {
        const $field = $(this);

        if ($field.val()) {
            $field.removeClass("is-invalid");
            $field.parent().next(".field-error").removeClass("show");
        }
    });

    $(".form-control.required-field, .form-select.required-field").on("change input", function () {
        const $field = $(this);
        const $error = $field.closest(".form-group").find(".field-error");

        if ($field.val() && $.trim($field.val()) !== "") {
            $field.removeClass("is-invalid");
            $error.removeClass("show");
        }
    });

    $('#next-btn').on('click', function (e) {
        if (!validateForm()) {
            showErrorPopup("Required Field", "Please complete the required fields.");
            return;
        }

        loadReview();

        $("#assignmentForm").hide();
        $("#reviewSection").addClass("show");
        $("#step1").addClass("completed");
        $("#step2").addClass("active");
        $("#step3").addClass("active");
        window.scrollTo({top: 0, behavior: "smooth"});
    });

    $('#back-btn').on('click', function (e) {
        e.preventDefault();

        $("#assignmentForm").show();
        $("#reviewSection").removeClass("show");
        $("#step3").removeClass("active");

        window.scrollTo({
            top: 0,
            behavior: "smooth"
        });
    });

    $('#confirm-btn').on('click', function (e) {
        e.preventDefault();

        const assessment = getAssignmentData();

        console.log("Confirmed assessment:", assessment);

        /* Save Neurodev Assessment */
        saveNeurodevAssessment(assessment);
    });

    $('#cancel-btn').on('click', function (e) {
        e.preventDefault();

        showConfirmPopup(
            "Cancel?",
            "Are you sure you want to cancel this assignment?",
            function () {
                window.location.reload();
            }
        );
    });
});

/* Get Neurodev Assessment Schedule Data */
function getAssignmentData() {
    return {
        clientName: $("#neurodev-client-full-name").val() || null,
        age: $("#neurodev-client-age").val() || null,
        gender: $("#neurodev-client-gender").val() || null,
        parentGuardian: $("#neurodev-client-parent-guardian").val() || null,
        contactNumber: $("#neurodev-parent-guardian-contact-no").val() || null,
        status: $("#neurodev-client-assessment-status").val() || null,
        assessmentDate: $("#neurodev-client-initial-assessment-date").val() || null,
        neurodevFee: $("#neurodev-client-neurodev-fee").val() || null,
        paymentStatus: $("#neurodev-client-payment-status").val() || null,
        notes: $.trim($("#neurodev-client-notes").val()) || null
    };
}

/* Load review */
function loadReview() {
    $("#clientFullName").text($("#neurodev-client-full-name").val() || "—");
    $("#reviewClientGender").text(
        getSelectedText("neurodev-client-gender")
    );
    $("#reviewClientAge").text($("#neurodev-client-age").val() || "—");
    $("#reviewClientParentGuardian").text($("#neurodev-client-parent-guardian").val() || "—");
    $("#reviewClientContactNo").text($("#neurodev-parent-guardian-contact-no").val() || "—");

    $("#reviewAssessmentStatus").text(getSelectedText("neurodev-client-assessment-status"));
    $("#reviewNeurodevAssessmentDate").text($("#neurodev-client-initial-assessment-date").val() || "—");
    $("#reviewNeurodevFee").text($("#neurodev-client-neurodev-fee").val() || "—");
    $("#reviewPaymentStatus").text(
        getSelectedText("neurodev-client-payment-status")
    );
    $("#reviewNotes").text($.trim($("#neurodev-client-notes").val()) || "—");
}

function getSelectedText(id) {
    const $select = $("#" + id);

    if (!$select.length || !$select.val()) {
        return "—";
    }

    return $select.find("option:selected").text();
}

/* Validation */
function validateForm() {
    let valid = true;

    $(".form-control.required-field, .form-select.required-field").each(function () {
        const $field = $(this);

        const $error = $field
            .closest(".form-group")
            .find(".field-error");

        if (!$field.val() || $.trim($field.val()) === "") {
            $field.addClass("is-invalid");
            $error.addClass("show");
            valid = false;
        } else {
            $field.removeClass("is-invalid");
            $error.removeClass("show");
        }
    });

    return valid;
}

function saveNeurodevAssessment(request) {

    $.ajax({
        url: "/api/v1/assessment/save-neurodev-assessment",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(request),

        success: function(response) {

            showSuccessThenRedirectPopup(
                "Success",
                response.returnMessage,
                () => {
                    window.location.href =
                        "/app/portal/client-management/client-schedule";
                }
            );
        },
        error: function(xhr, status, error) {
            let message = "Unable to save neurodev assessment.";

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

function formatDate(date) {
    if (!date) {
        return "-";
    }

    return new Date(date).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    });
}
