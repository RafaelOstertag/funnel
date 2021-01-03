package ch.guengel.funnel.notifier

import ch.guengel.funnel.feed.data.FeedEnvelope

interface NotificationSender {
    fun notify(feedEnvelope: FeedEnvelope)
}