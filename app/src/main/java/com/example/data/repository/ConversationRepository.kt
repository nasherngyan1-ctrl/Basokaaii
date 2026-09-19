package com.example.data.repository

import com.example.data.local.BasokaDao
import com.example.data.local.ConversationEntity
import com.example.data.local.MessageEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class ConversationRepository(private val dao: BasokaDao) {

  val allConversations: Flow<List<ConversationEntity>> = dao.getAllConversations()

  fun getConversationsForUser(userId: String): Flow<List<ConversationEntity>> {
    return dao.getConversationsForUser(userId)
  }

  fun getMessages(conversationId: Long): Flow<List<MessageEntity>> {
    return dao.getMessagesForConversation(conversationId)
  }

  suspend fun getMessagesSnapshot(conversationId: Long): List<MessageEntity> {
    return dao.getMessagesSnapshotForConversation(conversationId)
  }

  suspend fun createConversation(title: String, userId: String = "local_personal_user"): Long {
    val entity = ConversationEntity(
      title = title,
      userId = userId,
      createdAt = System.currentTimeMillis(),
      updatedAt = System.currentTimeMillis()
    )
    return dao.insertConversation(entity)
  }

  suspend fun saveUserMessage(conversationId: Long, text: String): Long {
    val message = MessageEntity(
      conversationId = conversationId,
      text = text,
      sender = "USER",
      timestamp = System.currentTimeMillis(),
      status = "COMPLETED"
    )
    val id = dao.insertMessage(message)
    // Update conversation timestamp & auto-title
    val conversation = dao.getConversationById(conversationId)
    if (conversation != null) {
      val isDefaultTitle = conversation.title.startsWith("New") ||
        conversation.title.startsWith("وتووێژ") ||
        conversation.title.startsWith("محادثة")
      val updatedTitle = if (isDefaultTitle) {
        text.take(35)
      } else {
        conversation.title
      }
      dao.updateConversation(
        conversation.copy(
          title = updatedTitle,
          updatedAt = System.currentTimeMillis()
        )
      )
    }
    return id
  }

  suspend fun saveAssistantMessage(
    conversationId: Long,
    text: String,
    status: String = "COMPLETED"
  ): Long {
    val message = MessageEntity(
      conversationId = conversationId,
      text = text,
      sender = "ASSISTANT",
      timestamp = System.currentTimeMillis(),
      status = status
    )
    val id = dao.insertMessage(message)
    val conversation = dao.getConversationById(conversationId)
    if (conversation != null) {
      dao.updateConversation(
        conversation.copy(updatedAt = System.currentTimeMillis())
      )
    }
    return id
  }

  suspend fun saveAssistantImageMessage(
    conversationId: Long,
    caption: String,
    imageUri: String,
    prompt: String,
    status: String = "COMPLETED"
  ): Long {
    val message = MessageEntity(
      conversationId = conversationId,
      text = caption,
      sender = "ASSISTANT",
      timestamp = System.currentTimeMillis(),
      status = status,
      messageType = "IMAGE",
      imageUri = imageUri,
      imagePrompt = prompt
    )
    val id = dao.insertMessage(message)
    val conversation = dao.getConversationById(conversationId)
    if (conversation != null) {
      dao.updateConversation(
        conversation.copy(updatedAt = System.currentTimeMillis())
      )
    }
    return id
  }

  suspend fun updateMessage(message: MessageEntity) {
    dao.insertMessage(message)
  }

  suspend fun deleteMessage(id: Long) {
    // optional helper
  }

  suspend fun deleteConversation(id: Long) {
    dao.deleteConversationById(id)
  }

  suspend fun clearAll() {
    dao.clearAllConversations()
  }
}
