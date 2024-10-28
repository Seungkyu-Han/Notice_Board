package org.seungkyu.board.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "Post")
data class PostDocument(
    val id: ObjectId?,
    var name: String,
    val userId: String,
    var content: String,
    @CreatedDate
    val createdAt: LocalDateTime?,
    @LastModifiedDate
    var updatedAt: LocalDateTime?,
    @DBRef(lazy = true)
    val categoryDocument: CategoryDocument
)