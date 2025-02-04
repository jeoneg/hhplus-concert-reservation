package kr.hhplus.be.server.common.uuid;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SimpleUUIDGenerator implements UUIDGenerator {

    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }

}
