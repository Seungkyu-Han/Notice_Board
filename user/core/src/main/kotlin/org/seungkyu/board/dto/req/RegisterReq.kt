package org.seungkyu.board.dto.req

import com.fasterxml.jackson.annotation.JsonProperty

data class RegisterReq(
    @JsonProperty("user_id")
    val userId: String,
    val password: String,
    val nickname: String,
)
