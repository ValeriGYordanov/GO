//package studios.devs.mobi.logging
//
//import android.content.Context
//import android.os.Bundle
//import android.util.Log
//import com.crashlytics.android.Crashlytics
//import com.google.firebase.analytics.FirebaseAnalytics
//import io.fabric.sdk.android.Fabric
//import studios.devs.logging.Logger
//
//class DefaultLogger(private val mAppContext: Context) : Logger {
//
//    private var isAnalyticsSendToServerEnabled: Boolean = false
//    private var isAnalyticsSendToLogCatEnabled: Boolean = false
//    private var analyticsLogCatTag: String = ""
//
//    private var isCrashlyticsSendToServerEnabled: Boolean = false
//    private var isCrashlyticsSendToLogCatEnabled: Boolean = false
//    private var crashlyticsLogCatTag: String = ""
//
//    override fun configureAnalytics(sendToServer: Boolean, sendToLogCat: Boolean, logCatTag: String): Logger {
//        analyticsLogCatTag = logCatTag
//        isAnalyticsSendToServerEnabled = sendToServer
//        isAnalyticsSendToLogCatEnabled = sendToLogCat
//        if (isAnalyticsSendToServerEnabled) {
//            FirebaseAnalytics.getInstance(mAppContext).setAnalyticsCollectionEnabled(true)
//        }
//        return this
//    }
//
//    override fun configureCrashlytics(sendToServer: Boolean, sendToLogCat: Boolean, logCatTag: String): Logger {
//        crashlyticsLogCatTag = logCatTag
//        isCrashlyticsSendToServerEnabled = sendToServer
//        isCrashlyticsSendToLogCatEnabled = sendToLogCat
//        if (isCrashlyticsSendToServerEnabled) {
//            Fabric.with(mAppContext, Crashlytics())
//        }
//        return this
//    }
//
//    override fun analytics(event: String, message: String) {
//        if (isAnalyticsSendToServerEnabled) {
//            val bundle = Bundle()
//            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, message)
//            FirebaseAnalytics.getInstance(mAppContext).logEvent(event, bundle)
//        }
//        if (isAnalyticsSendToLogCatEnabled) {
//            Log.d(analyticsLogCatTag, "$event $message")
//        }
//    }
//
//    override fun nonFatal(message: String) {
//        nonFatal(crashlyticsLogCatTag, message)
//    }
//
//    override fun nonFatal(tag: String, message: String) {
//        if (isCrashlyticsSendToServerEnabled) {
//            Crashlytics.log(1, tag, message)
//        }
//        if (isCrashlyticsSendToLogCatEnabled) {
//            Log.d(tag, message)
//        }
//    }
//
//    override fun exception(e: Exception) {
//        if (isCrashlyticsSendToServerEnabled) {
//            Crashlytics.logException(e)
//        }
//        if (isCrashlyticsSendToLogCatEnabled) {
//            Log.e(crashlyticsLogCatTag, e.message)
//        }
//    }
//}
