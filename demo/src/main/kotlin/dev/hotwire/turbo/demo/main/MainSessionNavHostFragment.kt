package dev.hotwire.turbo.demo.main

import android.content.Context
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import dev.hotwire.turbo.BuildConfig
import dev.hotwire.turbo.config.TurboPathConfiguration
import dev.hotwire.turbo.demo.features.imageviewer.ImageViewerFragment
import dev.hotwire.turbo.demo.features.numbers.NumberBottomSheetFragment
import dev.hotwire.turbo.demo.features.numbers.NumbersFragment
import dev.hotwire.turbo.demo.features.web.WebBottomSheetFragment
import dev.hotwire.turbo.demo.features.web.WebFragment
import dev.hotwire.turbo.demo.features.web.WebHomeFragment
import dev.hotwire.turbo.demo.features.web.WebModalFragment
import dev.hotwire.turbo.demo.util.HOME_URL
import dev.hotwire.turbo.demo.util.initDayNightTheme
import dev.hotwire.turbo.session.TurboSessionNavHostFragment
import dev.hotwire.turbo.views.TurboWebView
import kotlin.reflect.KClass


/** Instantiate the interface and set the context  */
class WebAppInterface(private val mContext: Context) {

    /** Show a toast from the web page  */
    @JavascriptInterface
    fun showToast(toast: String) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show()
    }
}


@Suppress("unused")
class MainSessionNavHostFragment : TurboSessionNavHostFragment() {
    override val sessionName = "main"

    override val startLocation = HOME_URL

    override val registeredActivities: List<KClass<out AppCompatActivity>>
        get() = listOf()

    override val registeredFragments: List<KClass<out Fragment>>
        get() = listOf(
            WebFragment::class,
            WebHomeFragment::class,
            WebModalFragment::class,
            WebBottomSheetFragment::class,
            NumbersFragment::class,
            NumberBottomSheetFragment::class,
            ImageViewerFragment::class
        )

    override val pathConfigurationLocation: TurboPathConfiguration.Location
        get() = TurboPathConfiguration.Location(
            assetFilePath = "json/configuration.json"
        )

    // Docs say to run addJavascriptInterface onSessionCreated, but context is missing there
    override fun onCreateWebView(context: Context): TurboWebView {
        var webView = super.onCreateWebView(context)

        // Bridge
        webView.addJavascriptInterface(WebAppInterface(context), "Android")

        return webView
    }

    override fun onSessionCreated() {
        super.onSessionCreated()
        session.webView.settings.userAgentString = customUserAgent(session.webView)
        session.webView.initDayNightTheme()

        if (BuildConfig.DEBUG) {
            session.setDebugLoggingEnabled(true)
            WebView.setWebContentsDebuggingEnabled(true)
        }
    }

    private fun customUserAgent(webView: WebView): String {
        return "Turbo Native Android ${webView.settings.userAgentString}"
    }
}
