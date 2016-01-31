/* Service Handler from Android JSON Parsing Tutorial
 * by  Ravi Tamada at AndroidHive, Jan. 21, 2012
 * Retrieved from http://www.androidhive.info/2012/01/android-json-parsing-tutorial/
 *
 * ServiceHandler.java
 * This class is responsible of making a HTTP call and getting the response using the
 * makeServiceCall(String url, int method, List params) using the method parameters:
 *      url – The url to make a http call
 *      method – The http method either GET or POST. We should pass ServiceHandler.GET or
 *          ServiceHandler.POST as value
 *      params – Any parameters you want to submit to that url. This is optional.
 *
 * Add INTERNET permission to the AndroidManifest.xml file:
 *      <uses-permission android:name="android.permission.INTERNET" />
 *
 * Android 6.0 removed support for the Apache HTTP client. To enable its use, declare the following
 * compile-time dependency in the build.gradle file:
 *      android {
 *          useLibrary 'org.apache.http.legacy'
 *      }
 */

package com.sasha_henry.lobbyday;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class ServiceHandler {

    static String response = null;
    public final static int GET = 1;
    public final static int POST = 2;

    public ServiceHandler() {

    }

    /**
     * Making service call
     * @url - url to make request
     * @method - http request method
     * */
    public String makeServiceCall(String url, int method) {
        return this.makeServiceCall(url, method, null);
    }

    /**
     * Making service call
     * @url - url to make request
     * @method - http request method
     * @params - http request params
     * */
    public String makeServiceCall(String url, int method, List<NameValuePair> params) {
        try {
            // http client
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;

            // Checking http request method type
            if (method == POST) {
                HttpPost httpPost = new HttpPost(url);
                // adding post params
                if (params != null) {
                    httpPost.setEntity(new UrlEncodedFormEntity(params));
                }

                httpResponse = httpClient.execute(httpPost);

            } else if (method == GET) {
                // appending params to url
                if (params != null) {
                    String paramString = URLEncodedUtils
                            .format(params, "utf-8");
                    url += "?" + paramString;
                }
                HttpGet httpGet = new HttpGet(url);

                httpResponse = httpClient.execute(httpGet);

            }
            httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;

    }
}