package org.seungkyu.board.dto.req

data class PostPostReq (
    val name: String,
    val content: String,
    val categoryId: String
)