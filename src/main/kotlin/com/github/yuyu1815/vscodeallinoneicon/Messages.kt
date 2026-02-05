package com.github.yuyu1815.vscodeallinoneicon

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

/**
 * Supported UI languages
 */
enum class Language(val displayName: String, val code: String) {
    ENGLISH("English", "en"),
    JAPANESE("日本語", "ja"),
    CHINESE_SIMPLIFIED("简体中文", "zh-CN"),
    KOREAN("한국어", "ko"),
    GERMAN("Deutsch", "de");

    companion object {
        fun fromCode(code: String): Language? = entries.find { it.code == code }
    }
}

/**
 * Localized messages loaded from JSON files
 */
object Messages {
    private val cache = mutableMapOf<String, Map<String, String>>()

    private fun loadMessages(langCode: String): Map<String, String> {
        return cache.getOrPut(langCode) {
            val path = "/messages/$langCode.json"
            val stream = Messages::class.java.getResourceAsStream(path)
                ?: return@getOrPut emptyMap()

            val type = object : TypeToken<Map<String, String>>() {}.type
            InputStreamReader(stream, Charsets.UTF_8).use { reader ->
                Gson().fromJson(reader, type)
            }
        }
    }

    fun get(key: String, lang: String = "en"): String {
        // Try requested language, fallback to English
        return loadMessages(lang)[key]
            ?: loadMessages("en")[key]
            ?: key
    }
}
