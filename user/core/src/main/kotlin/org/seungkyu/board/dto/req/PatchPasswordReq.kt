package org.seungkyu.board.dto.req

data class PatchPasswordReq (
    val beforePassword: String,
    val afterPassword: String
)