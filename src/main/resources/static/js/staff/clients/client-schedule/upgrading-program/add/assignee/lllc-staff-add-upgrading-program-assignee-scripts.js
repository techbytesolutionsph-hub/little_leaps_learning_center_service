const $button = $("#search-client-btn");
const originalHtml = $button.html();

$(document).ready(function () {

    $("#search-client-btn").on("click", function (e) {
        e.stopPropagation();

        let clientId = $("#client-id").val().trim();
        $("#selected-client-id").val(clientId);

        $("#client-id").removeClass("is-invalid");

        if (!clientId) {
            $("#client-id").addClass("is-invalid");
            return;
        }

        $("#clientCard").hide();
        $("#clientSummary").hide();

        getClientByClientID(clientId);
    });

    $("#therapy-session-notes").on("input", function () {
        $("#charCount").text($(this).val().length);
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

        getClientByClientID(clientId);
    });

    $('#confirm-btn').on('click', function (e) {
        e.preventDefault();

        const assessment = getAssignmentData();

        console.log("Confirmed assessment:", assessment);

        /* Save Initial Assessment */
        saveUpgradingProgramDetails(assessment);
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

/* Get Initial Assessment Schedule Data */
function getAssignmentData() {
    return {
        clientId: $("#clientIdValue").text(),
        employeeId: $("#therapy-session-behavioral-therapist").val(),
        assignmentRole: $("#therapy-session-role").val(),
        status: $("#therapy-session-status").val(),
        notes: $.trim($("#therapy-session-notes").val()) || null
    };
}

/* Load review */
function loadReview() {
    $("#clientFullName").text($("#clientName").text() || "—");
    $("#reviewRole").text(getSelectedText("therapy-session-role"));
    $("#reviewBehavioralTherapist").text(getSelectedText("therapy-session-behavioral-therapist"));
    $("#reviewScheduleStatus").text(getSelectedText("therapy-session-status"));
    $("#reviewNotes").text($.trim($("#therapy-session-notes").val()) || "—");
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

    $(".required-field").each(function () {
        const $field = $(this);
        const $error = $field.parent().next(".field-error");

        if (!$field.val()) {
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

function getClientByClientID(assignmentId){
    $.ajax({
        url: "/api/v1/client/get-assign-client?id=" + encodeURIComponent(assignmentId),
        type: "GET",
        success: function (response) {
            console.log("Assigned Client response:", response);

            populateAssignedClientDetails(response);
            $("#step2").addClass("active");
        },
        error: function (xhr) {
            console.error("Failed to retrieve client:", xhr);

            $("#clientCard").hide();
            $("#clientSummary").hide();

            if (xhr.status === 404) {
                showErrorPopup("Error", "Client not found.");
            } else {
                showErrorPopup("Error", "Unable to retrieve client information.");
            }
        },
        complete: function () {
            $button.prop("disabled", false);
            $button.html(originalHtml);
        }
    });
}

function saveUpgradingProgramDetails(request) {

    $.ajax({
        url: "/api/v1/assessment/save-upgrading-program-details",
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
            let message = "Unable to save upgrading program details.";

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

function populateAssignedClientDetails(client) {

    /* Client Details Card */
    $("#clientAvatar")
        .attr("src", client.clientProfilePicture || "/images/default-avatar.png")
        .attr("alt", client.clientFullName || "Client");

    $("#clientName").text(client.clientFullName || "-");
    $("#clientIdValue").text(client.clientId || "-");
    $("#clientDob").text(formatDate(client.clientBirthDate));
    $("#clientGender").text(formatEnumValue(client.clientGender));
    $("#clientGuardian").text(client.guardianFullName);
    $("#clientContact").text(client.guardianContactNo || "-");

    /* Client Summary */
    $('#case-manager-avatar').attr('src', client.caseManagerProfilePicture || '/img/base/default-profile.png');
    $("#case-manager-name").text(client.caseManagerFullName|| "-");
    $("#case-manager-position").text(client.caseManagerPosition|| "-");
    $("#case-manager-id").text(client.caseManagerId|| "-");
    $("#case-manager-role").text(formatEnumValue(client.caseManagerRole) || "-");

    $('#behavioral-therapist-avatar').attr('src', client.behavioralTherapistProfilePicture || '/img/base/default-profile.png');
    $("#behavioral-therapist-name").text(client.behavioralTherapistFullName|| "-");
    $("#behavioral-therapist-position").text(client.behavioralTherapistPosition|| "-");
    $("#behavioral-therapist-id").text(client.behavioralTherapistId|| "-");
    $("#behavioral-therapist-role").text(formatEnumValue(client.behavioralTherapistRole) || "-");

    const diagnosisConcerns = client.diagnosisConcerns;

    $("#diagnosisConcerns").text(
        diagnosisConcerns?.length
            ? diagnosisConcerns
                .map(value => value
                    .toLowerCase()
                    .replace(/_/g, " ")
                    .replace(/\b\w/g, char => char.toUpperCase())
                )
                .join(", ")
            : "-"
    );

    $("#assigned-at").text(formatDate(client.assignedAt));
    $("#assigned-branch").text(client.branch || "-");

    const $therapist = $("#therapy-session-behavioral-therapist");
    const therapistId = String(client.caseManagerId);

    if ($therapist.find(`option[value="${therapistId}"]`).length) {
        $therapist.val(therapistId).trigger("change").focus();
    }

    $("#therapy-session-role")
        .val(client.caseManagerRole)
        .trigger("change");

    function formatRole(role) {
        return role
            .toLowerCase()
            .split("_")
            .map(word => word.charAt(0).toUpperCase() + word.slice(1))
            .join(" ");
    }

    /* Profile Link */
    if (client.clientId) {
        $("#clientProfileLink").attr(
            "href",
            "/app/portal/client-management/registry/view-client?id=" +
            encodeURIComponent(client.clientId)
        );
    } else {
        $("#clientProfileLink").attr("href", "#");
    }

    $("#clientCard")
        .addClass("selected")
        .fadeIn(200);

    $("#clientSummary")
        .fadeIn(200);
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

function formatTime(value) {
    if (!value) return "—";

    const [hours, minutes] = value.split(":").map(Number);

    const period = hours >= 12 ? "PM" : "AM";
    const hour12 = hours % 12 || 12;

    return `${hour12}:${String(minutes).padStart(2, "0")} ${period}`;
}

function formatEnumValue(value) {

    if (!value) {
        return "-";
    }

    return value
        .toLowerCase()
        .replace(/_/g, " ")
        .replace(/\b\w/g, function (char) {
            return char.toUpperCase();
        });
}

