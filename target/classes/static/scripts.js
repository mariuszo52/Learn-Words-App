const toggle = document.querySelector(".menu-toggle");
const menu = document.querySelector(".menu");
const learnButton = document.querySelector(".learn-button");
const mainContentDiv = document.querySelector(".main-content-repeat");
const translationP = document.createElement("p");
const sentenceP = document.createElement("p");
const selectLanguage = document.querySelector("#select-language");

function toggleMenu() {
    if (menu.classList.contains("expanded")) {
        menu.classList.remove("expanded");
    } else {
        menu.classList.add("expanded");

    }
}

toggle.addEventListener("click", toggleMenu, false);

function translateWord(){
    const translation = word.translation;
    const sentence = word.sentence;
    translationP.appendChild(document.createTextNode(translation));
    sentenceP.appendChild(document.createTextNode(sentence));
    mainContentDiv.appendChild(translationP);
    mainContentDiv.appendChild(sentenceP);
    learnButton.setAttribute("disabled", false);
    const badAnswerButton = document.createElement("a");
    const goodAnswerButton = document.createElement("a");
    badAnswerButton.setAttribute("class", "learn-button");
    badAnswerButton.setAttribute("id", "bad-answer-button");
    let goodLink = new URL(document.URL);
    goodLink.searchParams.set("value", "good");
    goodAnswerButton.href = goodLink.toString();
    let badLink = new URL(document.URL);
    badLink.searchParams.set("value", "bad");
    badAnswerButton.href = badLink.toString();
    badAnswerButton.textContent = "Zła odpowiedź";
    goodAnswerButton.setAttribute("class", "learn-button");
    goodAnswerButton.textContent = "Dobra odpowiedź";
    mainContentDiv.appendChild(badAnswerButton);
    mainContentDiv.appendChild(goodAnswerButton);
}
if(learnButton) {
    learnButton.addEventListener("click", translateWord, false);
}


function setResultTableStyles() {
    let i=1;
    for (let resultListElement of resultList) {
        let tds = document.querySelectorAll(".result-td-" + i);
        if (!resultListElement.goodAnswer) {
            tds.forEach(td => {
                td.style.backgroundColor = "darkred";
            });
        }
        else {
            tds.forEach(td => {
                td.style.backgroundColor = "darkgreen";
            });

        }
        i++;
    }

}
if(resultList !== null){
    setResultTableStyles();
}

function setSelectedLanguage(){
    selectLanguage.value = language;

}
if (selectLanguage !== null) {
    setSelectedLanguage();
}







