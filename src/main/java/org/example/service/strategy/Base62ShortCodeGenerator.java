package org.example.service.strategy;

import org.example.service.ShortCodeGenerator;

import java.util.concurrent.atomic.AtomicLong;

public class Base62ShortCodeGenerator implements ShortCodeGenerator {

    private final AtomicLong counter = new AtomicLong(1000);

    public String generateShortCode() {
        long id = counter.incrementAndGet();
        return encodeBase62(id);
    }

    private String encodeBase62(long id) {
        String characters =
                "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder result = new StringBuilder();
        while(id>0){
            int remainder = (int) (id % 62);
            result.append(characters.charAt(remainder));
            id = id / 62;
        }
        return result.reverse().toString();
    }
}
