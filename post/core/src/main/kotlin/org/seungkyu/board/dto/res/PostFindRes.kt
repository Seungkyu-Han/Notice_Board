package org.seungkyu.board.dto.res

import java.time.LocalDateTime

data class PostFindRes(
    val id: String,
    val name: String,
    val isAdmin: Boolean,
    val views: Int,
    val categoryId: String?,
    val fileIds: List<String>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val comments: List<String>?
)
