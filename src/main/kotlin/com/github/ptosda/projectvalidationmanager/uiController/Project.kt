package com.github.ptosda.projectvalidationmanager.uiController

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import kotlin.collections.HashMap
import kotlin.collections.set

@Controller
class Project {

    @GetMapping("/")
    fun test (model: HashMap<String, Any>) : String{
        model["name"] = "Testing Mustache"
        model["description"] = "This is a test"
        model["title"] = "Testing UI"
        return "index"
    }

}