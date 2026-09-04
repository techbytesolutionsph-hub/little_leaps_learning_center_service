const CALENDAR_START_TIME = "08:00";
const HOUR_HEIGHT = 43;

let currentWeekStart = getMonday(new Date());
let currentView = "week";
let schedules = [];
let selectedDate = getToday();

$(document).ready(function() {
    initializeDatePicker2("#assessment-therapy-date", "Select therapy date");
    initializeTimePicker2("#assessment-start-time", "Select therapy start time");
    initializeTimePicker2("#assessment-end-time", "Select therapy end time");

    initializeDatePicker2("#edit-assessment-therapy-date", "Select therapy date");
    initializeTimePicker2("#edit-assessment-start-time", "Select therapy start time");
    initializeTimePicker2("#edit-assessment-end-time", "Select therapy end time");

    $("#clientIdInput").on("input", function() {
        $(this).removeClass("is-invalid");
    });

    $("#editAssessmentScheduleModal").on("hide.bs.modal", function () {
        if (document.activeElement) {
            document.activeElement.blur();
        }
    });

    /* RENDER CALENDAR */
    function renderCalendar() {
        updateCurrentWeekText();

        if (currentView === "day") {
            renderDayCalendar();
        } else {
            renderWeekCalendar();
        }
    }

    /* WEEK CALENDAR */
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

        /* HEADER */
        html += `
            <div class="calendar-grid calendar-header-grid">
                <div class="calendar-header-cell time-header">
                    Time
                </div>
        `;

        weekDates.forEach(function(date) {
            const dateValue = formatISODate(date);
            const dayName = date.toLocaleDateString("en-US", {
                weekday: "short",
            });

            html += `
                <div class="calendar-header-cell" data-date="${dateValue}">
                    <div class="day-name">
                        ${dayName}
                    </div>

                    <div class="day-date">
                        ${formatShortDate(date)}
                    </div>
                </div>
            `;
        });

        html += `</div>`;

        /* BODY */
        html += `<div class="calendar-grid calendar-body-grid">`;

        /* TIME COLUMN */
        html += `<div class="time-column">`;

        times.forEach(function(time) {
            html += `
                <div class="calendar-cell time-cell">
                    ${formatTime(time)}
                </div>
            `;
        });

        html += `</div>`;

        /* DATE COLUMNS */
        weekDates.forEach(function(date) {
            const dateValue = formatISODate(date);
            html += `<div class="date-column" data-date="${dateValue}">`;

            times.forEach(function(time) {
                html += `
                    <div class="calendar-cell slot-cell" data-date="${dateValue}" data-time="${time}">
                        <span class="plus-slot">
                            +
                        </span>
                    </div>
                `;
            });

            schedules.filter(function(schedule) {
                    return schedule.date === dateValue;
                })
                .forEach(function(schedule) {
                    html += createDateScheduleHTML(schedule);
                });

            html += `</div>`;
        });

        html += `</div>`;

        $("#calendar").html(html);

        bindCalendarEvents();
    }

    /* DAY CALENDAR */
    function renderDayCalendar() {
        const day = selectedDate || getToday();

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

        /* HEADER */
        html += `
            <div class="calendar-grid calendar-header-grid" style="grid-template-columns: 74px minmax(220px, 1fr);">
                <div class="calendar-header-cell time-header">
                    Time
                </div>

                <div class="calendar-header-cell" data-date="${dateValue}">
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

        /* BODY */
        html += `
            <div class="calendar-grid calendar-body-grid" style="grid-template-columns: 74px minmax(220px, 1fr);">
                <div class="time-column">
        `;

        times.forEach(function(time) {
            html += `
                <div class="calendar-cell time-cell">
                    ${formatTime(time)}
                </div>
            `;
        });

        html += `
                </div>
                    <div class="date-column" data-date="${dateValue}">
        `;

        /* HOURLY CELLS */
        times.forEach(function(time) {
            html += `
                <div class="calendar-cell slot-cell" data-date="${dateValue}" data-time="${time}">
                    <span class="plus-slot"> + </span>
                </div>
            `;
        });

        /* SCHEDULES FOR THIS DATE */
        schedules.filter(function(schedule) {
            return schedule.date === dateValue;
        })
            .forEach(function(schedule) {
                html += createDateScheduleHTML(schedule);
            });

        html += `
                </div>
            </div>
        `;

        $("#calendar").html(html);

        bindCalendarEvents();
    }

    /* CREATE ONE SCHEDULE BOX */
    function createDateScheduleHTML(schedule) {
        const top = calculateScheduleTop(schedule.start);
        const height = calculateScheduleHeight(schedule.start, schedule.end);

        return `
            <div class=" schedule-item appointment-${escapeHtml(schedule.status)}" data-id="${schedule.id}"
                data-date="${schedule.date}" style=" top: ${top}px; height: ${height}px;">

                <div class="appointment-title">
                    ${formatTime(schedule.start)} - ${formatTime(schedule.end)}
                </div>
                
                <div class="appointment-therapist">
                    ${escapeHtml(schedule.therapist)}
                </div>

                <div class="appointment-menu">
                    <i class=" bi bi-three-dots-vertical"></i>
                </div>
            </div>
        `;
    }

    /* CALENDAR EVENTS */
    function bindCalendarEvents() {
        /* EMPTY SLOT */
        $(".slot-cell").off("click").on("click", function(event) {
            if ($(event.target).closest(".schedule-item").length) {
                return;
            }

            const date = $(this).data("date");
            const time = $(this).data("time");

            openAddScheduleForm(date, time);
        });

        /* SCHEDULE */
        $(".schedule-item").off("click").on("click", function(event) {
            event.stopPropagation();

            const scheduleId = $(this).data("id");
            const schedule = schedules.find(function(item) {
                return String(item.id) === String(scheduleId);
            });

            if (schedule) {
                console.log(schedule);
                $("#editAssessmentScheduleModal").modal("show");
                $("#edit-assessment-id").val(schedule.id);

                $("#edit-assessment-status option").filter(function() {
                    return $(this).text().trim().toLowerCase() ===
                        schedule.status.trim().toLowerCase();
                }).prop("selected", true);

                $("#edit-assessment-day").val(getDayFromDate(schedule.day));
                $("#edit-assessment-therapy-date")[0]._flatpickr.setDate(schedule.date, true);
                $("#edit-assessment-start-time")[0]._flatpickr.setDate(schedule.start, true);
                $("#edit-assessment-end-time")[0]._flatpickr.setDate(schedule.end, true);
                $("#edit-assessment-notes").val(schedule.notes || "");
            }
        });
    }

    /* ADD SCHEDULE BUTTON */
    $("#addScheduleButton").on("click", function() {
        $("#addAssessmentScheduleModal").modal("show");
        $("#assessment-therapist option").filter(function() {
            return $(this).text().trim() === therapistFullName.trim();
        }).prop("selected", true);
    });

    function openAddScheduleForm(date = null, time = null) {
        $("#addAssessmentScheduleModal").modal("show");

        $("#assessment-day").val(getDayFromDate(date));
        $("#assessment-therapy-date")[0]._flatpickr.setDate(date, true);
        $("#assessment-start-time")[0]._flatpickr.setDate(time, true);
    }

    /* WEEK NAVIGATION */
    $("#previousWeek").on("click", function() {
        currentWeekStart = addDays(currentWeekStart, -7);

        if (currentView === "day") {
            selectedDate = addDays(selectedDate, -7);
        }

        renderCalendar();
    });

    $("#nextWeek").on("click", function () {
        currentWeekStart = addDays(currentWeekStart, 7);

        if (currentView === "day") {
            selectedDate = addDays(selectedDate, 7);
        }

        renderCalendar();
    });

    function addDays(date, days) {
        const result = new Date(date);
        result.setDate(result.getDate() + days);
        return result;
    }

    $("#todayButton").on("click", function() {
        currentWeekStart = getMonday(new Date());
        renderCalendar();
    });

    /* VIEW SWITCH  */
    $("#weekViewButton").on("click", function() {
        currentView = "week";
        $("#weekViewButton").addClass("active");
        $("#dayViewButton").removeClass("active");
        renderCalendar();
    });

    $("#dayViewButton").on("click", function() {
        currentView = "day";
        $("#dayViewButton").addClass("active");
        $("#weekViewButton").removeClass("active");
        renderCalendar();
    });

    function escapeHtml(value) {
        if (value === null || value === undefined) {
            return "";
        }

        return $("<div>").text(value).html();
    }

    $("#addAssessmentScheduleForm").on("submit", function(event) {
        event.preventDefault();

        if (!validateAssessmentScheduleForm()) { return; }

        const request = buildAddTherapySlotRequest();
        console.log("Request:", request);

        saveTherapySlot(request);
    });

    $("#editAssessmentScheduleForm").on("submit", function(event) {
        event.preventDefault();

        if (!validateEditAssessmentScheduleForm()) { return; }

        const request = buildEditTherapySlotRequest();
        console.log("Request:", request);

        updateTherapySlot(request);
    });

    function buildAddTherapySlotRequest() {
        return {
            therapySessionId: therapy.therapySessionId,
            therapyDate: $("#assessment-therapy-date").val(),
            day: $("#assessment-day").val().toUpperCase(),
            startTime: $("#assessment-start-time").val(),
            endTime: $("#assessment-end-time").val(),
            status: $("#assessment-status").val(),
            notes: $("#assessment-notes").val()
        };
    }

    function buildEditTherapySlotRequest() {
        return {
            id: $("#edit-assessment-id").val(),
            therapySessionId: therapy.therapySessionId,
            therapyDate: $("#edit-assessment-therapy-date").val(),
            day: $("#edit-assessment-day").val().toUpperCase(),
            startTime: $("#edit-assessment-start-time").val(),
            endTime: $("#edit-assessment-end-time").val(),
            status: $("#edit-assessment-status").val(),
            notes: $("#edit-assessment-notes").val()
        };
    }

    function convertTherapySessionToSchedules(response) {
        if (!response || !Array.isArray(response.scheduleSlots)) {
            return [];
        }

        return response.scheduleSlots.map((slot) => ({
            id: slot.id,
            therapist: response.behavioralTherapistFullName || "",
            date: slot.therapyDate,
            day: slot.day,
            start: slot.startTime.substring(0, 5),
            end: slot.endTime.substring(0, 5),
            status: slot.status
                ? slot.status.toLowerCase()
                : "",
            notes: slot.notes
        }));
    }

    function saveTherapySlot(request) {

        $.ajax({
            url: "/api/v1/assessment/save-therapy-slot",
            type: "POST",
            contentType: "application/json",
            dataType: "json",
            data: JSON.stringify(request),
            success: function(response) {
                console.log("Therapy slot saved successfully:", response);
                $("#addAssessmentScheduleModal").modal("hide");
                resetAssessmentScheduleForm();

                location.reload();
            },

            error: function(xhr) {
                console.error("Failed to save therapy slot:", xhr);
                if (xhr.responseJSON) {
                    console.error("Error response:", xhr.responseJSON);
                }
            }
        });
    }

    function updateTherapySlot(request) {

        $.ajax({
            url: "/api/v1/assessment/update-therapy-slot",
            type: "PUT",
            contentType: "application/json",
            dataType: "json",
            data: JSON.stringify(request),
            success: function(response) {
                console.log("Therapy slot saved successfully:", response);
                $("#addAssessmentScheduleModal").modal("hide");
                resetAssessmentScheduleForm();

                location.reload();
            },

            error: function(xhr) {
                console.error("Failed to save therapy slot:", xhr);
                if (xhr.responseJSON) {
                    console.error("Error response:", xhr.responseJSON);
                }
            }
        });
    }

    /* INITIAL DEMO DATA */
    schedules = convertTherapySessionToSchedules(therapy);

    /* INITIALIZE */
    currentWeekStart = getMonday(new Date());

    renderCalendar();

    /* FORM */

    const $form = $("#addAssessmentScheduleForm");
    const $therapist = $("#assessment-therapist");
    const $therapyDate = $("#assessment-therapy-date");
    const $day = $("#assessment-day");
    const $startTime = $("#assessment-start-time");
    const $endTime = $("#assessment-end-time");
    const $status = $("#assessment-status");

    const $editTherapist = $("#edit-assessment-therapist");
    const $editTherapyDate = $("#edit-assessment-therapy-date");
    const $editTherapyDay = $("#edit-assessment-day");
    const $editTherapyStartTime = $("#edit-assessment-start-time");
    const $editTherapyEndTime = $("#edit-assessment-end-time");
    const $editStatus = $("#edit-assessment-status");

    /* UPDATE DAY */
    $therapyDate.on("change", function() {
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

    $editTherapyDate.on("change", function() {
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

    /* VALIDATE TIME */
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

    function validateAssessmentScheduleForm() {

        clearAllFieldErrors();

        let isValid = true;

        if (!$therapist.val()) {
            setFieldError($("#assessment-therapist"), "Therapist is required.");
            isValid = false;
        }

        /* Therapy date */
        if (!$therapyDate.val()) {
            setFieldError($therapyDate, "Therapy date is required.");
            isValid = false;
        }

        /* Therapy day */
        if (!$day.val()) {
            setFieldError($("#assessment-day"), "Therapist day is required.");
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

        /* Status */
        if (!$status.val()) {
            setFieldError($status, "Status is required.");
            isValid = false;
        }

        /* Time comparison */
        if ($startTime.val() && $endTime.val()) {
            if (!validateTime()) {
                isValid = false;
            }
        }
        return isValid;
    }

    $startTime.on("change", validateTime);
    $endTime.on("change", validateTime);

    /* VALIDATE TIME */
    function editValidateTime() {
        const start = $editTherapyStartTime.val();
        const end = $editTherapyEndTime.val();

        if (!start || !end) {
            return true;
        }

        if (start >= end) {
            setFieldError($editTherapyEndTime, "End time must be later than start time.");
            return false;
        }

        clearFieldError($editTherapyEndTime);

        return true;
    }

    function validateEditAssessmentScheduleForm() {

        clearAllFieldErrors();

        let isValid = true;

        if (!$editTherapist.val()) {
            setFieldError($("#edit-assessment-therapist"), "Therapist is required.");
            isValid = false;
        }

        /* Therapy date */
        if (!$editTherapyDate.val()) {
            setFieldError($therapyDate, "Therapy date is required.");
            isValid = false;
        }

        /* Therapy day */
        if (!$editTherapyDay.val()) {
            setFieldError($("#edit-assessment-day"), "Therapist day is required.");
            isValid = false;
        }

        /* Start time */
        if (!$editTherapyStartTime.val()) {
            setFieldError($editTherapyStartTime, "Start time is required.");
            isValid = false;
        }

        /* End time */
        if (!$editTherapyEndTime.val()) {
            setFieldError($editTherapyEndTime, "End time is required.");
            isValid = false;
        }

        /* Status */
        if (!$editStatus.val()) {
            setFieldError($editStatus, "Status is required.");
            isValid = false;
        }

        /* Time comparison */
        if ($editTherapyStartTime.val() && $editTherapyEndTime.val()) {
            if (!editValidateTime()) {
                isValid = false;
            }
        }
        return isValid;
    }

    $editTherapyEndTime.on("change", editValidateTime);
    $editTherapyStartTime.on("change", editValidateTime);

    /* FIELD ERROR HELPERS */
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

    /* RESET FORM */
    function resetAssessmentScheduleForm() {
        if ($form.length) {
            $form[0].reset();
        }

        $day.val("");

        clearAllFieldErrors();
    }

    /* RESET WHEN MODAL CLOSES */
    $("#addAssessmentScheduleModal").on("hidden.bs.modal", function() {
        resetAssessmentScheduleForm();
    });
});

/* DATE UTILITIES */
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

function getWeekDates() {
    const dates = [];

    for (let i = 0; i < 7; i++) {
        const date = new Date(currentWeekStart);
        date.setDate(currentWeekStart.getDate() + i);
        dates.push(date);
    }

    return dates;
}

function getDayFromDate(dateString) {
    console.log("Day : " + dateString);

    if (!dateString) {
        return "";
    }

    const days = [
        "SUNDAY",
        "MONDAY",
        "TUESDAY",
        "WEDNESDAY",
        "THURSDAY",
        "FRIDAY",
        "SATURDAY"
    ];

    // Already a day name
    if (days.includes(dateString.toUpperCase())) {
        return dateString.charAt(0).toUpperCase() + dateString.slice(1).toLowerCase();
    }

    // Otherwise, treat it as a date
    const date = new Date(dateString + "T00:00:00");

    if (isNaN(date.getTime())) {
        return "";
    }

    const day = date.toLocaleDateString("en-US", {
        weekday: "long"
    });

    return day.charAt(0).toUpperCase() + day.slice(1).toLowerCase();
}

/* TIME UTILITIES */
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

/* CALENDAR HEADER */
function updateCurrentWeekText() {
    const weekDates = getWeekDates();
    const start = weekDates[0];
    const end = weekDates[6];

    $("#currentWeek").html(
        `<i class="bi bi-calendar3"></i> ${formatShortDate(start)} - ${formatShortDate(end)}, ${end.getFullYear()}`
    );
}