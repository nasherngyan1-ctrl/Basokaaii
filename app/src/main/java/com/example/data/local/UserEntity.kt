package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
  @PrimaryKey
  val id: String, // UUID or user ID
  val email: String = "biometric_user@basoka.ai",
  val displayName: String = "بەکارھێنەری باسۆکا",
  val passwordHash: String = "",
  val passwordSalt: String = "",
  val createdAt: Long = System.currentTimeMillis(),
  val lastLoginAt: Long = System.currentTimeMillis()
)
