package com.example.model

object Localization {

  fun getStrings(language: AppLanguage): AppStrings {
    return when (language) {
      AppLanguage.KURDISH_SORANI -> KurdishStrings
      AppLanguage.ARABIC -> ArabicStrings
      AppLanguage.ENGLISH -> EnglishStrings
    }
  }

  interface AppStrings {
    val appName: String
    val appSubtitle: String
    val newChat: String
    val history: String
    val settings: String
    val inputPlaceholder: String
    val send: String
    val stopGeneration: String
    val retry: String
    val voiceInputFuture: String
    val voiceTooltip: String
    val welcomeTitle: String
    val welcomeDesc: String
    val initialNoticeTitle: String
    val initialNoticeBody: String
    val configureApiAction: String
    val emptyHistory: String
    val conversationsTitle: String
    val currentChat: String
    val deleteChat: String
    val languageSection: String
    val themeSection: String
    val darkMode: String
    val securitySection: String
    val biometricLockTitle: String
    val biometricLockSubtitle: String
    val memorySection: String
    val personalMemoryTitle: String
    val personalMemorySubtitle: String
    val architectureSection: String
    val statusReady: String
    val statusConfigNeeded: String
    val geminiApiStatus: String
    val geminiApiDesc: String
    val localDbStatus: String
    val localDbDesc: String
    val secureBackendStatus: String
    val secureBackendDesc: String
    val voiceTtsStatus: String
    val voiceTtsDesc: String
    val version: String
    val back: String
    val close: String
    val systemAwaitingResponse: String
    val clearAll: String
    val generatingResponse: String
    val errorApiKeyMissing: String
    val errorNetwork: String
    val errorGeneral: String
    val copiedToClipboard: String

    // Auth & Security Strings
    val accountSection: String
    val loginTitle: String
    val registerTitle: String
    val emailLabel: String
    val passwordLabel: String
    val confirmPasswordLabel: String
    val displayNameLabel: String
    val loginButton: String
    val registerButton: String
    val logoutButton: String
    val createAccountPrompt: String
    val alreadyHaveAccountPrompt: String
    val profileTitle: String
    val personalAccountBadge: String
    val privateDeviceTitle: String
    val privateDeviceSubtitle: String
    val appLockTitle: String
    val appLockSubtitle: String
    val lockTimeoutTitle: String
    val lockImmediately: String
    val lockAfter1Min: String
    val lockAfter5Min: String
    val biometricUnlockTitle: String
    val biometricUnlockSubtitle: String
    val unlockWithBiometrics: String
    val unlockWithPasswordFallback: String
    val unlockApp: String
    val lockedStateNotice: String

    // Auth validation errors
    val errorInvalidEmail: String
    val errorPasswordShort: String
    val errorPasswordMismatch: String
    val errorUserExists: String
    val errorUserNotFound: String
    val errorIncorrectPassword: String
    val accountCreatedSuccess: String
    val logoutConfirmMessage: String
    val biometricHardwareNotice: String

    // Biometric Fingerprint Authentication Strings
    val biometricSetupTitle: String
    val biometricSetupSubtitle: String
    val biometricAuthTitle: String
    val biometricAuthSubtitle: String
    val biometricEnableButton: String
    val biometricScanButton: String
    val biometricNoHardware: String
    val biometricNoneEnrolled: String
    val biometricHardwareUnavailable: String
    val openDeviceSecuritySettings: String
    val biometricRegistrationSuccess: String
    val biometricLoginSuccess: String
    val userNameOptionalLabel: String
    val authMethodLabel: String
    val biometricFingerprintActive: String
    val biometricTouchSensorHint: String
    val switchAccountReset: String
    val biometricPromptTitle: String
    val biometricPromptSubtitle: String
    val biometricPromptCancel: String
    val biometricPromptFailed: String

    // Memory Management Strings
    val memoryManagementTitle: String
    val memoryManagementSubtitle: String
    val addMemory: String
    val editMemory: String
    val deleteMemory: String
    val deleteAllMemories: String
    val deleteAllMemoriesConfirm: String
    val emptyMemoriesNotice: String
    val memorySavedSuccess: String
    val memoryUpdatedSuccess: String
    val memoryDeletedSuccess: String
    val allMemoriesDeletedSuccess: String
    val explicitMemoryRule: String
    val manageMemoriesAction: String
    val memoryTitleLabel: String
    val memoryContentLabel: String
    val save: String
    val cancel: String

    // Voice & Speech Strings
    val speechListeningPrompt: String
    val speechServiceNotAvailable: String
    val micPermissionNeeded: String
    val kurdishTtsUnavailableNotice: String
    val audioPlaybackActive: String
    val stopSpeaking: String
    val playSpeaking: String

    // Image Generation Strings
    val imageGenerationTitle: String
    val imageGenerationMode: String
    val imageGenerationModeBadge: String
    val imageInputPlaceholder: String
    val generatingImage: String
    val generatingImageProgress: String
    val imagePromptLabel: String
    val saveToGallery: String
    val imageSavedSuccess: String
    val imageSaveFailed: String
    val shareImage: String
    val viewFullscreen: String
    val copyPrompt: String
    val errorImageApiKeyMissing: String
    val errorImageGenerationFailed: String
    val imageModelBadge: String
    val cancelImageGeneration: String
    val exitImageMode: String
    val tryImagePromptExample: String
  }

  private val KurdishStrings = object : AppStrings {
    override val appName = "باسۆکا AI"
    override val appSubtitle = "یاریدەدەری زیرەکی کەسی"
    override val newChat = "وتووێژی نوێ"
    override val history = "مێژووی وتووێژەکان"
    override val settings = "ڕێکخستنەکان"
    override val inputPlaceholder = "پەیامێک بنووسە بۆ باسۆکا..."
    override val send = "ناردن"
    override val stopGeneration = "وەستاندن"
    override val retry = "دووبارەکردنەوە"
    override val voiceInputFuture = "تۆماری دەنگ"
    override val voiceTooltip = "دەست بنێ بە مایکرۆفۆن بۆ قسەکردن بە دەنگ."
    override val welcomeTitle = "بەخێربێیت بۆ باسۆکا AI"
    override val welcomeDesc = "یاریدەدەری تایبەتی خۆت لەسەر مۆبایلەکەت، پارێزراو، خێرا و پشتگیریکەری زمانی کوردیی سۆرانی و جیهانی."
    override val initialNoticeTitle = "مۆدێلی Gemini 3.5 Flash"
    override val initialNoticeBody = "بۆ بەکارهێنانی ڕاستەوخۆ، کلیلی GEMINI_API_KEY لە بەشی نهێنییەکانی AI Studio دابنێ."
    override val configureApiAction = "ڕێکخستنی کلیل"
    override val emptyHistory = "هێشتا هیچ وتووێژێک تۆمار نەکراوە"
    override val conversationsTitle = "وتووێژەکان"
    override val currentChat = "وتووێژی ئێستا"
    override val deleteChat = "سڕینەوە"
    override val languageSection = "زمان (Language)"
    override val themeSection = "ڕووکار و شێواز"
    override val darkMode = "دۆخی تاریکی ئەلەکترۆنی"
    override val securitySection = "ئاسایش و پاراستن"
    override val biometricLockTitle = "قوفڵی بایۆمێتری و پەنجەمۆر"
    override val biometricLockSubtitle = "داواکردنی پەنجەمۆر یان ناسینەوەی دەموچاو لە کاتی کردنەوەی باسۆکا"
    override val memorySection = "بیرگەی کەسی (AI Memory)"
    override val personalMemoryTitle = "بیرگەی زیرەکی باسۆکا"
    override val personalMemorySubtitle = "هەڵگرتنی زانیاری و تایبەتمەندییە کەسییەکان"
    override val architectureSection = "دۆخی تەلارسازی سیستم"
    override val statusReady = "چالاکە (Active)"
    override val statusConfigNeeded = "ئامادەی بەستنەوە (Ready to Connect)"
    override val geminiApiStatus = "مۆدێلی ژیری دەستکرد (Gemini 3.5 Flash)"
    override val geminiApiDesc = "پەیوەندی ستریمینگ بە تەواوی ئامادەیە بە کلیلی GEMINI_API_KEY"
    override val localDbStatus = "بنکەدراوەی ناوخۆیی (Room Database)"
    override val localDbDesc = "کاش و پەیامەکان بە شێوازی سەربەخۆ لەسەر مۆبایلەکەت دەمێننەوە"
    override val secureBackendStatus = "باک-ئێندی پارێزراو (Secure Backend)"
    override val secureBackendDesc = "بۆ هەڵگرتنی کلیلی نهێنی بەبێ دزەپێکردن لە کۆدی کڕیار"
    override val voiceTtsStatus = "دەنگ و خوێندنەوە (Voice & TTS)"
    override val voiceTtsDesc = "پێکهاتەی دەنگی ئامادەکراوە بۆ پەیوەستکردنی دەنگ"
    override val version = "وەشانی ١.٠.٠ · بەهێزکراو بە Gemini 3.5 Flash"
    override val back = "گەڕانەوە"
    override val close = "داخستن"
    override val systemAwaitingResponse = "کلیلی GEMINI_API_KEY دابنێ لە بەشی نهێنییەکان تا باسۆکا AI وەڵامت بداتەوە."
    override val clearAll = "سڕینەوەی هەمووی"
    override val generatingResponse = "باسۆکا خەریکی بیرکردنەوە و نووسینە..."
    override val errorApiKeyMissing = "کلیلی GEMINI_API_KEY دیارینەکراوە. تکایە کلیلی API لە پنێلی نهێنییەکان (Secrets) دابنێ."
    override val errorNetwork = "هەڵەی هێڵی ئینتەرنێت. تکایە هێڵەکەت بپشکنە و دووبارە هەوڵبدەرەوە."
    override val errorGeneral = "هەڵەیەک ڕوویدا لە کاتی پەیوەندی بە Gemini. تکایە دووبارە هەوڵبدەرەوە."
    override val copiedToClipboard = "دەق کۆپیکرا"

    // Auth & Security Strings
    override val accountSection = "هەژماری کەسی"
    override val loginTitle = "چوونەژوورەوەی کەسی"
    override val registerTitle = "دروستکردنی هەژماری کەسی"
    override val emailLabel = "ئیمەیڵ"
    override val passwordLabel = "وشەی نهێنی"
    override val confirmPasswordLabel = "دووبارەکردنەوەی وشەی نهێنی"
    override val displayNameLabel = "ناوی بەکارهێنەر"
    override val loginButton = "چوونەژوورەوە"
    override val registerButton = "دروستکردنی هەژمار"
    override val logoutButton = "چوونەدەرەوە لە هەژمار"
    override val createAccountPrompt = "هێشتا هەژمارت نییە؟ دروستی بکە"
    override val alreadyHaveAccountPrompt = "هەژمارت هەیە؟ لێرە بچۆ ژوورەوە"
    override val profileTitle = "پڕۆفایلی کەسی"
    override val personalAccountBadge = "ئامێری تایبەت · پارێزراو"
    override val privateDeviceTitle = "دۆخی ئامێری تایبەت (Private Device)"
    override val privateDeviceSubtitle = "سنووردارکردنی باسۆکا تەنها بۆ هەژماری خاوەنی ئەم مۆبایلە"
    override val appLockTitle = "قوفڵی ئەپ (App Lock)"
    override val appLockSubtitle = "داخستنی باسۆکا لە پاش ماوەیەکی دیاریکراو یان کاتی کردنەوە"
    override val lockTimeoutTitle = "ماوەی قوفڵبوونەوەی خۆکار"
    override val lockImmediately = "دەستبەجێ کاتی دەرچوون"
    override val lockAfter1Min = "پاش ١ خولەک"
    override val lockAfter5Min = "پاش ٥ خولەک"
    override val biometricUnlockTitle = "قوفڵی بایۆمێتری"
    override val biometricUnlockSubtitle = "پەنجەمۆر یان دەموچاو پێشکەش بکە بۆ کردنەوەی باسۆکا"
    override val unlockWithBiometrics = "کردنەوە بە پەنجەمۆر / ناسینەوە"
    override val unlockWithPasswordFallback = "کردنەوە بە وشەی نهێنی"
    override val unlockApp = "کردنەوەی باسۆکا"
    override val lockedStateNotice = "باسۆکا پارێزراوە. تکایە خۆت بناسێنە تا بەردەوام بیت."

    // Errors
    override val errorInvalidEmail = "تکایە ناونیشانی ئیمەیڵێکی دروست بنووسە."
    override val errorPasswordShort = "وشەی نهێنی دەبێت لانیکەم ٦ پیت بێت."
    override val errorPasswordMismatch = "وشە نهێنییەکان وەک یەک نین."
    override val errorUserExists = "ئەم ئیمەیڵە پێشتر تۆمارکراوە."
    override val errorUserNotFound = "ئەم هەژمارە نەدۆزرایەوە. تکایە ناوی ئیمەیڵ بپشکنە."
    override val errorIncorrectPassword = "وشەی نهێنی هەڵەیە. دووبارە هەوڵبدەرەوە."
    override val accountCreatedSuccess = "هەژمارەکەت بە سەرکەوتوویی دروستکرا."
    override val logoutConfirmMessage = "ئایا دڵنیایت لە چوونەدەرەوە لە هەژماری کەسی؟"
    override val biometricHardwareNotice = "بایۆمێتری بە فەرمی لەسەر مۆبایلی فیزیایی لە ڕێگەی Android BiometricPrompt کاردەکات."

    // Biometric Fingerprint Authentication Strings
    override val biometricSetupTitle = "تۆمارکردن بە پەنجەمۆر"
    override val biometricSetupSubtitle = "پەنجەمۆری مۆبایلەکەت چالاک بکە بۆ پاراستنی گفتوگۆ و بیرەوەرییە کەسییەکانت لە باسۆکا AI."
    override val biometricAuthTitle = "چوونەژوورەوە بە پەنجەمۆر"
    override val biometricAuthSubtitle = "پەنجەت لەسەر هەستەوەری پەنجەمۆری مۆبایلەکەت دابنێ بۆ چوونەژوورەوە."
    override val biometricEnableButton = "چالاککردنی پەنجەمۆر بۆ باسۆکا"
    override val biometricScanButton = "ناسینەوە بە پەنجەمۆر"
    override val biometricNoHardware = "ئەم ئامێرە هەستەوەری پەنجەمۆری نییە یان لەلایەن سیستەمەوە پشتگیری ناکرێت. باسۆکا AI پێویستی بە ناسینەوەی بایۆمێترییە."
    override val biometricNoneEnrolled = "هیچ پەنجەمۆرێک لە ڕێکخستنەکانی ئامێرەکەتدا تۆمار نەکراوە. تکایە سەرەتا لە ڕێکخستنەکانی سیستەم (Security) پەنجەمۆرەکەت تۆمار بکە تا بتوانیت بەردەوام بیت."
    override val biometricHardwareUnavailable = "هەستەوەری پەنجەمۆر لەم ساتەدا بەردەست نییە یان سەرقاڵە. تکایە کەمێکی تر دووبارە هەوڵبدەرەوە."
    override val openDeviceSecuritySettings = "کردنەوەی ڕێکخستنەکانی ئامێر"
    override val biometricRegistrationSuccess = "پەنجەمۆر بە سەرکەوتوویی بۆ باسۆکا AI چالاککرا."
    override val biometricLoginSuccess = "بە سەرکەوتوویی بە پەنجەمۆر هاتیتە ژوورەوە."
    override val userNameOptionalLabel = "ناوی بەکارهێنەر (ئارەزوومەندانە)"
    override val authMethodLabel = "شێوازی چوونەژوورەوە"
    override val biometricFingerprintActive = "پەنجەمۆری پارێزراوی ئامێر"
    override val biometricTouchSensorHint = "پەنجەت بخەرە سەر هەستەوەری پەنجەمۆری مۆبایلەکەت"
    override val switchAccountReset = "ڕێکخستنەوەی هەژمار و دەستپێکردنەوە لەسەرەتاوە"
    override val biometricPromptTitle = "ناسینەوەی باسۆکا AI"
    override val biometricPromptSubtitle = "پەنجەمۆرەکەت لەسەر هەستەوەر دابنێ تا بێیتە ژوورەوە"
    override val biometricPromptCancel = "پاشگەزبوونەوە"
    override val biometricPromptFailed = "پەنجەمۆر نەناسرایەوە. تکایە دووبارە هەوڵبدەرەوە."

    // Memory Management
    override val memoryManagementTitle = "یادگە و بیرەوەرییە کەسییەکان"
    override val memoryManagementSubtitle = "بیرەوەرییە هەڵگیراوەکان کە داوات کردووە باسۆکا لەبیری بمێنێت"
    override val addMemory = "زیادکردنی بیرەوەری"
    override val editMemory = "دەستکاریکردنی بیرەوەری"
    override val deleteMemory = "سڕینەوە"
    override val deleteAllMemories = "سڕینەوەی هەموو بیرەوەرییەکان"
    override val deleteAllMemoriesConfirm = "ئایا دڵنیایت لە سڕینەوەی هەموو بیرەوەرییە تایبەتەکانت؟ ئەم زانیارییانە بە تەواوی دەسڕدرێنەوە."
    override val emptyMemoriesNotice = "هیچ بیرەوەرییەکی هەڵگیراو نییە.\n\nباسۆکا تەنها کاتێک زانیاری هەڵدەگرێت کە بە ڕوونی داوای لێبکەیت (وەک: 'ئەمە لەبیرت بێت...'). هەروەها دەتوانیت لێرەوە بیرەوەری نوێ تۆمار بکەیت."
    override val memorySavedSuccess = "بیرەوەرییەکە بە سەرکەوتوویی لە یادگەی باسۆکادا هەڵگیرا."
    override val memoryUpdatedSuccess = "بیرەوەرییەکە بە سەرکەوتوویی نوێکرایەوە."
    override val memoryDeletedSuccess = "بیرەوەرییەکە سڕایەوە."
    override val allMemoriesDeletedSuccess = "هەموو بیرەوەرییەکان سڕانەوە."
    override val explicitMemoryRule = "یاسای تایبەتمەندی: باسۆکا تەنها ئەو زانیارییانە دەپارێزێت کە بە ئاشکرا داوای بکەیت، و تەنها تایبەتن بەم هەژمارە."
    override val manageMemoriesAction = "بینین و بەڕێوەبردنی بیرەوەرییەکان"
    override val memoryTitleLabel = "ناونیشان (ئارەزوومەندانە)"
    override val memoryContentLabel = "دەقی بیرەوەری"
    override val save = "پاشەکەوتکردن"
    override val cancel = "پاشگەزبوونەوە"

    // Voice & Speech
    override val speechListeningPrompt = "قسە بکە، باسۆکا گوێت لێدەگرێت..."
    override val speechServiceNotAvailable = "سیستەمی ناسینەوەی دەنگ لەسەر ئەم ئامێرە بەردەست نییە. دەتوانیت بە نووسین پەیامەکەت بنێریت."
    override val micPermissionNeeded = "مۆڵەتی مایکرۆفۆن پێویستە بۆ قسەکردن بە دەنگ."
    override val kurdishTtsUnavailableNotice = "دەنگی سۆرانی کوردی لە بزوێنەری خوێندنەوەی دەنگی ئەم ئامێرە دانەمەزراوە؛ بە دەنگی سیستەم دەخوێندرێتەوە."
    override val audioPlaybackActive = "دەنگی وەڵام لێدەدرێت..."
    override val stopSpeaking = "وەستاندن"
    override val playSpeaking = "خوێندنەوەی دەنگ"

    // Image Generation
    override val imageGenerationTitle = "دروستکردنی وێنە"
    override val imageGenerationMode = "دۆخی وێنە"
    override val imageGenerationModeBadge = "دۆخی دروستکردنی وێنەی ژیری دەستکرد چالاکە"
    override val imageInputPlaceholder = "وەسفی وێنەکە بنووسە (بۆ نموونە: شارێکی داهاتوو لە شەودا)..."
    override val generatingImage = "باسۆکا خەریکی دروستکردنی وێنەیە..."
    override val generatingImageProgress = "ڕەنگڕێژکردنی پێکسڵەکان بە مۆدێلی Gemini 2.5 Flash Image..."
    override val imagePromptLabel = "داواکاری وێنە:"
    override val saveToGallery = "پاشەکەوت لە مۆبایل"
    override val imageSavedSuccess = "وێنەکە بە سەرکەوتوویی لە گەلەری مۆبایلەکەت پاشەکەوت کرا."
    override val imageSaveFailed = "پاشەکەوتکردنی وێنە سەرکەوتوو نەبوو."
    override val shareImage = "هاوبەشکردن"
    override val viewFullscreen = "بینینی تەواوی شاشە"
    override val copyPrompt = "کۆپیکردنی وەسف"
    override val errorImageApiKeyMissing = "کلیلی GEMINI_API_KEY دیارینەکراوە. بۆ دروستکردنی وێنەی ڕاستەقینە، تکایە کلیلی API لە پنێلی نهێنییەکان (Secrets) دابنێ."
    override val errorImageGenerationFailed = "دروستکردنی وێنە سەرکەوتوو نەبوو. تکایە پشکنینی کلیل و پەیوەندی هێڵ بکە."
    override val imageModelBadge = "Gemini 2.5 Flash Image"
    override val cancelImageGeneration = "ڕاگرتن"
    override val exitImageMode = "دەرچوون لە دۆخی وێنە"
    override val tryImagePromptExample = "نموونە: وێنەی قەڵای هەولێر لە شەودا بە تیشکی نیۆن بکێشە"
  }

  private val ArabicStrings = object : AppStrings {
    override val appName = "باسوكا AI"
    override val appSubtitle = "المساعد الشخصي الذكي"
    override val newChat = "محادثة جديدة"
    override val history = "سجل المحادثات"
    override val settings = "الإعدادات"
    override val inputPlaceholder = "اكتب رسالة إلى باسوكا..."
    override val send = "إرسال"
    override val stopGeneration = "إيقاف"
    override val retry = "إعادة المحاولة"
    override val voiceInputFuture = "الإدخال الصوتي"
    override val voiceTooltip = "زر الميكروفون جاهز؛ يتطلب ربط خدمة التعرف الصوتي."
    override val welcomeTitle = "مرحباً بك في باسوكا AI"
    override val welcomeDesc = "مساعدك الذكي الشخصي على هاتفك، آمن وخاص وسريع مع دعم كامل للكردية السورانية واللغات العالمية."
    override val initialNoticeTitle = "محرك Gemini 3.5 Flash"
    override val initialNoticeBody = "للتفعيل المباشر، أضف GEMINI_API_KEY في لوحة الأسرار (Secrets Panel)."
    override val configureApiAction = "تهيئة المفتاح"
    override val emptyHistory = "لا توجد محادثات سابقة حتى الآن"
    override val conversationsTitle = "المحادثات"
    override val currentChat = "المحادثة الحالية"
    override val deleteChat = "حذف"
    override val languageSection = "اللغة"
    override val themeSection = "المظهر والتصميم"
    override val darkMode = "الوضع المظلم المستقبلي"
    override val securitySection = "الأمان والخصوصية"
    override val biometricLockTitle = "القفل بالبصمة / القياسات الحيوية"
    override val biometricLockSubtitle = "المطالبة ببصمة الإصبع أو التعرف على الوجه عند فتح باسوكا"
    override val memorySection = "الذاكرة الشخصية (AI Memory)"
    override val personalMemoryTitle = "ذاكرة باسوكا الذكية"
    override val personalMemorySubtitle = "حفظ تفضيلاتك وسياقك الشخصي محلياً"
    override val architectureSection = "حالة البنية التحتية"
    override val statusReady = "نشط ومتاح"
    override val statusConfigNeeded = "جاهز للربط"
    override val geminiApiStatus = "محرك الذكاء الاصطناعي (Gemini 3.5 Flash)"
    override val geminiApiDesc = "البث التدفقي الفوري مهيأ وجاهز مع المفتاح الآمن"
    override val localDbStatus = "قاعدة البيانات المحلية (Room DB)"
    override val localDbDesc = "حفظ الرسائل والمحادثات على هاتفك بأمان تام"
    override val secureBackendStatus = "الخادم الخلفي الآمن (Secure Backend)"
    override val secureBackendDesc = "لحماية المفاتيح السرية من الانكشاف في كود التطبيق"
    override val voiceTtsStatus = "الإدخال الصوتي وتحويل النص لصوت"
    override val voiceTtsDesc = "واجهة الصوت مهيأة لربط خدمات معالجة الصوت"
    override val version = "الإصدار 1.0.0 · مدعوم بـ Gemini 3.5 Flash"
    override val back = "رجوع"
    override val close = "إغلاق"
    override val systemAwaitingResponse = "يرجى تعيين GEMINI_API_KEY في لوحة الأسرار لتلقي الردود الحية."
    override val clearAll = "مسح الكل"
    override val generatingResponse = "باسوكا يكتب الرد الآن..."
    override val errorApiKeyMissing = "مفتاح GEMINI_API_KEY غير موجود. يرجى إدخاله في لوحة Secrets في AI Studio."
    override val errorNetwork = "فشل الاتصال بالإنترنت. يرجى التحقق من الشبكة وإعادة المحاولة."
    override val errorGeneral = "حدث خطأ أثناء الاتصال بمحرك Gemini. يرجى إعادة المحاولة."
    override val copiedToClipboard = "تم نسخ النص"

    // Auth & Security
    override val accountSection = "الحساب الشخصي"
    override val loginTitle = "تسجيل الدخول الشخصي"
    override val registerTitle = "إنشاء حساب شخصي"
    override val emailLabel = "البريد الإلكتروني"
    override val passwordLabel = "كلمة المرور"
    override val confirmPasswordLabel = "تأكيد كلمة المرور"
    override val displayNameLabel = "اسم المستخدم"
    override val loginButton = "تسجيل الدخول"
    override val registerButton = "إنشاء الحساب"
    override val logoutButton = "تسجيل الخروج"
    override val createAccountPrompt = "ليس لديك حساب؟ أنشئ حسابك الشخصي"
    override val alreadyHaveAccountPrompt = "لديك حساب بالفعل؟ سجل دخولك هنا"
    override val profileTitle = "الملف الشخصي"
    override val personalAccountBadge = "جهاز شخصي · مؤمن"
    override val privateDeviceTitle = "وضع الجهاز الشخصي (Private Device)"
    override val privateDeviceSubtitle = "قصر استخدام التطبيق على الحساب الشخصي المسجل على هذا الجهاز"
    override val appLockTitle = "قفل التطبيق (App Lock)"
    override val appLockSubtitle = "طلب البصمة أو المصادقة عند فتح التطبيق أو بعد مهلة"
    override val lockTimeoutTitle = "مدة القفل التلقائي"
    override val lockImmediately = "فوراً عند المغادرة"
    override val lockAfter1Min = "بعد دقيقة واحدة"
    override val lockAfter5Min = "بعد 5 دقائق"
    override val biometricUnlockTitle = "قفل القياسات الحيوية"
    override val biometricUnlockSubtitle = "استخدم بصمة الإصبع أو الوجه لفتح باسوكا"
    override val unlockWithBiometrics = "فتح بالبصمة / الوجه"
    override val unlockWithPasswordFallback = "استخدام كلمة المرور"
    override val unlockApp = "فتح باسوكا"
    override val lockedStateNotice = "التطبيق مقفل لحماية بياناتك الشخصية."

    // Errors
    override val errorInvalidEmail = "يرجى إدخال عنوان بريد إلكتروني صحيح."
    override val errorPasswordShort = "كلمة المرور يجب أن لا تقل عن 6 أحرف."
    override val errorPasswordMismatch = "كلمتا المرور غير متطابقتين."
    override val errorUserExists = "البريد الإلكتروني مسجل بالفعل."
    override val errorUserNotFound = "الحساب غير موجود. يرجى التحقق من البريد."
    override val errorIncorrectPassword = "كلمة المرور غير صحيحة."
    override val accountCreatedSuccess = "تم إنشاء الحساب الشخصي بنجاح."
    override val logoutConfirmMessage = "هل أنت متأكد من تسجيل الخروج من جهازك؟"
    override val biometricHardwareNotice = "تعمل البصمة عبر واجهة Android BiometricPrompt الرسمية على الهاتف الفعلي."

    // Biometric Fingerprint Authentication Strings
    override val biometricSetupTitle = "التسجيل بالبصمة"
    override val biometricSetupSubtitle = "قم بتفعيل بصمة الهاتف لحماية محادثاتك وذكرياتك في باسوكا AI."
    override val biometricAuthTitle = "الدخول بالبصمة"
    override val biometricAuthSubtitle = "ضع إصبعك على مستشعر البصمة للدخول إلى باسوكا."
    override val biometricEnableButton = "تفعيل البصمة لباسوكا"
    override val biometricScanButton = "المصادقة بالبصمة"
    override val biometricNoHardware = "هذا الجهاز لا يحتوي على مستشعر بصمة أو غير مدعوم من النظام."
    override val biometricNoneEnrolled = "لم يتم تسجيل أي بصمة في إعدادات جهازك. يرجى تسجيل بصمة في إعدادات الأمان أولاً."
    override val biometricHardwareUnavailable = "مستشعر البصمة غير متاح حالياً. يرجى المحاولة لاحقاً."
    override val openDeviceSecuritySettings = "فتح إعدادات أمان الجهاز"
    override val biometricRegistrationSuccess = "تم تفعيل البصمة لباسوكا بنجاح."
    override val biometricLoginSuccess = "تم تسجيل الدخول بالبصمة بنجاح."
    override val userNameOptionalLabel = "اسم المستخدم (اختياري)"
    override val authMethodLabel = "طريقة تسجيل الدخول"
    override val biometricFingerprintActive = "بصمة الإصبع الحيوية للجهاز"
    override val biometricTouchSensorHint = "المس مستشعر البصمة للمتابعة"
    override val switchAccountReset = "إعادة ضبط الحساب والبدء من جديد"
    override val biometricPromptTitle = "مصادقة باسوكا AI"
    override val biometricPromptSubtitle = "تحقق من بصمة إصبعك للمتابعة"
    override val biometricPromptCancel = "إلغاء"
    override val biometricPromptFailed = "لم يتم التعرف على البصمة. يرجى المحاولة مجدداً."

    // Memory Management
    override val memoryManagementTitle = "إدارة الذاكرة الشخصية"
    override val memoryManagementSubtitle = "الذكريات المحفوظة التي طلبت صراحة من باسوكا حفظها"
    override val addMemory = "إضافة ذاكرة"
    override val editMemory = "تعديل الذاكرة"
    override val deleteMemory = "حذف"
    override val deleteAllMemories = "مسح جميع الذكريات"
    override val deleteAllMemoriesConfirm = "هل أنت متأكد من رغبتك في مسح كافة الذكريات المحفوظة؟ لا يمكن التراجع عن هذا الإجراء."
    override val emptyMemoriesNotice = "لا توجد ذكريات محفوظة حالياً.\n\nباسوكا يحفظ فقط ما تطلب منه صراحة حفظه (مثل: 'احفظ هذا...'). يمكنك أيضاً إضافة ذكريات جديدة يدوياً هنا."
    override val memorySavedSuccess = "تم حفظ الذاكرة في السجل الشخصي بنجاح."
    override val memoryUpdatedSuccess = "تم تحديث الذاكرة بنجاح."
    override val memoryDeletedSuccess = "تم حذف الذاكرة."
    override val allMemoriesDeletedSuccess = "تم مسح كافة الذكريات المحفوظة."
    override val explicitMemoryRule = "قاعدة الخصوصية: باسوكا يحفظ حصرياً ما تطلب صراحة تذكره ومخصص لحسابك فقط."
    override val manageMemoriesAction = "عرض وإدارة الذكريات"
    override val memoryTitleLabel = "العنوان (اختياري)"
    override val memoryContentLabel = "نص الذاكرة"
    override val save = "حفظ"
    override val cancel = "إلغاء"

    // Voice & Speech
    override val speechListeningPrompt = "تحدث الآن، باسوكا يستمع إليك..."
    override val speechServiceNotAvailable = "خدمة التعرف على الصوت غير متوفرة على هذا الجهاز. يمكنك استخدام الكتابة."
    override val micPermissionNeeded = "مطلوب إذن الميكروفون للتحدث الصوتي."
    override val kurdishTtsUnavailableNotice = "الصوت الكردي السوراني غير مثبت في محرك النطق لهذا الجهاز؛ سيتم النطق بالصوت الافتراضي للجهاز."
    override val audioPlaybackActive = "جاري تشغيل الرد الصوتي..."
    override val stopSpeaking = "إيقاف"
    override val playSpeaking = "تشغيل الصوت"

    // Image Generation
    override val imageGenerationTitle = "توليد الصور بالذكاء الاصطناعي"
    override val imageGenerationMode = "وضع الصور"
    override val imageGenerationModeBadge = "وضع توليد الصور الذكية نشط"
    override val imageInputPlaceholder = "صف الصورة التي ترغب في توليدها..."
    override val generatingImage = "باسوكا يقوم بتوليد الصورة الآن..."
    override val generatingImageProgress = "معالجة ورسم البكسلات بواسطة Gemini 2.5 Flash Image..."
    override val imagePromptLabel = "وصف الصورة:"
    override val saveToGallery = "حفظ في الهاتف"
    override val imageSavedSuccess = "تم حفظ الصورة بنجاح في معرض الصور بجهازك."
    override val imageSaveFailed = "فشل حفظ الصورة في الجهاز."
    override val shareImage = "مشاركة الصورة"
    override val viewFullscreen = "ملء الشاشة"
    override val copyPrompt = "نسخ الوصف"
    override val errorImageApiKeyMissing = "مفتاح GEMINI_API_KEY غير مهيأ. لتوليد الصور يرجى إضافته في لوحة الأسرار (Secrets)."
    override val errorImageGenerationFailed = "فشل توليد الصورة. يرجى التحقق من المفتاح والاتصال بالشبكة."
    override val imageModelBadge = "Gemini 2.5 Flash Image"
    override val cancelImageGeneration = "إلغاء"
    override val exitImageMode = "الخروج من وضع الصور"
    override val tryImagePromptExample = "مثال: ارسم مدينة مستقبلية بأضواء النيون ليلاً"
  }

  private val EnglishStrings = object : AppStrings {
    override val appName = "Basoka AI"
    override val appSubtitle = "Personal AI Assistant"
    override val newChat = "New Conversation"
    override val history = "Conversation History"
    override val settings = "Settings"
    override val inputPlaceholder = "Type a message to Basoka..."
    override val send = "Send"
    override val stopGeneration = "Stop"
    override val retry = "Retry"
    override val voiceInputFuture = "Voice Input"
    override val voiceTooltip = "Microphone UI is ready; requires voice recognition API integration."
    override val welcomeTitle = "Welcome to Basoka AI"
    override val welcomeDesc = "Your private, personal AI companion on your Android phone, fluent in Kurdish Sorani and multi-language AI."
    override val initialNoticeTitle = "Gemini 3.5 Flash Engine"
    override val initialNoticeBody = "To activate streaming responses, ensure GEMINI_API_KEY is configured in AI Studio Secrets."
    override val configureApiAction = "Configure API"
    override val emptyHistory = "No conversations yet"
    override val conversationsTitle = "Conversations"
    override val currentChat = "Current Chat"
    override val deleteChat = "Delete"
    override val languageSection = "Language"
    override val themeSection = "Appearance & Theme"
    override val darkMode = "Electric Dark Mode"
    override val securitySection = "Security & Privacy"
    override val biometricLockTitle = "Biometric & Fingerprint Lock"
    override val biometricLockSubtitle = "Require fingerprint or face unlock when opening Basoka"
    override val memorySection = "Personal AI Memory"
    override val personalMemoryTitle = "Basoka Context Memory"
    override val personalMemorySubtitle = "Store personal preferences and notes locally"
    override val architectureSection = "System Architecture Status"
    override val statusReady = "Active"
    override val statusConfigNeeded = "Ready to Connect"
    override val geminiApiStatus = "AI Engine (Gemini 3.5 Flash)"
    override val geminiApiDesc = "Streaming REST pipeline connected securely via BuildConfig"
    override val localDbStatus = "Local Persistence (Room DB)"
    override val localDbDesc = "Encapsulates messages and history offline on-device"
    override val secureBackendStatus = "Secure Backend Service"
    override val secureBackendDesc = "Prevents client-side API key leaks"
    override val voiceTtsStatus = "Voice Input & Text-to-Speech"
    override val voiceTtsDesc = "Voice UI hook prepared for microphone and audio pipeline"
    override val version = "Version 1.0.0 · Powered by Gemini 3.5 Flash"
    override val back = "Back"
    override val close = "Close"
    override val systemAwaitingResponse = "Please set GEMINI_API_KEY in Secrets panel to receive real-time answers."
    override val clearAll = "Clear All"
    override val generatingResponse = "Basoka is generating response..."
    override val errorApiKeyMissing = "GEMINI_API_KEY is not configured. Please set your key in AI Studio Secrets."
    override val errorNetwork = "Network error. Please check your internet connection and retry."
    override val errorGeneral = "Error connecting to Gemini API. Please try again."
    override val copiedToClipboard = "Copied to clipboard"

    // Auth & Security
    override val accountSection = "Personal Account"
    override val loginTitle = "Personal Login"
    override val registerTitle = "Create Personal Account"
    override val emailLabel = "Email Address"
    override val passwordLabel = "Password"
    override val confirmPasswordLabel = "Confirm Password"
    override val displayNameLabel = "Your Name"
    override val loginButton = "Sign In"
    override val registerButton = "Create Account"
    override val logoutButton = "Sign Out"
    override val createAccountPrompt = "No personal account yet? Create one"
    override val alreadyHaveAccountPrompt = "Already registered? Sign in here"
    override val profileTitle = "Personal Profile"
    override val personalAccountBadge = "Personal Device · Secured"
    override val privateDeviceTitle = "Private Device Mode"
    override val privateDeviceSubtitle = "Restrict app access exclusively to this personal device account"
    override val appLockTitle = "App Lock"
    override val appLockSubtitle = "Lock Basoka with biometrics upon launch and after inactivity"
    override val lockTimeoutTitle = "Auto-Lock Inactivity"
    override val lockImmediately = "Immediately on app exit"
    override val lockAfter1Min = "After 1 minute"
    override val lockAfter5Min = "After 5 minutes"
    override val biometricUnlockTitle = "Biometric Security"
    override val biometricUnlockSubtitle = "Verify fingerprint or face to unlock Basoka AI"
    override val unlockWithBiometrics = "Unlock with Biometrics"
    override val unlockWithPasswordFallback = "Use Password Fallback"
    override val unlockApp = "Unlock Basoka"
    override val lockedStateNotice = "Basoka is locked for your privacy."

    // Errors
    override val errorInvalidEmail = "Please enter a valid email address."
    override val errorPasswordShort = "Password must be at least 6 characters long."
    override val errorPasswordMismatch = "Passwords do not match."
    override val errorUserExists = "An account with this email already exists."
    override val errorUserNotFound = "Account not found. Please check your email."
    override val errorIncorrectPassword = "Incorrect password. Please try again."
    override val accountCreatedSuccess = "Personal account created successfully."
    override val logoutConfirmMessage = "Are you sure you want to sign out of this device?"
    override val biometricHardwareNotice = "Native biometrics utilize official Android BiometricPrompt on physical devices."

    // Biometric Fingerprint Authentication Strings
    override val biometricSetupTitle = "Setup Fingerprint"
    override val biometricSetupSubtitle = "Enable device fingerprint authentication to protect your Basoka AI conversations and memories."
    override val biometricAuthTitle = "Fingerprint Sign In"
    override val biometricAuthSubtitle = "Touch the fingerprint sensor to unlock and access Basoka AI."
    override val biometricEnableButton = "Enable Fingerprint for Basoka"
    override val biometricScanButton = "Authenticate Fingerprint"
    override val biometricNoHardware = "This device lacks biometric fingerprint hardware or it is not supported."
    override val biometricNoneEnrolled = "No fingerprints are enrolled on this device. Please enroll a fingerprint in Android Settings first."
    override val biometricHardwareUnavailable = "Biometric fingerprint sensor is currently unavailable. Please try again."
    override val openDeviceSecuritySettings = "Open Device Security Settings"
    override val biometricRegistrationSuccess = "Fingerprint registered successfully for Basoka AI."
    override val biometricLoginSuccess = "Authenticated successfully with fingerprint."
    override val userNameOptionalLabel = "Display Name (Optional)"
    override val authMethodLabel = "Authentication Method"
    override val biometricFingerprintActive = "Native Device Fingerprint"
    override val biometricTouchSensorHint = "Touch the fingerprint sensor on your device"
    override val switchAccountReset = "Reset Account and Re-enroll"
    override val biometricPromptTitle = "Basoka AI Authentication"
    override val biometricPromptSubtitle = "Verify your fingerprint to continue"
    override val biometricPromptCancel = "Cancel"
    override val biometricPromptFailed = "Fingerprint not recognized. Please try again."

    // Memory Management
    override val memoryManagementTitle = "Personal AI Memory"
    override val memoryManagementSubtitle = "Explicit memories you asked Basoka to permanently remember"
    override val addMemory = "Add Memory"
    override val editMemory = "Edit Memory"
    override val deleteMemory = "Delete"
    override val deleteAllMemories = "Delete All Memories"
    override val deleteAllMemoriesConfirm = "Are you sure you want to delete all saved memories? This action is permanent."
    override val emptyMemoriesNotice = "No saved memories yet.\n\nBasoka only permanently remembers information when you explicitly ask it to (e.g. 'remember this...'). You can also add memories manually here."
    override val memorySavedSuccess = "Memory saved successfully to your personal account."
    override val memoryUpdatedSuccess = "Memory updated successfully."
    override val memoryDeletedSuccess = "Memory deleted."
    override val allMemoriesDeletedSuccess = "All memories have been deleted."
    override val explicitMemoryRule = "Privacy Rule: Basoka only saves what you explicitly ask to remember, isolated strictly to your personal account."
    override val manageMemoriesAction = "View & Manage Memories"
    override val memoryTitleLabel = "Title (Optional)"
    override val memoryContentLabel = "Memory Content"
    override val save = "Save"
    override val cancel = "Cancel"

    // Voice & Speech
    override val speechListeningPrompt = "Speak now, Basoka is listening..."
    override val speechServiceNotAvailable = "Speech recognition is not available on this device. You can use text input."
    override val micPermissionNeeded = "Microphone permission is required for voice input."
    override val kurdishTtsUnavailableNotice = "Kurdish Sorani TTS voice is not installed on this device's speech engine; playing with system voice."
    override val audioPlaybackActive = "Playing audio response..."
    override val stopSpeaking = "Stop Audio"
    override val playSpeaking = "Read Aloud"

    // Image Generation
    override val imageGenerationTitle = "AI Image Generation"
    override val imageGenerationMode = "Image Mode"
    override val imageGenerationModeBadge = "AI Image Generation Mode Active"
    override val imageInputPlaceholder = "Describe the image you want to generate..."
    override val generatingImage = "Basoka is generating the image..."
    override val generatingImageProgress = "Rendering pixels with Gemini 2.5 Flash Image..."
    override val imagePromptLabel = "Prompt:"
    override val saveToGallery = "Save to Device"
    override val imageSavedSuccess = "Image saved successfully to device gallery."
    override val imageSaveFailed = "Failed to save image to device."
    override val shareImage = "Share Image"
    override val viewFullscreen = "Full Screen"
    override val copyPrompt = "Copy Prompt"
    override val errorImageApiKeyMissing = "GEMINI_API_KEY is not configured. Please set your key in AI Studio Secrets to generate images."
    override val errorImageGenerationFailed = "Image generation failed. Please check your API key and connection."
    override val imageModelBadge = "Gemini 2.5 Flash Image"
    override val cancelImageGeneration = "Cancel"
    override val exitImageMode = "Exit Image Mode"
    override val tryImagePromptExample = "Try: 'A futuristic city illuminated by neon lights at night'"
  }
}
