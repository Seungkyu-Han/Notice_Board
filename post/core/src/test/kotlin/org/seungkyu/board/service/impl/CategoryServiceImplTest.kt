package org.seungkyu.board.service.impl

import kotlinx.coroutines.reactor.mono
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.seungkyu.board.config.SeungkyuAuthentication
import org.seungkyu.board.data.enums.Role
import org.seungkyu.board.dto.req.CategoryPostReq
import org.seungkyu.board.entity.CategoryDocument
import org.seungkyu.board.repository.CategoryMongoRepository
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.reactive.function.server.ServerRequest
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [CategoryServiceImpl::class])
class CategoryServiceImplTest{

    @MockBean
    private lateinit var categoryMongoRepository: CategoryMongoRepository

    private lateinit var categoryServiceImpl: CategoryServiceImpl

    @Mock
    private lateinit var serverRequest: ServerRequest

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        categoryServiceImpl = CategoryServiceImpl(categoryMongoRepository)
    }

    @Nested
    inner class PostCategory{
        private val testCategory = CategoryDocument(
            id = null, name = "testName", isAscending = true, searchCount = 0
        )

        @Test
        fun post_with_admin_return_created(){
            mono{
                //given
                `when`(categoryMongoRepository.save(any()))
                    .thenReturn(Mono.just(testCategory))

                `when`(serverRequest.bodyToMono(CategoryPostReq::class.java))
                    .thenReturn(Mono.just(CategoryPostReq(name = "testCategoryName", isAscending = true)))

                //when
                val target = Mono.just(categoryServiceImpl.post(serverRequest))

                //then
                StepVerifier.create(target)
                    .expectNextMatches {
                        HttpStatus.CREATED == it.statusCode()
                    }
                    .verifyComplete()

            }.contextWrite(
                ReactiveSecurityContextHolder.withAuthentication(
                    SeungkyuAuthentication("testName", Role.ADMIN.name))
            ).block()
        }

        @Test
        fun post_with_user_return_created(){
            mono{
                //given

                //when
                val target = Mono.just(categoryServiceImpl.post(serverRequest))

                //then
                StepVerifier.create(target)
                    .expectNextMatches {
                        HttpStatus.FORBIDDEN == it.statusCode()
                    }
                    .verifyComplete()

            }.contextWrite(
                ReactiveSecurityContextHolder.withAuthentication(
                    SeungkyuAuthentication("testName", Role.USER.name))
            ).block()
        }

        @Test
        fun post_with_empty_return_created(){
            mono{
                //given

                //when
                val target = Mono.just(categoryServiceImpl.post(serverRequest))

                //then
                StepVerifier.create(target)
                    .expectNextMatches {
                        HttpStatus.FORBIDDEN == it.statusCode()
                    }
                    .verifyComplete()

            }.block()
        }

    }



}