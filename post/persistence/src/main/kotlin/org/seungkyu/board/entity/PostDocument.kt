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
    val name: String,
    val userId: String,
    val content: String,
    @CreatedDate
    val createdAt: LocalDateTime?,
    @LastModifiedDate
    var updatedAt: LocalDateTime?,
    @DBRef
    val categoryDocument: CategoryDocument
)