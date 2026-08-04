$(document).ready(function () {
    flatpickr("#employee-birth-date", {
        enableTime: false,
        altInput: true,
        altFormat: "Y-m-d",
        dateFormat: "Y-m-d",
        maxDate: "today",
        altInputClass: "form-control text-sm birthdate-input",
        onReady: function(selectedDates, dateStr, instance) {
            instance.altInput.setAttribute("placeholder", "Select birth date");
        },
        position: "auto"
    })

    flatpickr("#employee-date-hired", {
        enableTime: false,
        altInput: true,
        altFormat: "Y-m-d",
        dateFormat: "Y-m-d",
        maxDate: "today",
        altInputClass: "form-control text-sm date-hired-input",
        onReady: function(selectedDates, dateStr, instance) {
            instance.altInput.setAttribute("placeholder", "Select date hired");
        },
        position: "auto"
    });

    flatpickr("#employee-regular-date-start", {
        enableTime: false,
        altInput: true,
        altFormat: "Y-m-d",
        dateFormat: "Y-m-d",
        altInputClass: "form-control text-sm regular-date-start-input",
        onReady: function(selectedDates, dateStr, instance) {
            instance.altInput.setAttribute("placeholder", "Select date start");
        },
        position: "auto"
    });

    flatpickr("#employee-salary-effective-date", {
        enableTime: false,
        altInput: true,
        altFormat: "Y-m-d",
        dateFormat: "Y-m-d",
        altInputClass: "form-control text-sm alary-effective-date-input",
        onReady: function(selectedDates, dateStr, instance) {
            instance.altInput.setAttribute("placeholder", "Select effective date");
        },
        position: "auto"
    });

    /* Image Upload Preview */
    $(document).on('change', '.image-upload', async function (event) {

        const file = event.target.files[0];

        const $wrapper = $(this)
            .closest('.mb-4')
            .find('.color-upload-wrapper');

        const $img = $wrapper.find('.image-preview');

        $img.hide();
        $wrapper.find('.remove-image').remove();

        if (!file) return;

        if (!file.type.startsWith('image/')) {
            alert('Please select a valid image file.');
            $(this).val('');
            return;
        }

        const url = await uploadToCloudinary(file);

        if (url) {
            $img.attr('src', url).show();
        }
    });

    /* Remove image */
    $(document).on('click', '.remove-image', function () {
        const $wrapper = $(this).closest('.color-upload-wrapper');
        $wrapper.find('.image-preview').attr('src', '').hide();
        $wrapper.siblings('.custom-file-upload').find('.image-upload').val('');
        $(this).remove();
    });

    /* Cloudinary upload function */
    async function uploadToCloudinary(file) {
        const formData = new FormData();
        formData.append("file", file);
        formData.append("upload_preset", "littleleapslearningcenter");

        try {
            const res = await fetch("https://api.cloudinary.com/v1_1/fe7zqjnd/image/upload", {
                method: "POST",
                body: formData
            });
            const data = await res.json();
            return data.secure_url;
        } catch (err) {
            console.error("Cloudinary upload error:", err);
            alert("Upload failed. Try again.");
            return null;
        }
    }

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