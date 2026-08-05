$(document).ready(function () {

    /* Initialized Date Pickers */
    initializeMaxTodayDatePicker("#employee-birth-date", "Select birth date");
    initializeMaxTodayDatePicker("#employee-date-hired", "Select date hired");
    initializeMaxTodayDatePicker("#employee-regular-date-start", "Select date start");
    initializeDatePicker("#employee-salary-effective-date", "Select effective date");

    /* Initialized Image Upload */
    initializeImageUpload();

    $("#search-credentials-btn").click(function () {
        let username = $("#employee-cred-username").val();

        if (!username) {
            showErrorPopup("Required Field", "Please enter username");
            return;
        }

        getUserByUsername(username);
    });

    $("#generate-id-number-btn").click(function () {
        let dateHired = $("#employee-date-hired").val();

        if (!dateHired) {
            showErrorPopup("Required Field", "Please select Date Hired first.");
            return;
        }

        let mmdd = dateHired.substring(5, 7) + dateHired.substring(8, 10);

        getRunningSequence(mmdd);
    });

    $('#add-employee-btn').on('click', function () {

        const employeePayload = getEmployeeFormData();
        createEmployee(employeePayload);
    });

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

function getUserByUsername(username) {
    $.ajax({
        url: "/api/v1/account/admin/get-user/" + username,
        type: "GET",
        success: function(response) {
            console.log("User details:", response);

            $("#employee-cred-password").val(response.lastPassword);
            $("#employee-cred-email").val(response.email);
            $("#employee-cred-status").val(formatStatus(response.status));
        },
        error: function(xhr, status, error) {
            console.error("Error fetching user:", xhr.responseText);
            showInfoPopup("Info", "User not found.");
        }
    });
}

function getRunningSequence(dateHired) {
    $.ajax({
        url: "/api/v1/account/admin/get-running-sequence/" + dateHired,
        type: "GET",
        success: function(response) {
            console.log("Running Sequence:", response);
            $("#employee-id-number").val(response);
        },
        error: function(xhr) {
            let message = "Error getting running sequence";

            if (xhr.responseJSON && xhr.responseJSON.message) {
                message = xhr.responseJSON.message;
            }

            showErrorPopup("Error", message);
        }
    });
}

function createEmployee(employeeRequest) {

    $.ajax({
        url: "/api/v1/management/create-employee",
        type: "POST",
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
            let message = "Unable to create employee.";

            if (xhr.responseJSON && xhr.responseJSON.message) {
                message = xhr.responseJSON.message;
            }

            showErrorPopup("Error", message);
        }
    });
}

function formatStatus(role) {
    return role
        .toLowerCase()
        .replace(/_/g, " ")
        .replace(/\b\w/g, char => char.toUpperCase());
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
            employeeType: $('#employee-type').val()
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
            immediateSupervisor: $('#employee-immediate-supervisor').val()
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