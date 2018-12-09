
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
        console.log("Received response " + data);
        $("#responsesize").html("<p>" + data.length + " websites retrieved</p>\n<p>Deep Blue performed the search in " + (t1 - t0) + " milliseconds</p>\n<p>Garry Kasparov is still computing</p>");
        var buffer = "";

        $.each(data, function (index, value) {
            var preview = JSON.stringify(value.words).replace(/,/g, " ");
            buffer += "<div>\n<a href=\"" + value.url + "\" target=\"_blank\">" + value.title + "</a>\n<p>" + preview + "</p></div>\n";
        });

        $("#urllist").html(buffer);
    });
}