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

function initializeFragments() {
    var fragment = window.location.hash.substring(1)

    if(fragment && fragment !== 'detail' && (fragment === 'licenses' || fragment === 'vulnerabilities')) {
        document.querySelector(`#detail`).classList.remove('active')
        document.querySelector(`#${fragment}`).classList.add('active')

        window.addEventListener('load', function() {
            window.onhashchange = hashChange
            document.getElementById(fragment).click()
        })
    }
}

function hashChange() {
    var fragment = window.location.hash.substring(1)
    document.getElementById(fragment).click()
}

function search(searchElemId, searchRelUrl, searchOutputElemId) {
    var searchValue = document.getElementById(searchElemId).value
    var outputElement = document.getElementById(searchOutputElemId)

    httpRequest('GET', `/${searchRelUrl}?value=${searchValue}`, null, function(err, data) {
        outputElement.innerHTML = data
    })
}

function filter(filterUrl, fragmentId) {
    var informationToShow = document.getElementById('show-information')

    window.location.hash = fragmentId

    informationToShow.innerHTML = '<div> Loading... </div>'

    httpRequest('GET', `/${filterUrl}`, null, function(err, data) {
        informationToShow.innerHTML = data
    })
}