$(document).ready(function () {

    /* Initialized Location */
    initEmployeeAddressLocationAutoFill();

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

    $(document).on("change", "#employee-gender", function () {

        const gender = $(this).val();

        const maternity = $("#employee-maternity-leave");
        const paternity = $("#employee-paternity-leave");

        if (gender === "MALE") {

            // Disable Maternity Leave
            maternity.val(0)
                .prop("disabled", true)
                .removeClass("is-invalid");

            // Enable Paternity Leave
            paternity.prop("disabled", false);

        } else if (gender === "FEMALE") {

            // Disable Paternity Leave
            paternity.val(0)
                .prop("disabled", true)
                .removeClass("is-invalid");

            // Enable Maternity Leave
            maternity.prop("disabled", false);

        } else {

            // Enable both if no gender selected
            maternity.prop("disabled", false);
            paternity.prop("disabled", false);
        }

    });

    $("#add-employee-btn").on("click", function (e) {

        e.preventDefault();

        let isValid = true;
        let firstInvalidTab = null;
        let firstInvalidField = null;

        /* Remove previous validation styles */
        $("#add-employee-form .form-control, #add-employee-form .form-select")
            .removeClass("is-invalid");

        /* Validate all required fields except HMO fields */
        $("#add-employee-form [required]")
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
        createEmployee(employeePayload);
    });


    /* Remove validation style while typing/selecting */
    $("#add-employee-form").on(
        "input change",
        ".form-control, .form-select",
        function () {
            $(this).removeClass("is-invalid");
        }
    );


    /* Remove invalid state when user types/selects */
    $("#add-employee-form").on(
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

/**
 * Initializes checkout location autofill functionality.
 * Loads PSGC data, binds cascading dropdowns, and auto-detects user location.
 *
 * External APIs used:
 * - https://psgc.gitlab.io/api/provinces.json
 * - https://psgc.gitlab.io/api/cities-municipalities.json
 * - https://psgc.gitlab.io/api/barangays.json
 * - https://nominatim.openstreetmap.org/reverse
 *
 * @function initEmployeeAddressLocationAutoFill
 * @returns {void}
 */
function initEmployeeAddressLocationAutoFill() {

    const countries = ["", "Philippines"];

    const $country = $('#employee-country');
    const $province = $('#employee-province');
    const $city = $('#employee-city');
    const $barangay = $('#employee-brgy');

    let provinces = [];
    let cities = [];
    let barangays = [];


    countries
        .filter(c => c.trim() !== "")
        .forEach(c =>
            $country.append(
                `<option value="${c}">${c}</option>`
            )
        );


    $.when(
        $.getJSON(
            "https://psgc.gitlab.io/api/provinces.json",
            d => provinces = d
        ),

        $.getJSON(
            "https://psgc.gitlab.io/api/cities-municipalities.json",
            d => cities = d
        ),

        $.getJSON(
            "https://psgc.gitlab.io/api/barangays.json",
            d => barangays = d
        )

    ).done(() => {
        detectLocation();
    });


    $country.on('change', function () {

        reset($province);
        reset($city, true);
        reset($barangay, true);


        if(this.value === "Philippines") {
            provinces.forEach(p => {
                $province.append(
                    `<option value="${p.code}">
                        ${p.name}
                     </option>`
                );
            });

            $province.prop('disabled', false);
        }
    });

    $province.on('change', function(){
        reset($city);
        reset($barangay,true);

        cities
            .filter(c => c.provinceCode === this.value)
            .forEach(c => {
                $city.append(
                    `<option value="${c.code}">
                        ${c.name}
                     </option>`
                );
            });

        $city.prop('disabled',false);
    });



    $city.on('change',function(){
        reset($barangay);

        barangays
            .filter(b => b.cityCode === this.value)
            .forEach(b=>{
                $barangay.append(
                    `<option value="${b.code}">
                        ${b.name}
                     </option>`
                );
            });

        $barangay.prop('disabled',false);
    });

    function detectLocation(){

        if(!navigator.geolocation)
            return;

        navigator.geolocation.getCurrentPosition(async ({coords})=>{

            try{
                const response = await fetch(
                    `https://nominatim.openstreetmap.org/reverse?format=json&lat=${coords.latitude}&lon=${coords.longitude}`
                );

                const data = await response.json();

                if(data.address.country === "Philippines"){
                    $country
                        .val("Philippines")
                        .trigger('change');

                    setTimeout(()=>{
                        selectProvince(
                            data.address.state ||
                            data.address.region
                        );
                    },500);
                }
            }catch(error){
                console.error(
                    "Location detection failed",
                    error
                );
            }
        });
    }

    function selectProvince(name){
        const province = provinces.find(p =>
            p.name
                .toLowerCase()
                .includes(
                    (name || "")
                        .toLowerCase()
                )
        );

        if(province){
            $province
                .val(province.code)
                .trigger('change');
        }
    }

    function reset($element, disable=false){
        $element
            .empty()
            .append(`<option value="">Select</option>`);

        $element
            .prop('disabled', disable);
    }
}