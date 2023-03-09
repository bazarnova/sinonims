package org.example;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class RController {
    RestTemplate restTemplate = new RestTemplate();

    public String login(RequestCredentials requestCredentials) {
        ResponseEntity<String> response = restTemplate
                .getForEntity("http://paraphraser.ru/token?login=" + requestCredentials.getLogin()
                        + "&password=" + requestCredentials.getPassword(), String.class);
        return response.getBody();
    }


    public ResponseEntity<Map> getSynonyms(Payload payload, String token) {

        String url = "http://paraphraser.ru/api?token=" + token;

        URI uri = UriComponentsBuilder
                .fromUriString(url)
                .queryParam("c", payload.getC())
                .queryParam("query", payload.getQuery())
                .queryParam("top", payload.getTop())
                .queryParam("scores", payload.getScores())
                .queryParam("forms", payload.getForms())
                .queryParam("format", payload.getFormat())
                .queryParam("lang", payload.getLang())
                .build()
                .toUri();

        ResponseEntity<Map> response = restTemplate
                .getForEntity(uri, Map.class);
        return response;
    }

}
