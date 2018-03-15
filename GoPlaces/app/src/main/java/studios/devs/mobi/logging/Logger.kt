package studios.devs.logging

interface Logger {

    fun configureAnalytics(sendToServer: Boolean, sendToLogCat: Boolean, logCatTag: String): Logger
    fun configureCrashlytics(sendToServer: Boolean, sendToLogCat: Boolean, logCatTag: String): Logger

    fun analytics(event: String, message: String)
    fun nonFatal(message: String)
    fun nonFatal(tag: String, message: String)
    fun exception(e: Exception)

}