package core.connection.ssl;

import android.content.Context;

import java.io.InputStream;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;


@SuppressWarnings({"TryFinallyCanBeTryWithResources", "SameParameterValue"})
public class TrustedSslSocketFactory {

    public static SSLSocketFactory getTrustedSslSocketFactory(Context context, String keyStoreType, int keyStoreId, String password) {
        try {
            SSLContext sslContext = SSLContext.getInstance(keyStoreType);
            InputStream in = context.getResources().openRawResource(keyStoreId);
            try {
                TrustManager[] managers = new TrustManager[1];
                managers[0] = new SsX509TrustManager(keyStoreType, in, password);
                sslContext.init(null, managers, null);
            } finally {
                in.close();
            }
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }
}
