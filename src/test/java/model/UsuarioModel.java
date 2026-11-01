package model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class UsuarioModel {
    @Expose
    private String name;
    @Expose
    private String email;
    @Expose
    private String password;
    @Expose
    private String role;
    @Expose
    @SerializedName("company_cnpj")
    private String companyCnpj;
    @Expose
    private String department;
}