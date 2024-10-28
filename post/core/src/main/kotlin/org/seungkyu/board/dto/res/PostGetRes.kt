package org.seungkyu.board.dto.res

import org.bson.types.ObjectId
import java.time.LocalDateTime

data class PostGetRes(
    val id: ObjectId?,
    val name: String,
    val content: String,
    val createdAt: LocalDateTime,
    var updatedAt: LocalDateTime,
    val categoryId: String
)