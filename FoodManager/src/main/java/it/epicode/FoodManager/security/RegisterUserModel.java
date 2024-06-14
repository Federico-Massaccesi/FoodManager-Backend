package it.epicode.FoodManager.security;

import it.epicode.FoodManager.UserEntity.Roles;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import javax.management.relation.Role;
import java.util.List;

public record RegisterUserModel(

        @NotBlank(message = "Lo username  non può contenere solo spazi vuoti")
        @Size(max = 50, message ="Il tuo username è troppo lungo max 50 caratteri")
        String username,
        @Email(message = "Inserisci una email valida")
        String email,
        @NotBlank(message = "La password non può contenere solo spazi vuoti")
        @Size(max = 125, message ="La password è troppo lunga max 20 caratteri")
        String password,
        @NotBlank
        String address,
        @NotBlank
        String city,
        @NotBlank
        String companyName,
        @NotBlank
        String pIVA,
        @NotNull
        List<Roles> roles

) {
}