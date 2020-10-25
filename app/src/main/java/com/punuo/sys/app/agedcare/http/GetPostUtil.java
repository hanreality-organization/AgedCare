package com.punuo.sys.app.agedcare.http;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

/**
 * Created by 林逸磊 on 2017/9/26.
 */

public class GetPostUtil {
    public static String sendGet1111(String url, String params) {
        String result = "";

        String urlName = url + "?" + params;
        try {
            URL realUrl = new URL(urlName);
            HttpURLConnection conn =(HttpURLConnection) realUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0(compatible;MSIE 6.0;Windows NT 5.1;SV1)");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.connect();
            InputStream is = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            BufferedReader in = new BufferedReader(isr);
            String line;
            while ((line = in.readLine()) != null) {
                result = line+"\n";
            }
            in.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;

    }

    /**
     * @param uploadUrl      上传路径参数
     * @param uploadFilePath 文件路径
     * @category 上传文件至Server的方法
     * @author ylbf_dev
     */
    public static String uploadFile(String uploadUrl, String uploadFilePath, String id, String avatar) {
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "******";
        String result = "";
        try {
            URL url = new URL(uploadUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());
            dos.writeBytes(end + twoHyphens + boundary + end);
            dos.writeBytes("Content-Disposition: form-data; name=\"id\"" + end + end);
            dos.writeBytes(id + end);
            dos.writeBytes(twoHyphens + boundary + end);
            dos.writeBytes("Content-Disposition: form-data; name=\"avatar\"" + end + end);
            dos.writeBytes(avatar + end);
            dos.writeBytes(twoHyphens + boundary + end);
            dos.writeBytes("Content-Disposition: form-data; name=\"pic\"; filename=\""
                    + uploadFilePath.substring(uploadFilePath.lastIndexOf("/") + 1) + "\"" + end);
            dos.writeBytes("Content-Type: application/octet-stream" + end);
            dos.writeBytes("Content-Transfer-Encoding: binary" + end+end);
            // 文件通过输入流读到Java代码中-++++++++++++++++++++++++++++++`````````````````````````
            FileInputStream fis = new FileInputStream(uploadFilePath);
            byte[] buffer = new byte[8192]; // 8k
            int count = 0;
            while ((count = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, count);
            }
            fis.close();
            System.out.println("file send to server............");
            dos.writeBytes(end);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
            dos.flush();
            // 读取服务器返回结果
            InputStream is = httpURLConnection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line=br.readLine())!=null) {
                result = line+"\n";
            }
            dos.close();
            is.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param uploadUrl      上传路径参数
     * @param uploadFilePath 文件路径
     * @category 上传文件至Server的方法
     * @author ylbf_dev
     */

    public static String uploadFiletiezi(String uploadUrl, List<String> uploadFilePath, String id, String content) {
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "******";
        String result = "";
        try {
            URL url = new URL(uploadUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());


            dos.writeBytes(end + twoHyphens + boundary + end);
            dos.writeBytes("Content-Disposition: form-data; name=\"id\"" + end + end);
            dos.writeBytes(id);
            dos.writeBytes(end + twoHyphens + boundary + end);
            dos.writeBytes("Content-Disposition: form-data; name=\"content\"" + end + end);
            dos.write((content+ end).getBytes());
            for (int i = 0; i < uploadFilePath.size(); i++) {
                String uploadFile = uploadFilePath.get(i);
//                String filename = uploadFile.substring(uploadFile.lastIndexOf("//") + 1);
                dos.writeBytes(twoHyphens + boundary + end);
                dos.writeBytes("Content-Disposition: form-data; name=\"file[]\"; filename=\""
                        + uploadFile.substring(uploadFilePath.lastIndexOf("/") + 1) + "\"" + end);
                dos.writeBytes("Content-Type: application/octet-stream" + end);
                dos.writeBytes("Content-Transfer-Encoding: binary" + end);
                dos.writeBytes(end);
                FileInputStream fStream = new FileInputStream(uploadFile);
                int bufferSize = 1024;
                byte[] buffer = new byte[bufferSize];
                int length = -1;
                while ((length = fStream.read(buffer)) != -1) {
                    dos.write(buffer, 0, length);
                }
                dos.writeBytes(end);
              /* close streams */
                fStream.close();
            }
            dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
            dos.flush();
            Log.w("1111.....", "帖子上传中。。。。");
            // 读取服务器返回结果
            InputStream is = httpURLConnection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            result = br.readLine();
            dos.close();
            is.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public static String sendPost(String url1, String params) {
        String result="";
        try {
            //创建URL对象
            URL url = new URL(url1+"?"+params);
            //返回一个URLConnection对象，它表示到URL所引用的远程对象的连接
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            //在这里设置一些属性，详细见UrlConnection文档，HttpURLConnection是UrlConnection的子类
            //设置连接超时为5秒
            httpURLConnection.setConnectTimeout(5000);
            //设定请求方式(默认为get)
            httpURLConnection.setRequestMethod("POST");
            // 设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在
            // http正文内，因此需要设为true, 默认情况下是false;
            httpURLConnection.setDoOutput(true);
            // 设置是否从httpUrlConnection读入，默认情况下是true;
            httpURLConnection.setDoInput(true);
            // Post 请求不能使用缓存
            httpURLConnection.setUseCaches(false);


            //这边开始设置请求头
            // 设定传送的内容类型是可序列化的java对象(如果不设此项,在传送序列化对象时,当WEB服务默认的不是这种类型时可能抛java.io.EOFException)
            httpURLConnection.setRequestProperty("Content-type", "application/x-java-serialized-object");
            //方法setRequestProperty(String key, String value)设置一般请求属性。
            // 连接，从上述url.openConnection()至此的配置必须要在connect之前完成，
            httpURLConnection.connect();

            //这边设置请内容
            //getOutputStream()里默认就有connect（）了，可以不用写上面的连接
            //接下来我们设置post的请求参数，可以是JSON数据，也可以是普通的数据类型
            OutputStream outputStream = httpURLConnection.getOutputStream();
            /**
             * JSON数据的请求
             * outputStream.write(stringJson.getBytes(), 0, stringJson.getBytes().length);
             * outputStream.close();
             * **/

             //字符串数据的请求
             DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
             String content = "userid="+params;
             dataOutputStream.writeBytes(content);
             dataOutputStream.flush();
             dataOutputStream.close();

            //读取返回的数据
            //返回打开连接读取的输入流，输入流转化为StringBuffer类型，这一套流程要记住，常用
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String line = null;
            StringBuffer stringBuffer = new StringBuffer();
            while ((line = bufferedReader.readLine()) != null) {
                //转化为UTF-8的编码格式
                line = new String(line.getBytes("UTF-8"));
                stringBuffer.append(line);
            }
            result=stringBuffer.toString();
            Log.e("POST请求返回的数据", stringBuffer.toString());
            bufferedReader.close();
            httpURLConnection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
        }



}
