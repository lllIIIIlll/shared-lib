package net.ow.shared.errorutils.util;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

@Component
public class LocaleMessageSource extends ResourceBundleMessageSource {
    public String getMessage(String message) {
        return getMessage(message, new Object[] {}, message, LocaleContextHolder.getLocale());
    }

    public String getMessage(String message, Object... params) {
        return getMessage(message, params, message, LocaleContextHolder.getLocale());
    }
}
