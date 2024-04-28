package net.ow.shared.errorutils.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class APIResponse {
    private Error[] errors;

    private Map<String, Object> meta;

    public static APIResponse error(Error... errors) {
        var response = new APIResponse();
        response.errors = errors;
        return response;
    }
}
