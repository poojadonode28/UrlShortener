
package org.example.service;

import org.example.exceptions.InvalidUrlException;
import org.example.exceptions.ShortCodeConflictException;
import org.example.exceptions.ShortCodeNotFoundException;
import org.example.models.ShortUrl;
import org.example.models.User;
import org.example.repositories.InMemoryUrlRepository;
import org.example.repositories.InMemoryUserRepository;
import org.example.repositories.UrlRepository;
import org.example.repositories.UserRepository;
import org.example.service.strategy.Base62ShortCodeGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UrlServiceTest {

    private UrlService urlService;
    private UrlRepository urlRepository;
    private UserRepository userRepository;
    private User user;

    @BeforeEach
    void setUp() {
        urlRepository = new InMemoryUrlRepository();
        userRepository = new InMemoryUserRepository();

        user = new User(
                1L,
                "Test User",
                "test@example.com"
        );

        userRepository.save(user);

        urlService = new UrlService(
                urlRepository,
                new Base62ShortCodeGenerator(),
                new UrlValidator(),
                userRepository
        );
    }

    @Test
    void shouldCreateShortUrlForUser() {
        ShortUrl result = urlService.createShortUrl(
                "https://google.com",
                null,
                user.getId()
        );

        assertNotNull(result.getShortCode());
        assertEquals("https://google.com", result.getLongUrl());
        assertEquals(user.getId(), result.getUserId());
    }

    @Test
    void shouldRetrieveOriginalUrl() {
        ShortUrl result = urlService.createShortUrl(
                "https://google.com",
                null,
                user.getId()
        );

        assertEquals(
                "https://google.com",
                urlService.getOriginalUrl(result.getShortCode())
        );
    }

    @Test
    void shouldRejectUnknownShortCode() {
        assertThrows(
                ShortCodeNotFoundException.class,
                () -> urlService.getOriginalUrl("unknown")
        );
    }

    @Test
    void shouldRejectInvalidUrl() {
        assertThrows(
                InvalidUrlException.class,
                () -> urlService.createShortUrl(
                        "ftp://example.com",
                        null,
                        user.getId()
                )
        );
    }

    @Test
    void shouldCreateCustomAlias() {
        ShortUrl result = urlService.createCustomShortUrl(
                "https://github.com",
                "my-github",
                null,
                user.getId()
        );

        assertEquals("my-github", result.getShortCode());
        assertEquals("https://github.com", result.getLongUrl());
        assertEquals(
                "https://github.com",
                urlService.getOriginalUrl("my-github")
        );
    }

    @Test
    void shouldRejectDuplicateCustomAlias() {
        urlService.createCustomShortUrl(
                "https://google.com",
                "my-link",
                null,
                user.getId()
        );

        assertThrows(
                ShortCodeConflictException.class,
                () -> urlService.createCustomShortUrl(
                        "https://github.com",
                        "my-link",
                        null,
                        user.getId()
                )
        );
    }

    @Test
    void shouldRejectPastExpirationTime() {
        assertThrows(
                IllegalArgumentException.class,
                () -> urlService.createShortUrl(
                        "https://google.com",
                        LocalDateTime.now().minusMinutes(1),
                        user.getId()
                )
        );
    }

    @Test
    void shouldRejectExpiredUrlWhenAccessed() {
        ShortUrl expiredUrl = new ShortUrl(
                "expired1",
                "https://google.com",
                LocalDateTime.now().minusMinutes(1),
                user.getId()
        );

        urlRepository.save(expiredUrl);

        assertThrows(
                ShortCodeNotFoundException.class,
                () -> urlService.getOriginalUrl("expired1")
        );
    }

    @Test
    void shouldRejectUnknownUser() {
        assertThrows(
                IllegalArgumentException.class,
                () -> urlService.createShortUrl(
                        "https://google.com",
                        null,
                        999L
                )
        );
    }
}
