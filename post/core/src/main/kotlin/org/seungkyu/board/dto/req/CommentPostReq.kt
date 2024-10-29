package org.seungkyu.board.dto.req

data class CommentPostReq(
    val contents: String,
    val postId: String
)