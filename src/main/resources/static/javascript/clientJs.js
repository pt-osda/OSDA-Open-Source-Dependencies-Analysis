'use strict'

function httpRequest(method, path, data, cb) {
    const xhr = new XMLHttpRequest()
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

/**
 * Adds a user to a project
 * @param projectId the id of the project
 */
function addUser(projectId) {
    const userName = document.getElementById("user-name").value
    const userProjectList = document.getElementById("user-project-list")
    httpRequest('PUT', `/projs/${projectId}/user/${userName}`, null, function(err, data) {
        userProjectList.innerHTML += '<li class="list-group-item">' + userName + '</li>'
    })
}

/**
 * Logs out a user
 */
function logout() {
    httpRequest('POST', `/logout`, null, function(err, data) {
        location.reload()
    })
}

/**
 * Gets a user token
 */
function getToken() {
    const tokenElem = document.getElementById("token-info")
    tokenElem.innerText = "...Loading"
    httpRequest('PUT', `/user/token`, null, function(err, data) {
        tokenElem.innerText = data
    })
}

/**
 * Searches for a specific element
 * @param searchElemId the id for the element that holds the value for the search
 * @param searchRelUrl the url for the search
 * @param searchOutputElemId the id for the element that shows the output
 */
function search(searchElemId, searchRelUrl, searchOutputElemId) {
    const searchValue = document.getElementById(searchElemId).value
    const outputElement = document.getElementById(searchOutputElemId)

    httpRequest('GET', `/${searchRelUrl}?value=${searchValue}`, null, function(err, data) {
        outputElement.innerHTML = data
    })
}

/**
 * Ignores a vulnerability
 * @param projectId the id of the project
 * @param reportId the id of the report
 * @param dependencyId the id of the dependency
 * @param dependencyVersion the version of the dependency
 * @param vulnerabilityId the id of the vulnerability
 */
function ignoreVulnerability(projectId, reportId, dependencyId, dependencyVersion, vulnerabilityId) {
    httpRequest('PUT', '/report/dependency/vulnerability/edit', JSON.stringify({projectId,reportId,dependencyId,dependencyVersion,vulnerabilityId}), function(err, data) {
        const linkElement = document.getElementById(vulnerabilityId)
        const buttonElement = document.getElementById(vulnerabilityId+'-btn')
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

/**
 * Filters the affected versions for specific cases that they are too large
 * @param outputElemId the id of the element to show the output
 * @param affectedVersions the affected versions
 */
function filterAffectedVersions(outputElemId, affectedVersions) {
    const outputElem = document.getElementById(outputElemId)
    if(affectedVersions.includes('&')) {
        outputElem.innerHTML += '<li class="list-group-item">' + affectedVersions + '</li>'
    } else {
        const versions = affectedVersions.split(';')
        versions.forEach(elem => outputElem.innerHTML += '<li class="list-group-item">' + elem + '</li>')
    }
}