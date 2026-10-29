package org.example.service;

import org.example.exceptions.InvalidUrlException;

import java.net.URI;

public class UrlValidator {

    public void validate(String url){
        if(url==null || url.isEmpty()){
            throw new InvalidUrlException("URL cannot be null or empty");
        }
        try {
            URI uri = URI.create(url);
            String scheme = uri.getScheme();
            if (scheme == null ||
                    (!scheme.equalsIgnoreCase("http")
                            && !scheme.equalsIgnoreCase("https"))) {
                throw new InvalidUrlException(
                        "URL must start with http:// or https://"
                );
            }
        }catch(IllegalArgumentException e){
            throw new InvalidUrlException(
                    "Invalid URL: " + url
            );
        }
    }
}
