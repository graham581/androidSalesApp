package com.spartandoubleglazing.sales;

import android.os.Build;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
    // Perf T5 (2026-05-20): tune the Capacitor WebView for the sales-rep
    // workload. The wrapper just loads https://spaartan.tech, so almost all
    // perf comes from the web app — these settings are the small wrapper-
    // side polish: explicit cache mode (pairs with the immutable headers
    // landed in the web app's perf T3), off-screen pre-raster for smoother
    // scrolling through long deal/kanban lists, and a white WebView
    // background so the splash-to-page hand-off doesn't flash black.
    // See spartancrm/perf-review/T5_webview_perf_tuning.md.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WebView wv = (this.bridge != null) ? this.bridge.getWebView() : null;
        if (wv == null) return;

        WebSettings s = wv.getSettings();
        // Honour HTTP cache headers (Cache-Control / ETag) — needed for the
        // per-file content-hash strategy in perf T3 to actually pay off.
        s.setCacheMode(WebSettings.LOAD_DEFAULT);
        // Explicit defaults (defensive against future Capacitor changes).
        s.setDomStorageEnabled(true);
        s.setRenderPriority(WebSettings.RenderPriority.HIGH);

        // Pre-rasterise content slightly outside the viewport so scrolling
        // new rows into view doesn't have to paint from cold. Big readability
        // win on the deal kanban and contact lists.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            s.setOffscreenPreRaster(true);
        }

        // Avoid a brief black flash between the splash theme and the first
        // pixels of the web app rendering.
        wv.setBackgroundColor(0xFFFFFFFF);
    }
}
