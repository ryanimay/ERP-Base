package com.ex.erp.service.security;

import com.ex.erp.model.ClientModel;
import com.ex.erp.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserDetailServiceImpl implements UserDetailsService {
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
            System.out.println("Cant find user:" + username);
            throw new UsernameNotFoundException("Cant find user:" + username);
        }
        return userDetailFactory.build(client);
    }
}
