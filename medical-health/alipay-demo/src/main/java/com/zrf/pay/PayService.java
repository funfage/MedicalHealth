package com.zrf.pay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.CertAlipayRequest;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 张润发
 * 支付服务类
 * @date 2021/3/8
 */
public class PayService {

    private static Log log = LogFactory.getLog("trade_pay");

    // 商户操作员编号，添加此参数可以为商户操作员做销售统计
    private static String operatorId = "test_operator_id";

    // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
    private static String storeId = "test_store_id";

    // 支付超时，定义为120分钟
    private static String timeoutExpress = "120m";

    static Map<String, Object> pay(String outTradeNo,
                                   String subject,
                                   String totalAmount,
                                   String undiscountableAmount,
                                   String body,
                                   String notifyUrl) {
        Map<String, Object> res = new HashMap<>();
        try {
            //构造client
            CertAlipayRequest certAlipayRequest = new CertAlipayRequest();
            //设置网关地址
            certAlipayRequest.setServerUrl("https://" + PayConfig.gatewayHost + "/gateway.do");
            //设置应用Id
            certAlipayRequest.setAppId(PayConfig.appId);
            //设置应用私钥
            certAlipayRequest.setPrivateKey(PayConfig.merchantPrivateKey);
            //设置请求格式，固定值json
            certAlipayRequest.setFormat("json");
            //设置字符集
            certAlipayRequest.setCharset(PayConfig.charset);
            //设置签名类型
            certAlipayRequest.setSignType(PayConfig.signType);
            //设置应用公钥证书路径
            certAlipayRequest.setCertPath("F:\\java_source\\alipay\\" + PayConfig.merchantCertPath);
            //设置支付宝公钥证书路径
            certAlipayRequest.setAlipayPublicCertPath("F:\\java_source\\alipay\\" + PayConfig.alipayCertPath);
            //设置支付宝根证书路径
            certAlipayRequest.setRootCertPath("F:\\java_source\\alipay\\" + PayConfig.alipayRootCertPath);


            //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
            AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();
            model.setBody(body);
            model.setSubject(subject);
            model.setOutTradeNo(outTradeNo);
            model.setTotalAmount(totalAmount);
            model.setUndiscountableAmount(undiscountableAmount);
            //构造client
            AlipayClient alipayClient = new DefaultAlipayClient(certAlipayRequest);
            //构造API请求
            AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
            //发送请求
            request.setBizModel(model);
            request.setNotifyUrl(notifyUrl);
            AlipayTradePrecreateResponse response = alipayClient.certificateExecute(request);
            if (response.isSuccess()) {
                System.out.println("支付宝交易预创建调用成功");
                res.put("code", response.getCode());
                res.put("msg", response.getMsg());
                String qrCode = response.getQrCode();
                System.out.println(qrCode);
                res.put("qrCode", qrCode);
            }
        } catch (AlipayApiException e) {
            System.out.println("交易预创建出现异常");
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 获取项目根路径
     *
     * @return
     */
    private static String getResourceBasePath() throws UnsupportedEncodingException {
        // 获取跟目录
        File path = null;
        try {
            path = new File(ResourceUtils.getURL("classpath:").getPath());
        } catch (FileNotFoundException e) {
            // nothing to do
        }
        if (path == null || !path.exists()) {
            path = new File("");
        }
        String pathStr = path.getAbsolutePath();
        // 如果是在eclipse中运行，则和target同级目录,如果是jar部署到服务器，则默认和jar包同级
        pathStr = pathStr.replace("\\target\\classes", "");
        return URLDecoder.decode(pathStr, "UTF-8");
    }

    public static Map<String, Object> pay2(String outTradeNo,
                                           String subject,
                                           String totalAmount,
                                           String undiscountableAmount,
                                           String body,
                                           String notifyUrl) {
//        Factory.setOptions(PayConfig.getOptions());
        Map<String, Object> res = new HashMap<>();
        /*try {
            // 2. 发起API调用（以创建当面付收款二维码为例）
            AlipayTradePrecreateResponse response = Factory.Payment.FaceToFace()
                    // 调用optional扩展方法，完成可选业务参数（biz_content下的可选字段）的设置
                    .optional("body", body)
                    .optional("undiscountable_amount", undiscountableAmount)
                    .asyncNotify(notifyUrl)
                    .preCreate(subject, outTradeNo, totalAmount);
            // 3. 处理响应或异常
            if (ResponseChecker.success(response)) {
                String qrCode = response.getQrCode();
                res.put("qrCode", qrCode);
                System.out.println("调用成功");
                System.out.println(qrCode);
            } else {
                System.err.println("调用失败，原因：" + response.msg + "，" + response.subMsg);
            }
            res.put("code", response.getCode());
            res.put("msg", response.getMsg());
        } catch (Exception e) {
            System.err.println("调用遭遇异常，原因：" + e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }*/
        return res;
    }


}
