package org.zxx17.zsrpc.registry.zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.zxx17.zsrpc.common.helper.RpcServiceHelper;
import org.zxx17.zsrpc.protocol.meta.ServiceMeta;
import org.zxx17.zsrpc.registry.api.RegistryService;
import org.zxx17.zsrpc.registry.api.config.RegistryConfig;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * .
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/7/8
 **/
public class ZookeeperRegistryService implements RegistryService {

    public static final int BASE_SLEEP_TIME_MS = 1000;
    public static final int MAX_RETRIES = 3;
    public static final String ZK_BASE_PATH = "/zs_rpc";

    private ServiceDiscovery<ServiceMeta> serviceDiscovery;


    @Override
    public void init(RegistryConfig registryConfig) throws Exception {
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

    @Override
    public ServiceMeta discovery(String serviceName, int invokerHashCode) throws Exception {
        Collection<ServiceInstance<ServiceMeta>> serviceInstances = serviceDiscovery.queryForInstances(serviceName);
        ServiceInstance<ServiceMeta> instance = this.selectOneServiceInstance((List<ServiceInstance<ServiceMeta>>) serviceInstances);
        if (instance != null) {
            return instance.getPayload();
        }
        return null;

    }

    @Override
    public void destroy() throws IOException {
        serviceDiscovery.close();
    }

    /**
     * 随机选择一个服务实例
     * @param serviceInstances 服务实例列表
     * @return 服务实例
     */
    private ServiceInstance<ServiceMeta> selectOneServiceInstance(List<ServiceInstance<ServiceMeta>> serviceInstances) {
        if (serviceInstances == null || serviceInstances.isEmpty()) {
            return null;
        }
        Random random = new Random();
        int index = random.nextInt(serviceInstances.size());
        return serviceInstances.get(index);
    }

}
