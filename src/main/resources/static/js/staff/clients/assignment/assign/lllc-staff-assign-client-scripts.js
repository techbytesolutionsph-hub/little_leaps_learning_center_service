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

    $("#assign-client-notes").on("input", function () {
        $("#charCount").text($(this).val().length);
    });

    /* Client selection */
    $("#clientCard").on("click", function () {
        $(this).toggleClass("selected");
    });

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

    $(".required-field").on("change", function () {
        const $field = $(this);

        if ($field.val()) {
            $field.removeClass("is-invalid");
            $field.parent().next(".field-error").removeClass("show");
        }
    });

    /* Load review */
    function loadReview() {
        $("#reviewClient").text($("#clientName").text() || "—");
        $("#reviewCaseManager").text(getSelectedText("assign-client-case-manager"));
        $("#reviewCaseManagerRole").text(getSelectedText("assign-client-case-manager-role"));
        $("#reviewBehavioralTherapist").text(getSelectedText("assign-client-behavioral-therapist"));
        $("#reviewBehavioralTherapistRole").text(getSelectedText("assign-client-behavioral-therapist-role"));
        $("#reviewNotes").text($.trim($("#assign-client-notes").val()) || "—");
    }

    function getSelectedText(id) {
        const $select = $("#" + id);

        if (!$select.length || !$select.val()) {
            return "—";
        }

        return $select.find("option:selected").text();
    }


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

        const assignment = getAssignmentData();
        console.log("Confirmed assignment:", assignment);

        /* Save Assign Client */
        assignClient(assignment);
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

    /* Get assignment data */
    function getAssignmentData() {
        return {
            clientId: $("#selected-client-id").val(),
            caseManagerId: $("#assign-client-case-manager").val(),
            caseManagerRole: $("#assign-client-case-manager-role").val(),
            behavioralTherapistId: $("#assign-client-behavioral-therapist").val(),
            behavioralTherapistRole: $("#assign-client-behavioral-therapist-role").val(),
            assignStatus: "ASSIGNED",
            assignedDate: getLocalDateNow(),
            notes: $.trim($("#assign-client-notes").val()) || null
        };
    }

    function getLocalDateNow() {
        const today = new Date();

        return [
            today.getFullYear(),
            String(today.getMonth() + 1).padStart(2, "0"),
            String(today.getDate()).padStart(2, "0")
        ].join("-");
    }
});

function getClientByClientID(clientId){
    $.ajax({
        url: "/api/v1/client/get-client/" + encodeURIComponent(clientId),
        type: "GET",
        success: function (response) {

            console.log("Client response:", response);

            populateClientDetails(response);
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

function assignClient(request) {

    $.ajax({
        url: "/api/v1/client/assign-client",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(request),
        success: function(response) {
            showSuccessThenRedirectPopup(
                "Success",
                response.returnMessage,
                () => {
                    window.location.href =
                        "/app/portal/client-management/assignment";
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

function populateClientDetails(client) {

    const fullName = [client.firstName, client.middleName, client.lastName]
        .filter(value => value && value.trim() !== "")
        .join(" ");

    const guardian = client.parents && client.parents.length > 0
        ? client.parents[0]
        : null;

    const guardianName = guardian ? [guardian.firstName, guardian.middleName, guardian.lastName]
            .filter(value => value && value.trim() !== "")
            .join(" ")
        : "-";

    /* Client Details Card */
    $("#selected-client-id").val(client.clientId);
    $("#clientAvatar")
        .attr("src", client.profileImageUrl || "/images/default-avatar.png")
        .attr("alt", fullName || "Client");

    $("#clientName").text(fullName || "-");
    $("#clientIdValue").text(client.clientId || "-");
    $("#clientDob").text(formatDate(client.birthDate));
    $("#clientGender").text(formatEnumValue(client.gender));
    $("#clientGuardian").text(guardianName);
    $("#clientContact").text(guardian && guardian.contactNumber
        ? guardian.contactNumber
        : "-");

    /* Client Summary */
    $("#programType").text(client.programType || "-");
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
    $("#summaryDateEnrolled").text(formatDate(client.dateEnrolled));
    $("#summaryStatus").text(formatEnumValue(client.enrollmentStatus));
    $("#summaryBranch").text(client.branch || "-");

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

