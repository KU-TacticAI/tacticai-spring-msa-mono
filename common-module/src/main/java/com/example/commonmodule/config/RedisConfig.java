package com.example.commonmodule.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

  @Value("${spring.data.redis.host}")
  private String host;

  @Value("${spring.data.redis.port}")
  private int port;

//  @Value("${spring.data.redis.password}")
//  private String password;

  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(host, port);
//    configuration.setPassword(password); // 비밀번호 설정
    return new LettuceConnectionFactory(configuration);
  }

  @Bean(name = "redisTemplate")
  public RedisTemplate<String, String> redisTemplate() {
    RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(redisConnectionFactory());

    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setValueSerializer(new StringRedisSerializer());

    return redisTemplate;
  }

//  /**
//   * ChannelTopic : Redis의 Pub/Sub 채널 이름 설정(notifications)
//   */
//  @Bean
//  public ChannelTopic notificationTopic() {
//    return new ChannelTopic("notifications");
//  }
//
//  /**
//   * RedisMessageListenerContainer를 설정하여 Redis Pub/Sub 메시지를 처리하는것
//   *
//   * @param connectionFactory RedisConnectionFactory - Redis 연결을 관리하는 객체
//   * @param listenerAdapter   MessageListenerAdapter - 메시지 처리 로직을 포함한 어댑터
//   * @param notificationTopic ChannelTopic - Redis Pub/Sub 채널
//   * @return RedisMessageListenerContainer - 메시지 리스너 컨테이너
//   */
//  @Bean
//  public RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory,
//      MessageListenerAdapter listenerAdapter,
//      ChannelTopic notificationTopic) {
//
//    // RedisMessageListenerContainer 객체 생성
//    RedisMessageListenerContainer container = new RedisMessageListenerContainer();
//
//    // Redis 연결을 설정
//    container.setConnectionFactory(connectionFactory);
//
//    // listenerAdapter를 notificationTopic 채널에 연결
//    container.addMessageListener(listenerAdapter, notificationTopic);
//    return container; // 구성된 컨테이너 반환
//  }
//
//  /**
//   * MessageListenerAdapter를 생성
//   * RedisSubscriber의 onMessage 메서드가 호출되도록 어댑터를 설정
//   */
//  @Bean
//  public MessageListenerAdapter listenerAdapter(com.example.playcation.redis.RedisSubscriber subscriber) {
//    // 기본적으로 "onMessage" 메서드를 호출하도록 설정되ㄴ다
//    return new MessageListenerAdapter(subscriber);
//  }
}
