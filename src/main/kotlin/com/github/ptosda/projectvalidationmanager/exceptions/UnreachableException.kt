package com.github.ptosda.projectvalidationmanager.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_GATEWAY)
class UnreachableException(message: String?) : RuntimeException(message)