'use strict'

function httpRequest(method, path, data, cb) {
    var xhr = new XMLHttpRequest()
    xhr.open(method, path, true)

    xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");

    xhr.onreadystatechange = function() {
        if(xhr.readyState === XMLHttpRequest.DONE) {
            if(xhr.status === 200)
                cb( null, xhr.response )
        }
    }
    xhr.send(data);
}

function search(searchElemId, searchRelUrl, searchOutputElemId) {
    var searchValue = document.getElementById(searchElemId).value
    var outputElement = document.getElementById(searchOutputElemId)

    httpRequest('GET', `/${searchRelUrl}?value=${searchValue}`, null, function(err, data) {
        outputElement.innerHTML = data
    })
}

function filterElementToShow(filterUrl, outputElemId, tabToShowId) {
    $('#'+tabToShowId).tab('show')

    var informationToShow = document.getElementById(outputElemId)
    informationToShow.innerHTML = '<div> Loading... </div>'

    httpRequest('GET', `/${filterUrl}`, null, function(err, data) {
        informationToShow.innerHTML = data
    })
}