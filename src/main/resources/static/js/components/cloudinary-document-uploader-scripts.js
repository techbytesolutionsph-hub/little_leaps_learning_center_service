let uploadRequest;
/**
 * =========================================================
 * PROGRESS REPORT FILE UPLOAD
 * =========================================================
 */
function initializeDocumentUpload() {

    const $fileInput = $('#progressReportFile');
    const $dropZone = $('#fileDropZone');
    const $placeholder = $('#uploadPlaceholder');

    const $selectedFileContainer = $('#selectedFileContainer');
    const $selectedFileName = $('#selectedFileName');
    const $selectedFileSize = $('#selectedFileSize');

    const $chooseFileBtn = $('#chooseFileBtn');
    const $removeFileBtn = $('#removeFileBtn');

    /**
     * =====================================================
     * CHOOSE FILE BUTTON
     * =====================================================
     */
    $chooseFileBtn.on('click', function (event) {
        event.preventDefault();
        event.stopPropagation();
        $fileInput.trigger('click');
    });

    /**
     * =====================================================
     * DROP ZONE CLICK
     * =====================================================
     */
    $dropZone.on('click', function (event) {
        if ($(event.target).is('#progressReportFile')
            || $(event.target).closest('button').length) {
            return;
        }

        $fileInput.trigger('click');
    });

    /**
     * =====================================================
     * FILE INPUT CHANGE
     * =====================================================
     */
    $fileInput.on('change', async function (event) {
        const file = event.target.files[0];
        if (!file) { return; }
        await processDocument(file);
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


    /**
     * =====================================================
     * DROP FILE
     * =====================================================
     */
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
            $fileInput[0].files = dataTransfer.files;
        } catch (error) {
            console.warn('Unable to assign dropped file to input.', error);
        }

        await processDocument(file);
    });


    /**
     * =====================================================
     * REMOVE FILE
     * =====================================================
     */
    $removeFileBtn.on('click', function (event) {
        event.preventDefault();
        event.stopPropagation();
        removeDocument();
    });


    /**
     * =====================================================
     * PROCESS DOCUMENT
     * =====================================================
     */
    async function processDocument(file) {

        if (!validateDocument(file)) {
            $fileInput.val('');
            return;
        }

        displaySelectedFile(file);
        setUploadingState(true);

        const data = await uploadDocumentToCloudinary(file);
        console.log(data);

        if (!data) {
            removeDocument();
            return;
        }

        $selectedFileName.text(`${data.original_filename}.${data.format}`);
        $selectedFileSize.text(`${data.size_mb} MB`);

        setUploadingState(false);
    }

    /**
     * =====================================================
     * VALIDATE DOCUMENT
     * =====================================================
     */
    function validateDocument(file) {

        const allowedTypes = [
            'application/pdf',
            'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
            'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
        ];

        /* Validate file type */
        if (!allowedTypes.includes(file.type)) {
            showErrorPopup('Error','Please select a valid PDF, DOCX, or XLSX file.');
            return false;
        }

        /* Maximum file size: 5 MB */
        const maxSize = 5 * 1024 * 1024;

        if (file.size > maxSize) {
            showErrorPopup('Error', 'The maximum allowed file size is 5 MB.');
            return false;
        }

        return true;
    }

    /**
     * =====================================================
     * UPLOAD TO CLOUDINARY
     * =====================================================
     */
    async function uploadDocumentToCloudinary(file) {
        const formData = new FormData();
        formData.append('file', file);
        formData.append('upload_preset','lllcdocumentuploads');

        let data;
        try {
            const response = await fetch(
                'https://api.cloudinary.com/v1_1/fe7zqjnd/auto/upload',
                {
                    method: 'POST',
                    body: formData
                }
            );

            if (!response.ok) {
                throw new Error(`Cloudinary upload failed: ${response.status}`);
            }

            data = await response.json();
            uploadRequest = data;

            $("#fileDropZone").removeClass("has-error");

            /* Validate secure URL */
            if (!data.secure_url) {
                throw new Error('Cloudinary did not return a secure URL.');
            }

            /* Validate original filename */
            if (!data.original_filename) {
                throw new Error('Cloudinary did not return the original filename.');
            }

            /* Validate file size */
            if (typeof data.bytes !== 'number') {
                throw new Error('Cloudinary did not return the file size.');
            }

            const sizeMB = data.bytes / (1024 * 1024);

            /* Return normalized response */
            return {
                secure_url: data.secure_url,
                original_filename: data.original_filename,
                bytes: data.bytes,
                size_mb: sizeMB.toFixed(2)
            };
        } catch (error) {
            console.error('Cloudinary document upload error:', error);
            showErrorPopup('Error','Document upload failed. Please try again.');
            return null;
        }
    }

    async function deleteDocumentFromCloudinary(publicId) {

        const timestamp = Math.round(Date.now() / 1000);
        const stringToSign = `invalidate=true&public_id=${publicId}&timestamp=${timestamp}`;
        const signature = await generateCloudinarySignature(stringToSign);
        const formData = new FormData();

        formData.append('public_id', publicId);
        formData.append('timestamp', String(timestamp));
        formData.append('api_key', '316519133398881');
        formData.append('signature', signature);
        formData.append('invalidate', 'true');

        try {
            const response = await fetch('https://api.cloudinary.com/v1_1/fe7zqjnd/image/destroy',
                {
                    method: 'POST',
                    body: formData
                }
            );

            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.error?.message || 'Cloudinary deletion failed.');
            }

            if (data.result === 'ok') {
                return true;
            }

            return false;

        } catch (error) {
            console.error('Cloudinary document deletion error:', error);
            return false;
        }
    }


    async function generateCloudinarySignature(stringToSign) {
        const apiSecret = 'U4IC3ORH4Pla3qIRbN1gxrp2VHQ';
        return CryptoJS.SHA1(stringToSign + apiSecret).toString(CryptoJS.enc.Hex);
    }

    /**
     * =====================================================
     * DISPLAY SELECTED FILE
     * =====================================================
     */
    function displaySelectedFile(file) {

        const sizeMB = (file.size / (1024 * 1024)).toFixed(2);

        $selectedFileName.text(file.name);
        $selectedFileSize.text(`${sizeMB} MB`);
        $selectedFileContainer.show();

        $placeholder.hide();
        $chooseFileBtn.hide();
        $removeFileBtn.css('display', 'flex');
    }

    /**
     * =====================================================
     * REMOVE DOCUMENT
     * =====================================================
     */
    function removeDocument() {

        const publicId = uploadRequest.public_id;

        deleteDocumentFromCloudinary(publicId)
            .then(function (data) {
                if (data) {
                    console.log('Cloudinary delete response:', data);
                }
            })
            .catch(function (error) {
                console.error('Cloudinary delete error:', error);
            });

        $fileInput.val('');
        $selectedFileName.text('');
        $selectedFileSize.text('');

        $selectedFileContainer.hide();

        $placeholder.show();
        $chooseFileBtn.show();

        $removeFileBtn.hide();
        $dropZone.removeClass('drag-over');

        uploadRequest = null;
    }

    /**
     * =====================================================
     * UPLOADING STATE
     * =====================================================
     */
    function setUploadingState(isUploading) {

        if (isUploading) {
            $chooseFileBtn
                .prop('disabled', true)
                .addClass('uploading');

            $removeFileBtn
                .prop('disabled', true);

            $chooseFileBtn.html(`
                <i class="fa-solid fa-spinner fa-spin"></i>
                Uploading...
            `);
        } else {
            $chooseFileBtn
                .prop('disabled', false)
                .removeClass('uploading');

            $removeFileBtn
                .prop('disabled', false);

            $chooseFileBtn.html(`
                <i class="fa-solid fa-cloud-arrow-up"></i>
                Choose File
            `);
        }
    }
}