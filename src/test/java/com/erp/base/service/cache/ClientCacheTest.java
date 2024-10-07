package com.erp.base.service.cache;

import com.erp.base.model.dto.response.ClientNameObject;
import com.erp.base.model.dto.security.ClientIdentityDto;
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
        Mockito.when(clientService.findById(Mockito.anyLong())).thenReturn(null);
        ClientIdentityDto unknown = clientCache.getClient(9999L);
        Assertions.assertNull(unknown);
    }

    @Test
    void getClient_ok() {
        ClientIdentityDto expectClientDto = new ClientIdentityDto(client);
        Mockito.when(clientService.findById(Mockito.anyLong())).thenReturn(client);

        long id = client.getId();
        ClientIdentityDto result1 = clientCache.getClient(id);
        ClientIdentityDto result2 = clientCache.getClient(id);

        Assertions.assertEquals(expectClientDto, result1);
        Assertions.assertEquals(expectClientDto, result2);

        Mockito.verify(clientService, Mockito.times(1)).findById(id);
    }

    @Test
    void getClientNameList_ok() {
        List<ClientNameObject> list = new ArrayList<>();
        list.add(new ClientNameObject(client));
        Mockito.when(clientService.getClientNameList()).thenReturn(list);
        List<ClientNameObject> clientNameList = clientCache.getClientNameList();
        List<ClientNameObject> clientNameList1 = clientCache.getClientNameList();
        Assertions.assertEquals(list, clientNameList);
        Assertions.assertEquals(list, clientNameList1);
        Mockito.verify(clientService, Mockito.times(1)).getClientNameList();
        Mockito.verifyNoMoreInteractions(clientService);
    }

    @Test
    void getSystemUser_ok() {
        Mockito.when(clientService.getSystemUser()).thenReturn("1/1");
        String result = clientCache.getSystemUser();
        Mockito.when(clientService.getSystemUser()).thenReturn("1/2");
        String result1 = clientCache.getSystemUser();
        Assertions.assertEquals("1/1", result);
        Assertions.assertEquals("1/1", result1);
        Mockito.verify(clientService, Mockito.times(1)).getSystemUser();
        Mockito.verifyNoMoreInteractions(clientService);
    }
}