package com.erp.base.service.cache;

import com.erp.base.model.dto.response.ClientNameObject;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.service.ClientService;
import com.erp.base.testConfig.redis.TestRedisConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = TestRedisConfiguration.class)
@TestPropertySource(locations = {
        "classpath:application-redis-test.properties",
        "classpath:application-quartz-test.properties"
})
@AutoConfigureMockMvc
@Transactional
@DirtiesContext
class ClientCacheTest {
    @MockBean
    public ClientService clientService;
    @Autowired
    private ClientCache clientCache;

    @BeforeEach
    void setUp() {
            clientCache.setClientService(clientService);
    }
    private static final String key = "test";
    private static final ClientModel client = new ClientModel();

    static {
        client.setId(1);
        client.setUsername(key);
        client.setPassword(key);
        client.setEmail(key);
        client.setCreateBy(0);
    }

    @Test
    void getClient_unknownUser() {
        Mockito.when(clientService.findByUsername(Mockito.anyString())).thenReturn(null);
        ClientModel unknown = clientCache.getClient("unknown");
        Assertions.assertNull(unknown);
    }

    @Test
    void getClient_ok() {
        Mockito.when(clientService.findByUsername(Mockito.anyString())).thenReturn(client);

        ClientModel result1 = clientCache.getClient(key);
        ClientModel result2 = clientCache.getClient(key);

        Assertions.assertEquals(client, result1);
        Assertions.assertEquals(client, result2);

        Mockito.verify(clientService, Mockito.times(1)).findByUsername(Mockito.eq(key));
        Mockito.verifyNoMoreInteractions(clientService);
    }

    @Test
    void refreshClient_ok() {
        Mockito.when(clientService.findByUsername(Mockito.anyString())).thenReturn(client);
        clientCache.refreshClient(key);
        clientCache.getClient(key);
        clientCache.refreshClient(key);
        clientCache.getClient(key);
        Mockito.verify(clientService, Mockito.times(2)).findByUsername(Mockito.eq(key));
        Mockito.verifyNoMoreInteractions(clientService);
    }

    @Test
    void refreshAll_ok() {
        Mockito.when(clientService.findByUsername(Mockito.anyString())).thenReturn(client);
        clientCache.refreshAll();
        clientCache.getClient(key);
        clientCache.getClient(key + 1);
        clientCache.refreshAll();
        clientCache.getClient(key);
        clientCache.getClient(key + 1);
        Mockito.verify(clientService, Mockito.times(2)).findByUsername(Mockito.eq(key));
        Mockito.verify(clientService, Mockito.times(2)).findByUsername(Mockito.eq(key + 1));
        Mockito.verifyNoMoreInteractions(clientService);
    }

    @Test
    void getClientNameList_ok() {
        List<ClientNameObject> list = new ArrayList<>();
        list.add(new ClientNameObject(client));
        Mockito.when(clientService.getClientNameList()).thenReturn(list);
        clientCache.refreshAll();
        List<ClientNameObject> clientNameList = clientCache.getClientNameList();
        List<ClientNameObject> clientNameList1 = clientCache.getClientNameList();
        Assertions.assertEquals(list, clientNameList);
        Assertions.assertEquals(list, clientNameList1);
        Mockito.verify(clientService, Mockito.times(1)).getClientNameList();
        Mockito.verifyNoMoreInteractions(clientService);
    }
}