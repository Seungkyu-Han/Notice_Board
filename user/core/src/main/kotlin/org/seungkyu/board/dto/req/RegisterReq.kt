package org.seungkyu.board.dto.req

data class RegisterReq(
    val userId: String,
    val password: String,
    val nickname: String,
)
