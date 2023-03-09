package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RService {

    private String token;
    private final RController rController = new RController();

    public void login(RequestCredentials requestCredentials) {
        String response = rController.login(requestCredentials);
        try {
            token = new ObjectMapper().readValue(response, Token.class).getToken();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, List> getSynonymsAndForms(Payload payload) {
        payload.setToken(token);

        ResponseEntity<Map> response = rController.getSynonyms(payload, token);
        HashMap result = (HashMap) ((HashMap) response.getBody().get("response")).get("1");
        List<String> synonyms = (List<String>) result.get("vector");
        List<String> forms = (List<String>) ((HashMap) result.get("forms_query")).values().stream().collect(Collectors.toList());
        HashMap<String, List> synAndForms = new HashMap<>();
        synAndForms.put("synonyms", synonyms);
        synAndForms.put("forms", forms);

        return synAndForms;
    }

    public List<String> getSimilarStrings(List<String> firstList, List<String> secondList) {
        Payload payload = new Payload();
        payload.setC("vector");
        payload.setTop(10);
        payload.setLang("ru");
        payload.setFormat("json");
        payload.setForms(1);
        payload.setScores(0);

        List<String> similarStrings = new ArrayList<>();
        for (int i = 0; i < firstList.size(); i++) {
            String wordOfFirstList = firstList.get(i);

            List<String> stringsFirstList = Arrays.asList(wordOfFirstList.split(" "));

            if (stringsFirstList.size() == 1) {
                for (int j = 0; j < secondList.size(); j++) {
                    String wordOfSecondList = secondList.get(j);
                    if (wordOfFirstList.equals(wordOfSecondList)) {
                        if (!similarStrings.contains(wordOfFirstList + ":" + wordOfSecondList)) {
                            similarStrings.add(wordOfFirstList + ":" + wordOfSecondList);
                        }
                        i++;
                        break;
                    } else {
                        Map<String, List> synAndForms = getSynonymsAndForms(payload);
                        List<String> forms = (List<String>) synAndForms.get("forms").get(0);
                        for (int k = 0; k < forms.size(); k++) {
                            String form = forms.get(k);
                            if (form.equals(wordOfSecondList)) {
                                if (!similarStrings.contains(wordOfFirstList + ":" + wordOfSecondList)) {
                                    similarStrings.add(wordOfFirstList + ":" + wordOfSecondList);
                                    i++;
                                    break;
                                }
                            }
                        }
                        List<String> synonyms = (List<String>) synAndForms.get("synonyms").get(0);
                        for (String synonym : synonyms) {
                            if (synonym.equals(wordOfSecondList)) {
                                if (!similarStrings.contains(wordOfFirstList + ":" + wordOfSecondList)) {
                                    similarStrings.add(wordOfFirstList + ":" + wordOfSecondList);
                                    i++;
                                    break;
                                }
                            }
                        }
                    }
                }
            } else {


                for (int j = 0; j < secondList.size(); j++) {
                    String wordOfSecondList = secondList.get(j);
                    List<String> stringsSecondList = Arrays.asList(wordOfSecondList.split(" "));


                    if (wordOfFirstList.equals(wordOfSecondList)) {
                        if (!similarStrings.contains(wordOfFirstList + ":" + wordOfSecondList)) {
                            similarStrings.add(wordOfFirstList + ":" + wordOfSecondList);
                        }
                        i++;
                        break;
                    } else if (wordOfFirstList.contains(wordOfSecondList) || wordOfSecondList.contains(wordOfFirstList)) {
                        if (!similarStrings.contains(wordOfFirstList + ":" + wordOfSecondList)) {
                            similarStrings.add(wordOfFirstList + ":" + wordOfSecondList);
                        }
                        i++;
                    } else {
                        List<String> words = removePrepositions(new ArrayList<>(stringsFirstList));

                        payload.setQuery(wordOfFirstList);


                        Map<String, List> synAndForms = getSynonymsAndForms(payload);
                        List<String> forms = (List<String>) synAndForms.get("forms").get(0);
                        for (int k = 0; k < forms.size(); k++) {
                            String form = forms.get(k);
                            if (form.equals(wordOfSecondList)) {
                                if (!similarStrings.contains(wordOfFirstList + ":" + wordOfSecondList)) {
                                    similarStrings.add(wordOfFirstList + ":" + wordOfSecondList);
                                }
                            }
                        }
                        List<String> synonyms = (List<String>) synAndForms.get("synonyms").get(0);
                        for (String synonym : synonyms) {
                            if (synonym.equals(wordOfSecondList)) {
                                if (!similarStrings.contains(wordOfFirstList + ":" + wordOfSecondList)) {
                                    similarStrings.add(wordOfFirstList + ":" + wordOfSecondList);
                                }
                            }
                        }
                    }
                }
            }

        }
        return similarStrings;
    }

    List<String> removePrepositions(List<String> words) {
        List<String> prepositions = new ArrayList<>(Arrays.asList("а-ля", "без", "безо", "благодаря", "близ", "в", "во",
                "вблизи", "ввиду", "вдоль", "взамен", "включая", "вкось", "вкруг", "вместо", "вне", "внизу", "внутри",
                "внутрь", "вовнутрь", "возле", "вокруг", "вопреки", "вослед", "вперед", "впереди", "вроде", "вслед",
                "вследствие", "выключая", "выше", "для", "до", "за", "из", "изо", "из-за", "изнутри", "из-под",
                "исключая", "к", "ко", "касаемо", "касательно", "кроме", "кругом", "меж", "между", "мимо", "на",
                "над", "надо", "накануне", "наместо", "наперекор", "наперерез", "наподобие", "напротив", "насупротив",
                "насчет", "ниже", "о", "об", "обо", "около", "окрест", "округ", "опричь",
                "от", "ото", "относительно", "перед", "передо", "по", "поверх", "под", "подо", "подле", "подобно",
                "по-за", "позади", "позадь", "помимо", "по-над", "поперёк", "посереди", "посередине",
                "после", "посередь", "посреди", "посредине", "посредством", "превыше", "пред", "предо",
                "прежде", "при", "про", "промеж", "промежду", "против", "путем", "ради", "с", "со",
                "сверх", "сверху", "свыше", "середь", "сзади", "сквозь", "снизу", "согласно", "сообразно",
                "соответственно", "соразмерно", "спереди", "спустя", "среди", "средь", "супротив", "у", "через", "чрез"));
        List<String> wordsWithoutPrepositions = new ArrayList<>();
        for (String word : words) {
            if (!prepositions.contains(word)) {
                wordsWithoutPrepositions.add(word);
            }
        }
        return wordsWithoutPrepositions;

    }

}
