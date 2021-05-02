let isDarkMode = false;

function darkMode() {
    $("body").toggleClass("dark-mode");
    $(".master-header").toggleClass("dark-mode");
    $("#signUpButton").toggleClass("dark-mode");
    $(".card").toggleClass("dark-mode");
    isDarkMode = !isDarkMode;
}

$(document).ready(function () {
    $("#LogFormButton").click(function () {
        const login = $('#LogForm').find('input[name="login"]').val();
        const password = $('#LogForm').find('input[name="password"]').val();
        loginOrAlert(login, password);
    });
});

$(document).ready(function () {
    $("#SignUpFormButton").click(function () {
        const login = $('#RegisterForm').find('input[name="login"]').val();
        const password = $('#RegisterForm').find('input[name="password"]').val();
        signUpOrAlert(login, password);
    });
});

$(document).ready(function () {
    $("#ChangeNameButton").click(function () {
        const currentLogin = $('#ChangeNameForm').find('input[name="currentLogin"]').val();
        const currentPassword = $('#ChangeNameForm').find('input[name="currentPassword"]').val();
        const newLogin = $('#ChangeNameForm').find('input[name="newLogin"]').val();
        changeName(currentLogin, currentPassword, newLogin);
    });
});

$(document).ready(function () {
    $("#ChangePasswordButton").click(function () {
        const currentLogin = $('#ChangePasswordForm').find('input[name="currentLogin"]').val();
        const currentPassword = $('#ChangePasswordForm').find('input[name="currentPassword"]').val();
        const newPassword = $('#ChangePasswordForm').find('input[name="newPassword"]').val();
        changePassword(currentLogin, currentPassword, newPassword);
    });
});

function changePercentage(){
    $.ajax({
        url: "solitaire/gameProgressPercentage",
    }).done(function (percentage) {
        document.getElementById('GameProgressBar').style.width = percentage + '%';
    });
}

function changePassword(currentLogin, currentPassword, newPassword) {
    $.ajax({
        url: "/getUser?login=" + currentLogin + "&password=" + currentPassword,
    }).then(function (user) {
        if (user.includes(currentLogin) && user.includes(currentPassword)) {
            if (!newPassword.match(/^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$/)) {
                swal({
                    title: "New Password Incorrect",
                    text: "Min 8 characters, at least 1 letter and 1 number.",
                    icon: "warning",
                    button: "Try again",
                });
            } else {
                $("#ChangePasswordForm").submit();
            }
        } else {
            swal({
                title: "Wrong LogIn Parameters",
                text: "Please check your Username or Password",
                icon: "warning",
                button: "Try again",
            });
        }
    });
}

function changeName(currentLogin, currentPassword, newLogin) {
    $.ajax({
        url: "/getUser?login=" + currentLogin + "&password=" + currentPassword,
    }).then(function (user) {
        if (user.includes(currentLogin) && user.includes(currentPassword)) {
            $.ajax({
                url: "/getUser?login=" + newLogin,
            }).then(function (user2) {
                if (user2.includes(newLogin)) {
                    swal({
                        title: "Used Nickname",
                        text: "Your New Username is already used.",
                        icon: "warning",
                        button: "Try again",
                    });
                } else {
                    $("#ChangeNameForm").submit();
                }
            });

        } else {
            swal({
                title: "Wrong LogIn Parameters",
                text: "Please check your Username or Password",
                icon: "warning",
                button: "Try again",
            });
        }
    });
}

function loginOrAlert(login, password) {
    $.ajax({
        url: "/getUser?login=" + login + "&password=" + password,
    }).then(function (user) {
        if (user.includes(login) && user.includes(password)) {
            $("#LogForm").submit();
        } else {
            swal({
                title: "Cannot Login !",
                text: "Please check your Username or Password",
                icon: "error",
                button: "Try again",
            });
        }
    });
}

function signUpOrAlert(login, password) {
    $.ajax({
        url: "/getUser?login=" + login,
    }).then(function (user) {
        if (user.includes(login)) {
            swal({
                title: "Used Nickname",
                text: "Your Username is already used.",
                icon: "error",
                button: "Try again",
            });
        } else if (!password.match(/^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$/)) {
            swal({
                title: "Wrong password !",
                text: "Min 8 characters, at least 1 letter and 1 number.",
                icon: "warning",
                button: "Try again",
            });
        } else {
            $("#RegisterForm").submit();
        }
    });
}