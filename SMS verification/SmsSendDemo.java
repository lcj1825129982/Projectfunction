package com.qhit.SMSverification;

import com.qhit.SMSverification.Utils.HttpClient4Utils;
import com.qhit.SMSverification.Utils.SignatureUtils;
import org.apache.http.Consts;
import org.apache.http.client.HttpClient;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by lenovo on 2019/02/14.
 */


public class SmsSendDemo {
    /**
     * 业务ID，易盾根据产品业务特点分配
     */
    private final static String BUSINESSID = "931b94a7bb8341588a254a0a4da76962";
    /**
     * 产品密钥ID，产品标识
     */
    private final static String SECRETID = "0d69ff70ac8c62a8ea6ed41956d86c8a";
    /**
     * 产品私有密钥，服务端生成签名信息使用，请严格保管，避免泄露
     */
    private final static String SECRETKEY = "1cda6f7c83a8fe8c55020b32aff42cb5";

    /**
     * 接口地址
     */
    private final static String API_URL = "https://sms.dun.163yun.com/v2/sendsms";
    /**
     * 实例化HttpClient，发送http请求使用，可根据需要自行调参
     */
    private static HttpClient httpClient = HttpClient4Utils.createHttpClient(100, 20, 10000, 2000, 2000);

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        //此处用申请通过的模板id
        String templateId = "14795095";
        //模板参数对应,例如模板为您的验证码为${p1},请于${p2}时间登陆到我们的服务器
        String params = "p1=xx&p2=xx";
        String mobile = "17677401242";
        Map<String, String> datas = buildParam(mobile, templateId, params);
        String result = HttpClient4Utils.sendPost(httpClient, API_URL, datas, Consts.UTF_8);
        System.out.println("result = [" + result + "]");
    }

    private static Map<String, String> buildParam(String mobile, String templateId, String params) throws IOException {
        Map map = new HashMap<String, String>();
        map.put("businessId", BUSINESSID);
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("version", "v2");
        map.put("needUp", "false");
        map.put("templateId", templateId);
        map.put("mobile", mobile);
        //国际短信
        map.put("internationalCode", "对应的国家编码");
        map.put("nonce", UUID.randomUUID().toString().replace("-", ""));
        map.put("secretId", SECRETID);
        String sign = SignatureUtils.genSignature(SECRETKEY, map);
        map.put("signature", sign);
        return map;
    }
}
