package com.duyvv.basecompose.presentation.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.duyvv.basecompose.R

val SvnFontFamily = FontFamily(
    Font(R.font.svn_gilroy_regular, FontWeight.Normal),
    Font(R.font.svn_gilroy_medium, FontWeight.Medium),
    Font(R.font.svn_gilroy_semibold, FontWeight.SemiBold),
    Font(R.font.svn_gilroy_bold, FontWeight.Bold)
)

val TextStyleBold = TextStyle(
    fontWeight = FontWeight.Bold,
    fontFamily = SvnFontFamily,
    color = text500,
)
val TextStyleSemiBold = TextStyle(
    fontWeight = FontWeight.SemiBold,
    fontFamily = SvnFontFamily,
    color = text500
)

val TextStyleMedium = TextStyle(
    fontWeight = FontWeight.Medium,
    fontFamily = SvnFontFamily,
    color = text500
)

val TextStyleNormal = TextStyle(
    fontWeight = FontWeight.Normal,
    fontFamily = SvnFontFamily,
    color = text500
)

val TextStyleLight = TextStyle(
    fontWeight = FontWeight.Light,
    fontFamily = SvnFontFamily,
    color = text500
)