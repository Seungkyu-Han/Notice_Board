package org.seungkyu.board.service.impl

import org.seungkyu.board.repository.PostMongoRepository
import org.seungkyu.board.service.CommentService
import org.springframework.stereotype.Service

@Service
class CommentServiceImpl(
    private val postMongoRepository: PostMongoRepository
): CommentService {
}