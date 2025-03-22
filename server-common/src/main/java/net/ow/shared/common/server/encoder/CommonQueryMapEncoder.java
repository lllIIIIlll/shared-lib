package net.ow.shared.common.server.encoder;

import feign.QueryMapEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import net.ow.shared.common.server.annotation.QueryParameter;

@Slf4j
public class CommonQueryMapEncoder implements QueryMapEncoder {
    @Override
    public Map<String, Object> encode(Object object) {
        Map<String, Object> queryParameters = new HashMap<>();

        Arrays.stream(object.getClass().getDeclaredFields())
                .forEach(
                        field -> {
                            field.setAccessible(true);
                            String fieldName = field.getName();

                            try {
                                Object fieldValue = field.get(object);
                                QueryParameter annotation =
                                        field.getAnnotation(QueryParameter.class);
                                String parameterName =
                                        null != annotation ? annotation.name() : fieldName;
                                queryParameters.put(parameterName, fieldValue);
                            } catch (IllegalAccessException e) {
                                log.error(
                                        "Failed to parse field {} as request parameter: {}",
                                        field,
                                        fieldName);
                            }
                        });

        return queryParameters;
    }
}
