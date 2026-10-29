package org.example.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShortUrl {

    private String shortCode;
    private String longUrl;
    private LocalDateTime expiresAt;
    private Long userId;

}
