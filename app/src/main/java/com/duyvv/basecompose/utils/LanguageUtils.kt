package com.duyvv.basecompose.utils

import android.content.Context
import android.content.res.Configuration
import android.os.LocaleList
import com.duyvv.basecompose.R
import com.duyvv.basecompose.data.local.datastore.AppConfigManager
import com.duyvv.basecompose.domain.model.Language
import java.util.Locale

object LanguageUtils {
    fun getLocalizedContext(context: Context, languageCode: String): Context {
        if (languageCode.isEmpty()) return context

        val locale = Locale.forLanguageTag(languageCode)
        Locale.setDefault(locale)

        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)

        val localeList = LocaleList(locale)
        LocaleList.setDefault(localeList)
        configuration.setLocales(localeList)

        return context.createConfigurationContext(configuration)
    }

    suspend fun getListLanguage(): List<Language> {
        val languages: MutableList<Language> = arrayListOf()
        LanguageEnum.entries.forEach {
            languages.add(Language(it.code, it.internationalName, it.idIcon))
        }
        return languages.getHandleListLanguage(LanguageEnum.ENGLISH.code)
    }

    private suspend fun MutableList<Language>.getHandleListLanguage(
        languageCodeDefault: String
    ): List<Language> {
        find { it.code == languageCodeDefault }?.let {
            this.remove(it)
            add(0, it.copy(isChoose = false))
        }
        AppConfigManager.getInstance().languageCode.getValue().let { langCode ->
            find { language -> language.code == langCode }?.let { languageChoose ->
                remove(languageChoose)
                add(0, languageChoose.copy(isChoose = true))
            }
        }
        return this
    }

    suspend fun changeLang(lang: String, context: Context) {
        if (lang.isEmpty()) return
        AppConfigManager.getInstance().languageCode.set(lang)
        val locale = Locale.forLanguageTag(lang)
        Locale.setDefault(locale)
    }

    fun getLanguageString(code: String): String {
        return when (code) {
            "en" -> "English"
            "in" -> "Indonesian"
            "ur" -> "Urdu"
            "tl" -> "Tagalog"
            "zh" -> "Chinese"
            "hi" -> "Hindi"
            "es" -> "Spanish"
            "fr" -> "French"
            "ru" -> "Russian"
            "pt" -> "Portuguese"
            "bn" -> "Bengal"
            "de" -> "German"
            "ja" -> "Japanese"
            "ko" -> "Korean"
            "it" -> "Italian"
            "sw" -> "Swahili"
            "am" -> "Amharic"
            else -> "English"
        }
    }

    enum class LanguageEnum(val code: String, val internationalName: String, val idIcon: Int) {
        ENGLISH("en", "English", R.drawable.flag_english),
        SPANISH("es", "Spanish", R.drawable.flag_spanish),
        PORTUGUESE("pt", "Portuguese", R.drawable.flag_portugese),
        HINDI("hi", "Hindi", R.drawable.flag_hindi),
        KOREAN("ko", "Korean", R.drawable.flag_korean),
        JAPANESE("ja", "Japanese", R.drawable.flag_japanese),
        GERMAN("de", "German", R.drawable.flag_germany),
        FRENCH("fr", "French", R.drawable.flag_french),
        ITALIAN("it", "Italian", R.drawable.flag_italy),
        INDONESIAN("in", "Indonesian", R.drawable.flag_indonesia),
    }
}