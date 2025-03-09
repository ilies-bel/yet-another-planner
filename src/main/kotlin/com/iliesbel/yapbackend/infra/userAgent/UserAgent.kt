package com.iliesbel.yapbackend.infra.userAgent

import com.iliesbel.yapbackend.infra.userAgent.Parser.parser
import java.util.*

class UserAgent(userAgentString: String, val deviceId: UUID?) {

    private val capabilities = parser.parse(userAgentString)

    val deviceType: String
        get() {
            return capabilities.deviceType
        }


    val platform: String
        get() {
            return capabilities.platform
        }

    val platformVersion: String
        get() {
            return capabilities.platformVersion
        }


    val browser: String
        get() {
            return capabilities.browser
        }
}