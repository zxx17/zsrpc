package org.zxx17.zsrpc.loadbalancer.random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.zxx17.zsrpc.loadbalancer.api.ServiceLoadBalance;

import java.util.List;
import java.util.Random;

/**
 * .
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/7/29
 **/
public class RandomServiceLoadBalancer<T> implements ServiceLoadBalance<T> {

    private final Logger logger = LoggerFactory.getLogger(RandomServiceLoadBalancer.class);

    @Override
    public T select(List<T> services, int invokerHashCode) {
        logger.info("=====>随机算法的负载均衡策略");
        if (CollectionUtils.isEmpty(services)){
            return null;
        }
        Random random = new Random();
        int index = random.nextInt(services.size());
        return services.get(index);
    }
}
