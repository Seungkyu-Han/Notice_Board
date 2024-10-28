package org.seungkyu.board.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions
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
import org.seungkyu.board.dto.req.CategoryPatchReq
import org.seungkyu.board.dto.req.CategoryPostReq
import org.seungkyu.board.dto.res.CategoryGetRes
import org.seungkyu.board.entity.CategoryDocument
import org.seungkyu.board.repository.CategoryMongoRepository
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.http.codec.HttpMessageWriter
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.web.server.MockServerWebExchange
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.reactive.function.server.HandlerStrategies
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.result.view.ViewResolver
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.util.*

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [CategoryServiceImpl::class])
class CategoryServiceImplTest{

    @MockBean
    private lateinit var categoryMongoRepository: CategoryMongoRepository

    private lateinit var categoryServiceImpl: CategoryServiceImpl

    @Mock
    private lateinit var serverRequest: ServerRequest

    private val objectMapper = ObjectMapper()

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        categoryServiceImpl = CategoryServiceImpl(categoryMongoRepository)
    }

    private val testCategory = CategoryDocument(
        id =ObjectId.get(), name = "testName", isAscending = true, searchCount = 0, userId = "testUser"
    )

    @Nested
    inner class PostCategory{

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

    @Nested
    inner class GetCategory{
        @Test
        fun get_return_ok(){
            mono{
                //given
                val category1 = CategoryDocument(
                    id = null, name = "testName1", isAscending = true, userId = "testUser", searchCount = 1
                )

                val category2 = CategoryDocument(
                    id = null, name = "testName2", isAscending = true, userId = "testUser", searchCount = 2
                )

                `when`(categoryMongoRepository.findAll())
                    .thenReturn(
                        Flux.fromIterable(
                            listOf(
                                category1, category2
                            )
                        )
                    )

                //when
                val target = Mono.just(categoryServiceImpl.get(serverRequest))

                val extractedCategoryGet:ArrayList<CategoryGetRes> = fetchBody<ArrayList<CategoryGetRes>>(target.awaitSingle())

                Assertions.assertEquals(2, extractedCategoryGet.size)

                StepVerifier.create(target)
                    .expectNextMatches {
                        HttpStatus.OK == it.statusCode()
                    }
                    .verifyComplete()
            }.block()

        }
    }

    @Nested
    inner class PatchCategory{
        @Test
        fun patch_return_ok(){
            mono{
                //when
                `when`(serverRequest.bodyToMono(CategoryPatchReq::class.java))
                    .thenReturn(
                        Mono.just(
                            CategoryPatchReq(
                                id = testCategory.id!!.toHexString(),
                                name = testCategory.name,
                                isAscending = testCategory.isAscending)
                        )
                    )

                `when`(categoryMongoRepository.findById(testCategory.id!!))
                    .thenReturn(Mono.just(testCategory))

                `when`(categoryMongoRepository.save(any()))
                    .thenReturn(Mono.just(testCategory))

                //when
                val target = Mono.just(categoryServiceImpl.patch(serverRequest))

                //then
                StepVerifier.create(target)
                    .expectNextMatches {
                        HttpStatus.OK == it.statusCode()
                    }
                    .verifyComplete()


            }.contextWrite(
                ReactiveSecurityContextHolder.withAuthentication(
                    SeungkyuAuthentication(testCategory.userId, Role.ADMIN.name)
                )
            ).block()
        }

        @Test
        fun patch_invalid_user_return_forbidden(){
            mono{
                //when
                `when`(serverRequest.bodyToMono(CategoryPatchReq::class.java))
                    .thenReturn(
                        Mono.just(
                            CategoryPatchReq(
                                id = testCategory.id!!.toHexString(),
                                name = testCategory.name,
                                isAscending = testCategory.isAscending)
                        )
                    )

                `when`(categoryMongoRepository.findById(testCategory.id!!))
                    .thenReturn(Mono.just(testCategory))


                //when
                val target = Mono.just(categoryServiceImpl.patch(serverRequest))

                //then
                StepVerifier.create(target)
                    .expectNextMatches {
                        HttpStatus.FORBIDDEN == it.statusCode()
                    }
                    .verifyComplete()


            }.contextWrite(
                ReactiveSecurityContextHolder.withAuthentication(
                    SeungkyuAuthentication("testtest", Role.ADMIN.name)
                )
            ).block()
        }
    }

    @Nested
    inner class DeleteCategory{
        @Test
        fun delete_return_ok(){
            mono{
                //given
                `when`(serverRequest.pathVariable("id"))
                    .thenReturn(testCategory.id.toString())

                `when`(categoryMongoRepository.findById(testCategory.id!!))
                    .thenReturn(Mono.just(testCategory))

                `when`(categoryMongoRepository.deleteById(any<ObjectId>()))
                    .thenReturn(Mono.empty())

                //when
                val target = Mono.just(categoryServiceImpl.delete(serverRequest))

                //then
                StepVerifier.create(target)
                    .expectNextMatches {
                        HttpStatus.OK == it.statusCode()
                    }
                    .verifyComplete()

            }.contextWrite(ReactiveSecurityContextHolder.withAuthentication(
                SeungkyuAuthentication(testCategory.userId, Role.ADMIN.name)
            )).block()
        }

        @Test
        fun delete_invalid_user_return_forbidden(){
            mono{
                //given
                `when`(serverRequest.pathVariable("id"))
                    .thenReturn(testCategory.id.toString())

                `when`(categoryMongoRepository.findById(testCategory.id!!))
                    .thenReturn(Mono.just(testCategory))

                //when
                val target = Mono.just(categoryServiceImpl.delete(serverRequest))

                //then
                StepVerifier.create(target)
                    .expectNextMatches {
                        HttpStatus.FORBIDDEN == it.statusCode()
                    }
                    .verifyComplete()

            }.contextWrite(ReactiveSecurityContextHolder.withAuthentication(
                SeungkyuAuthentication("testtest", Role.ADMIN.name)
            )).block()
        }

        @Test
        fun delete_invalid_category_return_not_found(){
            mono{
                //given
                `when`(serverRequest.pathVariable("id"))
                    .thenReturn(testCategory.id.toString())

                `when`(categoryMongoRepository.findById(testCategory.id!!))
                    .thenReturn(Mono.empty())

                //when
                val target = Mono.just(categoryServiceImpl.delete(serverRequest))

                //then
                StepVerifier.create(target)
                    .expectNextMatches {
                        HttpStatus.NOT_FOUND == it.statusCode()
                    }
                    .verifyComplete()

            }.contextWrite(ReactiveSecurityContextHolder.withAuthentication(
                SeungkyuAuthentication("testtest", Role.ADMIN.name)
            )).block()
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