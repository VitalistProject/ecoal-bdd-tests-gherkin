package model;

import com.google.gson.annotations.Expose;
import lombok.Data;

@Data
public class ErrorFieldModel {
    @Expose
    private String error;
}
