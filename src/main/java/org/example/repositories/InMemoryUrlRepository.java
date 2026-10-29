package org.example.repositories;

import org.example.exceptions.ShortCodeConflictException;
import org.example.models.ShortUrl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUrlRepository implements UrlRepository {

    private final Map<String, ShortUrl> shortUrls = new ConcurrentHashMap<>();

    public void save(ShortUrl shortUrl) {
        String code = shortUrl.getShortCode();
        if (shortUrls.containsKey(code)) {
            throw new ShortCodeConflictException(
                    "Short code already exists: " + code
            );
        }
        shortUrls.put(shortUrl.getShortCode(), shortUrl);
    }

    public ShortUrl findByShortCode(String shortCode) {
        return shortUrls.get(shortCode);
    }

}
