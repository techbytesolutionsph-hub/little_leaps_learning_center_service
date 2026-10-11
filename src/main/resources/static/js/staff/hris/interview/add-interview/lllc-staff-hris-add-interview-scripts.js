$(document).ready(function () {
    initializeMaxTodayDatePicker2("#interview-interview-date", "Select interview date");
    initializeTimePicker("#interview-interview-time", "Select interview time")

    const $interviewMethod = $('#interview-method');
    const $meetingLinkContainer = $('#meetingLinkContainer');
    const $meetingLink = $('#interview-meeting-link');

    function toggleMeetingLink() {
        const isVideo = $interviewMethod.val() === 'VIDEO';

        $meetingLinkContainer.toggle(isVideo);
        $meetingLink.prop('required', isVideo);

        if (!isVideo) {
            $meetingLink.val('').removeClass('is-invalid is-valid');
        }
    }

    // Update when the interview method changes
    $interviewMethod.on('change', toggleMeetingLink);

    // Set the initial visibility
    toggleMeetingLink();

    $("#back-client-btn").on("click", function (e) {
        e.preventDefault();
        showConfirmPopup(
            "Leave this page?",
            "Your unsaved candidate data will be lost.",
            () => window.location.href = $(this).data("url")
        );
    });

    $('#add-interview-form').on('input change', '[required]', function () {
        const field = $(this);
        const empty = !field.val()?.toString().trim();

        field.toggleClass('is-invalid', empty);
        field.siblings('.invalid-feedback').toggle(empty);
    });

    function validateScheduleInterviewForm() {
        let isValid = true;

        $('#add-interview-form [required]').each(function () {
            const field = $(this);
            const empty = !field.val()?.toString().trim();

            field.toggleClass('is-invalid', empty);
            field.siblings('.invalid-feedback').toggle(empty);

            if (empty) {
                isValid = false;
            }
        });

        return isValid;
    }

    $('#add-schedule-interview-btn').on('click', function (e) {
        e.preventDefault();

        if (!validateScheduleInterviewForm()) return;

        const request = getScheduleInterviewFormData();

        console.log(request);

        registerApplicantInterview(request);
    });

    function getScheduleInterviewFormData() {
        return {
            interviewerId: $('#interview-interviewer').val(),
            interviewMethod: $('#interview-method').val(),
            interviewStage: $('#interview-stage').val(),
            interviewDate: $('#interview-interview-date').val(),
            interviewTime: $('#interview-interview-time').val(),
            scheduleStatus: $('#interview-schedule-status').val(),
            meetingLink: $('#interview-meeting-link').val(),
            notes: $('#interview-notes').val(),
            applicantId: $('#interview-applicant').val()
        };
    }

    function registerApplicantInterview(request) {
        $.ajax({
            url: '/api/v1/applicant-interview/register-applicant-interview',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(request),
            success: function (response) {
                showSuccessThenRedirectPopup(
                    "Success",
                    response.returnMessage,
                    () => {
                        window.location.href = "/app/portal/hr-recruitment/applicant-interview";
                    }
                );
            },
            error: function (xhr) {
                console.error("Failed to register applicant interview.", xhr.responseJSON);
                let message = "Unable to save applicant interview.";

                if (xhr.responseJSON && xhr.responseJSON.message) {
                    message = xhr.responseJSON.message;
                }

                showErrorPopup("Error", message);
            }
        });
    }
});