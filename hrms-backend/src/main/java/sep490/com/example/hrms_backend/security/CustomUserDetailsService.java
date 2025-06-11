package sep490.com.example.hrms_backend.security;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sep490.com.example.hrms_backend.entity.Account;
import sep490.com.example.hrms_backend.entity.Role;
import sep490.com.example.hrms_backend.repository.AccountRepository;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private AccountRepository accountRepository;
    /**
     * Load user details for authentication based on username or email.
     *
     * This method is used by Spring Security during the authentication process.
     * It fetches the account by matching either username or email, converts
     * the associated roles to Spring Security's GrantedAuthority format,
     * and RETURNS a UserDetails object that contains the login credentials and authorities.
     *
     * @param usernameOrEmail the username or email entered by the user for login
     * @return a fully populated UserDetails object for Spring Security
     * @throws UsernameNotFoundException if no matching account is found
     */

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Account not found"));

        // ✅ BẮT BUỘC PHẢI COPY RA HashSet — KHÔNG stream trực tiếp!
        Set<Role> roles = new HashSet<>(account.getRoles());

        Set<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
                .collect(Collectors.toSet());

        return new User(account.getUsername(), account.getPasswordHash(), authorities);
    }

}
