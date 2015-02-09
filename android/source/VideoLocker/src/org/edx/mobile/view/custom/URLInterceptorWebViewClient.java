package org.edx.mobile.view.custom;

import android.net.Uri;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.edx.mobile.logger.Logger;

/**
 * Created by rohan on 2/2/15.
 *
 * This class represents a custom {@link android.webkit.WebViewClient}.
 * This class is responsible for setting up a given {@link android.webkit.WebView}, assign itself
 * as a {@link android.webkit.WebViewClient} delegate and to intercept URLs being loaded.
 * Depending on the form of URL, this client may forward URL back to the app.
 */
public class URLInterceptorWebViewClient extends WebViewClient {

    // URL forms to be intercepted
    private static final String URL_TYPE_ENROLL         = "edxapp://enroll";
    private static final String URL_TYPE_COURSE_INFO    = "edxapp://course_info";
    private static final String PARAM_COURSE_ID         = "course_id";
    private static final String PARAM_EMAIL_OPT_IN      = "email_opt_in";
    private static final String PARAM_PATH_ID           = "path_id";

    private Logger logger = new Logger(URLInterceptorWebViewClient.class);
    private IActionListener actionListener;

    public URLInterceptorWebViewClient(WebView webView) {
        setupWebView(webView);
    }

    /**
     * Sets action listener for this client. Use this method to get callbacks
     * of actions as declared in {@link org.edx.mobile.view.custom.URLInterceptorWebViewClient.IActionListener}.
     * @param actionListener
     */
    public void setActionListener(IActionListener actionListener) {
        this.actionListener = actionListener;
    }

    /**
     * Sets up the WeView, applies minimal required settings and
     * sets this class itself as WebViewClient.
     * @param webView
     */
    private void setupWebView(WebView webView) {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(false);
        settings.setSupportZoom(true);
        webView.setWebViewClient(this);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        try {
            if (actionListener == null) {
                logger.warn("you have not set IActionLister to this WebViewClient, " +
                        "you might miss some event");
            }

            if (isCourseInfoLink(url)) {
                // we handled this URL
                return true;
            } else if(isEnrollLink(url)) {
                // we handled this URL
                return true;
            } else if (isExternalLink(url)) {
                // open URL in external web browser
                // return true means the host application handles the url
                // this should open the URL in the browser
                return true;
            } else {
                // return false means the current WebView handles the url.
                return false;
            }
        } catch(Exception ex) {
            logger.error(ex);
        }
        return super.shouldOverrideUrlLoading(view, url);
    }

    /**
     * Checks if the URL pattern matches with that of COURSE_INFO URL.
     * Extracts path_id from the URL and gives a callback to the registered
     * action listener with path_id parameter.
     * Returns true if pattern matches with COURSE_INFO URL pattern and callback succeeds with
     * extracted parameter, false otherwise.
     * @param strUrl
     * @return
     */
    boolean isCourseInfoLink(String strUrl) {
        try {
            if (strUrl.startsWith(URL_TYPE_COURSE_INFO)) {
                Uri uri = Uri.parse(strUrl);
                String pathId = uri.getQueryParameter(PARAM_PATH_ID);

                if (actionListener != null) {
                    actionListener.onClickCourseInfo(pathId);
                    return true;
                }
            }
        } catch(Exception ex) {
            logger.error(ex);
        }

        return false;
    }

    /**
     * Returns true if the pattern of the url matches with that of EXTERNAL URL pattern,
     * false otherwise.
     * @param url
     * @return
     */
    boolean isExternalLink(String url) {
        // TODO: update this method when form of external URL is determined
        return false;
    }

    /**
     * Checks if the URL pattern matches with that of ENROLL URL.
     * Extracts parameters (course_id and email_opt_in) from the URL and gives a callback to the registered
     * action listener with those parameters.
     * Returns true if pattern matches with ENROLL URL pattern and callback succeeds with
     * extracted parameters, false otherwise.
     * @param strUrl
     * @return
     */
    boolean isEnrollLink(String strUrl) {
        try {
            if (strUrl.startsWith(URL_TYPE_ENROLL)) {
                Uri uri = Uri.parse(strUrl);
                String courseId = uri.getQueryParameter(PARAM_COURSE_ID);
                boolean emailOptIn = Boolean.parseBoolean(uri.getQueryParameter(PARAM_EMAIL_OPT_IN));

                if (actionListener != null) {
                    actionListener.onClickEnroll(courseId, emailOptIn);
                    return true;
                }
            }
        } catch(Exception ex) {
            logger.error(ex);
        }

        return false;
    }

    /**
     * Action listener interface for handling enroll link click action
     * and course-info link click action.
     * We may need to add more actions to this interface in future.
     */
    public static interface IActionListener {
        /**
         * Callback that gets called when this client has intercepted Course Info URL.
         * Sub-classes or any implementation of this class should override this method to handle
         * tap of course info URL.
         * @param pathId
         */
        void onClickCourseInfo(String pathId);

        /**
         * Callback that gets called when this client has intercepted Enroll action.
         * Sub-classes or any implementation of this class should override this method to handle
         * enroll action further.
         * @param courseId
         * @param emailOptIn
         */
        void onClickEnroll(String courseId, boolean emailOptIn);
    }
}