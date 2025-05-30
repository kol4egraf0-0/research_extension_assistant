document.addEventListener('DOMContentLoaded', ()=>{
    chrome.storage.local.get(['researchNotes'], function(result){
        if(result.researchNotes){
            document.getElementById('notes').value = result.researchNotes
        }
    });
    document.getElementById('summarizeBtn').addEventListener('click', summarizeText);
    document.getElementById('saveNotesBtn').addEventListener('click', saveNotes)
})

// Функция для суммирования выделенного текста
async function summarizeText(){
    try {
        const [tab] = await chrome.tabs.query({active:true, currentWindow:true})
        const [{result}] = await chrome.scripting.executeScript({
            target:{tabId: tab.id},
            function: () => window.getSelection().toString()
        });
        if(!result){
            showResults("Please select text w mouse first");
            return;
        }

        const response = await fetch("http://localhost:8080/api/research/process", { //вывод с запроса
            method: 'POST',
            headers: {'Content-type': 'application/json'},
            body: JSON.stringify({content: result, operation:'summarize'})
        })

        if(!response.ok){
            throw new Error(`API ERROR: ${response.status}`);
        }
        const text = await response.text();
        showResults(text.replace(/\n/g,'<br>'));

    } catch (error) {
        showResults('Error: ' + error.message);
    }
}

async function saveNotes(){
    const notes = document.getElementById('notes').value;
    //сохраняем в буфер
    try {
        await navigator.clipboard.writeText(notes); 
        alert('Заметки скопированы в буфер обмена!');
    } catch (err) {
        alert('Ошибка при копировании: ' + err.message);
    }

}

function showResults(content){
    document.getElementById('results').innerHTML=`<div class="result-item"><div class="result-content">${content}</div></div>`;
}