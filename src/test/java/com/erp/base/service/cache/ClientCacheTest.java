package com.erp.base.service.cache;

import com.erp.base.model.constant.cache.CacheConstant;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.service.ClientService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = ClientCacheTest.Config.class)
class ClientCacheTest {
    @TestConfiguration
    @EnableCaching
    static class Config {
        @Bean
        public CacheManager cacheManager() {
            return new ConcurrentMapCacheManager(CacheConstant.CLIENT.NAME_CLIENT);
        }
    }

    private final ClientCache clientCache = new ClientCache();
    @Autowired
    private CacheManager cacheManager;
    @Mock
    private ClientService clientService;
    private static final String key = "test";
    private static final ClientModel client = new ClientModel();
    static {
        client.setId(1);
        client.setUsername(key);
        client.setPassword(key);
        client.setEmail(key);
        client.setCreateBy(0);
    }

        @BeforeEach
        void setUp() {
            clientCache.setClientService(clientService);
        }

        @Test
    void getClient_unknownUser() {
        ClientModel unknown = clientCache.getClient("unknown");
        Assertions.assertNull(unknown);
    }

    @Test
    void getClient_ok() {
//        Assertions.assertNull(cacheManager.getCache(CacheConstant.CLIENT.NAME_CLIENT).get(CacheConstant.CLIENT.CLIENT.replace("'", "") + key));
//        Mockito.when(clientService.findByUsername(Mockito.eq("test"))).thenReturn(client);
//        ClientModel result1 = clientCache.getClient("test");
//        ClientModel result2 = clientCache.getClient("test");
//
//        Assertions.assertEquals(client, result1);
//        Assertions.assertEquals(client, result2);
//
//        Mockito.verify(clientService, Mockito.times(1)).findByUsername(Mockito.eq("test"));
    }
}