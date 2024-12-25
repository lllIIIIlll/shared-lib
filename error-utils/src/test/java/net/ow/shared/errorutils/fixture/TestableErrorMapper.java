package net.ow.shared.errorutils.fixture;

import net.ow.shared.errorutils.mapper.ErrorMapper;
import org.springframework.stereotype.Component;

@Component
public class TestableErrorMapper extends ErrorMapper {
    @Override
    protected String generateId() {
        return "1234-5678-1234-5678";
    }
}
