package org.seungkyu.board.dto.req

data class UpdatePasswordReq (
    val beforePassword: String,
    val afterPassword: String
)