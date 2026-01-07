package com.duyvv.basecompose.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Language(
    val code: String,
    val internationalName: String,
    val idIcon: Int = 0,
    val isChoose: Boolean = false,
)
