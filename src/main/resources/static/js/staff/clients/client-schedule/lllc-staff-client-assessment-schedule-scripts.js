$(document).ready(function() {
    /* =========================================================
       ASSESSMENT SCHEDULE DATA
    ========================================================= */
    let therapists = [{
        id: 1,
        name: "Therapist A",
        role: "Primary Case Manager",
        email: "therapist.a@littleleaps.com",
        phone: "0917 123 4567",
        avatar: "https://i.pravatar.cc/150?img=47"
    }, {
        id: 2,
        name: "Therapist B",
        role: "Behavioral Therapist",
        email: "therapist.b@littleleaps.com",
        phone: "0917 765 4321",
        avatar: "https://i.pravatar.cc/150?img=12"
    }, {
        id: 3,
        name: "Case Manager A",
        role: "Case Manager",
        email: "casemanager.b@littleleaps.com",
        phone: "0917 765 4321",
        avatar: "https://i.pravatar.cc/150?img=12"
    }];
    /*
     * Dynamic schedules.
     *
     * One therapist can have MULTIPLE schedules
     * on the SAME DAY.
     */
    let schedules = [{
        id: 1,
        therapistId: 1,
        day: "Monday",
        startTime: "08:00",
        endTime: "10:00",
        status: "SCHEDULED",
        notes: ""
    }, {
        id: 2,
        therapistId: 1,
        day: "Monday",
        startTime: "10:00",
        endTime: "12:00",
        status: "SCHEDULED",
        notes: ""
    }, {
        id: 3,
        therapistId: 1,
        day: "Wednesday",
        startTime: "08:00",
        endTime: "10:00",
        status: "SCHEDULED",
        notes: ""
    }, {
        id: 4,
        therapistId: 1,
        day: "Wednesday",
        startTime: "10:00",
        endTime: "12:00",
        status: "SCHEDULED",
        notes: ""
    }, {
        id: 5,
        therapistId: 2,
        day: "Friday",
        startTime: "08:00",
        endTime: "10:00",
        status: "SCHEDULED",
        notes: ""
    }, {
        id: 6,
        therapistId: 2,
        day: "Friday",
        startTime: "10:00",
        endTime: "12:00",
        status: "SCHEDULED",
        notes: ""
    }, {
        id: 7,
        therapistId: 2,
        day: "Saturday",
        startTime: "08:00",
        endTime: "10:00",
        status: "SCHEDULED",
        notes: ""
    }, {
        id: 8,
        therapistId: 2,
        day: "Saturday",
        startTime: "10:00",
        endTime: "12:00",
        status: "SCHEDULED",
        notes: ""
    }];
    /* =========================================================
       CONSTANTS
    ========================================================= */
    const days = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"];
    let currentWeekStart = getMonday(new Date());
    let editingScheduleId = null;
    /* =========================================================
       INITIALIZE
    ========================================================= */
    initializeAssessmentSchedule();

    function initializeAssessmentSchedule() {
        createScheduleModal();
        renderAssessmentCalendar();
        updateWeekLabel();
        bindAssessmentScheduleEvents();
    }
    /* =========================================================
       EVENT BINDINGS
    ========================================================= */
    function bindAssessmentScheduleEvents() {
        /*
         * Add Schedule
         */
        $(document).on("click", "#openScheduleModal", function() {
            openScheduleModal();
        });
        /*
         * Previous week
         */
        $(document).on("click", "#previousWeek", function() {
            currentWeekStart.setDate(currentWeekStart.getDate() - 7);
            renderAssessmentCalendar();
            updateWeekLabel();
        });
        /*
         * Next week
         */
        $(document).on("click", "#nextWeek", function() {
            currentWeekStart.setDate(currentWeekStart.getDate() + 7);
            renderAssessmentCalendar();
            updateWeekLabel();
        });
        /*
         * Weekly view
         */
        $(document).on("click", "#weeklyViewBtn", function() {
            $("#weeklyViewBtn").addClass("active");
            $("#monthlyViewBtn").removeClass("active");
            renderAssessmentCalendar();
        });
        /*
         * Monthly view
         */
        $(document).on("click", "#monthlyViewBtn", function() {
            $("#monthlyViewBtn").addClass("active");
            $("#weeklyViewBtn").removeClass("active");
            /*
             * For now this remains the weekly
             * schedule structure.
             *
             * You can connect this later to
             * a monthly calendar.
             */
            renderAssessmentCalendar();
        });
        /*
         * Add therapist
         */
        $(document).on("click", "#addTherapist", function() {
            openAddTherapistDialog();
        });
        /*
         * Close modal
         */
        $(document).on("click", "#assessmentModalClose", function() {
            closeScheduleModal();
        });
        $(document).on("click", "#assessmentModalCancel", function() {
            closeScheduleModal();
        });
        /*
         * Click outside modal
         */
        $(document).on("click", ".assessment-schedule-modal-overlay", function(event) {
            if($(event.target).hasClass("assessment-schedule-modal-overlay")) {
                closeScheduleModal();
            }
        });
        /*
         * Save schedule
         */
        $(document).on("submit", "#assessmentScheduleForm", function(event) {
            event.preventDefault();
            saveSchedule();
        });
        /*
         * Edit schedule
         */
        $(document).on("click", ".assessment-schedule-slot", function() {
            const scheduleId = Number($(this).data("schedule-id"));
            openEditScheduleModal(scheduleId);
        });
        /*
         * Schedule menu
         *
         * Stop the parent slot click.
         */
        $(document).on("click", ".schedule-slot-menu", function(event) {
            event.stopPropagation();
            const scheduleId = Number($(this).closest(".assessment-schedule-slot").data("schedule-id"));
            openEditScheduleModal(scheduleId);
        });
        /*
         * Add schedule from empty day
         */
        $(document).on("click", ".empty-day-add", function() {
            const therapistId = Number($(this).data("therapist-id"));
            const day = $(this).data("day");
            openScheduleModal(therapistId, day);
        });
        /*
         * Therapist menu
         */
        $(document).on("click", ".therapist-menu", function(event) {
            event.stopPropagation();
            const therapistId = Number($(this).data("therapist-id"));
            showTherapistMenu(therapistId, this);
        });
        /*
         * Delete therapist
         */
        $(document).on("click", ".delete-therapist-action", function() {
            const therapistId = Number($(this).data("therapist-id"));
            deleteTherapist(therapistId);
            $(".assessment-therapist-menu").remove();
        });
        /*
         * Edit therapist
         */
        $(document).on("click", ".edit-therapist-action", function() {
            const therapistId = Number($(this).data("therapist-id"));
            editTherapist(therapistId);
            $(".assessment-therapist-menu").remove();
        });
        /*
         * Delete schedule
         */
        $(document).on("click", "#deleteAssessmentSchedule", function() {
            if(editingScheduleId === null) {
                return;
            }
            deleteSchedule(editingScheduleId);
        });
        /*
         * Close therapist popup
         */
        $(document).on("click", function(event) {
            if(!$(event.target).closest(".therapist-menu, .assessment-therapist-menu").length) {
                $(".assessment-therapist-menu").remove();
            }
        });
    }
    /* =========================================================
       CREATE MODAL
    ========================================================= */
    function createScheduleModal() {
        if($("#assessmentScheduleModal").length) {
            return;
        }
        const modalHtml = `

            <div
                id="assessmentScheduleModal"
                class="assessment-schedule-modal-overlay">

                <div class="assessment-schedule-modal">

                    <div class="assessment-modal-header">

                        <div>

                            <h2
                                id="assessmentModalTitle"
                                class="assessment-modal-title">

                                Add Assessment Schedule

                            </h2>

                            <p class="assessment-modal-description">

                                Create an assessment schedule
                                for the selected therapist.

                            </p>

                        </div>

                        <button
                            type="button"
                            id="assessmentModalClose"
                            class="assessment-modal-close">

                            <i class="fa-solid fa-xmark"></i>

                        </button>

                    </div>


                    <form
                        id="assessmentScheduleForm"
                        class="assessment-modal-body">


                        <div class="assessment-form-group">

                            <label
                                class="assessment-form-label">

                                Therapist
                                <span class="assessment-required">
                                    *
                                </span>

                            </label>

                            <select
                                id="assessmentTherapist"
                                class="assessment-form-control"
                                required>

                            </select>

                        </div>


                        <div class="assessment-form-row">


                            <div class="assessment-form-group">

                                <label
                                    class="assessment-form-label">

                                    Day
                                    <span class="assessment-required">
                                        *
                                    </span>

                                </label>

                                <select
                                    id="assessmentDay"
                                    class="assessment-form-control"
                                    required>

                                    <option value="">
                                        Select day
                                    </option>

                                    <option value="Monday">
                                        Monday
                                    </option>

                                    <option value="Tuesday">
                                        Tuesday
                                    </option>

                                    <option value="Wednesday">
                                        Wednesday
                                    </option>

                                    <option value="Thursday">
                                        Thursday
                                    </option>

                                    <option value="Friday">
                                        Friday
                                    </option>

                                    <option value="Saturday">
                                        Saturday
                                    </option>

                                    <option value="Sunday">
                                        Sunday
                                    </option>

                                </select>

                            </div>


                            <div class="assessment-form-group">

                                <label
                                    class="assessment-form-label">

                                    Status
                                    <span class="assessment-required">
                                        *
                                    </span>

                                </label>

                                <select
                                    id="assessmentStatus"
                                    class="assessment-form-control"
                                    required>

                                    <option value="SCHEDULED">
                                        Scheduled
                                    </option>

                                    <option value="PENDING">
                                        Pending
                                    </option>

                                    <option value="CANCELLED">
                                        Cancelled
                                    </option>

                                    <option value="COMPLETED">
                                        Completed
                                    </option>

                                </select>

                            </div>

                        </div>


                        <div class="assessment-form-row">


                            <div class="assessment-form-group">

                                <label
                                    class="assessment-form-label">

                                    Start Time
                                    <span class="assessment-required">
                                        *
                                    </span>

                                </label>

                                <input
                                    type="time"
                                    id="assessmentStartTime"
                                    class="assessment-form-control"
                                    required>

                            </div>


                            <div class="assessment-form-group">

                                <label
                                    class="assessment-form-label">

                                    End Time
                                    <span class="assessment-required">
                                        *
                                    </span>

                                </label>

                                <input
                                    type="time"
                                    id="assessmentEndTime"
                                    class="assessment-form-control"
                                    required>

                            </div>

                        </div>


                        <div class="assessment-form-group">

                            <label
                                class="assessment-form-label">

                                Notes

                            </label>

                            <textarea
                                id="assessmentNotes"
                                class="assessment-form-control"
                                placeholder="Add notes or special instructions..."></textarea>

                        </div>


                        <div class="assessment-modal-footer">

                            <button
                                type="button"
                                id="deleteAssessmentSchedule"
                                class="assessment-modal-btn cancel"
                                style="display:none; margin-right:auto; color:#d92d20;">

                                <i class="fa-solid fa-trash"></i>

                                Delete

                            </button>


                            <button
                                type="button"
                                id="assessmentModalCancel"
                                class="assessment-modal-btn cancel">

                                Cancel

                            </button>


                            <button
                                type="submit"
                                class="assessment-modal-btn save">

                                <i class="fa-solid fa-check"></i>

                                Save Schedule

                            </button>

                        </div>

                    </form>

                </div>

            </div>

        `;
        $("body").append(modalHtml);
        populateTherapistDropdown();
    }
    /* =========================================================
       POPULATE THERAPIST DROPDOWN
    ========================================================= */
    function populateTherapistDropdown(selectedId) {
        const $select = $("#assessmentTherapist");
        $select.empty();
        $select.append(`
            <option value="">
                Select therapist
            </option>
        `);
        $.each(therapists, function(_, therapist) {
            $select.append(`
                    <option value="${therapist.id}">
                        ${escapeHtml(therapist.name)}
                        -
                        ${escapeHtml(therapist.role)}
                    </option>
                `);
        });
        if(selectedId !== undefined && selectedId !== null && selectedId !== "") {
            $select.val(selectedId);
        }
    }
    /* =========================================================
       OPEN ADD MODAL
    ========================================================= */
    function openScheduleModal(therapistId, day) {
        editingScheduleId = null;
        $("#assessmentModalTitle").text("Add Assessment Schedule");
        $("#assessmentScheduleForm")[0].reset();
        populateTherapistDropdown(therapistId || "");
        if(day) {
            $("#assessmentDay").val(day);
        }
        $("#assessmentStatus").val("SCHEDULED");
        $("#deleteAssessmentSchedule").hide();
        $("#assessmentScheduleModal").addClass("show");
    }
    /* =========================================================
       OPEN EDIT MODAL
    ========================================================= */
    function openEditScheduleModal(scheduleId) {
        const schedule = $.grep(schedules, function(item) {
            return item.id === scheduleId;
        })[0];
        if(!schedule) {
            return;
        }
        editingScheduleId = scheduleId;
        $("#assessmentModalTitle").text("Edit Assessment Schedule");
        populateTherapistDropdown(schedule.therapistId);
        $("#assessmentDay").val(schedule.day);
        $("#assessmentStatus").val(schedule.status);
        $("#assessmentStartTime").val(schedule.startTime);
        $("#assessmentEndTime").val(schedule.endTime);
        $("#assessmentNotes").val(schedule.notes || "");
        $("#deleteAssessmentSchedule").show();
        $("#assessmentScheduleModal").addClass("show");
    }
    /* =========================================================
       CLOSE MODAL
    ========================================================= */
    function closeScheduleModal() {
        $("#assessmentScheduleModal").removeClass("show");
        editingScheduleId = null;
    }
    /* =========================================================
       SAVE SCHEDULE
    ========================================================= */
    function saveSchedule() {
        const therapistId = Number($("#assessmentTherapist").val());
        const day = $("#assessmentDay").val();
        const status = $("#assessmentStatus").val();
        const startTime = $("#assessmentStartTime").val();
        const endTime = $("#assessmentEndTime").val();
        const notes = $.trim($("#assessmentNotes").val());
        /*
         * Validation
         */
        if(!therapistId) {
            showAssessmentError("Please select a therapist.");
            return;
        }
        if(!day) {
            showAssessmentError("Please select a day.");
            return;
        }
        if(!startTime || !endTime) {
            showAssessmentError("Please select the start and end time.");
            return;
        }
        /*
         * Start must be before end.
         */
        if(startTime >= endTime) {
            showAssessmentError("End time must be later than start time.");
            return;
        }
        /*
         * Check overlapping schedules.
         */
        const hasOverlap = $.grep(schedules, function(schedule) {
            /*
             * Don't compare against
             * the schedule being edited.
             */
            if(editingScheduleId !== null && schedule.id === editingScheduleId) {
                return false;
            }
            if(schedule.therapistId !== therapistId) {
                return false;
            }
            if(schedule.day !== day) {
                return false;
            }
            /*
             * Overlap:
             *
             * existing start < new end
             *
             * AND
             *
             * existing end > new start
             */
            return(schedule.startTime < endTime && schedule.endTime > startTime);
        }).length > 0;
        if(hasOverlap) {
            showAssessmentError("This therapist already has a schedule that overlaps the selected time.");
            return;
        }
        /*
         * EDIT
         */
        if(editingScheduleId !== null) {
            const schedule = $.grep(schedules, function(item) {
                return(item.id === editingScheduleId);
            })[0];
            if(schedule) {
                schedule.therapistId = therapistId;
                schedule.day = day;
                schedule.startTime = startTime;
                schedule.endTime = endTime;
                schedule.status = status;
                schedule.notes = notes;
            }
        }
        /*
         * CREATE
         */
        else {
            schedules.push({
                id: Date.now(),
                therapistId: therapistId,
                day: day,
                startTime: startTime,
                endTime: endTime,
                status: status,
                notes: notes
            });
        }
        /*
         * Re-render calendar.
         */
        renderAssessmentCalendar();
        closeScheduleModal();
    }
    /* =========================================================
       DELETE SCHEDULE
    ========================================================= */
    function deleteSchedule(scheduleId) {
        const schedule = $.grep(schedules, function(item) {
            return item.id === scheduleId;
        })[0];
        if(!schedule) {
            return;
        }
        const confirmed = confirm("Are you sure you want to delete this assessment schedule?");
        if(!confirmed) {
            return;
        }
        schedules = $.grep(schedules, function(item) {
            return item.id !== scheduleId;
        });
        renderAssessmentCalendar();
        closeScheduleModal();
    }
    /* =========================================================
       RENDER CALENDAR
    ========================================================= */
    function renderAssessmentCalendar() {
        const weekDates = getWeekDates(currentWeekStart);
        let html = `

            <div class="assessment-calendar">

                <div class="assessment-calendar-header">

                    <div class="
                        calendar-header-cell
                        therapist-header
                    ">

                        Therapist

                    </div>

        `;
        /*
         * Header:
         *
         * Monday
         * Tuesday
         * Wednesday
         * ...
         */
        $.each(weekDates, function(index, date) {
            html += `

                    <div class="calendar-header-cell">

                        <span class="calendar-day-name">

                            ${days[index].substring(0, 3)}

                        </span>

                        <span class="calendar-day-date">

                            ${formatDate(date)}

                        </span>

                    </div>

                `;
        });
        html += `
                </div>
        `;
        /*
         * Therapist rows.
         */
        if(therapists.length === 0) {
            html += `

                <div class="assessment-empty-state">

                    No therapists have been assigned
                    to this client.

                </div>

            `;
        } else {
            $.each(therapists, function(_, therapist) {
                html += renderTherapistRow(therapist, weekDates);
            });
        }
        html += `
            </div>
        `;
        $("#calendar").html(html);
    }
    /* =========================================================
       RENDER THERAPIST ROW
    ========================================================= */
    function renderTherapistRow(therapist, weekDates) {
        let html = `

            <div class="assessment-therapist-row">


                <!-- THERAPIST INFO -->

                <div class="therapist-info">

                    <div class="therapist-avatar">

                        <img
                            src="${escapeHtml(therapist.avatar)}"
                            alt="${escapeHtml(therapist.name)}">

                    </div>


                    <div class="therapist-details">

                        <div class="therapist-name">

                            ${escapeHtml(
            therapist.name
        )}

                        </div>


                        <div class="therapist-role">

                            ${escapeHtml(
            therapist.role
        )}

                        </div>


                        <div class="therapist-contact">

                            <span>
                                Email:
                                ${escapeHtml(
            therapist.email
        )}
                            </span>

                            <span>
                                Phone:
                                ${escapeHtml(
            therapist.phone
        )}
                            </span>

                        </div>

                    </div>


                    <button
                        type="button"
                        class="therapist-menu"
                        data-therapist-id="${therapist.id}">

                        <i class="fa-solid fa-ellipsis-vertical"></i>

                    </button>

                </div>

        `;
        /*
         * Monday -> Sunday
         */
        $.each(weekDates, function(index) {
            const day = days[index];
            const therapistSchedules = $.grep(schedules, function(schedule) {
                return(schedule.therapistId === therapist.id && schedule.day === day);
            });
            /*
             * Sort by start time.
             */
            therapistSchedules.sort(function(a, b) {
                return a.startTime.localeCompare(b.startTime);
            });
            html += `

                    <div
                        class="assessment-day-cell"
                        data-therapist-id="${therapist.id}"
                        data-day="${day}"
                    >
                `;
            /*
             * No schedule
             */
            if(therapistSchedules.length === 0) {
                html += `

                        <div class="empty-day">

                            <button
                                type="button"
                                class="empty-day-add"
                                title="Add schedule"
                                data-therapist-id="${therapist.id}"
                                data-day="${day}">

                                <i class="fa-solid fa-plus"></i>

                            </button>

                        </div>

                    `;
            }
            /*
             * Existing schedules
             */
            else {
                $.each(therapistSchedules, function(_, schedule) {
                    html += renderScheduleSlot(schedule);
                });
            }
            html += `
                    </div>
                `;
        });
        html += `
            </div>
        `;
        return html;
    }
    /* =========================================================
       RENDER SCHEDULE SLOT
    ========================================================= */
    function renderScheduleSlot(schedule) {
        const statusClass = schedule.status.toLowerCase();
        return `

            <div
                class="
                    assessment-schedule-slot
                    ${statusClass}
                "
                data-schedule-id="${schedule.id}"
                title="Click to edit schedule">

                <span class="schedule-slot-time">

                    ${formatTime(
            schedule.startTime
        )}

                    -

                    ${formatTime(
            schedule.endTime
        )}

                </span>


                <button
                    type="button"
                    class="schedule-slot-menu"
                    title="Edit">

                    <i class="fa-solid fa-ellipsis-vertical"></i>

                </button>

            </div>

        `;
    }
    /* =========================================================
       ADD THERAPIST
    ========================================================= */
    function openAddTherapistDialog() {
        const name = prompt("Enter therapist name:");
        if(!name || $.trim(name) === "") {
            return;
        }
        const role = prompt("Enter therapist role:", "Behavioral Therapist");
        const email = prompt("Enter therapist email:", "therapist@littleleaps.com");
        const phone = prompt("Enter therapist phone:", "0917 000 0000");
        therapists.push({
            id: Date.now(),
            name: $.trim(name),
            role: $.trim(role || "Behavioral Therapist"),
            email: $.trim(email || "therapist@littleleaps.com"),
            phone: $.trim(phone || "0917 000 0000"),
            avatar: "https://i.pravatar.cc/150?img=" + (Math.floor(Math.random() * 50) + 1)
        });
        populateTherapistDropdown();
        renderAssessmentCalendar();
    }
    /* =========================================================
       THERAPIST MENU
    ========================================================= */
    function showTherapistMenu(therapistId, button) {
        $(".assessment-therapist-menu").remove();
        const therapist = $.grep(therapists, function(item) {
            return item.id === therapistId;
        })[0];
        if(!therapist) {
            return;
        }
        const $menu = $(`
                <div class="assessment-therapist-menu">

                    <button
                        type="button"
                        class="edit-therapist-action"
                        data-therapist-id="${therapistId}">

                        <i class="fa-solid fa-pen"></i>

                        Edit Therapist

                    </button>


                    <button
                        type="button"
                        class="delete-therapist-action delete"
                        data-therapist-id="${therapistId}">

                        <i class="fa-solid fa-trash"></i>

                        Remove Therapist

                    </button>

                </div>
            `);
        $("body").append($menu);
        const offset = $(button).offset();
        $menu.css({
            top: offset.top + $(button).outerHeight() + 5,
            left: offset.left - 125
        });
    }
    /* =========================================================
       EDIT THERAPIST
    ========================================================= */
    function editTherapist(therapistId) {
        const therapist = $.grep(therapists, function(item) {
            return item.id === therapistId;
        })[0];
        if(!therapist) {
            return;
        }
        const name = prompt("Therapist name:", therapist.name);
        if(name === null) {
            return;
        }
        const role = prompt("Therapist role:", therapist.role);
        const email = prompt("Therapist email:", therapist.email);
        const phone = prompt("Therapist phone:", therapist.phone);
        therapist.name = $.trim(name);
        therapist.role = $.trim(role || therapist.role);
        therapist.email = $.trim(email || therapist.email);
        therapist.phone = $.trim(phone || therapist.phone);
        populateTherapistDropdown();
        renderAssessmentCalendar();
    }
    /* =========================================================
       DELETE THERAPIST
    ========================================================= */
    function deleteTherapist(therapistId) {
        const therapist = $.grep(therapists, function(item) {
            return item.id === therapistId;
        })[0];
        if(!therapist) {
            return;
        }
        const confirmed = confirm("Remove " + therapist.name + " and all of this therapist's schedules?");
        if(!confirmed) {
            return;
        }
        /*
         * Remove therapist.
         */
        therapists = $.grep(therapists, function(item) {
            return item.id !== therapistId;
        });
        /*
         * Remove all schedules
         * belonging to therapist.
         */
        schedules = $.grep(schedules, function(schedule) {
            return(schedule.therapistId !== therapistId);
        });
        populateTherapistDropdown();
        renderAssessmentCalendar();
    }
    /* =========================================================
       WEEK CALCULATIONS
    ========================================================= */
    function getMonday(date) {
        const result = new Date(date);
        const day = result.getDay();
        const difference = day === 0 ? -6 : 1 - day;
        result.setDate(result.getDate() + difference);
        result.setHours(0, 0, 0, 0);
        return result;
    }

    function getWeekDates(monday) {
        const dates = [];
        for(let i = 0; i < 7; i++) {
            const date = new Date(monday);
            date.setDate(monday.getDate() + i);
            dates.push(date);
        }
        return dates;
    }
    /* =========================================================
       UPDATE WEEK LABEL
    ========================================================= */
    function updateWeekLabel() {
        const dates = getWeekDates(currentWeekStart);
        const first = dates[0];
        const last = dates[6];
        const firstMonth = first.toLocaleString("en-US", {
            month: "short"
        });
        const lastMonth = last.toLocaleString("en-US", {
            month: "short"
        });
        const firstYear = first.getFullYear();
        const lastYear = last.getFullYear();
        let label;
        if(firstYear === lastYear && firstMonth === lastMonth) {
            label = firstMonth + " " + first.getDate() + " - " + last.getDate() + ", " + firstYear;
        } else {
            label = firstMonth + " " + first.getDate() + ", " + firstYear + " - " + lastMonth + " " + last.getDate() + ", " + lastYear;
        }
        $("#weekLabel").text(label);
    }
    /* =========================================================
       DATE FORMAT
    ========================================================= */
    function formatDate(date) {
        return date.toLocaleDateString("en-US", {
            month: "short",
            day: "numeric"
        });
    }
    /* =========================================================
       TIME FORMAT
    ========================================================= */
    function formatTime(time) {
        if(!time) {
            return "";
        }
        const parts = time.split(":");
        let hours = parseInt(parts[0], 10);
        const minutes = parts[1];
        const suffix = hours >= 12 ? "PM" : "AM";
        hours = hours % 12 || 12;
        return(hours + ":" + minutes + " " + suffix);
    }
    /* =========================================================
       ERROR MESSAGE
    ========================================================= */
    function showAssessmentError(message) {
        /*
         * Replace this with your existing
         * showError/toastr/swal implementation
         * if you already have one.
         */
        alert(message);
    }
    /* =========================================================
       HTML ESCAPE
    ========================================================= */
    function escapeHtml(value) {
        return $("<div>").text(value == null ? "" : value).html();
    }
});