package com.example.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
  tableName = "messages",
  foreignKeys = [
    ForeignKey(
      entity = ConversationEntity::class,
      parentColumns = ["id"],
      childColumns = ["conversationId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [Index("conversationId")]
)
data class MessageEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val conversationId: Long,
  val text: String,
  val sender: String, // "USER", "SYSTEM", "ASSISTANT"
  val timestamp: Long = System.currentTimeMillis(),
  val status: String = "LOCAL_STORED", // "LOCAL_STORED", "COMPLETED", "FAILED"
  val messageType: String = "TEXT", // "TEXT", "IMAGE"
  val imageUri: String? = null,
  val imagePrompt: String? = null
)
