package org.seungkyu.board.dto.req

data class CommentPatchReq(
    val postId: String,
    val commentIndex: Int,
    val contents: String
)