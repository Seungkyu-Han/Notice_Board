package org.seungkyu.board.dto.req

import com.fasterxml.jackson.annotation.JsonProperty

data class CategoryPatchReq(
    val id: String,
    val name: String,
    @JsonProperty("isAscending")
    val isAscending: Boolean
)