
// Once the DOM is ready, attach event listeners
$(document).ready(function () {
    // Call lookup() on click
    $("#searchbutton").click(function () {
        lookup();
    });

    // Call lookup() on enter, if focus is in #searchbox
    $("#searchbox").keypress(function (e) {
        if (e.which == 13)
            lookup();
    });
});

// Look up the search query and write the results 
function lookup() {
    console.log("Sending request to server.");
    var baseUrl = "http://localhost:8080";
    var t0 = performance.now();

    $.ajax({
        method: "GET",
        url: baseUrl + "/search",
        data: { query: $('#searchbox').val() }
    }).success(function (data) {
        var t1 = performance.now();
        console.log("Received response " + JSON.stringify(data));
        $("#responsesize").html("<p>Deep Blue retrieved " + data.length + " websites in " + (t1 - t0) + " milliseconds. " + kasparov(data.length) + ".</p>");
        
        const regex = /"|\[|\]/gm;
        var buffer = "";

        $.each(data, function (index, value) {
            // Convert the JSON data to a string, replace commas with spaces 
            // and the special characters defined by the regular expression by the empty string. 
            // Finally, get the substring from index 0 to 140
            var preview = JSON.stringify(value.words).replace(/,/g, " ").replace(regex, "").substring(0, 140);
            buffer +=   "<div>\n" + 
                            "<a href=\"" + value.url + "\" target=\"_blank\" rel=\"noopener\">\n" + 
                                "<span>" + value.title + "</span>\n" + 
                                "<br>\n" + 
                                "<cite>" + value.url + "</cite>" + 
                            "</a>\n" + 
                            "<p>" + preview + "...</p>\n" +
                        "</div>\n";
        });

        $("#urllist").html(buffer);
    });
}

// Returns a random response regarding the activities of Garry Kasparov
function kasparov(responseSize){
    var responses = ["Kasparov is still computing", "Kasparov is contemplating e4", "Kasparov retrieved none", "Kasparov regrets the Sicilian Defense"];
    
    if(responseSize > 0){
        return responses[Math.floor(Math.random() * responses.length)];
    } else {
        return "Kasparov gloats excessively"
    }
}