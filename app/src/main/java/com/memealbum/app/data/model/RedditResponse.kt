package com.memealbum.app.data.model

import com.google.gson.annotations.SerializedName

data class RedditResponse(
    val data: RedditData
)

data class RedditData(
    val children: List<RedditChild>,
    val after: String?
)

data class RedditChild(
    val data: RedditPost
)

data class RedditPost(
    val id: String,
    val title: String,
    val url: String,
    val thumbnail: String?,
    val preview: Preview?,
    @SerializedName("post_hint") val postHint: String?,
    val score: Int,
    val author: String,
    @SerializedName("created_utc") val createdUtc: Long,
    @SerializedName("is_video") val isVideo: Boolean,
    val permalink: String
) {
    fun getImageUrl(): String? {
        // Prefer high-res preview image
        val previewUrl = preview?.images?.firstOrNull()?.source?.url
            ?.replace("&amp;", "&")
        if (previewUrl != null) return previewUrl

        // Fallback to direct url if it's an image
        if (url.endsWith(".jpg") || url.endsWith(".jpeg") ||
            url.endsWith(".png") || url.endsWith(".gif") ||
            url.endsWith(".webp")) {
            return url
        }

        // Try imgur direct
        if (url.contains("imgur.com") && !url.contains("gallery")) {
            val imgurId = url.substringAfterLast("/").substringBefore(".")
            return "https://i.imgur.com/$imgurId.jpg"
        }

        return null
    }
}

data class Preview(
    val images: List<PreviewImage>?
)

data class PreviewImage(
    val source: ImageSource?,
    val resolutions: List<ImageSource>?
)

data class ImageSource(
    val url: String,
    val width: Int,
    val height: Int
)
