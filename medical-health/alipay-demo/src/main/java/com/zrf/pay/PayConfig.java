package com.zrf.pay;

import com.alipay.easysdk.kernel.Config;

/**
 * @author 张润发
 * @date 2021/3/10
 */
public class PayConfig {

    public static String appId = "2021000117619455";

    public static String signType = "RSA2";

    public static String charset = "UTF-8";

    public static String protocol = "https";

    public static String gatewayHost = "openapi.alipaydev.com";

    public static String merchantPrivateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCmcuCm7Acxgf5YhLWx3ga3RL5tWhQVu01PEsZ6ho8Ghq4DsVH/Y9yAXsY4wGBTQTxu75H9/irbsadKvTCU+HcUW0g2bU3ZantsbN7JtiiUh6s40e58MyCHq/Ojs3KEWVH4/CPhMPMHJnn1DPxsARlUJ+8eK5FMmR3Kxw/luklB7lla38Ms+tSb7PMpyLAhgnK6yz9BHhoOdDk/hf/IrxinIpd9oVdrOPK1HhE+b+c1me9BDcHO++Dabx5l/322nDYWXkRfX2N3rdAEI2j30mEx0f5PyRmbgmpVOlqh2XqmBAiH0cUWvuPzysebxctgEHmYYvayghvUDMX9u5B3FxPrAgMBAAECggEAEa3oALLXPMHy8xH9fov3qKWrmE/WDEzdtEVpQYBjVq0ddJT0Aa1osVXftg1w7cbjFWFq31CM2tLu0vhvXZNaipX7gJRxjWNLAsQ5kbFAdC5I2Hsr2pNPpDl2Ym/f/QDDvS+C3Btn5N2IXKfpc8tWFmM7ebfk8f7ddDlajesQdukfJpu6DvXVGZEP1d4rtVfHBTROYwQlXTwfvO60AxXSfiSPjPfOcdpzB5EhSqLjWeqRt9VGwGhuYDS5HVEZAOeCPun9WZ6xsHCaf1cYeIPNvC4JjHektWe9LD3L4YM4+ZS9/HVXqyakHEtBOVr/K/4X8k4GybNBFLQWBrljFbFZ2QKBgQDfIxYyPk+Z87MW8wDj6EyVUSfVRCw81anbBlFf+L0GQRBI5SPGwEPCrxjeihS9TPpoCLGIujfhlfwzepS/5LbMobwaVmii5EnqiACukiyV5jxoMtiOXk3PtnHSU6qe+MPbQYpYMaLk673m6ODrm5GN7suv/Q0cmT+gnZh4EvLRvwKBgQC+9nokO6VOUVq5NJxFKXPIaf58u20yrQ8tdLru9nTdL0XXiABmbKAzqe0wNlqTY+/l3hyBP3TJ9H5X4unwYyTPDZPZNwsBL9Nw+sWB0AgNIB+B8OlK4PQ5YmXR/mlQhbn+9zwOi6hvdYUeBO8UGcUzzY8lLcmgmxNcqEbsJXVw1QKBgQCiCDKIDaN0Ifk8gv10gi2KiEIqpbSSAH21ZKMW0+M/dWPD61DNdLQ/3TJr900UIZ/8h8OTi9YkY+ud3jfCGO19Lz0Oywx0rN3YByanndHukB/xcmq7B1Ptr4h7uPG/OtnOj4tMe87T+TQPR7B8pp1NucgDZfhtegMwOQCMwQW+tQKBgES/o5Vry8V4UHsgz6mulLpwPmhVaai22SH6RlpFbCk9HZ3MnqhBLyOqZ3PV/aHWdsWAwy5c1ATo+ptVW/dpGhVYxAONZ27T8wFXpsdnR57CE9XKD5MfjY0jOZjySKPuoNmD1KE/8jG+U2BNUm0gIfjWZlRyOYaSjdPr2SynB7LJAoGAXksbCPwxuXpbsxEdQtPaDnoyBU+VidhtnflA10X6l7Am/8wwd2Yd8I21yd8Nxi6I2MY1wGxJG3CWaATtci9vdK9gC2BF15t57X23vUhlGUEBXDJ+igatgEwSvIpGLTXyoHJe5nw9Oo+ddJVabfwHbi1p9dRn7sh85q2iTVtPVxU=";

    public static String alipayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkr66BpiRxDNlktB9McFaBz0I5G9ixOBEEEkXkzuGv0JvwVvuAYLWOMuj6zGy48phaMLd9wOiFHLijAe9L305tGdweeo2eJxuDqNrxPgjk5dgr/a3f/1E2E/EsmMXZ12+zmqPLZKb6f7q2pn8mp935v777S48SnuI8GCsoUXGxnI2uM/uoEEgfMleJW7+YSPJvSI2SCfh2E6+QiPxNumnaFd3PcOJ5kX7XQxMWpUZvQ/2HVuQMZ6Gv6dHSGco6KVo4lKG0K/oVTxBQHaLSEHj5Aza8DfxMwjfeptydcTQxDiAhX9DUc6Y0gWzdneToIZFsGY1l52ITsvMRqAvuGaBZQIDAQAB";

    public static String merchantCertPath = "cert/appCertPublicKey_2021000117619455.crt";

    public static String alipayCertPath = "cert/alipayCertPublicKey_RSA2.crt";

    public static String alipayRootCertPath = "cert/alipayRootCert.crt";

    public static Config getOptions() {
        Config config = new Config();
        config.protocol = protocol;
        config.gatewayHost = gatewayHost;
        config.signType = signType;
        config.appId = appId;
        // 为避免私钥随源码泄露，推荐从文件中读取私钥字符串而不是写入源码中
        config.merchantPrivateKey = merchantPrivateKey;
        //注：证书文件路径支持设置为文件系统中的路径或CLASS_PATH中的路径，优先从文件系统中加载，加载失败后会继续尝试从CLASS_PATH中加载
        config.merchantCertPath = merchantCertPath;
        config.alipayCertPath = alipayCertPath;
        config.alipayRootCertPath = alipayRootCertPath;
        //可设置异步通知接收服务地址（可选）
//        config.notifyUrl = "http://38k07q0530.wicp.vip/pay/callback";
        return config;
    }

}
