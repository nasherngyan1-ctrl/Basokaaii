package com.example.model

import androidx.compose.ui.unit.LayoutDirection

enum class AppLanguage(
  val code: String,
  val displayName: String,
  val englishName: String,
  val nativeName: String,
  val layoutDirection: LayoutDirection
) {
  KURDISH_SORANI(
    code = "ckb",
    displayName = "Kurdish (Sorani)",
    englishName = "Kurdish (Sorani)",
    nativeName = "کوردیی سۆرانی",
    layoutDirection = LayoutDirection.Rtl
  ),
  ARABIC(
    code = "ar",
    displayName = "Arabic",
    englishName = "Arabic",
    nativeName = "العربية",
    layoutDirection = LayoutDirection.Rtl
  ),
  ENGLISH(
    code = "en",
    displayName = "English",
    englishName = "English",
    nativeName = "English",
    layoutDirection = LayoutDirection.Ltr
  )
}
