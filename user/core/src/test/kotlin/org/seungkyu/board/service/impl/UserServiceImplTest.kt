package org.seungkyu.board.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.jupiter.MockitoExtension
import org.seungkyu.board.config.JwtTokenProvider
import org.seungkyu.board.config.SeungkyuAuthentication
import org.seungkyu.board.data.enums.Role
import org.seungkyu.board.dto.req.LoginReq
import org.seungkyu.board.dto.req.RegisterReq
import org.seungkyu.board.dto.res.LoginRes
import org.seungkyu.board.dto.res.UserInfoRes
import org.seungkyu.board.entity.UserDocument
import org.seungkyu.board.repository.UserMongoRepository
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.http.codec.HttpMessageWriter
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.web.server.MockServerWebExchange
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.result.view.ViewResolver
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.util.*

@ExtendWith(SpringExtension::class, MockitoExtension::class)
@ContextConfiguration(
    classes = [UserServiceImpl::class, JwtTokenProvider::class, BCryptPasswordEncoder::class]
)
class UserServiceImplTest {
    @MockBean
    private lateinit var jwtTokenProvider: JwtTokenProvider
    @MockBean
    private lateinit var bcryptPasswordEncoder: BCryptPasswordEncoder
    @MockBean
    private lateinit var userMongoRepository: UserMongoRepository
    private lateinit var userServiceImpl: UserServiceImpl

    private val objectMapper = ObjectMapper()
    private val serverRequest: ServerRequest = mock()


    private val testId = "testId"
    private val testPassword = "testPassword"
    private val testNickname = "testNickname"
    private val testToken = "testToken"
    private val testRole = Role.USER

    private val testUserDocument =  UserDocument(
        id = null,
        userId = testId,
        password = testPassword,
        nickName = testNickname,
        isAdmin = false,
        isWithDraw = false,
        status = testRole.toString(),
        createdAt = null, updatedAt = null
    )


    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        userServiceImpl = UserServiceImpl(bcryptPasswordEncoder, jwtTokenProvider, userMongoRepository)
    }

    @Nested
    inner class Register {

        @Test
        fun register_with_uniqueId_return_created(){
            mono{
                //given
                registerMock(false)

                //when
                val target = Mono.just(userServiceImpl.register(serverRequest))

                //then
                StepVerifier.create(target)
                    .expectNextMatches {
                        HttpStatus.CREATED == it.statusCode()
                    }
                    .verifyComplete()

            }.block()
        }

        @Test
        fun register_with_duplicateId_return_conflict(){
            mono{
                //given
                registerMock(true)

                //when
                val target = Mono.just(userServiceImpl.register(serverRequest))

                //then
                StepVerifier.create(target)
                    .expectNextMatches {
                        HttpStatus.CONFLICT == it.statusCode()
                    }
                    .verifyComplete()

            }.block()
        }

        private fun registerMock(isExist: Boolean){
            `when`(serverRequest.bodyToMono(RegisterReq::class.java))
                .thenReturn(Mono.just(
                    RegisterReq(
                        userId = testId, password = testPassword, nickname = testNickname,
                    )
                ))

            `when`(userMongoRepository.existsByUserId(testId))
                .thenReturn(Mono.just(isExist))

            `when`(userMongoRepository.save(any())).thenReturn(Mono.empty())

            `when`(bcryptPasswordEncoder.encode(testPassword))
                .thenReturn(testPassword)
        }
    }

    @Nested
    inner class Login{

        private val testLoginReq = LoginReq(
            id = testId, password = testPassword
        )

        private val testUserDocument =  UserDocument(
            id = null,
            userId = testLoginReq.id,
            password = testLoginReq.password,
            nickName = "testNickname",
            isAdmin = false,
            isWithDraw = false,
            status = Role.USER.toString(),
            createdAt = null, updatedAt = null
        )

        @Test
        fun login_with_validId_return_token() {
            mono {
                //given
                `when`(serverRequest.bodyToMono(LoginReq::class.java))
                    .thenReturn(Mono.just(testLoginReq))

                `when`(userMongoRepository.findByUserId(testLoginReq.id))
                    .thenReturn(Mono.just(testUserDocument))

                `when`(jwtTokenProvider.getJwtToken(testLoginReq.id, Role.USER.name))
                    .thenReturn(testToken)

                `when`(bcryptPasswordEncoder.matches(any(), any()))
                    .thenReturn(true)

                //when
                val target = Mono.just(userServiceImpl.login(serverRequest))

                //then

                val expectedLoginRes = LoginRes(
                    token = testToken
                )

                val extractedLoginRes = fetchBody<LoginRes>(target.awaitSingle())

                StepVerifier.create(target)
                    .expectNextMatches {
                        (HttpStatus.OK == it.statusCode()) &&
                                (extractedLoginRes == expectedLoginRes)
                    }.verifyComplete()

            }.block()
        }


        @Test
        fun login_with_invalidId_return_forbidden() {
            mono {
                //given
                `when`(serverRequest.bodyToMono(LoginReq::class.java))
                    .thenReturn(Mono.just(testLoginReq))

                `when`(userMongoRepository.findByUserId(testLoginReq.id))
                    .thenReturn(Mono.just(testUserDocument))

                //when
                val target = Mono.just(userServiceImpl.login(serverRequest))

                //then

                StepVerifier.create(target)
                    .expectNextMatches {
                        HttpStatus.FORBIDDEN == it.statusCode()
                    }.verifyComplete()

            }.block()
        }

        @Test
        fun login_with_invalidPassword_return_forbidden(){
            mono {
                //given
                `when`(serverRequest.bodyToMono(LoginReq::class.java))
                    .thenReturn(Mono.just(testLoginReq))

                `when`(userMongoRepository.findByUserId(testLoginReq.id))
                    .thenReturn(Mono.empty())

                `when`(jwtTokenProvider.getJwtToken(testLoginReq.id, Role.USER.name))
                    .thenReturn(testToken)

                `when`(bcryptPasswordEncoder.matches(any(), any()))
                    .thenReturn(true)

                //when
                val target = Mono.just(userServiceImpl.login(serverRequest))

                //then

                StepVerifier.create(target)
                    .expectNextMatches {
                        HttpStatus.FORBIDDEN == it.statusCode()
                    }.verifyComplete()

            }.block()
        }
    }

    @Nested
    inner class GetUserInfo{

        @Test
        fun getUserInfo_with_validUser_return_ok(){
            mono{
                //given
                `when`(userMongoRepository.findByUserId(testId))
                    .thenReturn(Mono.just(
                        testUserDocument
                    ))

                //when
                val target = Mono.just(userServiceImpl.getUser(serverRequest))

                //then
                val expectedUserInfo = UserInfoRes(
                    userId = testId, nickname = testNickname, isAdmin = (testRole == Role.ADMIN)
                )
                val extractedUserInfo = fetchBody<UserInfoRes>(target.awaitSingle())

                StepVerifier.create(target)
                    .expectNextMatches {
                        (HttpStatus.OK == it.statusCode()) &&
                                (extractedUserInfo == expectedUserInfo)
                    }.verifyComplete()

            }.contextWrite(
                ReactiveSecurityContextHolder.withAuthentication(
                    SeungkyuAuthentication(testId, testRole.name)
                )
            ).block()
        }

        @Test
        fun getUserInfo_with_emptyContext_return_forbidden(){
            mono{
                //when
                val target = Mono.just(userServiceImpl.getUser(serverRequest))

                //then
                StepVerifier.create(target)
                    .expectNextMatches {
                        HttpStatus.FORBIDDEN == it.statusCode()
                    }.verifyComplete()

            }.block()
        }

        @Test
        fun get_user_info_with_invalid_user_return_not_found(){
            mono{
                //given
                `when`(userMongoRepository.findByUserId(testId))
                    .thenReturn(Mono.empty())

                //when
                val target = Mono.just(userServiceImpl.getUser(serverRequest))

                //then
                StepVerifier.create(target)
                    .expectNextMatches {
                        HttpStatus.NOT_FOUND == it.statusCode()
                    }.verifyComplete()

            }.contextWrite(
                ReactiveSecurityContextHolder.withAuthentication(
                    SeungkyuAuthentication(testId, testRole.name)
                )
            ).block()
        }
    }

    private suspend inline fun <reified T> fetchBody(serverResponse: ServerResponse): T {
        val defaultContext: ServerResponse.Context = object : ServerResponse.Context {
            override fun messageWriters(): List<HttpMessageWriter<*>> {
                return HandlerStrategies.withDefaults().messageWriters()
            }

            override fun viewResolvers(): List<ViewResolver> {
                return Collections.emptyList()
            }
        }

        val request = MockServerHttpRequest.get("https://github.com/Seungkyu-Han").build()
        val exchange = MockServerWebExchange.from(request)
        serverResponse.writeTo(exchange, defaultContext).awaitSingleOrNull()
        val response = exchange.response.bodyAsString.awaitSingle()
        return objectMapper.readValue(response, T::class.java)
    }
}