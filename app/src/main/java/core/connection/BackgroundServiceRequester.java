package core.connection;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RequestQueue.RequestFilter;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

import core.connection.request.BackgroundServiceRequest;
import core.connection.ssl.EasySslSocketFactory;
import core.connection.ssl.TrustedSslSocketFactory;
import core.connection.volley.BackgroundResponse;
import core.util.Constant;
import core.util.Constant.RequestType;
import core.util.DLog;

/**
 * @author Tyrael
 * @version 1.0 <br>
 * @since January 2014
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public final class BackgroundServiceRequester implements
        Listener<BackgroundResponse>, ErrorListener {

    private final static String TAG = BackgroundServiceRequester.class.getSimpleName();
    private static BackgroundServiceRequester instance;
    private static RequestQueue httpQueue;
    private static RequestQueue sslQueue;

    private BackgroundServiceRequester(Context context) {
        httpQueue = Volley.newRequestQueue(context);
        sslQueue = Volley.newRequestQueue(context, new HurlStack(null, Constant.SSL_ENABLED ?
                TrustedSslSocketFactory.getTrustedSslSocketFactory(context,
                        Constant.KEY_STORE_TYPE,
                        Constant.KEY_STORE_ID,
                        Constant.KEY_STORE_PASSWORD)
                : EasySslSocketFactory.getEasySslSocketFactory()));
    }

    public static BackgroundServiceRequester getInstance(Context context) {
        if (instance == null)
            instance = new BackgroundServiceRequester(context);
        return instance;
    }

    public void startRequest(BackgroundServiceRequest request) {
        if (request != null) {
            if (httpQueue != null
                    && request.getRequestType() == RequestType.HTTP) {
                httpQueue.add(request);
            }
            if (sslQueue != null
                    && request.getRequestType() == RequestType.HTTPS) {
                sslQueue.add(request);
            }
        }
    }

    public void stopRequest() {
        if (httpQueue != null)
            httpQueue.stop();
        if (sslQueue != null)
            sslQueue.stop();
    }

    public void cancelAll(Object tag) {
        if (tag == null) {
            cancelAllWithFilter(new RequestFilter() {

                @Override
                public boolean apply(Request<?> req) {
                    return true;
                }
            });
        } else {
            if (httpQueue != null)
                httpQueue.cancelAll(tag);
            if (sslQueue != null)
                sslQueue.cancelAll(tag);
        }
    }

    public void cancelAllWithFilter(RequestFilter filter) {
        if (httpQueue != null)
            httpQueue.cancelAll(filter);
        if (sslQueue != null)
            sslQueue.cancelAll(filter);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        DLog.d(TAG, "Background >> onErrorResponse >> " + error.getMessage());
    }

    @Override
    public void onResponse(BackgroundResponse response) {
        DLog.d(TAG, "Background >> onResponse >> "
                + response.getContent().length);
    }

}
