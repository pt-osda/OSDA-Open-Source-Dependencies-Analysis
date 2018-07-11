package com.github.ptosda.projectvalidationmanager.webapp.controller

import com.github.ptosda.projectvalidationmanager.SecurityServiceImpl
import com.github.ptosda.projectvalidationmanager.UserService
import com.github.ptosda.projectvalidationmanager.database.entities.User
import com.github.ptosda.projectvalidationmanager.database.repositories.*
import com.github.ptosda.projectvalidationmanager.webapp.service.ReportService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.view.RedirectView
import java.util.*
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("/")
class UserController(val userService: UserService, //TODO meter a negrito os titulos das propriedades como por exemplo na lista de licenças : "License Name", etc
                     val securityService: SecurityServiceImpl) {

    @GetMapping("login")
    fun getHome(model: HashMap<String, Any>, req: HttpServletRequest) : String
    {
        model["page_title"] = "Login"

        val referrer = req.getHeader("Referer")
        if (referrer != null) {
            req.session.setAttribute("url_prior_login", referrer)
        }

        return "user/login"
    }

    @PostMapping("login")
    fun checkLogin() : String
    {

        return "hehehe"
    }

    @GetMapping("register")
    fun getRegister(model: HashMap<String, Any>) : String
    {
        model["page_title"] = "Register"

        return "user/register"
    }

    @PostMapping(value = ["register"])
    fun postRegister(@RequestParam body: Map<String, String>) : RedirectView
    {
        val user = User(body["name"]!!, body["username"]!!, body["password"]!!, null, null)
        if(!userService.getUser("1111").isPresent) {
            userService.save(user)
        }
        securityService.autoLogin(body["username"]!!, body["password"]!!)
        return RedirectView("/")
    }

}