package org.zxx17.zsrpc.registry.zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.zxx17.zsrpc.common.helper.RpcServiceHelper;
import org.zxx17.zsrpc.loadbalancer.api.ServiceLoadBalance;
import org.zxx17.zsrpc.loadbalancer.random.RandomServiceLoadBalancer;
import org.zxx17.zsrpc.protocol.meta.ServiceMeta;
import org.zxx17.zsrpc.registry.api.RegistryService;
import org.zxx17.zsrpc.registry.api.config.RegistryConfig;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Zookeeper注册中心服务实现类，用于服务的注册、发现和注销。.
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/7/8
 **/
public class ZookeeperRegistryService implements RegistryService {

    // 定义重试策略的基础睡眠时间（毫秒）
    public static final int BASE_SLEEP_TIME_MS = 1000;
    // 定义最大重试次数
    public static final int MAX_RETRIES = 3;
    // Zookeeper的根路径
    public static final String ZK_BASE_PATH = "/zs_rpc";

    // 服务发现对象，用于与Zookeeper交互
    private ServiceDiscovery<ServiceMeta> serviceDiscovery;

    // 负载均衡策略
    private ServiceLoadBalance<ServiceInstance<ServiceMeta>> serviceLoadBalance;

    /**
     * 初始化方法，创建并启动Curator客户端，构建服务发现器。
     * @param registryConfig 注册中心配置
     * @throws Exception 异常抛出
     */
    @Override
    public void init(RegistryConfig registryConfig) throws Exception {
        this.serviceLoadBalance = new RandomServiceLoadBalancer<>();
        CuratorFramework client = CuratorFrameworkFactory.newClient(
                registryConfig.getRegistryAddr(),
                new ExponentialBackoffRetry(BASE_SLEEP_TIME_MS, MAX_RETRIES));
        client.start();
        JsonInstanceSerializer<ServiceMeta> serializer = new JsonInstanceSerializer<>(ServiceMeta.class);
        this.serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceMeta.class)
                .basePath(ZK_BASE_PATH)
                .client(client)
                .serializer(serializer)
                .build();
        this.serviceDiscovery.start();
    }

    /**
     * 注册服务到Zookeeper。
     * @param serviceMeta 服务元数据
     * @throws Exception 异常抛出
     */
    @Override
    public void register(ServiceMeta serviceMeta) throws Exception {
        ServiceInstance<ServiceMeta> serviceInstance = ServiceInstance
                .<ServiceMeta>builder()
                .name(RpcServiceHelper.buildServiceKey(serviceMeta.getServiceName(), serviceMeta.getServiceVersion(), serviceMeta.getServiceGroup()))
                .address(serviceMeta.getServiceAddr())
                .port(serviceMeta.getServicePort())
                .payload(serviceMeta)
                .build();
        serviceDiscovery.registerService(serviceInstance);
    }

    /**
     * 从Zookeeper中注销服务。
     * @param serviceMeta 服务元数据
     * @throws Exception 异常抛出
     */
    @Override
    public void unRegister(ServiceMeta serviceMeta) throws Exception {
        ServiceInstance<ServiceMeta> serviceInstance = ServiceInstance
                .<ServiceMeta>builder()
                .name(serviceMeta.getServiceName())
                .address(serviceMeta.getServiceAddr())
                .port(serviceMeta.getServicePort())
                .payload(serviceMeta)
                .build();
        serviceDiscovery.unregisterService(serviceInstance);
    }

    /**
     * 从Zookeeper中发现服务。
     * @param serviceName 服务名称
     * @param invokerHashCode 调用者哈希码，此处未使用
     * @return 返回服务元数据
     * @throws Exception 异常抛出
     */
    @Override
    public ServiceMeta discovery(String serviceName, int invokerHashCode) throws Exception {
        Collection<ServiceInstance<ServiceMeta>> serviceInstances = serviceDiscovery.queryForInstances(serviceName);
        ServiceInstance<ServiceMeta> instance = serviceLoadBalance.select((List<ServiceInstance<ServiceMeta>>) serviceInstances, invokerHashCode);
        if (instance != null) {
            return instance.getPayload();
        }
        return null;
    }

    /**
     * 销毁服务发现器，关闭资源。
     * @throws IOException IO异常抛出
     */
    @Override
    public void destroy() throws IOException {
        serviceDiscovery.close();
    }


}

