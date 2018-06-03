'use strict'

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

    var informationToShow = document.getElementById('show-information')

    document.querySelectorAll('#option-list')
        .forEach(function (elem) {
            elem.addEventListener('click', function () {
                var filter = elem.firstElementChild.getAttribute('name')

                httpRequest('GET', 'http://localhost:8080/' + filter, null, function(err, data) {
                    informationToShow.innerHTML = data
                })
            })
        })
}