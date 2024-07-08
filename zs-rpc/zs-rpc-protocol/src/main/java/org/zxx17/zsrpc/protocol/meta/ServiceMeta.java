package org.zxx17.zsrpc.protocol.meta;

import java.io.Serial;
import java.io.Serializable;

/**
 * 服务元数据.
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/7/8
 **/
public class ServiceMeta implements Serializable {

    @Serial
    private static final long serialVersionUID = 1055598368254592238L;

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 服务版本
     */
    private String serviceVersion;

    /**
     * 服务地址
     */
    private String serviceAddr;


    /**
     * 服务端口
     */
    private Integer servicePort;


    /**
     * 服务分组
     */
    private String serviceGroup;


    public ServiceMeta() {
    }

    public ServiceMeta(String serviceName, String serviceVersion,
                       String serviceAddr, Integer servicePort, String serviceGroup) {
        this.serviceName = serviceName;
        this.serviceVersion = serviceVersion;
        this.serviceAddr = serviceAddr;
        this.servicePort = servicePort;
        this.serviceGroup = serviceGroup;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceVersion() {
        return serviceVersion;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    public String getServiceAddr() {
        return serviceAddr;
    }

    public void setServiceAddr(String serviceAddr) {
        this.serviceAddr = serviceAddr;
    }

    public Integer getServicePort() {
        return servicePort;
    }

    public void setServicePort(Integer servicePort) {
        this.servicePort = servicePort;
    }

    public String getServiceGroup() {
        return serviceGroup;
    }

    public void setServiceGroup(String serviceGroup) {
        this.serviceGroup = serviceGroup;
    }
}
