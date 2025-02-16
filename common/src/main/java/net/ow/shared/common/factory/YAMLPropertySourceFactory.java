package net.ow.shared.common.factory;

import io.micrometer.common.lang.NonNullApi;
import io.micrometer.common.lang.Nullable;
import java.util.Objects;
import java.util.Properties;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

@NonNullApi
public class YAMLPropertySourceFactory implements PropertySourceFactory {
    private static final String ERROR_MESSAGE = "Failed to setup yaml property source factory";

    @Override
    public PropertySource<?> createPropertySource(@Nullable String name, EncodedResource resource) {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(resource.getResource());

        Properties properties = factory.getObject();
        assert properties != null : ERROR_MESSAGE;

        return new PropertiesPropertySource(
                Objects.requireNonNullElseGet(
                        name, () -> Objects.requireNonNull(resource.getResource().getFilename())),
                properties);
    }
}
