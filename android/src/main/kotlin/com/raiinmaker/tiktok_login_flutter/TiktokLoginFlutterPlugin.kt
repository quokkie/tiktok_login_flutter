package com.raiinmaker.tiktok_login_flutter

import android.app.Activity
import android.util.Log
import androidx.annotation.NonNull
import com.raiinmaker.tiktok_login_flutter.tiktokapi.TikTokEntryActivity
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler


/** TiktokLoginFlutterPlugin */
class TiktokLoginFlutterPlugin : FlutterPlugin, MethodCallHandler, ActivityAware {


    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel
    private var activity: Activity? = null
    private var clientKey: String? = null

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "tiktok_login_flutter")

        channel.setMethodCallHandler(this)

    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: MethodChannel.Result) {
        when (call.method) {
            "initializeTiktokLogin" -> initializeTiktokLogin(call = call, result = result)
            "authorize" -> authorize(call = call, result = result)
            else -> result.notImplemented()
        }
    }

    private fun initializeTiktokLogin(call: MethodCall, result: MethodChannel.Result) {
        try {
            clientKey = call.arguments as String
            result.success(true)
        } catch (e: Exception) {
            result.error(
                "INITIALIZATION_FAILED",
                e.localizedMessage,
                null
            )

        }
    }

    private fun authorize(call: MethodCall, result: MethodChannel.Result) {
        Log.d("TiktokLoginFlutter", "Authorize")

        val safeActivity = activity ?: return
        val safeClientKey = clientKey ?: return

        val scope: String = call.argument<String>("scope")!!
        val redirectUrl: String = call.argument<String>("redirectUrl")!!

        // passing result callback to TikTokEntryActivity for return the response
        if (TikTokEntryActivity.result == null) {
            TikTokEntryActivity.result = result
        }

      Log.d("TiktokLoginFlutter", "Authorize will start")
        if (safeActivity is TikTokEntryActivity) {
            safeActivity.authorize(scope, redirectUrl, safeClientKey)
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onDetachedFromActivity() {
        activity = null
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        activity = binding.activity
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity

    }

    override fun onDetachedFromActivityForConfigChanges() {
        activity = null
    }

}
