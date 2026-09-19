package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BasokaDao {

  // Users (Private Personal Account)
  @Query("SELECT * FROM users LIMIT 1")
  fun getPersonalUserFlow(): Flow<UserEntity?>

  @Query("SELECT * FROM users LIMIT 1")
  suspend fun getPersonalUser(): UserEntity?

  @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
  suspend fun getUserByEmail(email: String): UserEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertUser(user: UserEntity)

  @Query("DELETE FROM users")
  suspend fun deleteAllUsers()

  // Conversations (Filtered by user or all local)
  @Query("SELECT * FROM conversations ORDER BY updatedAt DESC")
  fun getAllConversations(): Flow<List<ConversationEntity>>

  @Query("SELECT * FROM conversations WHERE userId = :userId ORDER BY updatedAt DESC")
  fun getConversationsForUser(userId: String): Flow<List<ConversationEntity>>

  @Query("SELECT * FROM conversations WHERE id = :id LIMIT 1")
  suspend fun getConversationById(id: Long): ConversationEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertConversation(conversation: ConversationEntity): Long

  @Update
  suspend fun updateConversation(conversation: ConversationEntity)

  @Query("DELETE FROM conversations WHERE id = :id")
  suspend fun deleteConversationById(id: Long)

  @Query("DELETE FROM conversations")
  suspend fun clearAllConversations()

  // Messages
  @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
  fun getMessagesForConversation(conversationId: Long): Flow<List<MessageEntity>>

  @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
  suspend fun getMessagesSnapshotForConversation(conversationId: Long): List<MessageEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMessage(message: MessageEntity): Long

  @Query("DELETE FROM messages WHERE conversationId = :conversationId")
  suspend fun deleteMessagesForConversation(conversationId: Long)

  // AI Memories (Strictly Isolated to Authenticated User)
  @Query("SELECT * FROM ai_memories WHERE userId = :userId ORDER BY timestamp DESC")
  fun getMemoriesForUser(userId: String): Flow<List<AiMemoryEntity>>

  @Query("SELECT * FROM ai_memories WHERE userId = :userId ORDER BY timestamp DESC")
  suspend fun getMemoriesSnapshotForUser(userId: String): List<AiMemoryEntity>

  @Query("SELECT * FROM ai_memories WHERE id = :id AND userId = :userId LIMIT 1")
  suspend fun getMemoryById(id: Long, userId: String): AiMemoryEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMemory(memory: AiMemoryEntity): Long

  @Update
  suspend fun updateMemory(memory: AiMemoryEntity)

  @Query("DELETE FROM ai_memories WHERE id = :id AND userId = :userId")
  suspend fun deleteMemoryById(id: Long, userId: String)

  @Query("DELETE FROM ai_memories WHERE userId = :userId")
  suspend fun deleteAllMemoriesForUser(userId: String)
}
