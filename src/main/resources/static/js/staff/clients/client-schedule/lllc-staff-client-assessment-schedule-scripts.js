$(document).ready(function () {
    /* =========================================================
           STATE
      ========================================================= */

    let selectedClient = null;
    let currentWeekStart = getMonday(new Date());
    let selectedDate = null;
    let selectedTime = null;
    let currentView = "week";
    let schedules = [];

    /* =========================================================
           CALENDAR CONFIGURATION
      ========================================================= */

    const CALENDAR_START_TIME = "08:00";
    const CALENDAR_END_TIME = "20:00";

    /*
     * Must match:
     *
     * .calendar-cell {
     *     height: 43px;
     * }
     */
    const HOUR_HEIGHT = 43;

    /* =========================================================
           MOCK CLIENT
           Replace this with your AJAX/API call.
      ========================================================= */

    const mockClients = {
        "CLT-00001": {
            profileImageUrl:
                "https://res.cloudinary.com/fe7zqjnd/image/upload/v1787050879/b1h0wgsufoy2ewzdkqwu.jpg",

            clientId: "CLT-00001",

            clientName: "Cotton Trampe Anduiza",

            gender: "Female",

            programType: "ABA",

            guardian: "Juan Dela Cruz",

            branch: "Catanauan Branch",

            dateOfBirth: "March 12, 2018",

            contactNo: "0917 123 4567",

            email: "juan.delacruz@email.com",

            address: "Brgy. Poblacion, Catanauan",
        },
    };

    /* =========================================================
           CLIENT SEARCH
      ========================================================= */

    $("#searchClientButton").on("click", function () {
        const assignmentId = $("#assignmentIdInput").val().trim().toUpperCase();

        getClientByClientID(assignmentId);
    });

    $("#clientIdInput").on("keydown", function (event) {
        if (event.key === "Enter") {
            event.preventDefault();

            searchClient();
        }
    });

    function getClientByClientID(assignmentId) {
        if (!assignmentId) {
            showClientError("Please enter an assignment ID.");

            return;
        }

        $.ajax({
            url:
                "/api/v1/client/get-assign-client?id=" +
                encodeURIComponent(assignmentId),

            type: "GET",

            success: function (response) {
                console.log("Assigned Client response:", response);

                selectedClient = response;

                renderClient(response);
            },

            error: function (xhr) {
                console.error("Failed to retrieve client:", xhr);

                selectedClient = null;

                $("#clientProfile").removeClass("visible");

                if (xhr.status === 404) {
                    showErrorPopup("Error", "Client not found.");
                } else {
                    showErrorPopup("Error", "Unable to retrieve client information.");
                }
            },
        });
    }

    function searchClient() {
        const clientId = $("#clientIdInput").val().trim().toUpperCase();

        if (!clientId) {
            showClientError("Please enter a client ID.");

            return;
        }

        const client = mockClients[clientId];

        if (!client) {
            selectedClient = null;

            $("#clientProfile").removeClass("visible");

            showClientError("Client not found.");

            return;
        }

        selectedClient = client;

        renderClient(client);
    }

    /* =========================================================
           RENDER CLIENT
      ========================================================= */

    function renderClient(client) {
        $("#clientAvatar")
            .attr(
                "src",
                client.clientProfilePicture ||
                client.profileImageUrl ||
                "/images/default-avatar.png",
            )
            .attr("alt", client.clientFullName || client.clientName || "Client");

        $("#clientName").text(client.clientFullName || client.clientName || "-");

        $("#clientIdValue").text(client.clientId || "-");

        $("#clientProgramType").text(client.programType || "-");

        $("#clientDateOfBirth").text(
            formatDate(client.clientBirthDate || client.dateOfBirth),
        );

        $("#clientGender").text(
            formatEnumValue(client.clientGender || client.gender),
        );

        $("#clientGuardian").text(
            client.guardianFullName || client.guardian || "-",
        );

        $("#clientEmail").text(client.guardianEmail || client.email || "-");

        $("#clientContactNo").text(
            client.guardianContactNo || client.contactNo || "-",
        );

        $("#clientStatus").text(formatStatus(client.status));

        $("#clientBranch").text(client.branch || "-");

        $("#clientAddress").text(client.address || "-");

        $("#clientProfile").addClass("visible");
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

    function formatStatus(status) {
        if (!status) {
            return "-";
        }

        return status.toLowerCase().replace(/\b\w/g, function (letter) {
            return letter.toUpperCase();
        });
    }

    function formatDate(dateValue) {
        if (!dateValue) {
            return "-";
        }

        /*
         * Already formatted date.
         */
        if (typeof dateValue === "string" && dateValue.includes(" ")) {
            return dateValue;
        }

        const date = new Date(`${dateValue}T00:00:00`);

        if (isNaN(date.getTime())) {
            return "-";
        }

        return date.toLocaleDateString("en-US", {
            year: "numeric",
            month: "long",
            day: "numeric",
        });
    }

    function showClientError(message) {
        $("#clientIdInput").addClass("is-invalid").focus();

        showNotification(message);
    }

    $("#clientIdInput").on("input", function () {
        $(this).removeClass("is-invalid");
    });

    /* =========================================================
           DATE UTILITIES
      ========================================================= */

    function getMonday(date) {
        const result = new Date(date);

        const day = result.getDay();

        const difference = day === 0 ? -6 : 1 - day;

        result.setDate(result.getDate() + difference);

        result.setHours(0, 0, 0, 0);

        return result;
    }

    function getToday() {
        const result = new Date();

        result.setHours(0, 0, 0, 0);

        return result;
    }

    function formatISODate(date) {
        const year = date.getFullYear();

        const month = String(date.getMonth() + 1).padStart(2, "0");

        const day = String(date.getDate()).padStart(2, "0");

        return `${year}-${month}-${day}`;
    }

    function parseDate(dateString) {
        return new Date(dateString + "T00:00:00");
    }

    function formatShortDate(date) {
        return date.toLocaleDateString("en-US", {
            month: "short",
            day: "numeric",
        });
    }

    function formatTime(time) {
        if (!time) {
            return "-";
        }

        const parts = time.split(":");

        const hour = parseInt(parts[0], 10);

        const minute = parts[1] || "00";

        const suffix = hour >= 12 ? "PM" : "AM";

        const displayHour = hour % 12 || 12;

        return `${displayHour}:${minute} ${suffix}`;
    }

    function addHour(time) {
        const parts = time.split(":");

        let hour = parseInt(parts[0], 10);

        const minute = parts[1] || "00";

        hour++;

        if (hour > 23) {
            hour = 23;
        }

        return String(hour).padStart(2, "0") + ":" + minute;
    }

    function getWeekDates() {
        const dates = [];

        for (let i = 0; i < 7; i++) {
            const date = new Date(currentWeekStart);

            date.setDate(currentWeekStart.getDate() + i);

            dates.push(date);
        }

        return dates;
    }

    /* =========================================================
           TIME UTILITIES
      ========================================================= */

    function timeToMinutes(time) {
        if (!time) {
            return 0;
        }

        const parts = time.split(":");

        const hours = parseInt(parts[0], 10);

        const minutes = parseInt(parts[1] || "0", 10);

        return hours * 60 + minutes;
    }

    /*
     * Determine vertical position of schedule.
     *
     * Calendar starts at 08:00.
     *
     * 08:00 = 0px
     * 09:00 = 43px
     * 10:00 = 86px
     * 11:00 = 129px
     */
    function calculateScheduleTop(startTime) {
        const calendarStartMinutes = timeToMinutes(CALENDAR_START_TIME);

        const scheduleStartMinutes = timeToMinutes(startTime);

        const minutesFromStart = scheduleStartMinutes - calendarStartMinutes;

        return (minutesFromStart / 60) * HOUR_HEIGHT;
    }

    /*
     * Determine schedule height.
     *
     * 08:00 - 09:00 = 43px
     * 08:00 - 10:00 = 86px
     * 08:00 - 12:00 = 172px
     */
    function calculateScheduleHeight(startTime, endTime) {
        const startMinutes = timeToMinutes(startTime);

        const endMinutes = timeToMinutes(endTime);

        const durationMinutes = endMinutes - startMinutes;

        return (durationMinutes / 60) * HOUR_HEIGHT;
    }

    /* =========================================================
           CALENDAR HEADER
      ========================================================= */

    function updateCurrentWeekText() {
        const weekDates = getWeekDates();

        const start = weekDates[0];

        const end = weekDates[6];

        $("#currentWeek").text(
            `${formatShortDate(start)} - ${formatShortDate(end)}, ${end.getFullYear()}`,
        );
    }

    /* =========================================================
           RENDER CALENDAR
      ========================================================= */

    function renderCalendar() {
        updateCurrentWeekText();

        if (currentView === "day") {
            renderDayCalendar();
        } else {
            renderWeekCalendar();
        }
    }

    /* =========================================================
           WEEK CALENDAR
      ========================================================= */

    function renderWeekCalendar() {
        const weekDates = getWeekDates();

        const times = [
            "08:00",
            "09:00",
            "10:00",
            "11:00",
            "12:00",
            "13:00",
            "14:00",
            "15:00",
            "16:00",
            "17:00",
            "18:00",
            "19:00",
        ];

        let html = "";

        /* =====================================================
                 HEADER
            ===================================================== */

        html += `
            <div class="calendar-grid calendar-header-grid">

                <div class="calendar-header-cell time-header">
                    Time
                </div>
        `;

        weekDates.forEach(function (date) {
            const dateValue = formatISODate(date);

            const dayName = date.toLocaleDateString("en-US", {
                weekday: "short",
            });

            html += `
                <div
                    class="calendar-header-cell"
                    data-date="${dateValue}"
                >

                    <div class="day-name">
                        ${dayName}
                    </div>

                    <div class="day-date">
                        ${formatShortDate(date)}
                    </div>

                </div>
            `;
        });

        html += `
            </div>
        `;

        /* =====================================================
                 BODY
            ===================================================== */

        html += `
            <div class="calendar-grid calendar-body-grid">
        `;

        /* =====================================================
                 TIME COLUMN
            ===================================================== */

        html += `
            <div class="time-column">
        `;

        times.forEach(function (time) {
            html += `
                <div class="calendar-cell time-cell">
                    ${formatTime(time)}
                </div>
            `;
        });

        html += `
            </div>
        `;

        /* =====================================================
                 DATE COLUMNS
            ===================================================== */

        weekDates.forEach(function (date) {
            const dateValue = formatISODate(date);

            /*
             * IMPORTANT:
             *
             * Each date gets ONE column.
             *
             * Schedules for that date are rendered
             * inside this column.
             */
            html += `
                <div
                    class="date-column"
                    data-date="${dateValue}"
                >
            `;

            /* ---------------------------------------------
                     Hour cells
                  --------------------------------------------- */

            times.forEach(function (time) {
                html += `
                    <div
                        class="calendar-cell slot-cell"
                        data-date="${dateValue}"
                        data-time="${time}"
                    >
                        <span class="plus-slot">
                            +
                        </span>
                    </div>
                `;
            });

            /* ---------------------------------------------
                     Schedules for THIS DATE
                  --------------------------------------------- */

            schedules
                .filter(function (schedule) {
                    return schedule.date === dateValue;
                })
                .forEach(function (schedule) {
                    html += createDateScheduleHTML(schedule);
                });

            html += `
                </div>
            `;
        });

        html += `
            </div>
        `;

        $("#calendar").html(html);

        bindCalendarEvents();
    }

    /* =========================================================
           DAY CALENDAR
      ========================================================= */

    function renderDayCalendar() {
        const day = getToday();

        const times = [
            "08:00",
            "09:00",
            "10:00",
            "11:00",
            "12:00",
            "13:00",
            "14:00",
            "15:00",
            "16:00",
            "17:00",
            "18:00",
            "19:00",
        ];

        const dateValue = formatISODate(day);

        let html = "";

        /* =====================================================
                 HEADER
            ===================================================== */

        html += `
            <div
                class="calendar-grid calendar-header-grid"
                style="
                    grid-template-columns:
                    74px
                    minmax(220px, 1fr);
                "
            >

                <div class="calendar-header-cell time-header">
                    Time
                </div>

                <div
                    class="calendar-header-cell"
                    data-date="${dateValue}"
                >

                    <div class="day-name">
                        ${day.toLocaleDateString("en-US", {
            weekday: "long",
        })}
                    </div>

                    <div class="day-date">
                        ${formatShortDate(day)}
                    </div>

                </div>

            </div>
        `;

        /* =====================================================
                 BODY
            ===================================================== */

        html += `
            <div
                class="calendar-grid calendar-body-grid"
                style="
                    grid-template-columns:
                    74px
                    minmax(220px, 1fr);
                "
            >

                <div class="time-column">
        `;

        times.forEach(function (time) {
            html += `
                <div class="calendar-cell time-cell">
                    ${formatTime(time)}
                </div>
            `;
        });

        html += `
                </div>

                <div
                    class="date-column"
                    data-date="${dateValue}"
                >
        `;

        /* =====================================================
                 HOURLY CELLS
            ===================================================== */

        times.forEach(function (time) {
            html += `
                <div
                    class="calendar-cell slot-cell"
                    data-date="${dateValue}"
                    data-time="${time}"
                >
                    <span class="plus-slot">
                        +
                    </span>
                </div>
            `;
        });

        /* =====================================================
                 SCHEDULES FOR THIS DATE
            ===================================================== */

        schedules
            .filter(function (schedule) {
                return schedule.date === dateValue;
            })
            .forEach(function (schedule) {
                html += createDateScheduleHTML(schedule);
            });

        html += `
                </div>
            </div>
        `;

        $("#calendar").html(html);

        bindCalendarEvents();
    }

    /* =========================================================
           CREATE ONE SCHEDULE BOX
      ========================================================= */

    function createDateScheduleHTML(schedule) {
        const top = calculateScheduleTop(schedule.start);

        const height = calculateScheduleHeight(schedule.start, schedule.end);

        return `
            <div
                class="
                    schedule-item
                    appointment-${escapeHtml(schedule.status)}
                "

                data-id="${schedule.id}"

                data-date="${schedule.date}"

                style="
                    top: ${top}px;
                    height: ${height}px;
                "
            >

                <div class="appointment-title">

                    ${formatTime(schedule.start)}

                    -

                    ${formatTime(schedule.end)}

                </div>


                <div class="appointment-therapist">

                    ${escapeHtml(schedule.therapist)}

                </div>


                <div class="appointment-menu">

                    <i class="
                        bi
                        bi-three-dots-vertical
                    "></i>

                </div>

            </div>
        `;
    }

    /* =========================================================
           CALENDAR EVENTS
      ========================================================= */

    function bindCalendarEvents() {
        /* =====================================================
                 EMPTY SLOT
            ===================================================== */

        $(".slot-cell")
            .off("click")
            .on("click", function (event) {
                /*
                 * Ignore click if schedule
                 * somehow overlaps the cell.
                 */
                if ($(event.target).closest(".schedule-item").length) {
                    return;
                }

                const date = $(this).data("date");

                const time = $(this).data("time");

                openScheduleForm(date, time);
            });

        /* =====================================================
                 SCHEDULE
            ===================================================== */

        $(".schedule-item")
            .off("click")
            .on("click", function (event) {
                event.stopPropagation();

                const scheduleId = $(this).data("id");

                const schedule = schedules.find(function (item) {
                    return String(item.id) === String(scheduleId);
                });

                if (schedule) {
                    showScheduleDetails(schedule);
                }
            });
    }

    /* =========================================================
           ADD SCHEDULE BUTTON
      ========================================================= */

    $("#addScheduleButton").on("click", function () {
        $("#addAssessmentScheduleModal").modal("show");
    });

    function openScheduleForm(date = null, time = null) {
        if (!selectedClient) {
            showNotification("Please search for a client first.");

            $("#clientIdInput").focus();

            return;
        }

        selectedDate = date || formatISODate(new Date());

        selectedTime = time || "08:00";

        /*
         * Set values in your actual modal.
         */

        if ($("#assessment-therapy-date").length) {
            $("#assessment-therapy-date").val(selectedDate).trigger("change");
        }

        if ($("#assessment-start-time").length) {
            $("#assessment-start-time").val(selectedTime);
        }

        if ($("#assessment-end-time").length) {
            $("#assessment-end-time").val(addHour(selectedTime));
        }

        /*
         * Uncomment if you want clicking
         * an empty calendar cell to open modal.
         *
         * $("#addAssessmentScheduleModal")
         *     .modal("show");
         */

        showNotification(
            `Selected ${formatShortDate(
                parseDate(selectedDate),
            )} at ${formatTime(selectedTime)}`,
        );
    }

    /* =========================================================
           SCHEDULE DETAILS
      ========================================================= */

    function showScheduleDetails(schedule) {
        const details = [
            `Therapist: ${schedule.therapist}`,

            `Date: ${formatShortDate(parseDate(schedule.date))}`,

            `Time: ${formatTime(schedule.start)} - ${formatTime(schedule.end)}`,

            `Status: ${capitalize(schedule.status)}`,
        ];

        /*
         * Replace with your edit modal later.
         */

        const shouldDelete = window.confirm(
            details.join("\n") + "\n\nClick OK to delete this schedule.",
        );

        if (!shouldDelete) {
            return;
        }

        schedules = schedules.filter(function (item) {
            return item.id !== schedule.id;
        });

        renderCalendar();

        showNotification("Schedule deleted successfully.");
    }

    /* =========================================================
           WEEK NAVIGATION
      ========================================================= */

    $("#previousWeek").on("click", function () {
        currentWeekStart.setDate(currentWeekStart.getDate() - 7);

        renderCalendar();
    });

    $("#nextWeek").on("click", function () {
        currentWeekStart.setDate(currentWeekStart.getDate() + 7);

        renderCalendar();
    });

    $("#todayButton").on("click", function () {
        currentWeekStart = getMonday(new Date());

        renderCalendar();
    });

    /* =========================================================
           VIEW SWITCH
      ========================================================= */

    $("#weekViewButton").on("click", function () {
        currentView = "week";

        $("#weekViewButton").addClass("active");

        $("#dayViewButton").removeClass("active");

        renderCalendar();
    });

    $("#dayViewButton").on("click", function () {
        currentView = "day";

        $("#dayViewButton").addClass("active");

        $("#weekViewButton").removeClass("active");

        renderCalendar();
    });

    /* =========================================================
           NOTIFICATION
      ========================================================= */

    function showNotification(message) {
        const $toast = $("<div></div>").text(message).css({
            position: "fixed",

            right: "20px",

            bottom: "20px",

            zIndex: 9999,

            padding: "10px 15px",

            background: "#ffffff",

            border: "1px solid #dce4ec",

            borderLeft: "4px solid #079ca5",

            borderRadius: "6px",

            boxShadow: "0 5px 18px rgba(0,0,0,.12)",

            color: "#50617a",

            fontSize: "11px",
        });

        $("body").append($toast);

        setTimeout(function () {
            $toast.fadeOut(200, function () {
                $(this).remove();
            });
        }, 2500);
    }

    /* =========================================================
           HELPERS
      ========================================================= */

    function capitalize(value) {
        if (!value) {
            return "";
        }

        return value.charAt(0).toUpperCase() + value.slice(1);
    }

    function escapeHtml(value) {
        if (value === null || value === undefined) {
            return "";
        }

        return $("<div>").text(value).html();
    }

    /* =========================================================
           INITIAL DEMO DATA
      ========================================================= */

    schedules = [
        {
            id: 1,

            therapist: "Therapist A",

            date: "2026-08-24",

            start: "08:00",

            end: "09:00",

            status: "scheduled",
        },

        {
            id: 2,

            therapist: "Therapist A",

            date: "2026-08-24",

            start: "10:00",

            end: "12:00",

            status: "scheduled",
        },

        {
            id: 3,

            therapist: "Therapist A",

            date: "2026-08-26",

            start: "08:00",

            end: "10:00",

            status: "scheduled",
        },

        {
            id: 4,

            therapist: "Therapist A",

            date: "2026-08-26",

            start: "10:00",

            end: "12:00",

            status: "scheduled",
        },

        {
            id: 5,

            therapist: "Therapist B",

            date: "2026-08-28",

            start: "08:00",

            end: "10:00",

            status: "scheduled",
        },

        {
            id: 6,

            therapist: "Therapist B",

            date: "2026-08-29",

            start: "08:00",

            end: "10:00",

            status: "scheduled",
        },

        {
            id: 7,

            therapist: "Therapist B",

            date: "2026-08-29",

            start: "10:00",

            end: "12:00",

            status: "scheduled",
        },
    ];

    /* =========================================================
           INITIALIZE
      ========================================================= */

    currentWeekStart = getMonday(new Date());

    renderCalendar();

    /* =========================================================
           FORM
      ========================================================= */

    const $form = $("#addAssessmentScheduleForm");

    const $therapyDate = $("#assessment-therapy-date");

    const $day = $("#assessment-day");

    const $startTime = $("#assessment-start-time");

    const $endTime = $("#assessment-end-time");

    /* =========================================================
           UPDATE DAY
      ========================================================= */

    $therapyDate.on("change", function () {
        const selectedDate = $(this).val();

        if (!selectedDate) {
            $day.val("");

            return;
        }

        const [year, month, day] = selectedDate.split("-").map(Number);

        const date = new Date(year, month - 1, day);

        const dayName = date.toLocaleDateString("en-US", {
            weekday: "long",
        });

        $day.val(dayName);
    });

    /* =========================================================
           VALIDATE TIME
      ========================================================= */

    function validateTime() {
        const start = $startTime.val();

        const end = $endTime.val();

        if (!start || !end) {
            return true;
        }

        if (start >= end) {
            setFieldError($endTime, "End time must be later than start time.");

            return false;
        }

        clearFieldError($endTime);

        return true;
    }

    $startTime.on("change", validateTime);

    $endTime.on("change", validateTime);

    /* =========================================================
           FORM SUBMIT
      ========================================================= */

    $form.on("submit", function (event) {
        event.preventDefault();

        clearAllFieldErrors();

        let isValid = true;

        /* Frequency */

        if (!$("#assessment-frequency").val()) {
            setFieldError(
                $("#assessment-frequency"),
                "Session frequency is required.",
            );

            isValid = false;
        }

        /* Status */

        if (!$("#assessment-status").val()) {
            setFieldError($("#assessment-status"), "Schedule status is required.");

            isValid = false;
        }

        /* Therapist */

        if (!$("#assessment-therapist").val()) {
            setFieldError($("#assessment-therapist"), "Therapist is required.");

            isValid = false;
        }

        /* Therapy date */

        if (!$therapyDate.val()) {
            setFieldError($therapyDate, "Therapy date is required.");

            isValid = false;
        }

        /* Start time */

        if (!$startTime.val()) {
            setFieldError($startTime, "Start time is required.");

            isValid = false;
        }

        /* End time */

        if (!$endTime.val()) {
            setFieldError($endTime, "End time is required.");

            isValid = false;
        }

        /* Time comparison */

        if ($startTime.val() && $endTime.val()) {
            if (!validateTime()) {
                isValid = false;
            }
        }

        if (!isValid) {
            return;
        }

        /* =================================================
                     REQUEST OBJECT
                ================================================= */

        const request = {
            frequency: $("#assessment-frequency").val(),

            status: $("#assessment-status").val(),

            therapistId: $("#assessment-therapist").val(),

            therapyDate: $therapyDate.val(),

            day: $day.val(),

            startTime: $startTime.val(),

            endTime: $endTime.val(),
        };

        console.log("Assessment Schedule Request:", request);

        /*
         * =================================================
         * SPRING BOOT API
         * =================================================
         *
         * Uncomment and change URL when ready.
         */

        /*
                $.ajax({

                    url:
                        "/api/v1/client/assessment-schedule",

                    type: "POST",

                    contentType:
                        "application/json",

                    data:
                        JSON.stringify(request),

                    success:
                        function (response) {

                            $("#addAssessmentScheduleModal")
                                .modal("hide");

                            loadAssessmentSchedules();
                        },

                    error:
                        function (xhr) {

                            console.error(xhr);
                        }
                });
                */

        console.log("Schedule ready to be submitted.");

        $("#addAssessmentScheduleModal").modal("hide");

        resetAssessmentScheduleForm();
    });

    /* =========================================================
           FIELD ERROR HELPERS
      ========================================================= */

    function setFieldError($field, message) {
        const $group = $field.closest(".assessment-form-group");

        $group.addClass("has-error");

        $group.find(".field-error").text(message).show();
    }

    function clearFieldError($field) {
        const $group = $field.closest(".assessment-form-group");

        $group.removeClass("has-error");

        $group.find(".field-error").hide();
    }

    function clearAllFieldErrors() {
        $(".assessment-form-group").removeClass("has-error");

        $(".assessment-form-group .field-error").hide();
    }

    /* =========================================================
           RESET FORM
      ========================================================= */

    function resetAssessmentScheduleForm() {
        if ($form.length) {
            $form[0].reset();
        }

        $day.val("");

        clearAllFieldErrors();
    }

    /* =========================================================
           RESET WHEN MODAL CLOSES
      ========================================================= */

    $("#addAssessmentScheduleModal").on("hidden.bs.modal", function () {
        resetAssessmentScheduleForm();
    });
});
