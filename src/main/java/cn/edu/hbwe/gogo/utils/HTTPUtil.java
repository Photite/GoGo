package cn.edu.hbwe.gogo.utils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Map;

@Component
public class HTTPUtil {

    private static String eduSystemUrl;

    @Value("${edu-system.url}")
    public void setEduSystemUrl(String eduSystemUrl) {
        HTTPUtil.eduSystemUrl = eduSystemUrl;
    }

    static {
        init();
    }

    public static Connection newSession(Object... url) {
        return Jsoup.newSession()
                .ignoreContentType(true)
                .ignoreHttpErrors(true)
                .url(compile(url))
                .timeout(20000);
    }

    public static String compile(Object... p) {
        System.out.println(eduSystemUrl);
        if (p == null || p.length == 0) {
            // 处理空值的情况。这可能是返回一个默认值或抛出一个异常。
            return eduSystemUrl; // 或者 throw new IllegalArgumentException("p cannot be null or empty");
        }
        StringBuilder builder = new StringBuilder(eduSystemUrl);
        Arrays.stream(p).forEach(builder::append);
        return builder.toString();
    }

    public static Connection.Response sendPostRequest(String url, Map<String, String> headers, Map<String, String> data, Map<String, String> cookies) throws Exception {
        Connection connection = newSession(url);
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.header(entry.getKey(), entry.getValue());
            }
        }
        if (data != null) {
            for (Map.Entry<String, String> entry : data.entrySet()) {
                connection.data(entry.getKey(), entry.getValue());
            }
        }
        if (cookies != null) {
            connection.cookies(cookies);
        }
        return connection.method(Connection.Method.POST).execute();
    }

    public static Connection.Response sendGetRequest(String url, Map<String, String> headers, Map<String, String> cookies) throws Exception {
        Connection connection = newSession(url);
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.header(entry.getKey(), entry.getValue());
            }
        }
        if (cookies != null) {
            connection.cookies(cookies);
        }
        return connection.method(Connection.Method.GET).execute();
    }


    static public void init() {
        try {
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        } catch (NoSuchAlgorithmException | KeyManagementException ignored) {
        }
    }
}
