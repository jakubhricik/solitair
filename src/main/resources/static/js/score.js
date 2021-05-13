let score;


function sendScore(game, isLoggedIn) {
    $.ajax({
        url: game + "/getCurrentScore",
    }).done(function (points) {
        score = points;
        document.getElementById('ScorePoints').value = points;
    }).then(() => {
        checkIfIsLogin(isLoggedIn)
    });
}

function updateScore(game) {
    $.ajax({
        url: game + "/getCurrentScore",
    }).done(function (points) {
        $('#GameScore').html(points);
    });
}

function checkIfIsLogin(isLoggedIn) {

    swal({
        title: "You Won !",
        text: "Your Score is " + score,
        icon: "success",
        buttons: ["Don't Save", "Save Score"]
    }).then((saveScore) => {
        if (saveScore) {
            if (!isLoggedIn) {
                swal({
                    title: "Need to LogIn",
                    text: "Login or SignUp to save Score",
                    icon: "warning",
                    buttons: ["Log In", "Sign Up"]
                }).then((signUp) => {
                    if (signUp) {
                        document.getElementById('SignUpButton').click();
                    } else {
                        document.getElementById('LogInButton').click();
                    }
                });
            } else {
                $('#ScoreForm').submit();
                startNewGame();
            }
        } else {
            startNewGame();
        }
    });


}