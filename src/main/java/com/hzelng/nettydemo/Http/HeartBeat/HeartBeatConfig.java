package com.hzelng.nettydemo.Http.HeartBeat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author HzeLng
 * @version 1.0
 * @description HeartBeatConfig
 * @date 2021/5/5 15:27
 */
@Configuration
public class HeartBeatConfig {

    @Value("${channel.id}")
    private long id ;


    @Bean(value = "heartBeat")
    public CustomProtocol heartBeat(){
        return new CustomProtocol(id,"ping") ;
    }

}
