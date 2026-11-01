package model;

import com.google.gson.annotations.Expose;
import lombok.Data;

import java.util.Map;

@Data
public class ErrorMessageModel {
    @Expose
    private String error;

    @Expose
    private Map<String, String> fields;
}

