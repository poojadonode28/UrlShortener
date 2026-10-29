package org.example;

import org.example.models.ShortUrl;
import org.example.models.User;
import org.example.repositories.InMemoryUrlRepository;
import org.example.repositories.InMemoryUserRepository;
import org.example.repositories.UrlRepository;
import org.example.repositories.UserRepository;
import org.example.service.ShortCodeGenerator;
import org.example.service.strategy.Base62ShortCodeGenerator;
import org.example.service.UrlService;
import org.example.service.UrlValidator;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
//        // 1. Create the objects our service needs
//        InMemoryUrlRepository repository = new InMemoryUrlRepository();
//        Base62ShortCodeGenerator generator = new Base62ShortCodeGenerator();
//        UrlValidator validator = new UrlValidator();
//
//        // 2. Give those objects to the service
//        UrlService service = new UrlService(repository, generator,validator);

        /*// 3. Create a short URL
        ShortUrl shortUrl =
                service.createShortUrl("https://google.com");

        System.out.println("Short code: " + shortUrl.getShortCode());
        System.out.println("Original URL: " + shortUrl.getLongUrl());

        // 4. Retrieve the original URL using the short code
        String originalUrl =
                service.getOriginalUrl(shortUrl.getShortCode());

        System.out.println("Redirect destination: " + originalUrl);*/

//

//        UrlRepository repository = new InMemoryUrlRepository();
//
//        ShortCodeGenerator generator =
//                new Base62ShortCodeGenerator();
//
//        UrlValidator validator = new UrlValidator();
//
//        UrlService urlService = new UrlService(
//                repository,
//                generator,
//                validator,
//                userId
//        );
//
//        var shortUrl = urlService.createShortUrl(
//                "https://google.com"
//        );
//
//        System.out.println("Short code: " + shortUrl.getShortCode());
//        System.out.println("Original URL: " + shortUrl.getLongUrl());
//    }
        UserRepository userRepository = new InMemoryUserRepository();

        User user = new User(
                1L,
                "Kaushik",
                "user@example.com"
        );

        userRepository.save(user);

        UrlRepository urlRepository = new InMemoryUrlRepository();

        ShortCodeGenerator generator = new Base62ShortCodeGenerator();

        UrlValidator validator = new UrlValidator();

        UrlService urlService = new UrlService(
                urlRepository,
                generator,
                validator,
                userRepository
        );

        ShortUrl shortUrl = urlService.createShortUrl(
                "https://google.com",
                null,
                user.getId()
        );

        System.out.println("User ID: " + shortUrl.getUserId());
        System.out.println("Short code: " + shortUrl.getShortCode());
        System.out.println("Original URL: " + shortUrl.getLongUrl());
    }
}
