package org.seungkyu.board.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "Post")
data class PostDocument (
    @Id
    val id: ObjectId?,
    var name: String,
    val userId: String,
    var content: String,
    @CreatedDate
    var createdAt: LocalDateTime?,
    @LastModifiedDate
    var updatedAt: LocalDateTime?,
    var categoryDocument: ObjectId?,
    var comments: MutableList<CommentDocument>? = mutableListOf()
)