package com.github.ptosda.projectvalidationmanager.webapp.controller

import com.github.ptosda.projectvalidationmanager.websecurity.HashManager
import com.github.ptosda.projectvalidationmanager.websecurity.service.SecurityServiceImpl
import com.github.ptosda.projectvalidationmanager.websecurity.service.UserService
import com.github.ptosda.projectvalidationmanager.database.entities.Token
import com.github.ptosda.projectvalidationmanager.database.entities.User
import com.github.ptosda.projectvalidationmanager.database.repositories.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.view.RedirectView
import java.sql.Timestamp
import java.util.*
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("/")
class UserController(val userService: UserService, //TODO meter a negrito os titulos das propriedades como por exemplo na lista de licenças : "License Name", etc
                     val securityService: SecurityServiceImpl,
                     val tokenRepo: TokenRepository,
                     val userRepo: UserRepository)
{
    @GetMapping("login")
    fun getHome(model: HashMap<String, Any>, req: HttpServletRequest) : String
    {
        model["page_title"] = "Login"

        return "user/login"
    }

    @GetMapping("user")
    fun getUser(model: HashMap<String, Any?>,
                req: HttpServletRequest) : String
    {
        model["page_title"] = "User Detail"

        val userName = securityService.findLoggedInUsername() ?: throw Exception("No logged in user")

        val user = userService.getUser(userName).get()

        model["username"] = user.username
        model["name"] = user.name
        model["projects"] = user.projects
        model["token"] = user.token

        return "user/user-detail"
    }

    @PutMapping("user/token")
    fun generateUserToken() : ResponseEntity<String>
    {
        val userName = securityService.findLoggedInUsername()

        val user = userService.getUser(userName!!).get()

        if(user.token != null) {
            val token = user.token!!
            user.token = null
            userRepo.save(user)
            tokenRepo.delete(token)
        }

        val timestamp = Timestamp(System.currentTimeMillis())
        val tokenString: String = (user.name + timestamp.toString())

        val buffer = tokenString.toByteArray(Charsets.UTF_8)
        val hashManager = HashManager()

        val newToken = Token(hashManager.hashToHex(buffer))

        tokenRepo.save(newToken)
        user.token = newToken
        userRepo.save(user)

        return ResponseEntity(Base64.getEncoder().encodeToString(buffer), HttpStatus.OK)
    }

    @PutMapping("projs/{project-id}/user/{username}")
    fun addUserToProject(@PathVariable("project-id") projectId : String,
                         @PathVariable("username") userName : String) : ResponseEntity<String>
    {
        val userInfo = userService.getUser(userName)

        if(!userInfo.isPresent) {
            return ResponseEntity("User $userName does not exist", HttpStatus.OK)
        }

        userService.addUserToProject(userName, projectId)

        return ResponseEntity("Added project to user successfully", HttpStatus.OK)
    }

    @PostMapping("logout")
    fun checkLogin(@RequestParam body: Map<String, String>) : String
    {
        return "home"
    }

    @GetMapping("register")
    fun getRegister(model: HashMap<String, Any>) : String
    {
        model["page_title"] = "Register"

        return "user/register"
    }

    @PostMapping("register")
    fun postRegister(@RequestParam body: Map<String, String>) : RedirectView
    {
        val user = User(body["name"]!!, body["username"]!!, body["password"]!!, null, null)
        if(!userService.getUser(body["username"]!!).isPresent) {
            userService.save(user)
        }
        securityService.autoLogin(body["username"]!!, body["password"]!!)
        return RedirectView("/")
    }
}