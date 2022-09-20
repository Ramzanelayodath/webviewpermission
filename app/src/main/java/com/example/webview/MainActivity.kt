package com.example.webview

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {
    private var mUploadMessage: ValueCallback<Uri> = null!!
    private val FILECHOOSER_RESULTCODE = 1
    private val MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 101
    private var myRequest: PermissionRequest? = null
    var webview: WebView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

         webview = findViewById<WebView>(R.id.webview)

        webview!!.getSettings().setJavaScriptEnabled(true)
        webview!!.setWebViewClient(WebViewClient())
        webview!!.getSettings().setJavaScriptEnabled(true)
        webview!!.getSettings().setJavaScriptCanOpenWindowsAutomatically(true)
        webview!!.setWebViewClient(WebViewClient())

        webview!!.getSettings().setSaveFormData(true)
        webview!!.getSettings().setSupportZoom(false)
        webview!!.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE)
        webview!!.getSettings().setPluginState(WebSettings.PluginState.ON)

        webview!!.webChromeClient = object :WebChromeClient(){
            override fun onPermissionRequest(request: PermissionRequest?) {
                myRequest = request

                for (permission in request!!.resources) {
                    when (permission) {
                        "android.webkit.resource.AUDIO_CAPTURE" -> {
                            askForPermission(
                                request.origin.toString(),
                                Manifest.permission.RECORD_AUDIO,
                                MY_PERMISSIONS_REQUEST_RECORD_AUDIO
                            )
                        }
                    }
                }
            }
            fun openFileChooser(uploadMsg: ValueCallback<Uri>) {
                mUploadMessage = uploadMsg
                val i = Intent(Intent.ACTION_GET_CONTENT)
                i.addCategory(Intent.CATEGORY_OPENABLE)
                i.type = "image/*"
                this@MainActivity.startActivityForResult(
                    Intent.createChooser(i, "Image Browser"),
                    FILECHOOSER_RESULTCODE
                )
            }
        }

        webview!!.loadUrl("https://av2.sysapp.ae/en")

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_RECORD_AUDIO -> {
                Log.d("WebView", "PERMISSION FOR AUDIO")
                if (grantResults.size > 0
                    && grantResults[0] === PackageManager.PERMISSION_GRANTED
                ) {


                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    myRequest!!.grant(myRequest!!.resources)
                    webview!!.loadUrl("https://av2.sysapp.ae/en")
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }

        }
    }


    override fun onActivityResult(
        requestCode: Int, resultCode: Int,
        intent: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage) return
            val result = if (intent == null || resultCode != RESULT_OK) null else intent.data
            mUploadMessage!!.onReceiveValue(result)
            mUploadMessage = null!!
        }
    }

    fun askForPermission(origin: String, permission: String, requestCode: Int) {
        Log.d("WebView", "inside askForPermission for" + origin + "with" + permission)
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                permission
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@MainActivity,
                    permission
                )
            ) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this@MainActivity, arrayOf(permission),
                    requestCode
                )
            }
        } else {
            myRequest!!.grant(myRequest!!.resources)
        }
    }
}