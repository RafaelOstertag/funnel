package data

class FeedItemComparator : Comparator<FeedItem> {
    override fun compare(p0: FeedItem, p1: FeedItem): Int {
        if (p0 == p1) {
            return 0
        }

        return p0.created.compareTo(p1.created)
    }
}