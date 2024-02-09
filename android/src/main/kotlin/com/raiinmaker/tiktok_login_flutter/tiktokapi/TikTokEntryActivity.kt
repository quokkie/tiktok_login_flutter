package com.raiinmaker.tiktok_login_flutter.tiktokapi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.tiktok.open.sdk.auth.AuthApi
import com.tiktok.open.sdk.auth.AuthRequest
import com.tiktok.open.sdk.auth.utils.PKCEUtils
import io.flutter.plugin.common.MethodChannel


class TikTokEntryActivity : Activity() {

    companion object {
        var result: MethodChannel.Result? = null
    }

    private lateinit var authApi: AuthApi
    private var redirectUrl?: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authApi = AuthApi(activity = this)
        handleAuthResponse(intent)
        val scope = intent.getStringExtra("scope")
        val redirectUrl = intent.getStringExtra("redirectUrl")
        val clientKey = intent.getStringExtra("clientKey")
        Log.d("TiktokLoginFlutterAct", "On create $scope $redirectUrl $clientKey")

        if (scope != null && redirectUrl != null && clientKey != null) {
            this.redirectUrl = redirectUrl
            authorize(scope, redirectUrl, clientKey)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleAuthResponse(intent)
    }

    private fun handleAuthResponse(intent: Intent) {
        Log.d("TiktokLoginFlutterAct", "Handle auth response $intent $redirectUrl")
        if (redirectUrl != null) {
            authApi.getAuthResponseFromIntent(intent, redirectUrl)?.let {
                Log.d("TiktokLoginFlutterAct", "Did get auth code ? ${it.authCode}")
                if (it.authCode.isNotEmpty()) {
                    Log.d("TiktokLoginFlutterAct", "Did get auth code ${it.authCode}")
                    result?.success(it.authCode)
                } else if (it.errorCode != 0) {
                    Log.d("TiktokLoginFlutterAct", "Did get error ${it.errorMsg}")
                    result?.error(it.errorCode.toString(), it.errorMsg, it.authErrorDescription)
                }
                this.finish()
            }
        }
    }

    fun authorize(scope: String, redirectUrl: String, clientKey: String) {
        Log.d("TiktokLoginFlutterAct", "Authorize will start inside $scope $redirectUrl $clientKey")
        val codeVerifier = PKCEUtils.generateCodeVerifier()

        // STEP 2: Create an AuthRequest and set parameters
        val request = AuthRequest(
            clientKey = clientKey,
            scope = scope,
            redirectUri = redirectUrl,
            codeVerifier = codeVerifier
        )

        // STEP 3: Invoke the authorize method
        authApi.authorize(
            request = request
            //authMethod = AuthApi.AuthMethod.TikTokApp / AuthApi.AuthMethod.ChromeTab
        );
    }
}
