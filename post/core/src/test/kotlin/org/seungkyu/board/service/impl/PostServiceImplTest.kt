package org.seungkyu.board.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import org.bson.types.ObjectId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.seungkyu.board.config.SeungkyuAuthentication
import org.seungkyu.board.data.enums.Role
import org.seungkyu.board.dto.req.PostPatchReq
import org.seungkyu.board.dto.req.PostPostReq
import org.seungkyu.board.dto.res.PostGetRes
import org.seungkyu.board.entity.CategoryDocument
import org.seungkyu.board.entity.PostDocument
import org.seungkyu.board.repository.PostMongoRepository
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
@ContextConfiguration(classes = [PostServiceImpl::class])
class PostServiceImplTest{

    @MockBean
    private lateinit var postMongoRepository: PostMongoRepository

    private lateinit var postServiceImpl: PostServiceImpl

    @Mock
    private lateinit var serverRequest: ServerRequest

    private val objectMapper = ObjectMapper()

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        postServiceImpl = PostServiceImpl(postMongoRepository)
    }

    private val testPostDocument = PostDocument(
        id = ObjectId.get(),
        name = "testName",
        userId = "testUserId",
        content = "testContent",
        createdAt = null,
        updatedAt = null,
        categoryDocument = CategoryDocument(
            id = ObjectId.get(),
            name = null,
            userId = null,
            isAscending = null,
            searchCount = null
        )
    )

    @Nested
    inner class PostPost{
        @Test
        fun post_with_valid_content_return_created(){
            mono{
                //given
                `when`(serverRequest.bodyToMono(PostPostReq::class.java))
                    .thenReturn(
                        Mono.just(
                            PostPostReq(
                                name = testPostDocument.name,
                                content = testPostDocument.content,
                                categoryId = testPostDocument.categoryDocument.id.toString()
                            )
                        )
                    )

                `when`(postMongoRepository.save(any()))
                    .thenReturn(
                        Mono.just(testPostDocument)
                    )

                //when
                val result = Mono.just(postServiceImpl.post(serverRequest))

                //then
                StepVerifier.create(result)
                    .expectNextMatches {
                        HttpStatus.CREATED == it.statusCode()
                    }
                    .verifyComplete()
            }.contextWrite(
                ReactiveSecurityContextHolder.withAuthentication(
                    SeungkyuAuthentication(id = testPostDocument.userId, role = Role.ADMIN.name)
                )
            ).block()
        }

        @Test
        fun post_with_invalid_user_return_ok(){
            mono{
                //given

                //when
                val result = Mono.just(postServiceImpl.post(serverRequest))

                //then
                StepVerifier.create(result)
                    .expectNextMatches {
                        HttpStatus.FORBIDDEN == it.statusCode()
                    }
                    .verifyComplete()
            }.block()
        }
    }

    @Nested
    inner class PatchPost{
        @Test
        fun patch_with_valid_content_return_ok(){
            mono{
                //given
                `when`(serverRequest.bodyToMono(PostPatchReq::class.java))
                    .thenReturn(
                        Mono.just(
                            PostPatchReq(
                                id = testPostDocument.id!!.toHexString(),
                                name = testPostDocument.name,
                                content = testPostDocument.content,
                                categoryId = testPostDocument.categoryDocument.id.toString()
                            )
                        )
                    )

                `when`(postMongoRepository.findById(any<ObjectId>()))
                .thenReturn(Mono.just(testPostDocument))

                `when`(postMongoRepository.save(any()))
                .thenReturn(Mono.just(testPostDocument))

                //when
                val target = Mono.just(postServiceImpl.patch(serverRequest))

                //then
                StepVerifier.create(target)
                    .expectNextMatches {
                        it.statusCode() == HttpStatus.OK
                    }
                    .verifyComplete()
            }.contextWrite(
                ReactiveSecurityContextHolder.withAuthentication(SeungkyuAuthentication(id = testPostDocument.userId, role = Role.ADMIN.name))
            ).block()
        }
    }


    @Nested
    inner class GetPost{
        @Test
        fun get_with_valid_content_return_ok(){
            mono{
                //given

                `when`(postMongoRepository.findByUserId(testPostDocument.userId))
                    .thenReturn(Flux.just(testPostDocument))

                //when
                val target = Mono.just(postServiceImpl.get(serverRequest))

                //then
                val extractedGetPost = fetchBody<List<PostGetRes>>(target.awaitSingle())

                StepVerifier.create(target)
                    .expectNextMatches {
                        it.statusCode() == HttpStatus.OK &&
                        extractedGetPost.size == 1
                    }
                    .verifyComplete()
            }.contextWrite(
                ReactiveSecurityContextHolder.withAuthentication(SeungkyuAuthentication(id = testPostDocument.userId, role = Role.ADMIN.name))
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