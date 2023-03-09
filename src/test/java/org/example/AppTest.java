package org.example;

import static org.junit.Assert.assertTrue;

import lombok.RequiredArgsConstructor;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class AppTest
{

    RService rService = new RService();

    @Test
    public void authTest()
    {
        String name = "User";
        String pass = "password";
        RequestCredentials requestCredentials = new RequestCredentials(name, pass);
        rService.login(requestCredentials);

        assertTrue( true );
    }

    @Test
    public void getSynonymsTest(){
        String name = "User";
        String pass = "password";
        RequestCredentials requestCredentials = new RequestCredentials(name, pass);
        rService.login(requestCredentials);

        Payload payload = new Payload();
        payload.setC("vector");
        payload.setQuery("цемент");
        payload.setTop(10);
        payload.setLang("ru");
        payload.setFormat("json");
        payload.setForms(1);
        payload.setScores(0);

        Map synonyms = rService.getSynonymsAndForms(payload);

    }

    @Test
    public void getResultTest(){
        String name = "User";
        String pass = "password";
        RequestCredentials requestCredentials = new RequestCredentials(name, pass);
        rService.login(requestCredentials);

        List<String> firstList = new ArrayList<>(Arrays.asList("гвоздь", "шуруп", "краска синяя", "ведро для воды"));
        List<String> secondList = new ArrayList<>(Arrays.asList("краска","корыто для воды", "шуруп 3х1.5"));

        List<String> firstList1 = new ArrayList<>(Arrays.asList("Бетон с присадкой"));
        List<String> secondList1 = new ArrayList<>(Arrays.asList("Цемент"));

        List<String> firstList2 = new ArrayList<>(Arrays.asList("Бетон с присадкой"));
        List<String> secondList2 = new ArrayList<>(Arrays.asList("присадка для бетона"," доставка"));

        List<String> similarStrings = rService.getSimilarStrings(firstList, secondList);
        List<String> similarStrings1 = rService.getSimilarStrings(firstList1, secondList1);
        List<String> similarStrings2 = rService.getSimilarStrings(firstList2, secondList2);

    }
}
