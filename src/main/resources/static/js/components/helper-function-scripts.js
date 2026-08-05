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