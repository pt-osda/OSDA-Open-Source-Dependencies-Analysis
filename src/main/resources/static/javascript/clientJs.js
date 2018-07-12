'use strict'

function httpRequest(method, path, data, cb) {
    var xhr = new XMLHttpRequest()
    xhr.open(method, path, true)

    xhr.setRequestHeader("Content-type", "application/json");

    xhr.onreadystatechange = function() {
        if(xhr.readyState === XMLHttpRequest.DONE) {
            if(xhr.status === 200)
                cb( null, xhr.response )
        }
    }
    xhr.send(data);
}

function logout() {
    httpRequest('POST', `/logout`, null, function(err, data) {
        location.reload()
    })
}

function getToken() {
    var tokenElem = document.getElementById("token-info")
    tokenElem.innerText = "...Loading"
    httpRequest('PUT', `/user/token`, null, function(err, data) {
        tokenElem.innerText = data
    })
}

function search(searchElemId, searchRelUrl, searchOutputElemId) {
    var searchValue = document.getElementById(searchElemId).value
    var outputElement = document.getElementById(searchOutputElemId)

    httpRequest('GET', `/${searchRelUrl}?value=${searchValue}`, null, function(err, data) {
        outputElement.innerHTML = data
    })
}

function ignoreVulnerability(projectId, reportId, dependencyId, dependencyVersion, vulnerabilityId) {
    httpRequest('PUT', '/report/dependency/vulnerability/edit', JSON.stringify({projectId,reportId,dependencyId,dependencyVersion,vulnerabilityId}), function(err, data) {
        var linkElement = document.getElementById(vulnerabilityId)
        var buttonElement = document.getElementById(vulnerabilityId+'-btn')
        if(linkElement.classList.contains('disabled-link')) {
            linkElement.classList.remove('disabled-link')
            buttonElement.classList.remove('btn-outline-success')
            buttonElement.classList.add('btn-outline-danger')
            buttonElement.innerText = 'Ignore vulnerability'
        }
        else {
            linkElement.classList.add('disabled-link')
            buttonElement.classList.remove('btn-outline-danger')
            buttonElement.classList.add('btn-outline-success')
            buttonElement.innerText = 'Consider vulnerability'
        }
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