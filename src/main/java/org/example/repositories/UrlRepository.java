package org.example.repositories;

import org.example.models.ShortUrl;

public interface UrlRepository {

    void save(ShortUrl shortUrl);
    ShortUrl findByShortCode(String shortCode);
}
