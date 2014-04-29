(function() {
    var searchButton = document.getElementById('search-button');
    var searchField = document.getElementById('search-field');
    searchButton.onclick = function() {
        var request = new XMLHttpRequest();
        request.open('GET', '/AutoComplete/Searcher?q=' + searchField.value, true);

        request.onload = function() {
            if (request.status >= 200 && request.status < 400) {
                // Success!
                var data = JSON.parse(request.responseText);
                console.log(data);
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
