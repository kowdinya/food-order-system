package com.foodorder.auth.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    private String username;
    @NotBlank(message ="Email cannot be blank")
    private String email;
    @NotBlank(message ="Email cannot be blank")
    private String password;


}
