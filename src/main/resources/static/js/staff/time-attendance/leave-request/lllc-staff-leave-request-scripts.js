$(document).ready(function () {
    initializeDatePicker("#leave-start-date", "Select start date");
    initializeDatePicker("#leave-end-date", "Select end date");

    /* Comment character counter */
    $("#leave-comment").on("input", function() {
        $("#charCount").text($(this).val().length);
    });

    $("#leave-type").on("change", function() {
        const leaveType = $(this).val();
        const employeeId = $("#employee-id").val()
        console.log("Selected leave type:", leaveType);

        getAvailableLeaveBalance(employeeId, leaveType);
    });

    /* Absence Duration */
    $("#absence-duration").on("change", function() {
        updateLeaveDetails();
    });

    /* Date fields */
    $("#leave-start-date, #leave-end-date").on("change input blur", function() {
        setTimeout(function() {
            updateLeaveDetails();

            const startDateValue = $.trim($("#leave-start-date").val());
            const endDateValue = $.trim($("#leave-end-date").val());
            const employeeId = $("#employee-id").val();

            if (!startDateValue || !endDateValue) {
                return;
            }

            const startDate = parseDate(startDateValue);
            const endDate = parseDate(endDateValue);

            if (!startDate || !endDate || endDate < startDate) {
                return;
            }

            checkOverlappingLeave(employeeId, formatDateForApi(startDate), formatDateForApi(endDate))
                .then(function(hasOverlap) {
                    if (hasOverlap) {
                        console.log("Overlapping leave detected.");
                        showErrorPopup("Error", "You already requested time off period "
                            + "<b>" + formatDateForApi(startDate) + "</b>" + " - " + "<b>" + formatDateForApi(endDate) + "</b>"
                            + ". Please choose a different period.");

                        setTimeout(function() {
                            $("#leave-start-date")[0]._flatpickr.clear();
                            $("#leave-end-date")[0]._flatpickr.clear();
                        }, 100);
                    } else {
                        console.log("No overlapping leave.");
                    }
                })
                .catch(function(error) {
                    console.error("Error checking overlapping leave:", error);
                    showErrorPopup("Error", "Error checking overlapping leave.");
                });
        }, 100);
    });

    /* Submit Leave Request */
    $("#submit-leave").on("click", function() {
        const leaveRequest = getLeaveRequestJson();

        if (!leaveRequest) {
            return;
        }

        console.log(leaveRequest);
        saveLeaveRequest(leaveRequest)
    });

    /* Update Leave Details */
    function updateLeaveDetails() {
        const duration = $("#absence-duration").val();
        const startDateValue = $.trim($("#leave-start-date").val());
        const endDateValue = $.trim($("#leave-end-date").val());

        /* Half-day time fields */
        const $timeFields = $("#half-day-time-fields");
        const $startTime = $("#leave-start-time");
        const $endTime = $("#leave-end-time");

        if (duration === "HALF_DAY_MORNING") {
            $timeFields.show();
            $startTime.val("8:00 AM");
            $endTime.val("12:00 PM");
        } else if (duration === "HALF_DAY_AFTERNOON") {
            $timeFields.show();
            $startTime.val("1:00 PM");
            $endTime.val("5:00 PM");
        } else {
            $timeFields.hide();
            $startTime.val("8:00 AM");
            $endTime.val("5:00 PM");
        }

        /* Clear calculated fields if incomplete */
        if (!duration || !startDateValue || !endDateValue) {
            $("#requesting-leave").val("");
            $("#returning-to-work-on").val("");

            return;
        }

        /* Parse dates */
        const startDate = parseDate(startDateValue);
        const endDate = parseDate(endDateValue);

        if (!startDate || !endDate || endDate < startDate) {
            $("#requesting-leave").val("");
            $("#returning-to-work-on").val("");

            return;
        }

        /* Calculate working days */
        let workingDays = 0;
        const currentDate = new Date(startDate);

        while (currentDate <= endDate) {
            if (!isWeekend(currentDate)) {
                workingDays++;
            }

            currentDate.setDate(currentDate.getDate() + 1);
        }

        /* Requesting */
        let requestingDays = workingDays;

        if (duration === "HALF_DAY_MORNING" || duration === "HALF_DAY_AFTERNOON") {
            requestingDays = workingDays * 0.5;
        }

        $("#requesting-leave").val(requestingDays + (requestingDays === 1 ? " day" : " days"));

        /* Returning to Work On */
        getNextWorkingDay(endDate).then(function(returnDate) {
            console.log(returnDate);
            $("#returning-to-work-on").val(formatDate(returnDate));
        });
    }


    /* Format Leave Days */
    function formatLeaveDays(days) {
        if (days === 1) {
            return "1 day";
        }

        return days + " days";
    }

    function parseDate(value) {
        if (!value) {
            return null;
        }

        value = value.trim();

        if (value.includes("-")) {
            const parts = value.split("-");

            if (parts.length !== 3) {
                return null;
            }

            const year = parseInt(parts[0], 10);
            const month = parseInt(parts[1], 10) - 1;
            const day = parseInt(parts[2], 10);

            if (isNaN(year) || isNaN(month) || isNaN(day)) {
                return null;
            }

            const date = new Date(year, month, day);

            if (date.getFullYear() !== year || date.getMonth() !== month || date.getDate() !== day) {
                return null;
            }

            return date;
        }

        if (value.includes("/")) {
            const parts = value.split("/");

            if (parts.length !== 3) {
                return null;
            }

            const month = parseInt(parts[0], 10) - 1;
            const day = parseInt(parts[1], 10);
            const year = parseInt(parts[2], 10);

            if (isNaN(month) || isNaN(day) || isNaN(year)) {
                return null;
            }

            const date = new Date(year, month, day);

            if (date.getFullYear() !== year || date.getMonth() !== month || date.getDate() !== day) {
                return null;
            }
            return date;
        }
        return null;
    }

    /* Check Weekend */
    function isWeekend(date) {
        const day = date.getDay();
        return day === 0 || day === 6;
    }

    /* Get Next Working Day */
    /* Get Next Working Day */
    function getNextWorkingDay(date) {
        const nextDate = new Date(date);
        nextDate.setDate(nextDate.getDate() + 1);

        if (isWeekend(nextDate)) {
            return getNextWorkingDay(nextDate);
        }

        const dateString = formatDateForApi(nextDate);

        return isHoliday(dateString).then(function(holiday) {
            if (holiday) {
                return getNextWorkingDay(nextDate);
            }

            return nextDate;
        });
    }

    /* Format Date for API */
    function formatDateForApi(date) {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, "0");
        const day = String(date.getDate()).padStart(2, "0");

        return `${year}-${month}-${day}`;
    }

    /* Format Date */
    function formatDate(date) {
        const month = String(date.getMonth() + 1).padStart(2, "0");
        const day = String(date.getDate()).padStart(2, "0");
        const year = date.getFullYear();

        return `${month}/${day}/${year}`;
    }

    /* Required Field Validation */
    function validateRequiredFields() {
        let valid = true;
        let firstInvalid = null;

        $(".required-field").each(function() {
            const $field = $(this);
            const value = $.trim(
                $field.val() || ""
            );

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
            }

            else {
                $field.removeClass("field-invalid");
                $field
                    .closest("[class*='col-']")
                    .find(".field-error")
                    .first()
                    .removeClass("show");
            }
        });

        if (firstInvalid) {
            firstInvalid.trigger("focus");
        }

        return valid;
    }

    /* Remove Validation Error */
    $(".required-field").on("input change", function() {
            const $field = $(this);
            const value = $.trim($field.val() || "");

            const $visibleField =
                $field.hasClass("flatpickr-input") ?
                    $field.next(".form-control") :
                    $field;

            const $error = $visibleField
                .closest("[class*='col-']")
                .find(".field-error")
                .first();

            if (value) {
                $visibleField.removeClass("field-invalid");
                $error.removeClass("show");
            }
        }
    );

    /* Convert time to LocalTime format */
    function toLocalTime(value) {
        if (!value) {
            return null;
        }

        value = value.trim();
        const match = value.match(
            /^(\d{1,2}):(\d{2})\s*(AM|PM)$/i
        );

        if (!match) {
            return null;
        }

        let hour = parseInt(match[1], 10);
        const minute = match[2];
        const period = match[3].toUpperCase();

        if (period === "AM") {
            if (hour === 12) {
                hour = 0;
            }
        } else if (period === "PM") {
            if (hour !== 12) {
                hour += 12;
            }
        }

        return `${String(hour).padStart(2, "0")}:${minute}`;
    }


    /* Create Leave Request JSON */
    function getLeaveRequestJson() {
        if (!validateRequiredFields()) {
            return null;
        }

        const requestedDaysText = $("#requesting-leave").val() || "";
        const returningToWork = $("#returning-to-work-on").val() || "";

        const startDate = parseDate($("#leave-start-date").val());
        const endDate = parseDate($("#leave-end-date").val());
        const returningDate = parseDate(returningToWork);

        return {
            employeeId: $("#employee-id").val(),
            leaveType: $("#leave-type").val(),
            absenceDuration: $("#absence-duration").val(),
            startDate: startDate ? formatDateForApi(startDate) : null,
            endDate: endDate ? formatDateForApi(endDate) : null,
            startTime: toLocalTime($("#leave-start-time").val()),
            endTime: toLocalTime($("#leave-end-time").val()),
            requestedDays: parseFloat(requestedDaysText),
            returningToWorkOn: returningDate ? formatDateForApi(returningDate) : null,
            comment: $("#leave-comment").val() || null
        };
    }

    function checkOverlappingLeave(employeeId, startDate, endDate) {
        const params = new URLSearchParams({
            employeeId: employeeId,
            startDate: startDate,
            endDate: endDate
        });

        return fetch(
            `/api/v1/leave-request/check-overlapping-leave-dates?${params.toString()}`,
            {
                method: 'POST'
            }
        )
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to check overlapping leave dates.');
                }

                return response.json();
            });
    }

    function isHoliday(date) {
        return fetch(`/api/v1/ph-holiday/is-holiday?date=${encodeURIComponent(date)}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error: ${response.status}`);
                }
                return response.json();
            })
            .catch(error => {
                console.error('Failed to check holiday:', error);
                return false;
            });
    }

    function getAvailableLeaveBalance(employeeId, leaveType) {
        fetch(`/api/v1/leave-request/available-leave-balance?employeeId=${encodeURIComponent(employeeId)}&leaveType=${encodeURIComponent(leaveType)}`, {
            method: "POST"
        })
            .then(function(response) {
                if (!response.ok) {
                    throw new Error("Failed to get available leave balance.");
                }

                return response.json();
            })
            .then(function(balance) {
                console.log("Available leave balance:", balance);
                $("#available-balance").val(balance);
            })
            .catch(function(error) {
                console.error("Error getting available leave balance:", error);
                $("#available-balance").val("");
            });
    }

    function saveLeaveRequest(request) {

        fetch('/api/v1/leave-request/save-leave-request', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(request)
        })
            .then(function(response) {
                return response.json().then(function(result) {
                    if (!response.ok) {
                        throw new Error(result.message || 'Failed to save leave request.');
                    }
                    return result;
                });
            })
            .then(function(result) {
                console.log('Leave request saved:', result);
                showSuccessThenRedirectPopup(
                    "Success",
                    result.returnMessage,
                    () => {
                        window.location.reload();
                    }
                );
            })
            .catch(function(error) {
                console.error('Error saving leave request:', error);
                showErrorPopup("Error", "Error saving leave request.");
            });
    }
});