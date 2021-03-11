package com.zrf.config.pay;

/**
 * @author 张润发
 * @date 2021/3/10
 */
public class AliPayConfig {

    /**
     * 应用的ID，申请时的APPID
     */
    public static String appId = "2021000117620129";

    /**
     * SHA256withRsa对应支付宝公钥
     */
    public static String alipayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkr66BpiRxDNlktB9McFaBz0I5G9ixOBEEEkXkzuGv0JvwVvuAYLWOMuj6zGy48phaMLd9wOiFHLijAe9L305tGdweeo2eJxuDqNrxPgjk5dgr/a3f/1E2E/EsmMXZ12+zmqPLZKb6f7q2pn8mp935v777S48SnuI8GCsoUXGxnI2uM/uoEEgfMleJW7+YSPJvSI2SCfh2E6+QiPxNumnaFd3PcOJ5kX7XQxMWpUZvQ/2HVuQMZ6Gv6dHSGco6KVo4lKG0K/oVTxBQHaLSEHj5Aza8DfxMwjfeptydcTQxDiAhX9DUc6Y0gWzdneToIZFsGY1l52ITsvMRqAvuGaBZQIDAQAB";

    /**
     * 签名方式
     */
    public static String signType = "RSA2";

    /**
     * 字码编码格式
     */
    public static String charset = "utf-8";

    /**
     * 回调地址
     */
    public static String notifyUrl="http://38k07q0530.wicp.vip/pay/callback/";

}
