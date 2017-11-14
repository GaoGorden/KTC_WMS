/**
 *
 */
package com.WMSLib.utils;

import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class WebServiceUtil {
    // 定义Web Service的命名空间
    static final String SERVICE_NS = "http://tempuri.org/";
    // 定义Web Service提供服务的URL
    //static final String SERVICE_URL =
    //	"http://192.168.0.180/abb.svr/Service.asmx";

    public static String GetData(String methodName, String SERVICE_URL) {
        // 调用的方法
        //final String methodName = "HelloWorld";
        //final String mName = methodName;
        // 创建HttpTransportSE传输对象
        final HttpTransportSE ht = new HttpTransportSE(SERVICE_URL);
        ht.debug = true;
        // 使用SOAP1.1协议创建Envelop对象
        final SoapSerializationEnvelope envelope =
                new SoapSerializationEnvelope(SoapEnvelope.VER12);
        // 实例化SoapObject对象
        SoapObject soapObject = new SoapObject(SERVICE_NS, methodName);
        envelope.bodyOut = soapObject;
        // 设置与.Net提供的Web Service保持较好的兼容性
        envelope.dotNet = true;

        FutureTask<String> task = new FutureTask<String>(
                new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        // 调用Web Service
                        ht.call(null, envelope);
                        if (envelope.getResponse() != null) {
                            // 获取服务器响应返回的SOAP消息
                            SoapObject result = (SoapObject) envelope.bodyIn;

                            //SoapObject detail = (SoapObject)result.getProperty(mName+"Result");
                            // detail.getPropertyCount().toString()
                            //int j = detail.getPropertyCount();
                            // 解析服务器响应的SOAP消息。
                            return result.toString();


                        }
                        return null;
                    }

                });
        new Thread(task).start();
        try {
            return task.get();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }

    //  methodName :方法
    //  SERVICE_URL : 地址
    //  Map<String, String> values = new HashMap<String, String>();
    //  values.put("msg", "这是Android手机发出的信息");
    //  GetData("getdata",velues);
    public static String GetData(String methodName, String SERVICE_URL, Map<String, String> Params) {
        // 调用的方法
        //final String methodName = "HelloWorld";
        //final String mName = methodName;
        // 创建HttpTransportSE传输对象
        final HttpTransportSE ht = new HttpTransportSE(SERVICE_URL);
        ht.debug = true;
        // 使用SOAP1.1协议创建Envelop对象
        final SoapSerializationEnvelope envelope =
                new SoapSerializationEnvelope(SoapEnvelope.VER12);
        // 实例化SoapObject对象
        SoapObject soapObject = new SoapObject(SERVICE_NS, methodName);
        envelope.bodyOut = soapObject;
        // 设置与.Net提供的Web Service保持较好的兼容性
        envelope.dotNet = true;
        // 2、设置调用方法的参数值，如果没有参数，可以省略，
        if (Params != null) {
            Iterator iter = Params.entrySet().iterator();
            int j = 0;
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                soapObject.addProperty((String) entry.getKey(),
                        (String) entry.getValue());
                j++;
            }
            if (j < 15) {
                for (int i = j; i < 15; i++) {
                    soapObject.addProperty("param" + String.valueOf(i + 1),
                            "");
                }
            }
        }

        FutureTask<String> task = new FutureTask<String>(
                new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        // 调用Web Service
                        ht.call(null, envelope);
                        if (envelope.getResponse() != null) {
                            // 获取服务器响应返回的SOAP消息
                            SoapObject result = (SoapObject) envelope.bodyIn;

                            //SoapObject detail = (SoapObject)result.getProperty(mName+"Result");
                            // detail.getPropertyCount().toString()
                            //int j = detail.getPropertyCount();
                            // 解析服务器响应的SOAP消息。
                            return TrimStr(result.toString());


                        }
                        return null;
                    }

                });
        new Thread(task).start();
        try {
            return task.get();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }


    /**
     * Description:
     * <br/>
     * <br/>
     * <br/>
     * <br/>
     * <br/>
     *
     * @author ROCK.ZHOU UFOROCK@163.com
     * @version 1.0
     */
    public static String TrimStr(String nvalue) {
        int i = nvalue.indexOf("=");
        int j = nvalue.lastIndexOf("}");
        if (j > i && i >= 0) {
            nvalue = nvalue.substring(i + 1, j - 2);

        }
        return nvalue;

    }


    /**
     * Description:
     * <br/>
     * <br/>
     * <br/>
     * <br/>
     * <br/>
     *
     * @author ROCK.ZHOU UFOROCK@163.com
     * @version 1.0
     */

    //下面暂时没用

    // 调用远程Web Service获取省份列表
    public static List<String> getProvinceList(String methodName, String SERVICE_URL) {
        // 调用的方法
        //final String methodName = "getRegionProvince";
        //final String methodName="HelloWorld";
        // 创建HttpTransportSE传输对象
        final HttpTransportSE ht = new HttpTransportSE(SERVICE_URL);
        ht.debug = true;
        // 使用SOAP1.1协议创建Envelop对象
        final SoapSerializationEnvelope envelope =
                new SoapSerializationEnvelope(SoapEnvelope.VER12);
        // 实例化SoapObject对象
        SoapObject soapObject = new SoapObject(SERVICE_NS, methodName);
        envelope.bodyOut = soapObject;
        // 设置与.Net提供的Web Service保持较好的兼容性
        envelope.dotNet = true;

        //设置参数


        FutureTask<List<String>> task = new FutureTask<List<String>>(
                new Callable<List<String>>() {
                    @Override
                    public List<String> call()
                            throws Exception {
                        // 调用Web Service
                        ht.call(null, envelope);
                        if (envelope.getResponse() != null) {
                            // 获取服务器响应返回的SOAP消息
                            SoapObject result = (SoapObject) envelope.bodyIn;
                            SoapObject detail = (SoapObject) result.getProperty(
                                    "Result");
//					SoapObject detail = (SoapObject) result.getProperty(
//						methodName + "Result");
                            // 解析服务器响应的SOAP消息。
                            return parseProvinceOrCity(detail);
                        }
                        return null;
                    }
                });
        new Thread(task).start();
        try {
            return task.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 根据省份获取城市列表
    public static List<String> getCityListByProvince(String province, String methodName, String SERVICE_URL) {
        // 调用的方法
        //final String methodName = "getSupportCityString";
        // 创建HttpTransportSE传输对象
        final HttpTransportSE ht = new HttpTransportSE(SERVICE_URL);
        ht.debug = true;
        // 实例化SoapObject对象
        SoapObject soapObject = new SoapObject(SERVICE_NS, methodName);
        // 添加一个请求参数
        soapObject.addProperty("theRegionCode", province);
        // 使用SOAP1.1协议创建Envelop对象
        final SoapSerializationEnvelope envelope =
                new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.bodyOut = soapObject;
        // 设置与.Net提供的Web Service保持较好的兼容性
        envelope.dotNet = true;
        FutureTask<List<String>> task = new FutureTask<List<String>>(
                new Callable<List<String>>() {
                    @Override
                    public List<String> call()
                            throws Exception {
                        // 调用Web Service
                        ht.call(null, envelope);
                        if (envelope.getResponse() != null) {
                            // 获取服务器响应返回的SOAP消息
                            SoapObject result = (SoapObject) envelope.bodyIn;
                            SoapObject detail = (SoapObject) result.getProperty(
                                    "Result");
                            // 解析服务器响应的SOAP消息。
                            return parseProvinceOrCity(detail);
                        }
                        return null;
                    }
                });
        new Thread(task).start();
        try {
            return task.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<String> parseProvinceOrCity(SoapObject detail) {
        ArrayList<String> result = new ArrayList<String>();
        for (int i = 0; i < detail.getPropertyCount(); i++) {
            // 解析出每个省份
            result.add(detail.getProperty(i).toString().split(",")[0]);
        }
        return result;
    }

    public static SoapObject getWeatherByCity(String cityName, String methodName, String SERVICE_URL) {
        //final String methodName = "getWeather";
        final HttpTransportSE ht = new HttpTransportSE(SERVICE_URL);
        ht.debug = true;
        final SoapSerializationEnvelope envelope =
                new SoapSerializationEnvelope(SoapEnvelope.VER11);
        SoapObject soapObject = new SoapObject(SERVICE_NS, methodName);
        soapObject.addProperty("theCityCode", cityName);
        envelope.bodyOut = soapObject;
        // 设置与.Net提供的Web Service保持较好的兼容性
        envelope.dotNet = true;
        FutureTask<SoapObject> task = new FutureTask<SoapObject>(
                new Callable<SoapObject>() {
                    @Override
                    public SoapObject call()
                            throws Exception {
                        ht.call(null, envelope);
                        SoapObject result = (SoapObject) envelope.bodyIn;
                        SoapObject detail = (SoapObject) result.getProperty(
                                "Result");
                        return detail;
                    }
                });
        new Thread(task).start();
        try {
            return task.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 调用WebService
     *
     * @return WebService的返回值
     */
    private static String CallWebService(String MethodName, Map<String, String> Params) {
        // 1、指定webservice的命名空间和调用的方法名

        SoapObject request = new SoapObject(SERVICE_NS, MethodName);
        // 2、设置调用方法的参数值，如果没有参数，可以省略，
        if (Params != null) {
            Iterator iter = Params.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                request.addProperty((String) entry.getKey(),
                        (String) entry.getValue());
            }
        }
        // 3、生成调用Webservice方法的SOAP请求信息。该信息由SoapSerializationEnvelope对象描述
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER12);
        envelope.bodyOut = request;
        // c#写的应用程序必须加上这句
        envelope.dotNet = true;
        HttpTransportSE ht = new HttpTransportSE("WEB_SERVICE_URL");
        // 使用call方法调用WebService方法
        try {
            ht.call(null, envelope);
        } catch (HttpResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        try {
            final SoapPrimitive result = (SoapPrimitive) envelope.getResponse();
            if (result != null) {
                Log.d("----收到的回复----", result.toString());
                return result.toString();
            }

        } catch (SoapFault e) {
            Log.e("----发生错误---", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 执行异步任务
     *
     * @param params 方法名+参数列表（哈希表形式）
     */
    public static String Request(Object... params) {
        new AsyncTask<Object, Object, String>() {

            @Override
            protected String doInBackground(Object... params) {
                if (params != null && params.length == 2) {
                    return CallWebService((String) params[0],
                            (Map<String, String>) params[1]);
                } else if (params != null && params.length == 1) {
                    return CallWebService((String) params[0], null);
                } else {
                    return null;
                }
            }

            protected void onPostExecute(String result) {
                if (result != null) {
                    //tvMessage.setText("服务器回复的信息 : " + result);
                    //return result;
                }
            }

            ;

        }.execute(params);
        return null;
    }


}
