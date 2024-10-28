package org.seungkyu.board.dto.res

import java.time.LocalDateTime

data class PostGetRes(
    val id: String?,
    val name: String,
    val content: String,
    val createdAt: LocalDateTime?,
    var updatedAt: LocalDateTime?,
    val categoryId: String
)