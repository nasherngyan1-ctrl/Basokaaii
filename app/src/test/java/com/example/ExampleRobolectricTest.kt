package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.BasokaDatabase
import com.example.data.repository.AuthRepository
import com.example.data.repository.AuthResult
import com.example.data.repository.ConversationRepository
import com.example.data.repository.MemoryRepository
import com.example.model.AppLanguage
import com.example.model.Localization
import com.example.security.SecureSessionManager
import com.example.security.SecurityUtils
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Basoka AI", appName)
  }

  @Test
  fun `test password hashing and salt verification`() {
    val password = "StrongPersonalPassword123"
    val salt = SecurityUtils.generateSalt()
    val hash = SecurityUtils.hashPassword(password, salt)

    assertTrue(SecurityUtils.verifyPassword(password, salt, hash))
    assertFalse(SecurityUtils.verifyPassword("WrongPassword", salt, hash))
  }

  @Test
  fun `test email and password validation`() {
    assertTrue(SecurityUtils.isValidEmail("personal@basoka.ai"))
    assertFalse(SecurityUtils.isValidEmail("invalid-email"))

    assertTrue(SecurityUtils.isValidPassword("123456"))
    assertFalse(SecurityUtils.isValidPassword("123"))
  }

  @Test
  fun `test explicit personal memory trigger recognition in Kurdish`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = Room.inMemoryDatabaseBuilder(context, BasokaDatabase::class.java).allowMainThreadQueries().build()
    val repo = MemoryRepository(db.basokaDao())

    val explicitKurdish = "ئەمە لەبیرت بێت ناوی هاوڕێی مناڵیم ئاریە"
    val extracted = repo.extractExplicitMemoryTrigger(explicitKurdish)
    assertNotNull(extracted)
    assertEquals("ناوی هاوڕێی مناڵیم ئاریە", extracted)

    val generalChat = "کەش و هەوای ئەمڕۆ چۆنە لە سلێمانی؟"
    assertNull(repo.extractExplicitMemoryTrigger(generalChat))

    db.close()
  }

  @Test
  fun `test explicit personal memory trigger recognition in English and Arabic`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = Room.inMemoryDatabaseBuilder(context, BasokaDatabase::class.java).allowMainThreadQueries().build()
    val repo = MemoryRepository(db.basokaDao())

    val englishTrigger = "Please remember that my favorite language is Kotlin"
    val extractedEn = repo.extractExplicitMemoryTrigger(englishTrigger)
    assertNotNull(extractedEn)
    assertEquals("my favorite language is Kotlin", extractedEn)

    val arabicTrigger = "احفظ هذا: مكتبي في الطابق الرابع"
    val extractedAr = repo.extractExplicitMemoryTrigger(arabicTrigger)
    assertNotNull(extractedAr)
    assertEquals("مكتبي في الطابق الرابع", extractedAr)

    db.close()
  }

  @Test
  fun `test create account, login, session persistence, and logout flow`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = Room.inMemoryDatabaseBuilder(context, BasokaDatabase::class.java).allowMainThreadQueries().build()
    val sessionManager = SecureSessionManager(context)
    sessionManager.clearSession()
    val authRepo = AuthRepository(db.basokaDao(), sessionManager)

    // 1. Create account
    val regResult = authRepo.register(
      email = "personal@basoka.ai",
      displayName = "Basoka Owner",
      password = "MySecurePassword2026",
      confirmPassword = "MySecurePassword2026"
    )
    assertTrue("Account creation should succeed", regResult is AuthResult.Success)
    val user = (regResult as AuthResult.Success).user
    assertEquals("personal@basoka.ai", user.email)

    // 2. Verify session persistence
    val activeSession = sessionManager.getActiveSession()
    assertNotNull("Session should be persisted after registration", activeSession)
    assertEquals(user.id, activeSession?.userId)

    // 3. Logout
    authRepo.logout()
    assertNull("Session should be cleared after logout", sessionManager.getActiveSession())

    // 4. Login with wrong password
    val failLogin = authRepo.login("personal@basoka.ai", "WrongPassword")
    assertTrue("Login with wrong password should fail", failLogin is AuthResult.Error)

    // 5. Login with correct password
    val loginResult = authRepo.login("personal@basoka.ai", "MySecurePassword2026")
    assertTrue("Login with correct password should succeed", loginResult is AuthResult.Success)
    assertNotNull("Session should be restored after login", sessionManager.getActiveSession())

    db.close()
  }

  @Test
  fun `test biometric registration, login, and session flow`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = Room.inMemoryDatabaseBuilder(context, BasokaDatabase::class.java).allowMainThreadQueries().build()
    val sessionManager = SecureSessionManager(context)
    sessionManager.clearSession()
    val authRepo = AuthRepository(db.basokaDao(), sessionManager)

    // 1. First setup: Register with Biometrics (No email, no password)
    val regResult = authRepo.registerBiometric("کاوە خاوەنی مۆبایل")
    assertTrue("Biometric registration should succeed", regResult is AuthResult.Success)
    val user = (regResult as AuthResult.Success).user
    assertEquals("کاوە خاوەنی مۆبایل", user.displayName)

    // 2. Session should be active
    val activeSession = sessionManager.getActiveSession()
    assertNotNull(activeSession)
    assertEquals(user.id, activeSession?.userId)

    // 3. Logout
    authRepo.logout()
    assertNull(sessionManager.getActiveSession())

    // 4. Returning user later launch: Login with Biometrics
    val loginResult = authRepo.loginBiometric()
    assertTrue("Biometric login should succeed", loginResult is AuthResult.Success)
    assertNotNull(sessionManager.getActiveSession())

    // 5. Account deletion/reset
    authRepo.deletePersonalAccount()
    assertNull(authRepo.getPersonalUser())
    assertNull(sessionManager.getActiveSession())

    db.close()
  }

  @Test
  fun `test personal memory CRUD and account data isolation`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = Room.inMemoryDatabaseBuilder(context, BasokaDatabase::class.java).allowMainThreadQueries().build()
    val memoryRepo = MemoryRepository(db.basokaDao())

    val user1Id = "user_account_alpha"
    val user2Id = "user_account_beta"

    // Save memory for User 1
    val mem1Id = memoryRepo.saveMemory(user1Id, "حەزم لە خواردنەوەی قاوەی کەم شەکرە", "قاوە")
    assertTrue(mem1Id > 0)

    // Save memory for User 2
    val mem2Id = memoryRepo.saveMemory(user2Id, "I live in Sulaymaniyah", "Location")
    assertTrue(mem2Id > 0)

    // Verify isolation: User 1 memories snapshot only contains User 1 memory
    val user1Memories = memoryRepo.getMemoriesSnapshot(user1Id)
    assertEquals(1, user1Memories.size)
    assertEquals("حەزم لە خواردنەوەی قاوەی کەم شەکرە", user1Memories.first().content)

    // Verify User 2 isolation
    val user2Memories = memoryRepo.getMemoriesSnapshot(user2Id)
    assertEquals(1, user2Memories.size)
    assertEquals("I live in Sulaymaniyah", user2Memories.first().content)

    // Edit memory for User 1
    val updatedMem1 = user1Memories.first().copy(content = "حەزم لە قاوەی تاڵە بە تەواوی")
    memoryRepo.updateMemory(updatedMem1)
    val refreshed = memoryRepo.getMemoriesSnapshot(user1Id)
    assertEquals("حەزم لە قاوەی تاڵە بە تەواوی", refreshed.first().content)

    // Delete single memory
    memoryRepo.deleteMemory(mem1Id, user1Id)
    assertEquals(0, memoryRepo.getMemoriesSnapshot(user1Id).size)
    // User 2 memory remains untouched
    assertEquals(1, memoryRepo.getMemoriesSnapshot(user2Id).size)

    // Delete all memories for User 2
    memoryRepo.deleteAllMemories(user2Id)
    assertEquals(0, memoryRepo.getMemoriesSnapshot(user2Id).size)

    db.close()
  }

  @Test
  fun `test conversation history and message persistence`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = Room.inMemoryDatabaseBuilder(context, BasokaDatabase::class.java).allowMainThreadQueries().build()
    val convRepo = ConversationRepository(db.basokaDao())

    val convId = convRepo.createConversation("تەلارسازی باسۆکا", "user_alpha")
    assertTrue(convId > 0)

    val userMsgId = convRepo.saveUserMessage(convId, "سڵاو باسۆکا")
    assertTrue(userMsgId > 0)

    val assistantMsgId = convRepo.saveAssistantMessage(convId, "سڵاو! من یاریدەدەری تایبەتی تۆم.")
    assertTrue(assistantMsgId > 0)

    val messages = convRepo.getMessagesSnapshot(convId)
    assertEquals(2, messages.size)
    assertEquals("USER", messages[0].sender)
    assertEquals("سڵاو باسۆکا", messages[0].text)
    assertEquals("ASSISTANT", messages[1].sender)
    assertEquals("سڵاو! من یاریدەدەری تایبەتی تۆم.", messages[1].text)

    db.close()
  }

  @Test
  fun `test multilingual localization strings resolution`() {
    val ckb = Localization.getStrings(AppLanguage.KURDISH_SORANI)
    assertEquals("باسۆکا AI", ckb.appName)
    assertTrue(ckb.inputPlaceholder.contains("باسۆکا"))

    val ar = Localization.getStrings(AppLanguage.ARABIC)
    assertEquals("باسوكا AI", ar.appName)

    val en = Localization.getStrings(AppLanguage.ENGLISH)
    assertEquals("Basoka AI", en.appName)
  }

  @Test
  fun `test image generation prompt intent detection in Kurdish, Arabic, and English`() {
    val kurdishPrompts = listOf(
      "وێنەی شارێکی داهاتوو دروست بکە",
      "وێنەیەکی قەڵای هەولێر بکێشە",
      "رەسمێکی جوانی سروشت دروست بکە",
      "تکایە وێنەیەکم بۆ بکێشە لەسەر بەفر",
      "شێوەی ئۆتۆمبێلێکی کارەبایی دروست بکە"
    )
    for (prompt in kurdishPrompts) {
      assertTrue("Prompt '$prompt' should be detected as image prompt", com.example.viewmodel.BasokaViewModel.isImageGenerationPrompt(prompt))
    }

    val englishPrompts = listOf(
      "generate an image of a cyber dragon",
      "draw a futuristic city with neon lights",
      "create a photo of snowy mountains",
      "paint an ancient Kurdish castle",
      "make an illustration of space exploration"
    )
    for (prompt in englishPrompts) {
      assertTrue("Prompt '$prompt' should be detected as image prompt", com.example.viewmodel.BasokaViewModel.isImageGenerationPrompt(prompt))
    }

    val arabicPrompts = listOf(
      "ولد صورة لمدينة مستقبلية",
      "ارسم لي قلعة أثرية",
      "صمم صورة لطبيعة كردستان الخلابة"
    )
    for (prompt in arabicPrompts) {
      assertTrue("Prompt '$prompt' should be detected as image prompt", com.example.viewmodel.BasokaViewModel.isImageGenerationPrompt(prompt))
    }

    val regularChatPrompts = listOf(
      "سڵاو چۆنی؟",
      "کەش و هەوای ئەمڕۆ چۆنە لە سلێمانی؟",
      "What is the capital of Kurdistan?",
      "كيف أتعلم البرمجة بلغة كوتلن؟"
    )
    for (prompt in regularChatPrompts) {
      assertFalse("Prompt '$prompt' should NOT be detected as image prompt", com.example.viewmodel.BasokaViewModel.isImageGenerationPrompt(prompt))
    }
  }

  @Test
  fun `test image message persistence in ConversationRepository`(): Unit = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = Room.inMemoryDatabaseBuilder(context, BasokaDatabase::class.java).allowMainThreadQueries().build()
    val convRepo = ConversationRepository(db.basokaDao())

    val convId = convRepo.createConversation("تەلارسازی وێنەیی", "user_image_tester")
    assertTrue(convId > 0)

    val prompt = "وێنەی هەولێر لە ساڵی ٢٠٧٠"
    val fakeUri = "file:///data/user/0/com.example/files/generated_images/test_img.png"
    val caption = "ئەمەش وێنەی هەولێر لە ساڵی ٢٠٧٠ کە داوات کردبوو."

    val msgId = convRepo.saveAssistantImageMessage(
      conversationId = convId,
      imageUri = fakeUri,
      prompt = prompt,
      caption = caption
    )
    assertTrue(msgId > 0)

    val messages = convRepo.getMessagesSnapshot(convId)
    assertEquals(1, messages.size)
    val savedMsg = messages.first()
    assertEquals("IMAGE", savedMsg.messageType)
    assertEquals(fakeUri, savedMsg.imageUri)
    assertEquals(prompt, savedMsg.imagePrompt)
    assertEquals(caption, savedMsg.text)
    assertEquals("ASSISTANT", savedMsg.sender)

    db.close()
  }

  @Test
  fun `test image storage manager file creation and resolution`(): Unit = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val fakeBytes = byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8)

    val localUriString = com.example.data.local.ImageStorageManager.saveInternalImage(
      context = context,
      imageBytes = fakeBytes,
      prefix = "test_gen"
    )

    assertTrue("URI string should not be empty", localUriString.isNotEmpty())
    val resolvedFile = com.example.data.local.ImageStorageManager.resolveLocalFile(context, localUriString)
    assertNotNull("Resolved file should exist", resolvedFile)
    assertTrue("Resolved file must actually exist on disk", resolvedFile!!.exists())
    assertEquals(fakeBytes.size.toLong(), resolvedFile.length())

    // Clean up
    resolvedFile.delete()
    Unit
  }
}
