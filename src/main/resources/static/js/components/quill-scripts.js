function createQuill(id){
    const quill = new Quill(id, {
        theme: "snow",
        placeholder: "Enter size guide...",
        modules: {
            toolbar: {
                container: [
                    [{
                        header: [1, 2, false]
                    }],
                    ["bold", "italic", "underline"],
                    [{
                        list: "ordered"
                    }, {
                        list: "bullet"
                    }],
                    ["image", "clean"]
                ],
                handlers: {
                    image: function() {
                        imageHandler(quill);
                    }
                }
            }
        }
    });
    return quill;
}

function createQuillForCoupon(id){
    const quill = new Quill(id, {
        theme: "snow",
        placeholder: "Enter size guide...",
        modules: {
            toolbar: {
                container: [
                    ["bold", "italic", "underline"],
                    [{
                        list: "ordered"
                    }, {
                        list: "bullet"
                    }]
                ],
                handlers: {
                    image: function() {
                        imageHandler(quill);
                    }
                }
            }
        }
    });
    return quill;
}

function viewQuillForCoupon(id){
    const quill = new Quill(id, {
        theme: "snow",
        readOnly: true,
        modules: {
            toolbar: {
                container: [
                    ["bold", "italic", "underline"],
                    [{
                        list: "ordered"
                    }, {
                        list: "bullet"
                    }]
                ],
                handlers: {
                    image: function() {
                        imageHandler(quill);
                    }
                }
            }
        }
    });
    return quill;
}

function imageHandler(quill) {
    const input = document.createElement('input');
    input.setAttribute('type', 'file');
    input.setAttribute('accept', 'image/*');
    input.click();

    input.onchange = async () => {
        const file = input.files[0];
        if (!file) return;

        const url = await uploadToCloudinary(file);
        if (url) {
            const range = quill.getSelection();
            quill.insertEmbed(range ? range.index : quill.getLength(), 'image', url);
        }
    };
}