package org.seungkyu.board.dto.res

import com.fasterxml.jackson.annotation.JsonProperty

data class LoginRes(
    @JsonProperty("token")
    val token: String
)
