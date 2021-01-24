package ch.guengel.funnel.notifier.smtp

data class SmtpNotificationSettings(val server: String, val port: Int, val sender: String)