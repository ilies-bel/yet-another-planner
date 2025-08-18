package com.iliesbel.yapbackend.domain.users

import com.iliesbel.yapbackend.domain.users.domain.Device
import com.iliesbel.yapbackend.domain.users.domain.DeviceService
import com.iliesbel.yapbackend.domain.users.domain.UserService
import com.iliesbel.yapbackend.infra.userAgent.UserAgent
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class UserController(
    private val userService: UserService,
    private val deviceService: DeviceService
) {

    @PostMapping("/users")
    fun saveUser(@RequestBody user: UserToCreate): Long {
        return userService.createOrUpdate(user)
    }

    @PostMapping("/users/current/devices")
    fun addDeviceToUser(@RequestBody device: DeviceCreationCommand, userAgent: UserAgent): UUID {
        return userService.addDeviceToUser(userAgent, device)
    }

    @GetMapping("/users/current/devices")
    fun getCurrentUserDevices(): List<Device> {
        return deviceService.getCurrentUserDevices()
    }

    @DeleteMapping("/users/current/devices/{deviceId}")
    fun deleteDevice(@PathVariable deviceId: String) {
        deviceService.deleteDevice(deviceId)
    }
}

data class DeviceCreationCommand(
    val name: String,
)


data class UserToCreate(
    val name: String,
)