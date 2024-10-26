package org.seungkyu.board.config

import io.jsonwebtoken.SignatureException
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.util.*

class JwtTokenProviderTest{

    private val jwtTokenProvider = JwtTokenProvider("testSecretKey")


    /**
     * @author: Seungkyu-Han
     * valid 토큰에서 아이디를 반환하는지 검증
     */
    @Test
    fun test_get_valid_id(){
        //given
        val userId = UUID.randomUUID().toString()
        val userRole = "user"

        //when
        val token = jwtTokenProvider.getJwtToken(
            id = userId, role = userRole)

        //then
        StepVerifier
            .create(Mono.just(jwtTokenProvider.getId(token)))
            .expectNext(userId)
            .verifyComplete()
    }

    /**
     * @author: Seungkyu-Han
     * @exception SignatureException
     * invalid 토큰에서 에러가 발생하는지 검증
     */
    @Test
    fun test_get_invalid_id(){
        //given
        val userId = UUID.randomUUID().toString()
        val userRole = "user"

        //when
        val token = jwtTokenProvider.getJwtToken(id = userId, role = userRole) + "suffix"

        //then
        StepVerifier
            .create(Mono.fromCallable{jwtTokenProvider.getId(token)})
            .expectError(SignatureException::class.java)
            .verify()
    }

    /**
     * @author: Seungkyu-Han
     * valid 토큰에서 권한을 반환하는지 검증
     */
    @Test
    fun test_get_valid_role(){
        //given
        val userId = UUID.randomUUID().toString()
        val userRole = "user"

        //when
        val token = jwtTokenProvider.getJwtToken(
            id = userId, role = userRole)

        //then
        StepVerifier
            .create(Mono.just(jwtTokenProvider.getRole(token)))
            .expectNext(userRole)
            .verifyComplete()
    }

    /**
     * @author: Seungkyu-Han
     * @exception SignatureException
     * invalid 토큰에서 에러가 발생하는지 검증
     */
    @Test
    fun test_get_invalid_role(){
        //given
        val userId = UUID.randomUUID().toString()
        val userRole = "user"

        //when
        val token = jwtTokenProvider.getJwtToken(id = userId, role = userRole) + "suffix"

        //then
        StepVerifier
            .create(Mono.fromCallable{jwtTokenProvider.getRole(token)})
            .expectError(SignatureException::class.java)
            .verify()
    }

}