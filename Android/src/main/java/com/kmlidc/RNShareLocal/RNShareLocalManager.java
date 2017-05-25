package com.kmlidc.RNShareLocal;
import android.net.Uri;
import android.content.Intent;
import android.content.ComponentName;
import android.app.Activity;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.uimanager.IllegalViewOperationException;

import java.lang.String;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

/**
 * Created by yangbin on 2017/5/20.
 */

public class RNShareLocalManager extends ReactContextBaseJavaModule{

    public RNShareLocalManager(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "RNShareLocal";
    }

    @ReactMethod
    public void message(String winTitle,String subject,String message,ReadableArray component, Callback callback) {
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_SUBJECT,subject);
        intent.putExtra(Intent.EXTRA_TEXT,message);
        intent.setType("text/plain");

        Intent chooser = Intent.createChooser(intent, winTitle);
        chooser.addCategory(Intent.CATEGORY_DEFAULT);

        //是否指定App打开
        if(component != null && component.size() == 2) {
            intent.setComponent(new ComponentName(component.getString(0), component.getString(1)));
        }

        Activity currentActivity = getCurrentActivity();
        if (currentActivity != null) {
            currentActivity.startActivity(chooser);
        } else {
            getReactApplicationContext().startActivity(chooser);
        }

        try{
            callback.invoke(false);
        } catch (IllegalViewOperationException e){
            callback.invoke(true);
        }
    }

    @ReactMethod
    public void link(String winTitle,String subject,String url,ReadableArray component, Callback callback) {
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_SUBJECT,subject);
        intent.putExtra(Intent.EXTRA_TEXT, url);
        intent.setType("text/html");

        Intent chooser = Intent.createChooser(intent, winTitle);
        chooser.addCategory(Intent.CATEGORY_DEFAULT);

        //是否指定App打开
        if(component != null && component.size() == 2) {
            intent.setComponent(new ComponentName(component.getString(0), component.getString(1)));
        }

        Activity currentActivity = getCurrentActivity();
        if (currentActivity != null) {
            currentActivity.startActivity(chooser);
        } else {
            getReactApplicationContext().startActivity(chooser);
        }

        try{
            callback.invoke(false);
        } catch (IllegalViewOperationException e){
            callback.invoke(true);
        }
    }

    @ReactMethod
    public void pictures(String winTitle,String subject,String message,ReadableArray imagesFile,ReadableArray component, Callback callback) {
        ArrayList<Uri> uris = new ArrayList<Uri>();
        for(int i=0; i<imagesFile.size();i++){
            uris.add(Uri.parse(imagesFile.getString(i)));
        }
        Intent intent=new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.putExtra(Intent.EXTRA_SUBJECT,subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        intent.putExtra(Intent.EXTRA_STREAM, uris);
        intent.putExtra ("Kdescription", message);
        intent.setType("image/*");

        Intent chooser = Intent.createChooser(intent, winTitle);
        chooser.addCategory(Intent.CATEGORY_DEFAULT);

        //是否指定App打开
        if(component != null && component.size() == 2) {
            intent.setComponent(new ComponentName(component.getString(0), component.getString(1)));
        }

        Activity currentActivity = getCurrentActivity();
        if (currentActivity != null) {
            currentActivity.startActivity(chooser);
        } else {
            getReactApplicationContext().startActivity(chooser);
        }
        callback.invoke(false);
    }

    @ReactMethod
    public void downloadImage(ReadableArray imagesUrl, Promise promise){
        List<String> paths = new ArrayList<String>();
        for(int i=0; i<imagesUrl.size();i++){
            String imageUrl = imagesUrl.getString(i);
            String fileName = i + ".jpg";
            String path = this.download(imageUrl,fileName);
            paths.add(path);
        }

        JSONArray array = new JSONArray(paths);
        promise.resolve(array.toString());
    }

    //下载图片并保存
    public String download( String imageUrl, String fileName) {
        final String savePath = getReactApplicationContext().getExternalCacheDir() + "/" + fileName;
        try {
            File f = new File(savePath);
            File dir = f.getParentFile();
            if(!dir.exists())
                dir.mkdirs();
            FileOutputStream os = new FileOutputStream(f);
            byte[] data = getImage(imageUrl);
            os.write(data);
            os.close();
        } catch (Exception e) {
            e.getLocalizedMessage();
        }

        return "file://" + savePath;
    }

    /*
    * 获取网络图片
    */
    public byte[] getImage(String imageUrl) throws Exception{
        URL url = new URL(imageUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("GET");
        InputStream inStream = conn.getInputStream();
        if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
            return readStream(inStream);
        }
        return null;
    }


    /*
    获取图片流数据
     */
    public static byte[] readStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while( (len=inStream.read(buffer)) != -1){
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        inStream.close();
        return outStream.toByteArray();
    }
}
