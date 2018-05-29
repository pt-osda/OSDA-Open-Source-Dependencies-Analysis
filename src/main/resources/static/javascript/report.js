'use strict'


var errorListId = 'error-list'
var dependencyListId = 'dependency-list'
var successListClass = 'list-group-item list-group-item-success'
var errorListClass = 'list-group-item list-group-item-danger'

function filterVulnerableDependencies(vulnerabilitiesCount, text) {
    console.log(vulnerabilitiesCount)

    if(vulnerabilitiesCount > 0) {
        var errorList = document.getElementById(errorListId)

        errorList.innerHTML += text.replace('%s', errorListClass)
    }
    else {
        var dependencyList = document.getElementById(dependencyListId)
        dependencyList.innerHTML += text.replace('%s', successListClass)
    }
}

var filterTypeId = 'filterTypeSelect'
var filterTextId = 'filterText'

function filterHomeScreen(filter) {

    var filterType
    var filterText

    if(!filter) {
        var filterTypeNode = document.getElementById(filterTypeId)
        filterType = filterTypeNode.options[filterTypeNode.selectedIndex].text

        filterText = document.getElementById(filterTextId).value

        if(filterText === '') {
            var filterError = document.getElementById("filter-error")
            filterError.innerText = 'Text cannot be empty'
            return
        }
    else {
           filterType = "None"
           filterText = "None"
        }
    }


    httpRequest('GET', 'http://localhost:8080/report/filter/'+filterType+'/'+filterText, null, filterCb)
}

function filterCb (err, data) {
    var projectList = document.getElementById("project-list")
    projectList.innerHTML = data
}
function httpRequest(method, path, data, cb) {
    var xhr = new XMLHttpRequest()
    xhr.open(method, path, true)

    xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");

    xhr.onreadystatechange = function() {
        if(xhr.readyState === XMLHttpRequest.DONE) {
            if(xhr.status === 200){
                cb( null, xhr.response )
            }
            else{
                document.open()
                document.write(xhr.response)
                document.close()
            }
        }
    }
    xhr.send(data);
}