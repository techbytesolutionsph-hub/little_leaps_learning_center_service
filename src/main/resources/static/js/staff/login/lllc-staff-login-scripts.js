$(document).ready(function () {

    const errorDiv = $("#login-error");

    $("form").on("submit", function (e) {
        if (!this.checkValidity()) {
            e.preventDefault();
            this.reportValidity();
            return;
        }

        e.preventDefault();

        login();
    });

    function login(){
        const request = buildLoginRequest();
        submitSellerLogin(request, errorDiv);
    }

    function buildLoginRequest() {
        return {
            username: $('#staff-username').val(),
            password: $('#staff-password').val()
        };
    }

    function submitSellerLogin(loginRequest, errorDiv) {
        errorDiv.text("").hide();

        fetch("/api/v1/auth/staff/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(loginRequest),
            credentials: "include"
        })
        .then(response => {
            console.log(response);
            if (!response.ok) {
                return response.json().then(data => {
                    return Promise.reject(data?.message || "Login failed");
                });
            }
            return response.json();
        })
        .then(data => {
            redirectPage("/lllc/staff/dashboard");
        })
        .catch(error => {
            showToast("error", "Error", error);
        });
    }
});