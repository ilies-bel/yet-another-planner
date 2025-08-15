package com.iliesbel.yapbackend.infra.userAgent

import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import java.util.*


class UserAgentMethodArgumentResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == UserAgent::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any {
        val deviceId = webRequest.getHeader("X-Device")?.let { UUID.fromString(webRequest.getHeader("X-Device")) }

        val userAgentString = webRequest.getHeader("User-Agent")
            ?: throw IllegalArgumentException("User-Agent header is missing")

        return UserAgent(userAgentString, deviceId)
    }
}