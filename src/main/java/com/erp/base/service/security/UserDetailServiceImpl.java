package com.erp.base.service.security;

import com.erp.base.model.entity.ClientModel;
import com.erp.base.service.ClientService;
import com.erp.base.tool.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserDetailServiceImpl implements UserDetailsService {
    LogFactory LOG = new LogFactory(UserDetailServiceImpl.class);
    private ClientService clientService;
    private UserDetailFactory userDetailFactory;

    @Autowired
    public void setClientService(ClientService clientService) {
        this.clientService = clientService;
    }
    @Autowired
    public void setUserDetail(UserDetailFactory userDetailFactory) {
        this.userDetailFactory = userDetailFactory;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ClientModel client = clientService.findByUsername(username);
        if (client == null) {
            LOG.warn("Cant find user: {0}", username);
            throw new UsernameNotFoundException("Cant find user:" + username);
        }
        return userDetailFactory.build(client);
    }
}
