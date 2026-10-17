package com.cinecompare.aggregator.base.models

import java.time.LocalDate

data class Movie(
    val id: String,
    val title: String,
    val posterUrl: String,
    val bannerUrl: String = "",
    val genres: List<String> = emptyList(),
    val durationMinutes: Int = 0,
    val rating: Float = 0f,
    val releaseDate: LocalDate = LocalDate.now(),
    val languages: List<String> = emptyList(),
    val formats: List<String> = emptyList(), // 2D, 3D, IMAX, 4DX
    val trailerUrl: String? = null,
    val synopsis: String = "",
    val certification: String = "UA", // U, UA, A
    val cast: List<CastMember> = emptyList()
)

data class CastMember(
    val name: String,
    val role: String,
    val photoUrl: String = ""
)
