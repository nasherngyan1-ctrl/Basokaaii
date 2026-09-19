package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
  tableName = "ai_memories",
  indices = [Index(value = ["userId"])]
)
data class AiMemoryEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val userId: String,
  val title: String = "",
  val content: String,
  val category: String = "EXPLICIT_NOTE", // "EXPLICIT_NOTE", "PREFERENCE", "FACT", "GENERAL"
  val timestamp: Long = System.currentTimeMillis()
)
