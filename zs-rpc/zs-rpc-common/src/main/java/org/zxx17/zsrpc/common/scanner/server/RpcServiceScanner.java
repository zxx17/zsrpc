package org.zxx17.zsrpc.common.scanner.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zxx17.zsrpc.annotation.RpcService;
import org.zxx17.zsrpc.common.helper.RpcServiceHelper;
import org.zxx17.zsrpc.common.scanner.ClassScanner;

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
            /*String host, int port, */
            String scanPackage
            /*, RegistryService*/) throws Exception {

        Map<String, Object> handlerMap = new HashMap<>();
        List<String> classNameList = getClassNameList(scanPackage);
        if (classNameList == null || classNameList.isEmpty()){
            return handlerMap;
        }
        classNameList.stream().forEach((className) -> {
            try {
                Class<?> clazz = Class.forName(className);
                RpcService rpcService = clazz.getAnnotation(RpcService.class);
                if (rpcService != null){
                    //优先使用interfaceClass, interfaceClass的name为空，再使用interfaceClassName
                    //TODO 后续逻辑向注册中心注册服务元数据，同时向handlerMap中记录标注了RpcService注解的类实例
//                    LOGGER.info("当前标注了@RpcService注解的类实例名称===>>> " + clazz.getName());
//                    LOGGER.info("@RpcService注解上标注的属性信息如下：");
//                    LOGGER.info("interfaceClass===>>> " + rpcService.interfaceClass().getName());
//                    LOGGER.info("interfaceClassName===>>> " + rpcService.interfaceClassName());
//                    LOGGER.info("version===>>> " + rpcService.version());
//                    LOGGER.info("group===>>> " + rpcService.group());
                    String serviceName = getServiceName(rpcService);
                    // key = service+version+group
                    String key = RpcServiceHelper.buildServiceKey(serviceName, rpcService.version(), rpcService.group());
                    handlerMap.put(key, clazz.getDeclaredConstructor().newInstance());
                }
            } catch (Exception e) {
                logger.error("ZS-RPC scan classes throws exception: {}", e);
            }
        });
        return handlerMap;
    }

    private static String getServiceName(RpcService rpcService){
        return rpcService.interfaceClass().getName();
    }


}
