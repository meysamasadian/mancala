package me.asadian.mancala.player.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerRegistrationRequest {
    private String username;
    private String password;
    private String repeatPassword;
    private String avatar;
}
