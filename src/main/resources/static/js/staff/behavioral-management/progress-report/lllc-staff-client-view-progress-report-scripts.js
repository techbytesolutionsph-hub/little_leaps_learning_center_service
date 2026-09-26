$(document).ready(function() {
    initializeDatePicker2("#report-date-start", "mm/dd/yyyy");
    initializeDatePicker2("#report-date-end", "mm/dd/yyyy");

    initializeDocumentUpload();

    $("#report-submit-btn").on("click", function () {

        if (!validateProgressReportForm()) {
            return;
        }

        const request = buildProgressReportPayload(uploadRequest);
        console.log(request);

        createProgressReport(request);
    });

    function createProgressReport(request) {
        $.ajax({
            url: "/api/v1/client/progress-report",
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(request),
            success: function (response) {
                showSuccessThenRedirectPopup(
                    "Success",
                    response.returnMessage,
                    () => {
                        window.location.reload();
                    }
                );
                console.log("Progress report created successfully:", response);
            },
            error: function (xhr) {
                console.error("Failed to create progress report:", xhr);
                showErrorPopup("Error", "Failed to create progress report.");
            }
        });
    }

    /* Remove error when client is selected */
    $("#report-select-client").on("change", function () {
        if ($(this).val()) {
            $(this).closest(".assessment-form-group").removeClass("has-error");
        }
    });

    /* Remove error when start date is selected */
    $("#report-date-start").on("change", function () {
        if ($(this).val()) {
            $(this).closest(".assessment-form-group").removeClass("has-error");
        }
    });

    /* Remove error when end date is selected */
    $("#report-date-end").on("change", function () {
        if ($(this).val()) {
            $(this).closest(".assessment-form-group").removeClass("has-error");
        }
    });

    function validateProgressReportForm() {

        let isValid = true;

        const $client = $("#report-select-client");
        const $dateStart = $("#report-date-start");
        const $dateEnd = $("#report-date-end");

        /* Client */
        if (!$client.val()) {
            $client.closest(".assessment-form-group").addClass("has-error");
            isValid = false;
        } else {
            $client.closest(".assessment-form-group").removeClass("has-error");
        }

        /* Reporting Period From */
        if (!$dateStart.val()) {
            $dateStart.closest(".assessment-form-group").addClass("has-error");
            isValid = false;
        } else {
            $dateStart.closest(".assessment-form-group").removeClass("has-error");
        }

        /* Reporting Period To */
        if (!$dateEnd.val()) {
            $dateEnd.closest(".assessment-form-group").addClass("has-error");
            isValid = false;
        } else {
            $dateEnd.closest(".assessment-form-group").removeClass("has-error");
        }

        /* File */
        if (!uploadRequest) {
            $("#fileDropZone").addClass("has-error");
            isValid = false;
        }else{
            $("#fileDropZone").removeClass("has-error");
        }

        return isValid;
    }

    function buildProgressReportPayload() {
        return {
            reportingPeriodFrom: $("#report-date-start").val(),
            reportingPeriodTo: $("#report-date-end").val(),
            notes: $("#report-notes").val(),
            fileUrl: uploadRequest.secure_url ?? null,
            fileName: uploadRequest.original_filename
                ? `${uploadRequest.original_filename}.${uploadRequest.format}`
                : null,
            fileSize: uploadRequest.bytes ?? null,
            assigneeId: $("#currentEmployeeId").text().trim(),
            clientId: $("#report-select-client").val()
        };
    }
});