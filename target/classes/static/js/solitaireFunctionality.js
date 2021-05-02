let canUndo = false;
let hardMode;

$(document).ready(isHardMode());
$(document).ready(refreshGameBoard());
$(document).ready(getAverageRating('solitaire'));


function startNewGame() {
    if (canUndo === true) {
        canUndo = false;
        $("#UndoButton").toggleClass("disabled");
    }
    $.ajax({
        url: "solitaire/new",
    }).done(function (html) {
        $("#gameBoard").html(html);
        if(hardMode){
            hardMode = !hardMode;
            $("#hardModeCheckbox").prop("checked", hardMode);
        }
        reinitialize();
    });
}

function refreshGameBoard() {
    $.ajax({
        url: "solitaire/gameBoard",
    }).done(function (html) {
        $("#gameBoard").html(html);
        reinitialize();
    });
}

function dealCard() {
    if (canUndo === false) {
        canUndo = true;
        $("#UndoButton").toggleClass("disabled");
    }
    $.ajax({
        url: "solitaire/deal",
    }).done(function (html) {
        $("#StockTalonBoard").html(html);
        reinitialize();
    });
}

function moveCards(source, destination, count) {
    if (canUndo === false) {
        canUndo = true;
        $("#UndoButton").toggleClass("disabled");
    }

    $.ajax({
        url: "solitaire/moveCards?sourcePile=" + source + "&destinationPile=" + destination + "&numberOfCards=" + count,
    }).done().then(()=>{
            if(source.includes('foundation')){
                foundation(sourcePile);
            }
            else if(source.includes('tableau')){;
                tableau(sourcePile);
            }
            else if(source.includes('talon')){
                stockTalon();
            }
            if(destination.includes('foundation')){
                foundation(destinationPile);
            }
            else if(destination.includes('tableau')){
                tableau(destinationPile);
            }
            reinitialize();
    });
}

function stockTalon(){
    $.ajax({
        url: "solitaire/talonStock",
    }).done(function (html) {
        $("#StockTalonBoard").html(html);
        reinitialize();
    });
}

function isHardMode(){
    $.ajax({
        url: "solitaire/isHardMode",
    }).done(function (html) {
        hardMode = !!html.includes('true');
        $( ".form-check-input" ).prop( "checked", hardMode );
    });
}

function foundation(pile){
    $.ajax({
        url: "solitaire/foundation?sourcePile=" + getPileNumber(pile),
    }).done(function (html) {
        let id = '#foundation' + getPileNumber(pile);
        $(id).html(html);
    });
    reinitialize();
}

function tableau(pile){
    $.ajax({
        url: "solitaire/tableau?sourcePile=" + getPileNumber(pile),
    }).done(function (html) {
        let id = '#tableau' + getPileNumber(pile);
        $(id).html(html);
    });
    reinitialize();
}

function undo() {
    if (canUndo === true) {
        $.ajax({
            url: "solitaire/undo",
        }).done(function (html) {
            $("#gameBoard").html(html);
            reinitialize();
        });
        canUndo = false;
        $("#UndoButton").toggleClass("disabled");
    }
}

function changeCardsTheme() {
    $.ajax({
        url: "solitaire/cardTheme",
    }).done(function (html) {
        $("#gameBoard").html(html);
        if (isDarkMode){
            $(".card").toggleClass("dark-mode");
        }
        reinitialize();
    });
}

function toggleHardMode(){
    $.ajax({
        url: "solitaire/hardMode",
    }).done(function (html) {
        $("#gameBoard").html(html);
        hardMode = !hardMode;
        reinitialize();
    });
}

function changeColHeight(){
    const tableau = document.getElementById('tableauBoard');
    const cols = tableau.querySelectorAll('.col');

    for( const col of cols){
        const cards = col.querySelectorAll('.card');
        col.style.height = (cards.length * 50) + 100 +  'px';
    }
}

function getPileNumber(pile){
    for( let i = 0; i<7; i++){
        if(pile.includes(i)) return i.toString() ;
    }
}