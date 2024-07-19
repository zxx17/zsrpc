package org.zxx17.zsrpc.test;

import org.junit.Test;
import org.zxx17.zsrpc.common.scanner.ClassScanner;
import org.zxx17.zsrpc.common.scanner.reference.RpcReferenceScanner;

import java.util.List;

public class ScannerTest {
    /**
     * 扫描org.zxx17.zsrpc.test包下所有的类
     */
    @Test
    public void testScannerClassNameList() throws Exception {
        List<String> classNameList = ClassScanner.getClassNameList("org.zxx17.zsrpc.test");
        classNameList.forEach(System.out::println);
    }

    /**
     * 扫描org.zxx17.zsrpc.test包下所有标注了@RpcService注解的类
     */
//    @Test
//    public void testScannerClassNameListByRpcService() throws Exception {
//        RpcServiceScanner.
//                doScannerWithRpcServiceAnnotationFilterAndRegistryService("org.zxx17.zsrpc.test");
//    }

    /**
     * 扫描org.zxx17.zsrpc.test包下所有标注了@RpcReference注解的类
     */
    @Test
    public void testScannerClassNameListByRpcReference() throws Exception {
        RpcReferenceScanner.
                doScannerWithRpcReferenceAnnotationFilter("org.zxx17.zsrpc.test");
    }

}
