$(document).ready(function () {
    const game = $('#RatingForm').find('input[name="game"]').val();
    const player = $('#RatingForm').find('input[name="player"]').val();
    checkRatedStars(game, player);
});

function getAverageRating(game) {
    this.game = game
    $.ajax({
        url: "rating/getAverageRating?game=" + game,
    }).done(function (html) {
        $("#AverageRating").html(html);
    });
}

function sendRating() {
    $('#RatingForm').submit();
}

function checkRatedStars(game, player) {
    $.ajax({
        url: "rating/getRating?game=" + game + "&player=" + player,
    }).done(function (rating) {
        $("#" + rating).prop("checked", true);
    });
}

