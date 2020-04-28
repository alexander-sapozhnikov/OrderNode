package ru.sapozhnikov.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.sapozhnikov.dao.UserSecurityDAO;
import ru.sapozhnikov.entity.UserSecurity;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserSecurityDAO userSecurityDAO;
    private String token;

    public UserDetailsServiceImpl(UserSecurityDAO userSecurityDAO) {
        this.userSecurityDAO = userSecurityDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String username)  throws  UsernameNotFoundException{
        Optional<UserSecurity> optional = userSecurityDAO.findByUsername(username);

        if(!optional.isPresent()){
            throw new UsernameNotFoundException("User don't exist." + username);
        }
        UserSecurity userSecurity = optional.get();
        if(!token.equals(userSecurity.getToken())){
            return new User(userSecurity.getUsername(), userSecurity.getPassword(),
                    true, false, true, true, Collections.emptyList());
        }
        return new User(userSecurity.getUsername(), userSecurity.getPassword(), Collections.emptyList());
    }

    public void setToken(String token) {
        this.token = token;
    }
}
