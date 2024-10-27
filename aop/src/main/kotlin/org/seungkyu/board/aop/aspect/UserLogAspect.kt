package org.seungkyu.board.aop.aspect

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.seungkyu.board.config.JwtTokenProvider
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.ServerRequest

@Aspect
@Configuration
open class UserLogAspect(
    private val jwtTokenProvider: JwtTokenProvider
) {

    companion object {
        private val log = LoggerFactory.getLogger(UserLogAspect::class.java)
    }

    @Around("@annotation(org.seungkyu.board.aop.UserLog)")
    fun userLogger(joinPoint: ProceedingJoinPoint): Any {

        log.info("Executing method: {}", joinPoint.signature)

        val authorization = (joinPoint.args[0] as ServerRequest)
            .headers().firstHeader("Authorization") ?: return joinPoint.proceed()

        log.info("Request userId: {}", jwtTokenProvider.getId(authorization.removePrefix("Bearer ")))

        return joinPoint.proceed()
    }
}