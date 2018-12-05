package ch.guengel.funnel.xmlretriever

enum class FeedType {
    RSS,
    ATOM;

    companion object {
        fun mimeTypeToFeedType(mimeType: String): FeedType {
            return when (mimeTypeWithoutCharacterSet(mimeType)) {
                "application/atom+xml" -> ATOM
                "text/xml" -> RSS
                "application/rss+xml" -> RSS
                else -> throw UnknownFeedType("Unknown feed type '${mimeType}'")
            }
        }

        private fun mimeTypeWithoutCharacterSet(fullMimeType: String): String =
                fullMimeType.split(";")[0]

    }
}

class UnknownFeedType(message: String) : Exception(message)
