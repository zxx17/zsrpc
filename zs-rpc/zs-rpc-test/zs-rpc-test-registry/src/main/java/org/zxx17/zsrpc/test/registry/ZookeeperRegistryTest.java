package org.zxx17.zsrpc.test.registry;

import org.junit.Before;
import org.junit.Test;
import org.zxx17.zsrpc.protocol.meta.ServiceMeta;
import org.zxx17.zsrpc.registry.api.RegistryService;
import org.zxx17.zsrpc.registry.api.config.RegistryConfig;
import org.zxx17.zsrpc.registry.zk.ZookeeperRegistryService;

import java.io.IOException;

/**
 * .
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/7/8
 **/
public class ZookeeperRegistryTest {
    private RegistryService registryService;
    private ServiceMeta serviceMeta;
    @Before
    public void init() throws Exception{
        RegistryConfig registryConfig = new RegistryConfig("120.26.210.135:2181", "zookeeper");
        this.registryService = new ZookeeperRegistryService();
        this.registryService.init(registryConfig);
        this.serviceMeta = new ServiceMeta(ZookeeperRegistryTest.class.getName(), "1.0.0", "120.26.210.135", 8080, "zxx17");
    }
    @Test
    public void testRegister() throws Exception {
        this.registryService.register(serviceMeta);
    }
    @Test
    public void testUnRegister() throws Exception {
        this.registryService.unRegister(serviceMeta);
    }
    @Test
    public void testDiscovery() throws Exception {
        ServiceMeta discovery = this.registryService.discovery(ZookeeperRegistryTest.class.getName(), "zxx17".hashCode());
        System.out.println(discovery);
    }
    @Test
    public void testDestroy() throws IOException {
        this.registryService.destroy();
    }
}
