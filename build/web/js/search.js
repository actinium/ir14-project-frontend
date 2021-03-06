(function() {
    var searchButton = document.getElementById('search-button');
    var searchField = document.getElementById('search-field');
    var resultList = document.getElementById('results-list');
    searchButton.onclick = function() {
        var request = new XMLHttpRequest();
        request.open('GET', '/AutoComplete/Searcher?q=' + searchField.value, true);

        request.onload = function() {
            if (request.status >= 200 && request.status < 400) {
                // Success!
                var data = JSON.parse(request.responseText);
                var str = '';
                for(var d in data){
                    str += '<div class="result-box">';
                    str += '<details>';
                    str += '<summary><p class="label">' + data[d].name + ' (' + data[d].party + ')' + '</p></summary>';
                    str += '<p>' + data[d].content + '</p>';
                    str += '</details>';
                    str += '</div>';
                }
                resultList.innerHTML = str;
            } else {
                // We reached our target server, but it returned an error
            }
        };
        request.onerror = function() {
            // There was a connection error of some sort
        };
        request.send();
    };
})();
