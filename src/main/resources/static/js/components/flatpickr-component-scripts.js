function initializeDatePicker(id, placeholder) {
    flatpickr(id, {
        enableTime: false,
        altInput: true,
        altFormat: "Y-m-d",
        dateFormat: "Y-m-d",
        altInputClass: "form-control text-sm default-input",
        onReady: function (selectedDates, dateStr, instance) {
            instance.altInput.setAttribute("placeholder", placeholder);
        },
        position: "auto"
    });
}

function initializeMaxTodayDatePicker(id, placeholder) {
    flatpickr(id, {
        enableTime: false,
        altInput: true,
        altFormat: "Y-m-d",
        dateFormat: "Y-m-d",
        maxDate: "today",
        altInputClass: "form-control text-sm default-input",
        onReady: function (selectedDates, dateStr, instance) {
            instance.altInput.setAttribute("placeholder", placeholder);
        },
        position: "auto"
    });
}

function initializeMinTodayDatePicker(id, placeholder) {
    flatpickr(id, {
        enableTime: false,
        altInput: true,
        altFormat: "Y-m-d",
        dateFormat: "Y-m-d",
        minDate: "today",
        altInputClass: "form-control text-sm default-input",
        onReady: function (selectedDates, dateStr, instance) {
            instance.altInput.setAttribute("placeholder", placeholder);
        },
        position: "auto"
    });
}

function initializePastDatePicker(id, placeholder) {
    flatpickr(id, {
        enableTime: false,
        altInput: true,
        altFormat: "Y-m-d",
        dateFormat: "Y-m-d",
        maxDate: new Date().fp_incr(-1),
        altInputClass: "form-control text-sm default-input",
        onReady: function (selectedDates, dateStr, instance) {
            instance.altInput.setAttribute("placeholder", placeholder);
        },
        position: "auto"
    });
}

function initializeFutureDatePicker(id, placeholder) {
    flatpickr(id, {
        enableTime: false,
        altInput: true,
        altFormat: "Y-m-d",
        dateFormat: "Y-m-d",
        minDate: new Date().fp_incr(1),
        altInputClass: "form-control text-sm default-input",
        onReady: function (selectedDates, dateStr, instance) {
            instance.altInput.setAttribute("placeholder", placeholder);
        },
        position: "auto"
    });
}

function initializeDateRangePicker(id, placeholder, minDate = null, maxDate = null) {
    flatpickr(id, {
        mode: "range",
        enableTime: false,
        altInput: true,
        altFormat: "Y-m-d",
        dateFormat: "Y-m-d",
        minDate: minDate,
        maxDate: maxDate,
        altInputClass: "form-control text-sm default-input",
        onReady: function (selectedDates, dateStr, instance) {
            instance.altInput.setAttribute("placeholder", placeholder);
        },
        position: "auto"
    });
}

function initializeMonthYearPicker(id, placeholder) {
    flatpickr(id, {
        plugins: [
            new monthSelectPlugin({
                shorthand: false,
                dateFormat: "Y-m",
                altFormat: "F Y"
            })
        ],
        altInput: true,
        altInputClass: "form-control text-sm default-input",
        onReady: function (selectedDates, dateStr, instance) {
            instance.altInput.setAttribute("placeholder", placeholder);
        },
        position: "auto"
    });
}

function initializeYearPicker(id, placeholder) {
    flatpickr(id, {
        plugins: [
            new monthSelectPlugin({
                shorthand: false,
                dateFormat: "Y",
                altFormat: "Y"
            })
        ],
        altInput: true,
        altInputClass: "form-control text-sm default-input",
        onReady: function (selectedDates, dateStr, instance) {
            instance.altInput.setAttribute("placeholder", placeholder);
        },
        position: "auto"
    });
}

function initializeDateTimePicker(id, placeholder) {
    flatpickr(id, {
        enableTime: true,
        dateFormat: "Y-m-d H:i",
        altInput: true,
        altFormat: "Y-m-d h:i K",
        altInputClass: "form-control text-sm default-input",
        time_24hr: false,
        onReady: function (selectedDates, dateStr, instance) {
            instance.altInput.setAttribute("placeholder", placeholder);
        },
        position: "auto"
    });
}

function initializeTimePicker(id, placeholder) {
    flatpickr(id, {
        enableTime: true,
        noCalendar: true,
        dateFormat: "H:i",
        altInput: true,
        altFormat: "h:i K",
        altInputClass: "form-control text-sm default-input",
        time_24hr: false,
        onReady: function (selectedDates, dateStr, instance) {
            instance.altInput.setAttribute("placeholder", placeholder);
        },
        position: "auto"
    });
}

function initializeMultipleDatePicker(id, placeholder) {
    flatpickr(id, {
        mode: "multiple",
        enableTime: false,
        altInput: true,
        altFormat: "Y-m-d",
        dateFormat: "Y-m-d",
        altInputClass: "form-control text-sm default-input",
        onReady: function (selectedDates, dateStr, instance) {
            instance.altInput.setAttribute("placeholder", placeholder);
        },
        position: "auto"
    });
}