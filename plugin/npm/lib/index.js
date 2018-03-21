'use strict'

const fs = require('fs')

switch(process.argv[2]){

case 'read':{
	const fileData = fs.readFileSync('./package.json', 'utf-8')
	console.log(fileData)
	break
}
case 'write':{
	const fileData = fs.readFileSync('./package.json', 'utf-8')
	writeToFile('./lib/testFileWrite.txt', fileData)
	break
}

}


function writeToFile(filePath, data){
	const fileDescriptor = fs.openSync(filePath, 'w')
	fs.writeFileSync(fileDescriptor, data, 'utf-8')
	fs.closeSync(fileDescriptor)
}