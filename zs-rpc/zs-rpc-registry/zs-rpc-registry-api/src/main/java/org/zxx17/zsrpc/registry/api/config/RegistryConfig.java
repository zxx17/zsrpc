package org.zxx17.zsrpc.registry.api.config;

import java.io.Serial;
import java.io.Serializable;

/**
 * 注册配置.
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/7/8
 **/
public class RegistryConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 8428044439627895713L;

    /**
     * 注册地址
     */
    private String registryAddr;

    /**
     * 注册类型
     */
    private String registryType;

    public RegistryConfig(String registryAddr, String registryType) {
        this.registryAddr = registryAddr;
        this.registryType = registryType;
    }

    public String getRegistryAddr() {
        return registryAddr;
    }

    public void setRegistryAddr(String registryAddr) {
        this.registryAddr = registryAddr;
    }

    public String getRegistryType() {
        return registryType;
    }

    public void setRegistryType(String registryType) {
        this.registryType = registryType;
    }
}
