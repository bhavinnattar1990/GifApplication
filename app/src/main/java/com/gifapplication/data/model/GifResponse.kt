package com.gifapplication.data.model

data class GifResponse (
    val data: MutableList<Gif>,
    val pagination: Pagination,
    val meta: Meta
)

data class Pagination(
    val total_count: Int?,
    val count: Int?,
    val offset: Int?
)

data class Meta(
    val status: Int?,
    val msg: String?,
    val response_id: String?
)