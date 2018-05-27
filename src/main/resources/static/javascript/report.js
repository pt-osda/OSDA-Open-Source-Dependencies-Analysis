'use strict'


var errorListId = 'error-list'
var dependencyListId = 'dependency-list'

function filterVulnerableDependencies(vulnerabilitiesCount, text) {
    if(vulnerabilitiesCount > 0) {
        var errorList = document.getElementById(errorListId)
        errorList.innerHTML += text
    }
    else {
        var dependencyList = document.getElementById(dependencyListId)
        dependencyList.innerHTML += text
    }
}

var filterTypeId = 'filterTypeSelect'
var filterTextId = 'filterText'

function filterHomeScreen() {
    var filterTypeNode = document.getElementById(filterTypeId)
    var filterType = filterTypeNode.options[filterTypeNode.selectedIndex].text

    if(filterType === 'None') {
        location.reload(true)
    }

    var filterText = document.getElementById(filterTextId).value

    console.log(filterType)
    console.log(filterText)

    httpRequest('GET', 'http://localhost:8080/report/filter/'+filterType+'/'+filterText, null, filterCb)
}

function filterCb (err, data) {
    console.log(data)
}
function httpRequest(method, path, data, cb) {
    var xhr = new XMLHttpRequest()
    xhr.open(method, path, true)

    //Send the proper header information along with the request
    xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");

    xhr.onreadystatechange = function() {//Call a function when the state changes.
        if(xhr.readyState == XMLHttpRequest.DONE) {
            if(xhr.status == 200){
                if(xhr.getResponseHeader('Content-Type') != 'application/json')
                    cb( null, xhr.response )
                else
                    cb( new Error( JSON.parse(xhr.response).error ) )
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