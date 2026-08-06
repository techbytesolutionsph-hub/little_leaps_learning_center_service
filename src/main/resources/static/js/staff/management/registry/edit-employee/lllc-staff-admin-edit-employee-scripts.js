$(document).ready(function () {

    /* Initialized Date Pickers */
    initializeMaxTodayDatePicker("#employee-birth-date", "Select birth date");
    initializeMaxTodayDatePicker("#employee-date-hired", "Select date hired");
    initializeMaxTodayDatePicker("#employee-regular-date-start", "Select date start");
    initializeDatePicker("#employee-salary-effective-date", "Select effective date");

    /* Initialized Image Upload */
    initializeImageUpload();

    $(document).on("change", "#employee-gender", function () {

        const gender = $(this).val();

        const maternity = $("#employee-maternity-leave");
        const paternity = $("#employee-paternity-leave");

        if (gender === "MALE") {

            /* Disable Maternity Leave */
            maternity.val(0)
                .prop("disabled", true)
                .removeClass("is-invalid");

            /* Enable Paternity Leave */
            paternity.prop("disabled", false);

        } else if (gender === "FEMALE") {

            /* Disable Paternity Leave */
            paternity.val(0)
                .prop("disabled", true)
                .removeClass("is-invalid");

            /* Enable Maternity Leave */
            maternity.prop("disabled", false);

        } else {

            /* Enable both if no gender selected */
            maternity.prop("disabled", false);
            paternity.prop("disabled", false);
        }

    });

    $("#update-employee-btn").on("click", function (e) {

        e.preventDefault();

        let isValid = true;
        let firstInvalidTab = null;
        let firstInvalidField = null;

        /* Remove previous validation styles */
        $("#edit-employee-form .form-control, #edit-employee-form .form-select")
            .removeClass("is-invalid");

        /* Validate all required fields except HMO fields */
        $("#edit-employee-form [required]")
            .not("#employee-hmo-provider, #employee-hmo-no")
            .each(function () {

                let field = $(this);

                /* Skip only disabled fields */
                if (field.prop("disabled")) {
                    return true;
                }

                let value = $.trim(field.val());

                if (!value) {
                    console.log("Invalid Field:", field.attr("id"));

                    isValid = false;
                    field.addClass("is-invalid");

                    if (!firstInvalidField) {
                        firstInvalidField = field;
                        firstInvalidTab = field.closest(".tab-pane");
                    }
                }
            });

        if (!isValid) {

            /* Open the tab containing the first invalid field */
            if (firstInvalidTab.length) {
                let tabId = "#" + firstInvalidTab.attr("id");
                $('.nav-link[data-bs-target="' + tabId + '"]').tab("show");
            }

            /* Focus the first invalid field */
            setTimeout(function () {
                firstInvalidField.trigger("focus");
            }, 300);

            showErrorPopup(
                "Required Field",
                "Please fill out all required fields before submitting."
            );

            return;
        }

        const employeePayload = getEmployeeFormData();
        console.log(employeePayload);

        updateEmployee(employeePayload);
    });


    /* Remove validation style while typing/selecting */
    $("#edit-employee-form").on(
        "input change",
        ".form-control, .form-select",
        function () {
            $(this).removeClass("is-invalid");
        }
    );


    /* Remove invalid state when user types/selects */
    $("#edit-employee-form").on(
        "input change",
        ".form-control, .form-select",
        function () {

            if ($(this).val()) {
                $(this).removeClass("is-invalid");
            }

        }
    );

    $('#back-btn').on('click', function (e) {
        e.preventDefault();

        const url = $(this).data('url');
        console.log(url);

        showConfirmPopup(
            'Leave this page?',
            'Your unsaved product data will be lost.',
            function () {
                window.location.href = url;
            }
        );
    });
});

function updateEmployee(employeeRequest) {

    $.ajax({
        url: "/api/v1/management/update-employee",
        type: "PUT",
        contentType: "application/json",
        data: JSON.stringify(employeeRequest),
        success: function(response) {

            showSuccessThenRedirectPopup(
                "Success",
                response.returnMessage,
                () => {
                    window.location.href = "/app/portal/hr-management/employee-registry";
                }
            );
        },
        error: function(xhr, status, error) {
            let message = "Unable to update employee.";

            if (xhr.responseJSON && xhr.responseJSON.message) {
                message = xhr.responseJSON.message;
            }

            showErrorPopup("Error", message);
        }
    });
}

function getEmployeeFormData() {

    return {
        personalInformation: {
            firstName: $('#employee-firstname').val(),
            middleName: $('#employee-middlename').val(),
            lastName: $('#employee-lastname').val(),
            age: Number($('#employee-age').val()) || 0,
            birthDate: $('#employee-birth-date').val(),
            gender: $('#employee-gender').val(),
            email: $('#employee-email').val(),
            phoneNumber: $('#employee-phone-no').val(),
            maritalStatus: $('#employee-marital-status').val()
        },

        address: {
            street: $('#employee-street').val(),
            barangay: $('#employee-brgy').val(),
            city: $('#employee-city').val(),
            province: $('#employee-province').val(),
            country: $('#employee-country').val(),
            postalCode: $('#employee-postal-code').val()
        },

        contactInformation: {
            contactNumber: $('#employee-contact-no-home').val(),
            workEmail: $('#employee-email-work').val(),
            homeEmail: $('#employee-email-home').val()
        },

        emergencyContact: {
            name: $('#employee-emergency-contact-name').val(),
            contactNumber: $('#employee-emergency-contact-no').val(),
            relationship: $('#employee-emergency-contact-relationship').val()
        },

        employmentInformation: {
            position: $('#employee-position').val(),
            employeeIdNumber: $('#employee-id-number').val(),
            dateHired: $('#employee-date-hired').val(),
            regularDateStart: $('#employee-regular-date-start').val(),
            companyAddress: $('#employee-company-address').val(),
            employmentStatus: $('#employee-employment-status').val(),
            employmentType: $('#employee-employment-type').val(),
            branch: $('#employee-assign-branch').val(),
            immediateSupervisor: $('#employee-immediate-supervisor').val(),
            employeeType: $('#employee-type').val()
        },

        benefits: {
            sssNumber: $('#employee-sss-number').val(),
            pagibigNumber: $('#employee-pagibig-no').val(),
            philhealthNumber: $('#employee-philhealth-no').val(),
            tinNumber: $('#employee-tin-no').val(),

            sickLeave: Number($('#employee-sick-leave').val()) || 0,
            vacationLeave: Number($('#employee-vacation-leave').val()) || 0,
            paternityLeave: Number($('#employee-paternity-leave').val()) || 0,
            maternityLeave: Number($('#employee-maternity-leave').val()) || 0,

            hmoProvider: $('#employee-hmo-provider').val(),
            hmoNumber: $('#employee-hmo-no').val(),

            allowance: Number($('#employee-allowance').val()) || 0,
            riceAllowance: Number($('#employee-rice-allowance').val()) || 0,
            transportationAllowance: Number($('#employee-transportation-allowance').val()) || 0,
            communicationAllowance: Number($('#employee-communication-allowance').val()) || 0
        },

        payrollInformation: {
            basicSalary: Number($('#employee-basic-salary').val()) || 0,
            dailyRate: Number($('#employee-daily-rate').val()) || 0,
            hourlyRate: Number($('#employee-hourly-rate').val()) || 0,

            salaryType: $('#employee-salary-type').val(),
            payrollCycle: $('#employee-payroll-cycle').val(),
            effectiveDate: $('#employee-salary-effective-date').val(),

            bankName: $('#employee-bank-name').val(),
            bankAccountNumber: $('#employee-bank-account-no').val(),
            bankBranch: $('#employee-bank-branch-no').val()
        },

        accountAccess: {
            username: $('#employee-cred-username').val(),
            password: $('#employee-cred-password').val(),
            email: $('#employee-cred-email').val(),
            status: $('#employee-cred-status').val()
        },

        profileImageUrl: $('.image-preview').attr('src')
    };
}