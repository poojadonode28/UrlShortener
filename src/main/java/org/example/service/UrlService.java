
package org.example.service;

import org.example.exceptions.ShortCodeConflictException;
import org.example.exceptions.ShortCodeNotFoundException;
import org.example.models.ShortUrl;
import org.example.repositories.UrlRepository;
import org.example.repositories.UserRepository;

import java.time.LocalDateTime;

public class UrlService {

    private final UrlRepository urlRepository;
    private final ShortCodeGenerator shortCodeGenerator;
    private final UrlValidator validator;
    private final UserRepository userRepository;

    public UrlService(
            UrlRepository urlRepository,
            ShortCodeGenerator shortCodeGenerator,
            UrlValidator validator,
            UserRepository userRepository
    ) {
        this.urlRepository = urlRepository;
        this.shortCodeGenerator = shortCodeGenerator;
        this.validator = validator;
        this.userRepository = userRepository;
    }

    // Create a short URL without a user or expiration time.
    public ShortUrl createShortUrl(String longUrl) {
        return createShortUrl(longUrl, null, null);
    }

    // Create a short URL with an optional expiration time and user.
    public ShortUrl createShortUrl(
            String longUrl,
            LocalDateTime expiresAt,
            Long userId
    ) {
        validator.validate(longUrl);
        validateUser(userId);
        validateExpiration(expiresAt);

        int maxAttempts = 5;

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            String shortCode = shortCodeGenerator.generateShortCode();

            ShortUrl shortUrl = new ShortUrl(
                    shortCode,
                    longUrl,
                    expiresAt,
                    userId
            );

            try {
                urlRepository.save(shortUrl);
                return shortUrl;
            } catch (ShortCodeConflictException exception) {
                // Retry if another request has already claimed this code.
            }
        }

        throw new IllegalStateException(
                "Unable to generate a unique short code after "
                        + maxAttempts + " attempts"
        );
    }

    // Create a custom alias without associating a user.
    public ShortUrl createCustomShortUrl(
            String longUrl,
            String customCode,
            LocalDateTime expiresAt
    ) {
        return createCustomShortUrl(
                longUrl,
                customCode,
                expiresAt,
                null
        );
    }

    // Create a custom alias with an optional user.
    public ShortUrl createCustomShortUrl(
            String longUrl,
            String customCode,
            LocalDateTime expiresAt,
            Long userId
    ) {
        validator.validate(longUrl);
        validateUser(userId);
        validateExpiration(expiresAt);

        if (customCode == null || customCode.isBlank()) {
            throw new IllegalArgumentException(
                    "Custom short code cannot be empty"
            );
        }

        if (!customCode.matches("[a-zA-Z0-9_-]+")) {
            throw new IllegalArgumentException(
                    "Custom code can contain only letters, digits, "
                            + "underscores and hyphens"
            );
        }

        ShortUrl shortUrl = new ShortUrl(
                customCode,
                longUrl,
                expiresAt,
                userId
        );

        // The repository enforces uniqueness.
        urlRepository.save(shortUrl);

        return shortUrl;
    }

    // Resolve a short code to its original URL.
    public String getOriginalUrl(String shortCode) {
        ShortUrl shortUrl = urlRepository.findByShortCode(shortCode);

        if (shortUrl == null) {
            throw new ShortCodeNotFoundException(
                    "Short URL not found: " + shortCode
            );
        }

        if (shortUrl.getExpiresAt() != null
                && !shortUrl.getExpiresAt().isAfter(LocalDateTime.now())) {
            throw new ShortCodeNotFoundException(
                    "Short URL has expired: " + shortCode
            );
        }

        return shortUrl.getLongUrl();
    }

    private void validateUser(Long userId) {
        if (userId != null && userRepository.findById(userId) == null) {
            throw new IllegalArgumentException(
                    "User not found: " + userId
            );
        }
    }

    private void validateExpiration(LocalDateTime expiresAt) {
        if (expiresAt != null
                && !expiresAt.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "Expiration time must be in the future"
            );
        }
    }
}
