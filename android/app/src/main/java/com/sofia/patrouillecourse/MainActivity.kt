package com.sofia.patrouillecourse

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.view.WindowManager
import android.webkit.JavascriptInterface
import android.webkit.WebView
import java.util.Locale

/**
 * Le jeu (HTML/JS) tourne dans une WebView, hors-ligne, depuis les assets.
 * La voix passe par la synthèse vocale d'Android, forcée en français de France.
 */
class MainActivity : Activity() {

    private lateinit var web: WebView
    private var tts: TextToSpeech? = null
    @Volatile private var ttsReady = false

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        web = WebView(this)
        setContentView(web)
        hideSystemBars()

        web.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            mediaPlaybackRequiresUserGesture = false
            allowFileAccess = true
        }
        web.addJavascriptInterface(Voice(), "AndroidTTS")

        tts = TextToSpeech(this) { status ->
            val engine = tts
            if (status == TextToSpeech.SUCCESS && engine != null) {
                // Meilleure voix française de France disponible sans internet ; sinon la langue fr-FR par défaut.
                val voice = engine.voices
                    ?.filter { it.locale.language == "fr" && it.locale.country == "FR" && !it.isNetworkConnectionRequired }
                    ?.maxByOrNull { it.quality }
                if (voice != null) engine.voice = voice else engine.language = Locale.FRANCE
                engine.setPitch(1.1f)
                engine.setSpeechRate(0.95f)
                ttsReady = true
            }
        }

        web.loadUrl("file:///android_asset/index.html")
    }

    inner class Voice {
        @JavascriptInterface
        fun speak(text: String) {
            if (ttsReady) tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "sofia")
        }

        @JavascriptInterface
        fun stop() {
            tts?.stop()
        }
    }

    @Suppress("DEPRECATION")
    private fun hideSystemBars() {
        web.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemBars()
    }

    // Quand on quitte l'app, la course se met en pause et la musique s'arrête.
    override fun onPause() {
        web.evaluateJavascript("window.appPause && appPause()", null)
        tts?.stop()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        web.evaluateJavascript("window.appResume && appResume()", null)
    }

    // Bouton retour : met en pause, puis revient au menu ; quitte seulement depuis l'accueil.
    @Deprecated("Gestion simple du bouton retour")
    override fun onBackPressed() {
        web.evaluateJavascript("(window.onAndroidBack && onAndroidBack()) ? '1' : '0'") { result ->
            if (result != "\"1\"") {
                @Suppress("DEPRECATION")
                super.onBackPressed()
            }
        }
    }

    override fun onDestroy() {
        tts?.shutdown()
        web.destroy()
        super.onDestroy()
    }
}
