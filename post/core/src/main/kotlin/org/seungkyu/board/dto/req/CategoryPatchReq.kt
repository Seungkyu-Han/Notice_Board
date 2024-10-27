package org.seungkyu.board.dto.req

import org.bson.types.ObjectId

data class CategoryPatchReq(
    val id: ObjectId,
    val name: String,
    val isAscending: Boolean
)