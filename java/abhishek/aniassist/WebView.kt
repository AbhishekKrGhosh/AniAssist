package abhishek.aniassist

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient

class WebView : AppCompatActivity() {
    private lateinit var webView: WebView
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        webView = findViewById(R.id.webView)
        webView.webViewClient = WebViewClient()
        webView.loadUrl("https://animalkingdomclient.onrender.com/")
        webView.settings.javaScriptEnabled = true
        webView.settings.setSupportZoom(true)
    }

    override fun onBackPressed() {
        if(webView.canGoBack())
            webView.goBack()
        else
            super.onBackPressed()
    }
}