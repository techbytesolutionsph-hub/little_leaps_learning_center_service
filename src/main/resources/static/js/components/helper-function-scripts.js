function initializeImageUpload(imageUploadSelector = '.image-upload') {

    /* Image Upload Preview */
    $(document).on('change', imageUploadSelector, async function (event) {

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

    /* Remove Image */
    $(document).on('click', '.remove-image', function () {
        const $wrapper = $(this).closest('.color-upload-wrapper');

        $wrapper.find('.image-preview')
            .attr('src', '')
            .hide();

        $wrapper
            .siblings('.custom-file-upload')
            .find(imageUploadSelector)
            .val('');

        $(this).remove();
    });
}

async function uploadToCloudinary(file) {

    const formData = new FormData();
    formData.append("file", file);
    formData.append("upload_preset", "littleleapslearningcenter");

    try {
        const response = await fetch(
            "https://api.cloudinary.com/v1_1/fe7zqjnd/image/upload",
            {
                method: "POST",
                body: formData
            }
        );

        const data = await response.json();
        return data.secure_url;

    } catch (error) {
        console.error("Cloudinary upload error:", error);
        alert("Upload failed. Try again.");
        return null;
    }
}

/**
 * =========================================================
 * CLIENT PHOTO UPLOAD
 * =========================================================
 */
function initializeClientPhotoUpload() {

    const $photoInput = $('#clientPhoto');
    const $dropZone = $('#photoDropZone');
    const $placeholder = $('#photoPlaceholder');
    const $preview = $('#clientPhotoPreview');
    const $chooseButton = $('#choosePhotoBtn');
    const $removeButton = $('#removePhotoBtn');
    const $profileImageUrl = $('#profileImageUrl');


    /*
     * -----------------------------------------------------
     * Choose File button
     * -----------------------------------------------------
     */

    $chooseButton.on('click', function (event) {
        event.preventDefault();
        $photoInput.trigger('click');
    });

    /*
     * -----------------------------------------------------
     * Click anywhere on drop zone
     * -----------------------------------------------------
     */

    $dropZone.on('click', function (event) {
        if ($(event.target).is('#clientPhoto')) {
            return;
        }
        $photoInput.trigger('click');
    });

    $photoInput.on('change', async function (event) {
        const file = event.target.files[0];

        if (!file) {
            return;
        }
        await processClientPhoto(file);
    });

    $dropZone.on('dragover', function (event) {
        event.preventDefault();
        event.originalEvent.dataTransfer.dropEffect = 'copy';
        $dropZone.addClass('drag-over');
    });


    $dropZone.on('dragleave', function (event) {
        event.preventDefault();
        $dropZone.removeClass('drag-over');
    });

    $dropZone.on('drop', async function (event) {
        event.preventDefault();
        $dropZone.removeClass('drag-over');

        const files = event.originalEvent.dataTransfer.files;

        if (!files || files.length === 0) {
            return;
        }

        const file = files[0];

        try {
            const dataTransfer = new DataTransfer();
            dataTransfer.items.add(file);
            $photoInput[0].files = dataTransfer.files;
        } catch (error) {
            console.warn('Unable to assign dropped file to input.', error);
        }

        await processClientPhoto(file);
    });

    $removeButton.on('click', function (event) {
        event.preventDefault();
        event.stopPropagation();
        removeClientPhoto();
    });


    /**
     * -----------------------------------------------------
     * Process selected image
     * -----------------------------------------------------
     */
    async function processClientPhoto(file) {

        if (!validateClientPhoto(file)) {
            $photoInput.val('');
            return;
        }

        const reader = new FileReader();
        reader.onload = function (event) {

            $preview
                .attr('src', event.target.result)
                .show();

            $placeholder.hide();
        };

        reader.readAsDataURL(file);

        setPhotoUploadingState(true);

        const url = await uploadClientPhotoToCloudinary(file);


        /*
         * Upload failed
         */
        if (!url) {
            removeClientPhoto();
            return;
        }

        $profileImageUrl.val(url);
        $preview.attr('src', url).show();
        $placeholder.hide();
        $chooseButton.hide();
        $removeButton.css('display', 'flex');
    }


    function validateClientPhoto(file) {
        const allowedTypes = ['image/jpeg', 'image/png'];

        if (!allowedTypes.includes(file.type)) {
            showErrorPopup("Error", 'Please select a valid JPG or PNG image.');
            return false;
        }

        /*
         * Validate file size
         *
         * Maximum = 1 MB
         */
        const maxSize = 1 * 1024 * 1024;

        if (file.size > maxSize) {
            showErrorPopup("Error", 'The maximum allowed image size is 1 MB.');
            return false;
        }

        return true;
    }

    /**
     * -----------------------------------------------------
     * Upload to Cloudinary
     * -----------------------------------------------------
     */
    async function uploadClientPhotoToCloudinary(file) {

        const formData = new FormData();
        formData.append('file', file);

        formData.append('upload_preset', 'littleleapslearningcenter');

        try {

            const response = await fetch('https://api.cloudinary.com/v1_1/fe7zqjnd/image/upload',
                {
                    method: 'POST',
                    body: formData
                }
            );

            /*
             * Check HTTP status
             */
            if (!response.ok) {
                throw new Error(`Cloudinary upload failed: ${response.status}`);
            }

            const data = await response.json();

            if (!data.secure_url) {
                throw new Error('Cloudinary did not return a secure URL.');
            }

            return data.secure_url;
        } catch (error) {
            console.error('Cloudinary upload error:', error);
            showErrorPopup("Error", 'Photo upload failed. Please try again.');
            return null;
        } finally {
            setPhotoUploadingState(false);
        }
    }

    /**
     * -----------------------------------------------------
     * Remove photo
     * -----------------------------------------------------
     */
    function removeClientPhoto() {

        $photoInput.val('');
        $profileImageUrl.val('');
        $preview
            .attr('src', '')
            .hide();

        $placeholder.show();
        $chooseButton.show();
        $removeButton.hide();
        $dropZone.removeClass('drag-over');
    }

    function setPhotoUploadingState(isUploading) {
        if (isUploading) {
            $chooseButton
                .prop('disabled', true)
                .addClass('uploading');

            $removeButton.prop('disabled', true);

            $chooseButton.html(`
                            <i class="fa-solid fa-spinner fa-spin"></i>
                            Uploading...
                        `);
        } else {
            $chooseButton
                .prop('disabled', false)
                .removeClass('uploading');

            $removeButton.prop('disabled', false);

            if ($preview.is(':visible')) {
                $chooseButton.html(`
                                <i class="fa-solid fa-cloud-arrow-up"></i>
                                Choose File
                            `);
            } else {
                $chooseButton.html(`
                                <i class="fa-solid fa-cloud-arrow-up"></i>
                                Choose File
                            `);
            }
        }
    }
}