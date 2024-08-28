package org.zxx17.zsrpc.proxy.asm.classloader;

import java.util.HashMap;
import java.util.Map;

/**
 * 加载基于ASM动态生成的代理类.
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/8/28
 **/
public class ASMClassLoader extends ClassLoader {
    private final Map<String, byte[]> classMap = new HashMap<>();

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (classMap.containsKey(name)) {
            byte[] bytes = classMap.get(name);
            classMap.remove(name);
            return defineClass(name, bytes, 0, bytes.length);
        }
        return super.findClass(name);
    }

    public void add(String name, byte[] bytes) {
        classMap.put(name, bytes);
    }

}
