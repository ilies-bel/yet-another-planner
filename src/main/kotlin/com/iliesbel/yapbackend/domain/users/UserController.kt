package com.iliesbel.yapbackend.domain.users

import com.iliesbel.yapbackend.infra.userAgent.UserAgent
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class UserController(private val userService: UserService) {

    @PostMapping("/users")
    fun saveUser(@RequestBody user: UserToCreate): Long {
        return userService.saveUser(user)
    }

    @PostMapping("/users/current/devices")
    fun addDeviceToUser(userAgent: UserAgent): UUID {
        return userService.addDeviceToUser(userAgent)
    }
}


data class UserToCreate(
    val name: String,
)