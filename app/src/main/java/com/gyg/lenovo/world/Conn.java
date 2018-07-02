package com.gyg.lenovo.world;

import android.text.TextUtils;
import android.util.Log;
import org.json.JSONObject;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 连接远程服务器，传入请求路径,和JSONObject，返回JSONObject
 * 如果出现异常，手动构建json格式错误
 */
public class Conn {
    public static JSONObject doJsonPost(String urlPath, JSONObject JsonObj) {
        String result = "";
        urlPath = "http://47.95.243.80:3000" + urlPath;
        JSONObject objRes = null;
        BufferedReader reader = null;
        String Json = "";
        if(JsonObj != null)
            Json = JsonObj.toString();
        try {
            //基础配置
            URL url = new URL(urlPath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type","application/json; charset=UTF-8");
            conn.setRequestProperty("accept","application/json");
            //发送不能为空
            byte[] writebytes = Json.getBytes();
            conn.setRequestProperty("Content-Length", String.valueOf(writebytes.length));
            try {
                OutputStream outwritestream = conn.getOutputStream();
                outwritestream.write(Json.getBytes());
                outwritestream.flush();
                outwritestream.close();
                Log.d("upload", "doJsonPost: conn" + conn.getResponseCode());
            }catch (Exception e){
                Log.d("upload", "error:" + e.toString());
                result = "{'code':500, 'msg':'服务器网络错误'}";
                //System.out.println("服务器网络错误----------");
            }
            if (conn.getResponseCode() == 200) {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                result = reader.readLine();
            }
            else{
                result = "{'code':500, 'msg':'未知错误1'}";
            }
        } catch (Exception e) {
            result = "{'code':500, 'msg':'未知错误2'}";
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //将接收的字符串转换成json格式
        try {
            objRes = new JSONObject(result);
        }
        catch (JSONException e){
            e.printStackTrace();
            try {
                objRes = new JSONObject("{'code':500, 'msg':'format error'}");
            }
            catch (JSONException e1){
                e1.printStackTrace();
            }
        }
        //返回json对象
        return objRes;
    }

    //重载
    public static JSONObject doJsonPost(String urlPath, JSONObject JsonObj, String type) {
        String result = "";
        urlPath = "http://47.95.243.80:3000" + urlPath;
        JSONObject objRes = null;
        BufferedReader reader = null;
        String Json = "";
        if(JsonObj != null)
            Json = JsonObj.toString();
        if((type !="POST")&&(type != "GET"))
            type = "POST";
        try {
            //基础配置
            URL url = new URL(urlPath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(type);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type","application/json; charset=UTF-8");
            conn.setRequestProperty("accept","application/json");
            //发送不能为空
            byte[] writebytes = Json.getBytes();
            conn.setRequestProperty("Content-Length", String.valueOf(writebytes.length));
            try {
                OutputStream outwritestream = conn.getOutputStream();
                outwritestream.write(Json.getBytes());
                outwritestream.flush();
                outwritestream.close();
                Log.d("upload", "doJsonPost: conn" + conn.getResponseCode());
            }catch (Exception e){
                Log.d("upload", "error:" + e.toString());
                result = "{'code':500, 'msg':'服务器网络错误'}";
                //System.out.println("服务器网络错误----------");
            }
            if (conn.getResponseCode() == 200) {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                result = reader.readLine();
            }
            else{
                result = "{'code':500, 'msg':'未知错误1'}";
            }
        } catch (Exception e) {
            result = "{'code':500, 'msg':'未知错误2'}";
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //将接收的字符串转换成json格式
        try {
            objRes = new JSONObject(result);
        }
        catch (JSONException e){
            e.printStackTrace();
            try {
                objRes = new JSONObject("{'code':500, 'msg':'format error'}");
            }
            catch (JSONException e1){
                e1.printStackTrace();
            }
        }
        //返回json对象
        return objRes;
    }
}
