package org.example;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class Payload {

    private String c;
    private String query;
    private Integer top;
    private Integer scores;
    private Integer forms;
    private String format;
    private String lang;
    private String token;
}
