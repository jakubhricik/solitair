const movables = document.querySelectorAll('.movable');
const dropAbles = document.querySelectorAll('.dropAble');


// Fill listeners
for ( const card of movables){
    card.addEventListener('dragstart', dragStart);
    card.addEventListener('dragend', dragEnd);
}

for ( const stack of dropAbles){
    stack.addEventListener('dragover', dragOver);
    stack.addEventListener('dragenter', dragEnter);
    stack.addEventListener('dragleave', dragLeave);
    stack.addEventListener('drop', dragDrop);
}

let sourcePile = 'none';
let destinationPile = 'none';
let numberOfCards = 1;


// Drag Functions

function dragStart() {
    sourcePile = getPileName(this.className);
    numberOfCards = getNumberOfDraggedCards(this.className);

    setTimeout(() => (this.style.display = 'none'), 0);

    if (numberOfCards > 1){
        for (const card of movables){
            if(card.className.includes(sourcePile)){
                for(let i = 1; i < numberOfCards; i++){
                    if(card.className.includes("cardNum"+i)){
                        setTimeout(() => (card.style.display = 'none'), 0);
                    }
                }
            }
        }

    }else{
        setTimeout(() => (this.style.display = 'none'), 0);
    }
}

function dragEnd() {
    for (const card of movables){
        card.style.display = 'block';
    }
}

function dragOver(e){
    e.preventDefault();
}

function dragEnter(e){
    e.preventDefault();
    destinationPile= getPileName(this.className);
    console.log(destinationPile);
}

function dragLeave(){

}

function dragDrop(){
    location.href = "/solitaire?move=true&sourcePile=" + sourcePile + "&destinationPile=" + destinationPile + "&numberOfCards=" + numberOfCards;
}

function getPileName(className){
    if(className.includes("talon")) return "talon";
    else if( className.includes("tableau")){
        for( let i = 0; i<7; i++){
            if(className.includes("tableau" + i)) return ("tableau" + i);
        }
    }
    else if( className.includes("foundation")){
        for( let i = 0; i<4; i++){
            if(className.includes("foundation" + i)) return ("foundation" + i);
        }
    }
    else return null;
}

function getNumberOfDraggedCards(className){
    if(className.includes("cardNum")){
        for(let i = 0; i<14; i++){
            if(className.includes("cardNum" + i)) return i;
        }
    }
    return 1;
}

