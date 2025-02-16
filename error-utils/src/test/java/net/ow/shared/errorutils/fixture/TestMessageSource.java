package net.ow.shared.errorutils.fixture;

import net.ow.shared.errorutils.util.LocaleMessageSource;

public class TestMessageSource extends LocaleMessageSource {
    public TestMessageSource() {
        super();
        setBasename("messages");
    }
}
