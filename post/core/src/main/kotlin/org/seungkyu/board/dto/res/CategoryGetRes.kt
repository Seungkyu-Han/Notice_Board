package org.seungkyu.board.dto.res

import org.bson.types.ObjectId

data class CategoryGetRes(
    val id: ObjectId?,
    val name: String,
    val searchCount: Int
)
