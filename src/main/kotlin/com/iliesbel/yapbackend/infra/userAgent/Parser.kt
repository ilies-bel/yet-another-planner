package com.iliesbel.yapbackend.infra.userAgent

import com.blueconic.browscap.UserAgentParser
import com.blueconic.browscap.UserAgentService

object Parser {
    val parser: UserAgentParser = UserAgentService().loadParser() // handle IOException and ParseException
}