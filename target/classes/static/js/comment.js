let currentOffset = 0;
let maxCommentsPerSite = 5;

function updateComments(game) {
    $.ajax({
        url: "comment/getComments?game=" + game + "&offset=" + currentOffset + "&max=" + maxCommentsPerSite,
    }).done(function (comments) {
        $('#GameComments').html(comments);
    });
}

function nextPage(game) {
    if (getNumberOfComments() === maxCommentsPerSite)
        currentOffset++;

    $.ajax({
        url: "comment/getComments?game=" + game + "&offset=" + currentOffset + "&max=" + maxCommentsPerSite,
    }).done(function (comments) {
        $('#GameComments').html(comments);
    });

}

function previousPage(game) {
    if (currentOffset > 0)
        currentOffset--;
    $.ajax({
        url: "comment/getComments?game=" + game + "&offset=" + currentOffset + "&max=" + maxCommentsPerSite,
    }).done(function (comments) {
        $('#GameComments').html(comments);
    });

}

function getNumberOfComments() {
    return document.getElementsByClassName('comment').length;
}
