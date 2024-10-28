package org.seungkyu.board.dto.req

data class PostPatchReq (
    val id: String,
    val name: String,
    val content: String,
    val categoryId: String
)