package org.seungkyu.board.service.impl

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
import org.seungkyu.board.dto.req.RegisterReq
import org.seungkyu.board.repository.UserMongoRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.reactive.function.server.ServerRequest
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@ExtendWith(SpringExtension::class, MockitoExtension::class)
@ContextConfiguration(
    classes = [UserServiceImpl::class, JwtTokenProvider::class, BCryptPasswordEncoder::class]
)
class UserServiceImplTest @Autowired constructor(
    private val bcryptPasswordEncoder: BCryptPasswordEncoder,
) {
    @MockBean
    private lateinit var jwtTokenProvider: JwtTokenProvider
    private lateinit var userServiceImpl: UserServiceImpl


    @MockBean
    private lateinit var userMongoRepository: UserMongoRepository

    private val serverRequest: ServerRequest = mock()

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        userServiceImpl = UserServiceImpl(bcryptPasswordEncoder, jwtTokenProvider, userMongoRepository)
    }

    @Nested
    inner class Register {

        private val testId = "testId"
        private val testPassword = "testPassword"
        private val testNickname = "testNickname"

        @Test
        fun register_with_uniqueId_return_created(){
            mono{
                //given
                `when`(serverRequest.bodyToMono(RegisterReq::class.java))
                    .thenReturn(Mono.just(
                        RegisterReq(
                            userId = testId, password = testPassword, nickname = testNickname,
                        )
                    ))

                `when`(userMongoRepository.existsByUserId(testId))
                    .thenReturn(Mono.just(false))

                `when`(userMongoRepository.save(any())).thenReturn(Mono.empty())

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
        fun register_with_duplicateId_return_created(){
            mono{
                //given
                `when`(serverRequest.bodyToMono(RegisterReq::class.java))
                    .thenReturn(Mono.just(
                        RegisterReq(
                            userId = testId, password = testPassword, nickname = testNickname,
                        )
                    ))

                `when`(userMongoRepository.existsByUserId(testId))
                    .thenReturn(Mono.just(true))

                `when`(userMongoRepository.save(any())).thenReturn(Mono.empty())

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

    }
}