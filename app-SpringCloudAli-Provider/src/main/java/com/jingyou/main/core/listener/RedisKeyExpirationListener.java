package com.jingyou.main.core.listener;

import com.jingyou.main.core.constants.GlobalConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;


//@Component
@Slf4j
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {
    /**
     *
     * @param listenerContainer
     */
    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        // 用户做自己的业务处理即可,注意message.toString()可以获取失效的key
        String expiredKey = message.toString();
        log.info("------------------redis key 失效; key = " + expiredKey);
        if (expiredKey.startsWith(GlobalConstant.RedisPrefixKey.ORDER_PREFIX)) {
            // 获取订单orderNO
            String orderNo = expiredKey.substring(expiredKey.lastIndexOf(":")+1);
        }
    }

}
