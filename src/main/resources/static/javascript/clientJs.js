'use strict'


$('#options').on('click', function(event) {
    console.log("hello");
    var val = $(this).find('input').val();
    $('#output').html(val);
});


function filterInformation(type) {

    $(type).button('toggle')

    $(filterIds[type]).hidden = false

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

window.onload = function() {

    var filterIds = {
        detail: document.getElementById('detail'),
        licenses: document.getElementById('licenses'),
        vulnerabilities: document.getElementById('vulnerabilities')
    }

    document.querySelectorAll('#option-list')
        .forEach(function (elem) {
            elem.addEventListener('click', function () {
                var filter = filterIds[elem.firstElementChild.getAttribute('name')]
                filter.hidden = false
                for (var f in filterIds) {
                    if (filter.id !== f) {
                        filterIds[f].hidden = true
                    }
                }
            })
        })
}