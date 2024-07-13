package org.zxx17.zsrpc.provider.common.scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zxx17.zsrpc.annotation.RpcService;
import org.zxx17.zsrpc.common.helper.RpcServiceHelper;
import org.zxx17.zsrpc.common.scanner.ClassScanner;
import org.zxx17.zsrpc.protocol.meta.ServiceMeta;
import org.zxx17.zsrpc.registry.api.RegistryService;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RpcServiceScanner extends ClassScanner {

    private static final Logger logger = LoggerFactory.getLogger(RpcServiceScanner.class);


    /**
     * 扫描指定包下的类，并筛选使用@RpcService注解标注的类
     *
     * @param scanPackage 扫描的包
     * @return @RpcService注解标注的实现类放入一个Map缓存
     * @throws Exception 异常
     */
    public static Map<String, Object> doScannerWithRpcServiceAnnotationFilterAndRegistryService(
            String host, int port,
            String scanPackage,
            RegistryService registryService) throws Exception {

        Map<String, Object> handlerMap = new HashMap<>();
        List<String> classNameList = getClassNameList(scanPackage);
        if (classNameList.isEmpty()) {
            return handlerMap;
        }
        classNameList.forEach((className) -> {
            try {
                Class<?> clazz = Class.forName(className);
                RpcService rpcService = clazz.getAnnotation(RpcService.class);
                if (rpcService != null) {
                    ServiceMeta serviceMeta = new ServiceMeta(
                            getServiceName(rpcService),
                            rpcService.version(),
                            host, port,
                            rpcService.group());
                    // 将元数据注册到注册中心
                    registryService.register(serviceMeta);


                    Constructor<?> declaredConstructor = clazz.getDeclaredConstructor();
                    declaredConstructor.setAccessible(true);
                    handlerMap.put(
                            RpcServiceHelper.buildServiceKey(serviceMeta.getServiceName(),
                                    serviceMeta.getServiceVersion(), serviceMeta.getServiceGroup()),
                            declaredConstructor.newInstance());
                }
            } catch (Exception e) {
                logger.error("ZS-RPC scan classes throws exception: {}", e);
            }
        });
        return handlerMap;
    }

    private static String getServiceName(RpcService rpcService) {
        return rpcService.interfaceClass().getName();
    }


}
