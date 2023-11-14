package me.asadian.mancala.player.model;


import jakarta.persistence.*;
import lombok.*;
import me.asadian.mancala.shared.security.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Table(indexes = {
        @Index(name = "username_index", columnList = "username", unique = true)
})
@EqualsAndHashCode
public class UserModel implements UserDetails {

    @Id
    @GeneratedValue
    private Long id;

    @EqualsAndHashCode.Include
    private String username;
    private String password;
    private String avatar;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
