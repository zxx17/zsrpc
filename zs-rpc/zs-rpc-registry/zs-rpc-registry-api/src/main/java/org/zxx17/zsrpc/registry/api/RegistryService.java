package org.zxx17.zsrpc.registry.api;

import org.zxx17.zsrpc.protocol.meta.ServiceMeta;
import org.zxx17.zsrpc.registry.api.config.RegistryConfig;

import java.io.IOException;

/**
 * 服务注册与发现顶层接口.
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/7/8
 **/
public interface RegistryService {

    /**
     * 服务注册
     *
     * @param serviceMeta 服务元数据
     * @throws Exception 抛出异常
     */
    void register(ServiceMeta serviceMeta) throws Exception;

    /**
     * 服务取消注册
     *
     * @param serviceMeta 服务元数据
     * @throws Exception 抛出异常
     */
    void unRegister(ServiceMeta serviceMeta) throws Exception;

    /**
     * 服务发现
     *
     * @param serviceName     服务名称
     * @param invokerHashCode HashCode值
     * @return 服务元数据
     * @throws Exception 抛出异常
     */
    ServiceMeta discovery(String serviceName, int invokerHashCode) throws Exception;

    /**
     * 服务销毁
     *
     * @throws IOException 抛出异常
     */
    void destroy() throws IOException;

    /**
     * 默认初始化方法
     */
    default void init(RegistryConfig registryConfig) throws Exception {
    }

}
