package org.zxx17.zsrpc.common.scanner.reference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zxx17.zsrpc.annotation.RpcReference;
import org.zxx17.zsrpc.common.scanner.ClassScanner;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class RpcReferenceScanner extends ClassScanner {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcReferenceScanner.class);

    /**
     * 扫描指定包下的类，并筛选使用@RpcService注解标注的类
     *
     * @param scanPackage 扫描的包
     * @return @RpcService注解标注的实现类放入一个Map缓存
     * @throws Exception 异常
     */
    public static Map<String, Object> doScannerWithRpcReferenceAnnotationFilter(/*String host, int port, */ String scanPackage/*, RegistryService registryService*/) throws Exception{
        Map<String, Object> handlerMap = new HashMap<>();
        List<String> classNameList = getClassNameList(scanPackage);
        if (classNameList == null || classNameList.isEmpty()){
            return handlerMap;
        }
        // 遍历类名集合，获取类对象，并筛选出有@RpcService注解的类
        classNameList.stream().forEach((className) -> {
            try {
                Class<?> clazz = Class.forName(className);
                Field[] declaredFields = clazz.getDeclaredFields();
                Stream.of(declaredFields).forEach((field) -> {
                    RpcReference rpcReference = field.getAnnotation(RpcReference.class);
                    if (rpcReference != null){
                        //TODO 处理后续逻辑，将@RpcReference注解标注的接口引用代理对象，放入全局缓存中
                        LOGGER.info("当前标注了@RpcReference注解的字段名称===>>> " + field.getName());
                        LOGGER.info("@RpcReference注解上标注的属性信息如下：");
                        LOGGER.info("version===>>> " + rpcReference.version());
                        LOGGER.info("group===>>> " + rpcReference.group());
                        LOGGER.info("registryType===>>> " + rpcReference.registryType());
                        LOGGER.info("registryAddress===>>> " + rpcReference.registryAddress());
                    }
                });
            } catch (Exception e) {
                LOGGER.error("scan classes throws exception: {}", e);
            }
        });
        return handlerMap;
    }

}
