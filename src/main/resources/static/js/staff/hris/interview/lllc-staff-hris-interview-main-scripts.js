$(document).ready(function () {
    initializeMaxTodayDatePicker2("#interview-interview-date", "Select interview date");
    initializeTimePicker("#interview-interview-time", "Select interview time")

    function toggleMeetingLink(form) {
        const method = form.find('[name="interviewMethod"]').val();
        const meetingLink = form.find('[name="meetingLink"]');
        const container = form.find('.meeting-link-container');

        const isVideoInterview = method === 'VIDEO';

        container.toggle(isVideoInterview);
        meetingLink.prop('required', isVideoInterview);

        if (!isVideoInterview) {
            meetingLink.removeClass('is-invalid');
        }
    }

    /* Initialize all reschedule forms. */
    $(document).ready(function () {
        $('.rescheduleInterviewForm').each(function () {
            toggleMeetingLink($(this));
        });
    });

    /* Update when the interview method changes. */
    $(document).on(
        'change',
        '.rescheduleInterviewForm [name="interviewMethod"]',
        function () {
            toggleMeetingLink($(this).closest('.rescheduleInterviewForm'));
        }
    );

    /* Edit interview notes */
    $(document).on('click', '.editNotesBtn', function () {
        const modal = $(this).closest('.view-schedule-interview-modal');

        modal.find('.notesDisplay').hide();
        modal.find('.editNotesBtn').hide();
        modal.find('.notesEdit').show();
    });

    /* Cancel editing interview notes */
    $(document).on('click', '.cancelNotesBtn', function () {
        const modal = $(this).closest('.view-schedule-interview-modal');

        modal.find('.notesEdit').hide();
        modal.find('.notesDisplay').show();
        modal.find('.editNotesBtn').show();
    });

    /* Save interview notes */
    $(document).on('click', '.saveNotesBtn', function () {
        const modal = $(this).closest('.view-schedule-interview-modal');

        const applicantId = modal.find('.applicantId').val();
        const interviewId = modal.find('.interviewId').val();
        const notes = modal.find('.notesTextarea').val();

        updateInterviewNotes(applicantId, interviewId, notes, modal);
    });

    $(document).on('submit', '.rescheduleInterviewForm', function (e) {
        e.preventDefault();

        const form = $(this);

        const request = {
            applicantId: Number(form.find('[name="applicantId"]').val()),
            interviewId: Number(form.find('[name="interviewId"]').val()),
            interviewMethod: form.find('[name="interviewMethod"]').val(),
            interviewDate: form.find('[name="interviewDate"]').val(),
            interviewTime: form.find('[name="interviewTime"]').val(),
            meetingLink: form.find('[name="meetingLink"]').val() || ''
        };

        const submitButton = form.find('button[type="submit"]');

        rescheduleInterview(request, submitButton);
    });

    function updateInterviewNotes(applicantId, interviewId, notes, modal) {
        $.ajax({
            url: '/api/v1/applicant-interview/update-interview-notes',
            type: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify({
                applicantId: applicantId,
                interviewId: interviewId,
                notes: notes
            }),
            success: function () {
                modal.find('.notesDisplay')
                    .text(notes || 'No notes available.');

                modal.find('.notesEdit').hide();
                modal.find('.notesDisplay').show();
                modal.find('.editNotesBtn').show();

                console.log('Interview notes updated successfully.');
            },
            error: function (xhr) {
                console.error('Failed to update interview notes:', xhr.responseText);
                showErrorPopup("Error", 'Failed to update interview notes. Please try again.');
            }
        });
    }

    function rescheduleInterview(request, submitButton) {

        submitButton.prop('disabled', true);

        $.ajax({
            url: '/api/v1/applicant-interview/reschedule-interview',
            type: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify(request),

            success: function () {
                window.location.reload();
            },

            error: function (xhr) {
                console.error('Failed to reschedule interview:', xhr.responseText);
                showErrorPopup("Error", 'Failed to reschedule interview. Please try again.');
            },

            complete: function () {
                submitButton.prop('disabled', false);
            }
        });
    }

    window.updateInterviewResult = function (applicantId, interviewId, result) {
        $.ajax({
            url: '/api/v1/applicant-interview/update-interview-result',
            type: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify({
                applicantId: applicantId,
                interviewId: interviewId,
                interviewResult: result
            }),
            success: function () {
                window.location.reload();
            },
            error: function (xhr) {
                console.error('Failed to update interview result:', xhr.responseText);
                showErrorPopup("Error", 'Failed to update interview result. Please try again.');
            }
        });
    };

});