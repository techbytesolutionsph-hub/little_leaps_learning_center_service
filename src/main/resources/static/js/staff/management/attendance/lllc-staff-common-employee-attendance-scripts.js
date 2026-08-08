$(document).ready(function () {

    calculateTotal();

    $(".hour-field").on("input", function () {
        calculateTotal();
    });

    let currentWeekEnding = getWeekEnding(new Date());
    renderWeek();
    loadAttendance(currentWeekEnding);

    $("#prevWeek").click(function () {
        currentWeekEnding.setDate(currentWeekEnding.getDate() - 7);
        renderWeek();
        loadAttendance(currentWeekEnding);
    });

    $("#nextWeek").click(function () {
        currentWeekEnding.setDate(currentWeekEnding.getDate() + 7);
        renderWeek();
        loadAttendance(currentWeekEnding);
    });

    $("#save-attendance-btn").click(function () {
        const request = buildTimesheetRequest("SAVED");
        console.log(JSON.stringify(request, null, 2));
        saveSubmitTimesheet(request)
    });

    $("#submit-attendance-btn").click(function () {
        const request = buildTimesheetRequest("SUBMITTED");
        console.log(JSON.stringify(request, null, 2));
        saveSubmitTimesheet(request)
    });

    function renderWeek() {
        $("#weekEnding").text(formatDate(currentWeekEnding));
        renderTableHeaders(currentWeekEnding);
    }

    function getWeekEnding(date) {
        const d = new Date(date);
        const day = d.getDay();

        let diff;

        switch (day) {
            case 0: diff = 6; break;
            case 1: diff = 5; break;
            case 2: diff = 4; break;
            case 3: diff = 3; break;
            case 4: diff = 2; break;
            case 5: diff = 1; break;
            case 6: diff = 0; break;
        }

        d.setDate(d.getDate() + diff);

        return d;
    }

    function formatDate(date){
        return date.toLocaleDateString("en-US",{
            month:"long",
            day:"numeric",
            year:"numeric"
        });
    }

    function renderTableHeaders(weekEnding) {

        const saturday = new Date(weekEnding);
        const monday = new Date(saturday);
        monday.setDate(saturday.getDate() - 5);

        const days = [
            { id: "#monHeader", date: new Date(monday) },
            { id: "#tueHeader", date: new Date(monday.getTime() + 86400000) },
            { id: "#wedHeader", date: new Date(monday.getTime() + 86400000 * 2) },
            { id: "#thuHeader", date: new Date(monday.getTime() + 86400000 * 3) },
            { id: "#friHeader", date: new Date(monday.getTime() + 86400000 * 4) },
            { id: "#satHeader", date: new Date(saturday) }
        ];

        days.forEach(day => {
            $(day.id).html(
                day.date.toLocaleDateString("en-US", {
                    weekday: "short"
                }) +
                "<br>" +
                day.date.toLocaleDateString("en-US", {
                    month: "short",
                    day: "numeric"
                })
            );
        });
    }

    function buildTimesheetRequest(status) {

        return {
            employeeId: $("#employee-id").val(),
            weekEnding: formatDateForBackend(currentWeekEnding),
            status: status,
            entries: [
                {
                    monday: getHours("#monday-field"),
                    tuesday: getHours("#tuesday-field"),
                    wednesday: getHours("#wednesday-field"),
                    thursday: getHours("#thursday-field"),
                    friday: getHours("#friday-field"),
                    saturday: getHours("#saturday-field"),
                    sunday: 0,
                    totalHours: calculateEntryTotal()
                }
            ]
        };
    }

    function formatDateForBackend(date) {

        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, "0");
        const day = String(date.getDate()).padStart(2, "0");

        return `${year}-${month}-${day}`;
    }

    function getHours(selector) {
        return parseFloat($(selector).val()) || 0;
    }

    function calculateEntryTotal() {

        return (
            getHours("#monday-field") +
            getHours("#tuesday-field") +
            getHours("#wednesday-field") +
            getHours("#thursday-field") +
            getHours("#friday-field") +
            getHours("#saturday-field")
        );
    }
});

function saveSubmitTimesheet(request){
    $.ajax({
        url: "/api/v1/timesheet/save-submit-timesheet",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(request),
        success: function(response) {
            showSuccessThenRedirectPopup(
                "Success",
                response.returnMessage,
                () => {
                    window.location.reload();
                }
            );
        },
        error: function(xhr, status, error) {
            let message = "Unable to submit/save attendance.";

            if (xhr.responseJSON && xhr.responseJSON.message) {
                message = xhr.responseJSON.message;
            }

            showErrorPopup("Error", message);
        }
    });
}

function loadAttendance(currentWeekEnding) {

    $.ajax({
        url: "/api/v1/timesheet/get-timesheet",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({
            employeeId: $("#employee-id").val(),
            weekEnding: formatDateForBackend(currentWeekEnding)
        }),
        success: function(response) {

            if (response && response.entries && response.entries.length > 0) {
                const entry = response.entries[0];

                $("#monday-field").val(entry.monday ?? 0);
                $("#tuesday-field").val(entry.tuesday ?? 0);
                $("#wednesday-field").val(entry.wednesday ?? 0);
                $("#thursday-field").val(entry.thursday ?? 0);
                $("#friday-field").val(entry.friday ?? 0);
                $("#saturday-field").val(entry.saturday ?? 0);

            } else {
                clearAttendance();
            }

            /* Update button state */
            if (response.status === "SUBMITTED") {
                $("#save-attendance-btn").prop("disabled", true);
                $("#submit-attendance-btn").prop("disabled", true);
            } else {
                $("#save-attendance-btn").prop("disabled", false);
                $("#submit-attendance-btn").prop("disabled", false);
            }

            calculateTotal();
        },
        error: function(xhr) {
            console.error(xhr.responseText);
            clearAttendance();
            calculateTotal();
        }
    });
}

function clearAttendance() {
    $("#monday-field").val(0);
    $("#tuesday-field").val(0);
    $("#wednesday-field").val(0);
    $("#thursday-field").val(0);
    $("#friday-field").val(0);
    $("#saturday-field").val(0);
}

function calculateTotal() {

    const monday = parseFloat($("#monday-field").val()) || 0;
    const tuesday = parseFloat($("#tuesday-field").val()) || 0;
    const wednesday = parseFloat($("#wednesday-field").val()) || 0;
    const thursday = parseFloat($("#thursday-field").val()) || 0;
    const friday = parseFloat($("#friday-field").val()) || 0;
    const saturday = parseFloat($("#saturday-field").val()) || 0;

    const total =
        monday +
        tuesday +
        wednesday +
        thursday +
        friday +
        saturday;

    $("#total-hours-field").val(total);

    $("#total-monday").text(monday);
    $("#total-tuesday").text(tuesday);
    $("#total-wednesday").text(wednesday);
    $("#total-thursday").text(thursday);
    $("#total-friday").text(friday);
    $("#total-saturday").text(saturday);

    $("#grand-total").text(total);
}

function formatDateForBackend(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1)
        .padStart(2, "0");
    const day = String(date.getDate())
        .padStart(2, "0");

    return `${year}-${month}-${day}`;
}