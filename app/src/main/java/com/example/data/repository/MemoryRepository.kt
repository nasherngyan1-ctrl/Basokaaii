package com.example.data.repository

import com.example.data.local.AiMemoryEntity
import com.example.data.local.BasokaDao
import kotlinx.coroutines.flow.Flow

class MemoryRepository(private val dao: BasokaDao) {

  fun getMemoriesForUser(userId: String): Flow<List<AiMemoryEntity>> {
    return dao.getMemoriesForUser(userId)
  }

  suspend fun getMemoriesSnapshot(userId: String): List<AiMemoryEntity> {
    return dao.getMemoriesSnapshotForUser(userId)
  }

  suspend fun saveMemory(
    userId: String,
    content: String,
    title: String = "",
    category: String = "EXPLICIT_NOTE"
  ): Long {
    val trimmed = content.trim()
    if (trimmed.isEmpty() || userId.isBlank()) return -1L

    val finalTitle = if (title.isNotBlank()) {
      title.trim()
    } else {
      trimmed.take(30).let { if (trimmed.length > 30) "$it..." else it }
    }

    val entity = AiMemoryEntity(
      userId = userId,
      title = finalTitle,
      content = trimmed,
      category = category,
      timestamp = System.currentTimeMillis()
    )
    return dao.insertMemory(entity)
  }

  suspend fun updateMemory(memory: AiMemoryEntity) {
    dao.updateMemory(memory)
  }

  suspend fun deleteMemory(id: Long, userId: String) {
    dao.deleteMemoryById(id, userId)
  }

  suspend fun deleteAllMemories(userId: String) {
    dao.deleteAllMemoriesForUser(userId)
  }

  /**
   * Evaluates whether the user's prompt contains an EXPLICIT instruction to remember something permanently.
   * Extracts and returns the core piece of information to store, or null if no explicit memory command was made.
   */
  fun extractExplicitMemoryTrigger(userPrompt: String): String? {
    val text = userPrompt.trim()
    if (text.length < 5) return null

    // Kurdish Sorani triggers
    val kurdishPatterns = listOf(
      Regex("""(?:ئەمە\s+)?لەبیرت\s+بێت\s*[:،,-]?\s*(.*)""", RegexOption.IGNORE_CASE),
      Regex("""(?:ئەمە\s+)?لە\s*بیرت\s+بێ(?:ت)?\s*[:،,-]?\s*(.*)""", RegexOption.IGNORE_CASE),
      Regex("""لەبیرت\s+بمێنێت(?:ەوە)?\s*[:،,-]?\s*(.*)""", RegexOption.IGNORE_CASE),
      Regex("""تۆماری\s+بکە\s*(?:لەبیرەوەریت|لە یادگە)?\s*[:،,-]?\s*(.*)""", RegexOption.IGNORE_CASE),
      Regex("""ئەم\s+زانیارییە\s+هەڵگرە\s*[:،,-]?\s*(.*)""", RegexOption.IGNORE_CASE)
    )

    for (pattern in kurdishPatterns) {
      val match = pattern.find(text)
      if (match != null) {
        val extracted = match.groupValues.getOrNull(1)?.trim()
        if (!extracted.isNullOrBlank()) {
          return extracted
        }
        return text
      }
    }

    // Arabic triggers
    val arabicPatterns = listOf(
      Regex("""(?:احفظ\s+هذا|تذكر\s+هذا|تذكر\s+أن|تذكر\s+دائماً|احفظ\s+في\s+ذاكرتك)\s*[:،,-]?\s*(.*)""", RegexOption.IGNORE_CASE),
      Regex("""لا\s+تنسى\s+(?:أن|هذا)\s*[:،,-]?\s*(.*)""", RegexOption.IGNORE_CASE)
    )

    for (pattern in arabicPatterns) {
      val match = pattern.find(text)
      if (match != null) {
        val extracted = match.groupValues.getOrNull(1)?.trim()
        if (!extracted.isNullOrBlank()) {
          return extracted
        }
        return text
      }
    }

    // English triggers
    val englishPatterns = listOf(
      Regex("""(?:please\s+)?remember\s+(?:this|that|always)?\s*[:,-]?\s*(.*)""", RegexOption.IGNORE_CASE),
      Regex("""(?:save\s+to\s+memory|memorize\s+this|keep\s+in\s+mind\s+that)\s*[:,-]?\s*(.*)""", RegexOption.IGNORE_CASE)
    )

    for (pattern in englishPatterns) {
      val match = pattern.find(text)
      if (match != null) {
        val extracted = match.groupValues.getOrNull(1)?.trim()
        if (!extracted.isNullOrBlank()) {
          return extracted
        }
        return text
      }
    }

    return null
  }
}
