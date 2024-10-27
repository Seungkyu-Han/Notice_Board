package org.seungkyu.board.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "User")
data class UserDocument (
    @Id
    val id: ObjectId?,
    @Indexed(unique = true)
    val userId: String,
    var password: String,
    var nickName: String,
    var isAdmin: Boolean,
    var isWithDraw: Boolean,
    var status: String,
    @CreatedDate
    var createdAt: LocalDateTime?,
    @LastModifiedDate
    var updatedAt: LocalDateTime?,
)