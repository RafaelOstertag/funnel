package ch.guengel.funnel.xmlretriever

import java.util.regex.Pattern

enum class FeedType {
    RSS,
    ATOM;

    companion object {
        val pattern = Pattern.compile("<\\?xml.*\\?>")
        fun detectFeedType(rawFeed: String): FeedType {
            val withoutNewLines = rawFeed.replace("\n", "").replace("\r", "")
            val matcher = pattern.matcher(withoutNewLines)
            val withoutXmlDeclaration = matcher.replaceAll("")

            if (withoutXmlDeclaration.startsWith("<rss ")) {
                return RSS
            }

            if (withoutXmlDeclaration.startsWith("<feed ")) {
                return ATOM
            }

            throw UnknownFeedType("Cannot detect feed type for: ${withoutXmlDeclaration}")
        }
    }
}

class UnknownFeedType(message: String) : Exception(message)
