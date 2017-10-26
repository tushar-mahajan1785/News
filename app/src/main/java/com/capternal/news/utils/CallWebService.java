package com.capternal.news.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.view.Window;
import android.view.WindowManager;

import com.capternal.news.R;
import com.capternal.news.base.AppController;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import javax.net.ssl.SSLHandshakeException;

/**
 * Created by CapternalSystems on 8/30/2016.
 */
public class CallWebService extends AsyncTask<String, Void, String> {

    private OnGetUrlResponse onGetUrlResponse;
    private JSONArray objImages = null;
    private JSONArray objFiles = null;
    String URL = "";
    JSONObject objJsonObject = null;
    Activity objActivity;
    WebserviceResponse objWebserviceResponse;
    public boolean showLoader = false;
    public static ProgressDialog progressDialog;
    boolean isMultiPart = false;

    //    Variable to be used in GET METHOD response.
    boolean isGet = false;
    private String urlId = "";

    //    Database.
    private AppController appController;
    private Activity activity;
    private HttpClient httpclient = null;

    public CallWebService(String URL, JSONObject objJsonObject, Activity objActivity, WebserviceResponse objWebserviceResponse, boolean showLoader) {
        this.URL = URL;
        this.objJsonObject = objJsonObject;
        this.objActivity = objActivity;
        this.objWebserviceResponse = objWebserviceResponse;
        this.showLoader = showLoader;
        appController = (AppController) objActivity.getApplication();
    }

    public CallWebService(String URL, JSONObject objJsonObject, Activity objActivity, WebserviceResponse objWebserviceResponse) {
        this.URL = URL;
        this.objJsonObject = objJsonObject;
        this.objActivity = objActivity;
        this.objWebserviceResponse = objWebserviceResponse;
        this.showLoader = false;
    }

    public CallWebService(String URL, JSONObject objJsonObject, WebserviceResponse objWebserviceResponse) {
        this.URL = URL;
        this.objJsonObject = objJsonObject;
        this.objActivity = objActivity;
        this.objWebserviceResponse = objWebserviceResponse;
        this.showLoader = false;
    }

    public CallWebService(String URL, JSONArray objImages, JSONArray objFiles, JSONObject objJsonObject, Activity objActivity, WebserviceResponse objWebserviceResponse, boolean showLoader, boolean isMultiPart) {
        this.URL = URL;
        this.objImages = objImages;
        this.objFiles = objFiles;
        this.objJsonObject = objJsonObject;
        this.objActivity = objActivity;
        this.objWebserviceResponse = objWebserviceResponse;
        this.showLoader = showLoader;
        this.isMultiPart = isMultiPart;
        appController = (AppController) objActivity.getApplication();
    }

    public CallWebService(String URL, Activity objActivity, OnGetUrlResponse onGetUrlResponse, boolean showLoader, boolean isGet, String urlId) {
        this.URL = URL;
        this.objActivity = objActivity;
        this.onGetUrlResponse = onGetUrlResponse;
        this.showLoader = showLoader;
        this.isGet = isGet;
        this.urlId = urlId;
        appController = (AppController) objActivity.getApplication();
    }

    @Override
    protected void onPreExecute() {

        super.onPreExecute();
        if (showLoader) {
            progressDialog = new ProgressDialog(objActivity, R.style.custom_progress_dialog_style);
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            progressDialog.show();
            progressDialog.setContentView(R.layout.custom_progress_layout);
        }
    }

    public static void dismissDialog() {
        try {
            if (progressDialog != null) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String doInBackground(String... params) {
        String strResult = "";
        JSONObject objJsonObjectResult = new JSONObject();
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            if (exitValue == 0) {
                System.out.println("CONNECTIVITY SUCCESS");
            } else {
                System.out.println("CONNECTIVITY FAIL");
                try {
                    objJsonObjectResult.put("result", false);
                    objJsonObjectResult.put("message", Constants.NO_INTERNET_CONNECTION);
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    return objJsonObjectResult.toString();
                } catch (NullPointerException ne) {
                    ne.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            if (isMultiPart) {
                Utils.d("API INPUT : MULTIPART : IMAGES : ", String.valueOf(objImages));
                Utils.d("API INPUT : MULTIPART : FILES : ", " " + String.valueOf(objFiles));
                Utils.d("API INPUT : MULTIPART : DATA : ", " " + String.valueOf(objJsonObject));
                Utils.d("API INPUT : MULTIPART : URL : ", " " + String.valueOf(URL));

                try {
                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost(URL);
                    post.setHeader("Accept", "application/json");
                    post.setHeader("Content-type", "application/json");
                    post.setHeader(Constants.KEY_AUTH_TOKEN, "");
                    HttpResponse response = null;
                    MultipartEntity entityBuilder = new MultipartEntity();

                    Iterator<String> iter = objJsonObject.keys();
                    while (iter.hasNext()) {
                        String key = iter.next();
                        try {
                            Object value = objJsonObject.get(key);
                            Utils.out("DATA : KEY " + key + " VALUE : " + value);
                            entityBuilder.addPart(key, new StringBody(String.valueOf(value)));
                        } catch (JSONException e) {
                            // Something went wrong!
                        }
                    }
                    if (objFiles != null) {
                        for (int fileIndex = 0; fileIndex < objFiles.length(); fileIndex++) {
                            File file = new File((objFiles.getJSONObject(fileIndex).getString("Files")));
                            FileBody objFile = new FileBody(file);
                            entityBuilder.addPart("file" + fileIndex, objFile);
                        }
                    }
                    if (objImages != null) {
                        for (int imageIndex = 0; imageIndex < objImages.length(); imageIndex++) {
                            File file = new File((objImages.getJSONObject(imageIndex).getString("Images")));
                            FileBody objFile = new FileBody(file);
                            entityBuilder.addPart("Image" + imageIndex, objFile);
                        }
                    }
                    post.setEntity(entityBuilder);
                    response = client.execute(post);
                    HttpEntity httpEntity = response.getEntity();
                    strResult = EntityUtils.toString(httpEntity);
                    Utils.d("API OUTPUT : JSON OBJECT : ", strResult);
                    return strResult;
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                } catch (ClientProtocolException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                return null;
            } else if (isGet) {
                System.out.println("API URL :" + this.URL);
                try {
                    if (android.os.Build.VERSION.SDK_INT > 9) {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                                .permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                    }
                    int TIMEOUT_MILLISEC = 60000; // = 10 seconds
                    HttpParams httpParams = new BasicHttpParams();
                    HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
                    HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
                    httpclient = new DefaultHttpClient(httpParams);
                    HttpGet objHttpGet = new HttpGet(this.URL);
                    objHttpGet.setHeader(Constants.KEY_AUTH_TOKEN, "");
                    Utils.out("API TOKEN : " + "");
                    HttpResponse objHttpResponse = httpclient.execute(objHttpGet);

                    Utils.out("ERROR CODE : " + objHttpResponse.getStatusLine());

                    for (int i = 0; i < objHttpResponse.getAllHeaders().length; i++) {
                        Utils.out("RESPONSE GET HEADERS : " + i + objHttpResponse.getAllHeaders()[i]);
                    }

                    for (int index = 0; index < objHttpResponse.getAllHeaders().length; index++) {
                        if (objHttpResponse.getAllHeaders()[index].getName().toLowerCase().equalsIgnoreCase("content-type")) {
                            if (!objHttpResponse.getAllHeaders()[index].getValue().toLowerCase().contains("application/json")) {
                                try {
                                    JSONObject objJsonObject = new JSONObject();
                                    objJsonObject.put("result", false);
                                    objJsonObject.put("message", "Please check headers in server response");
                                    return objJsonObject.toString();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    System.out.println("API OUTPUT STATUS CODE : " + objHttpResponse.getStatusLine());

                    if (objHttpResponse.getStatusLine().getStatusCode() > 400 && objHttpResponse.getStatusLine().getStatusCode() < 500) {
                        InputStream objInputStream = objHttpResponse.getEntity().getContent();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(
                                objInputStream, "iso-8859-1"), 8);
                        StringBuilder sb = new StringBuilder();
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        objInputStream.close();

                        strResult = sb.toString();
                        Utils.d("API OUTPUT : JSON OBJECT : ", strResult);

                    } else if (objHttpResponse.getStatusLine().getStatusCode() == 200) {
                        InputStream objInputStream = objHttpResponse.getEntity().getContent();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(
                                objInputStream, "iso-8859-1"), 8);
                        StringBuilder sb = new StringBuilder();
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        objInputStream.close();
                        strResult = sb.toString();
                        Utils.d("API OUTPUT : JSON OBJECT : ", strResult);
                    }

                } catch (HttpResponseException e) {
                    System.out.println("HTTP RESPONSE EXCEPTION : " + e.getMessage());
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    System.out.println("UNSUPPORTED ENCODING EXCEPTION : " + e.getMessage());
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    System.out.println("CLIENT PROTOCOL EXCEPTION : " + e.getMessage());
                    e.printStackTrace();
                } catch (SSLHandshakeException ssle) {
                    System.out.println("SSL HANDSHAKE EXCEPTION : " + ssle.getMessage());
                    ssle.printStackTrace();
                } catch (IOException e) {
                    System.out.println("IO EXCEPTION : " + e.getMessage());
                    e.printStackTrace();
                }

            } else {
                if (android.os.Build.VERSION.SDK_INT > 9) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                }
                try {
                    int TIMEOUT_MILLISEC = 60000; // = 10 seconds
                    HttpParams httpParams = new BasicHttpParams();
                    HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
                    HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
                    httpclient = new DefaultHttpClient(httpParams);
                    HttpPost httppost = new HttpPost(this.URL);
                    httppost.setHeader("Accept", "application/json");
                    httppost.setHeader("Content-type", "application/json");
                    httppost.setHeader(Constants.KEY_AUTH_TOKEN, "");// CLEAR LOG PROGRAMATICALLY
                    Utils.out("API TOKEN : " + "");
                    Process process = new ProcessBuilder().command("logcat", "-c").redirectErrorStream(true).start();
                    Utils.d("API INPUT : CALLED URL : ", this.URL);
                    Utils.d("API INPUT : JSON OBJECT : ", this.objJsonObject.toString());

                    httppost.setEntity(new ByteArrayEntity(this.objJsonObject.toString().getBytes("UTF8")));
                    HttpResponse objHttpResponse = httpclient.execute(httppost);
                    System.out.println("FINAL HTTP STATUS : " + objHttpResponse.getStatusLine().getStatusCode());
                    System.out.println("FINAL HTTP STATUS PHRASE: " + objHttpResponse.getStatusLine().getReasonPhrase());

                    for (int i = 0; i < objHttpResponse.getAllHeaders().length; i++) {
                        System.out.println("RESPONSE POST HEADERS : " + i + objHttpResponse.getAllHeaders()[i]);
                    }

                    for (int index = 0; index < objHttpResponse.getAllHeaders().length; index++) {
                        if (objHttpResponse.getAllHeaders()[index].getName().toLowerCase().equalsIgnoreCase("content-type")) {
                            if (!objHttpResponse.getAllHeaders()[index].getValue().toLowerCase().contains("application/json")) {
                                try {
                                    JSONObject objJsonObject = new JSONObject();
                                    objJsonObject.put("result", false);
                                    objJsonObject.put("message", "Please check headers in server response");
                                    return objJsonObject.toString();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    if (objHttpResponse.getStatusLine().getStatusCode() > 400 && objHttpResponse.getStatusLine().getStatusCode() < 500) {
                        InputStream objInputStream = objHttpResponse.getEntity().getContent();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(objInputStream, "iso-8859-1"), 8);
                        StringBuilder sb = new StringBuilder();
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        objInputStream.close();
                        strResult = sb.toString();
                        Utils.d("API OUTPUT : JSON OBJECT : ", strResult);
                    } else if (objHttpResponse.getStatusLine().getStatusCode() == 200) {
                        InputStream objInputStream = objHttpResponse.getEntity().getContent();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(objInputStream, "iso-8859-1"), 8);
                        StringBuilder sb = new StringBuilder();
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        objInputStream.close();
                        strResult = sb.toString();
                        Utils.d("API OUTPUT : JSON OBJECT : ", strResult);
                    }

                } catch (HttpResponseException e) {
                    System.out.println("HTTP RESPONSE EXCEPTION : " + e.getMessage());
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    System.out.println("UNSUPPORTED ENCODING EXCEPTION : " + e.getMessage());
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    System.out.println("CLIENT PROTOCOL EXCEPTION : " + e.getMessage());
                    e.printStackTrace();
                } catch (SSLHandshakeException ssle) {
                    System.out.println("SSL HANDSHAKE EXCEPTION : " + ssle.getMessage());
                    ssle.printStackTrace();
                } catch (IOException e) {
                    System.out.println("IO EXCEPTION : " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (NullPointerException ne) {
            ne.printStackTrace();
            dismissDialog();
        } catch (Exception e) {
            e.printStackTrace();
            dismissDialog();
        }
        return strResult;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        dismissDialog();
        /**
         * If Session expired then clear the preferences and take the user outside of the app to the login screen.
         * */

        try {
            JSONObject jsonObject = new JSONObject(s);
            if ((jsonObject.has(Constants.API_RESULT) && !jsonObject.getBoolean(Constants.API_RESULT))
                    && (jsonObject.has(Constants.API_MESSAGE) && jsonObject.getString(Constants.API_MESSAGE).equals(Constants.API_UNAUTHORIZED))) {
                AlertDialog alertDialog = Utils.prepareAlert(objActivity, "Error", jsonObject.getString(Constants.API_MESSAGE));
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        /*Intent intent = new Intent(objActivity, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        database.deleteTableData(Database.TABLE_USER);
                        Utils.pushToBack(objActivity, intent);
                        objActivity.finishAffinity();*/
                    }
                });
                alertDialog.show();
            } else {
                if (isGet) {
                    onGetUrlResponse.onGetUrlResponse(this.urlId, this.URL, s);
                } else {
                    objWebserviceResponse.onWebserviceResponse(this.URL, s);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCancelled(String s) {
        super.onCancelled(s);
        if (isGet) {
            onGetUrlResponse.onGetUrlResponse(this.urlId, this.URL, s);
        } else {
            objWebserviceResponse.onWebserviceResponse(this.URL, s);
        }
    }

    public interface WebserviceResponse {
        public void onWebserviceResponse(String strUrl, String strResult);
    }

    public interface OnGetUrlResponse {
        public void onGetUrlResponse(String urlId, String strUrl, String strResult);

        public void onGetUrlCancelled(String urlId, String strUrl, String cancelledResult);
    }
}
