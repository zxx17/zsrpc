package org.zxx17.zsrpc.common.helper;

/**
 * 将传入的服务名称、服务版本号，服务分组通过#符号进行连接，并返回连接后的字符串.
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/7/3
 **/
public class RpcServiceHelper {

    /**
     * 拼接字符串
     * @param serviceName 服务名称
     * @param serviceVersion 服务版本号
     * @param group 服务分组
     * @return 服务名称#服务版本号#服务分组
     */
    public static String buildServiceKey(String serviceName, String serviceVersion, String group) {
        return String.join("#", serviceName, serviceVersion, group);
    }

}
