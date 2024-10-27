package org.seungkyu.board.dto.res

import com.fasterxml.jackson.annotation.JsonProperty


data class UserInfoRes (
    @JsonProperty("user_id")
    var userId: String,
    @JsonProperty("nickname")
    var nickname: String,
    @JsonProperty("is_admin")
    var isAdmin: Boolean,
)