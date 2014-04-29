var SearchBox = (function () {
    function SearchBox(inputField, searchButton, suggestionBox, getSuggestions) {
        var self = this;
        self.selectedSuggestion = -1;
        self.inputField = inputField;
    	self.searchButton = searchButton;
    	self.suggestionsList = suggestionBox;
        self.suggestions = [];
    	
        self.inputField.oninput = function(){ 
            getSuggestions(self.inputField.value, self.setSuggestions);
        };
        self.inputField.onkeydown = function(evt) {
            evt = evt || window.event;
            if (evt.keyCode === 38) {
                evt.preventDefault();
                
            }else if(evt.keyCode === 40){
                evt.preventDefault();
                
            }else if(evt.keyCode === 13){
                
            }
        };
        self.setSuggestions = function (suggestions) {
            self.suggestions = suggestions;
            
            var str = '';
            for(s in suggestions){
                str += '<li>' + suggestions[s] + '</li>';
            }
            if(str === ''){
                document.getElementById('search-suggestions-box').hidden = true;
            }else{
                document.getElementById('search-suggestions-box').hidden = false;
            }
            self.suggestionsList.innerHTML = str;
        };
    }
    return SearchBox;
})();

function getSuggestions(str, callback){
    var request = new XMLHttpRequest();
    request.open('GET', '/AutoComplete/Suggester?q=' + str, true);

    request.onload = function() {
      if (request.status >= 200 && request.status < 400){
        // Success!
        callback(JSON.parse(request.responseText));
      } else {
        // We reached our target server, but it returned an error

      }
    };

    request.onerror = function() {
      // There was a connection error of some sort
    };

    request.send();
}

var searchBox = new SearchBox(document.getElementById("search-field"),
                              document.getElementById("search-button"),
                              document.getElementById("search-suggestions"),
                              getSuggestions);
