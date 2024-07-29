package org.zxx17.zsrpc.loadbalancer.api;

import java.util.List;

/**
 * .
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/7/29
 **/
public interface ServiceLoadBalance<T> {

    T select(List<T> services, int invokerHashCode);

}
