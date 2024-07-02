package com._A0508.__508.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedList;
import java.util.HashSet;
import java.util.List;


@Service
public class AverageCalculatorService {

    private final LinkedList<Integer> window = new LinkedList<>();
    private final HashSet<Integer> uniqueNumbers = new HashSet<>();
    private final int WINDOW_SIZE = 10;

    private final String accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJNYXBDbGFpbXMiOnsiZXhwIjoxNzE5OTAwNzQ5LCJpYXQiOjE3MTk5MDA0NDksImlzcyI6IkFmZm9yZG1lZCIsImp0aSI6IjMyYTBmM2ExLWNkZTUtNDRmNi1hYWMyLTZkY2UzOGE0YjE5YyIsInN1YiI6ImR1cmdhcmFvYW5kZTlAZ21haWwuY29tIn0sImNvbXBhbnlOYW1lIjoiU2VzaGFkcmkgUmFvIEd1ZGxhdmFsbGVydSBFbmdpbmVlcmluZyBDb2xsZWdlIiwiY2xpZW50SUQiOiIzMmEwZjNhMS1jZGU1LTQ0ZjYtYWFjMi02ZGNlMzhhNGIxOWMiLCJjbGllbnRTZWNyZXQiOiJyTmpST2ZqbllsRkJSdXVqIiwib3duZXJOYW1lIjoiQW5kZSBCdWxsaSBEdXJnYSBSYW8iLCJvd25lckVtYWlsIjoiZHVyZ2FyYW9hbmRlOUBnbWFpbC5jb20iLCJyb2xsTm8iOiIyMTQ4MUEwNTA4In0.1ThM9ib-LOagPfDEDIMXuCgk10-TGRgtlsROWwn_bWw";
    public String calculateAverage(String numberId) {
        if (!numberId.matches("[pfer]")) {
            return "Invalid number ID";
        }

        List<Integer> numbersFromServer = fetchNumbersFromServer(numberId);
        List<Integer> windowPrevState = new LinkedList<>(window);
        updateWindow(numbersFromServer);
        List<Integer> windowCurrState = new LinkedList<>(window);
        double avg = window.stream().mapToInt(Integer::intValue).average().orElse(0.0);

        return String.format("{\"numbers\": %s, \"windowPrevState\": %s, \"windowCurrState\": %s, \"avg\": %.2f}",
                numbersFromServer.toString(), windowPrevState.toString(), windowCurrState.toString(), avg);
    }

    private List<Integer> fetchNumbersFromServer(String numberId) {
        String url;
        switch (numberId) {
            case "p":
                url = "http://20.244.56.144/test/primes";
                break;
            case "f":
                url = "http://20.244.56.144/test/fibo";
                break;
            case "e":
                url = "http://20.244.56.144/test/even";
                break;
            case "r":
                url = "http://20.244.56.144/test/rand";
                break;
            default:
                return List.of(); 
        }
    
        RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());
    
       
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
    
  
        HttpEntity<String> entity = new HttpEntity<>(headers);
    
        try {
            
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
            );
    
            String responseBody = response.getBody();
            System.out.println("Raw response: " + responseBody);
    
            
            ObjectMapper objectMapper = new ObjectMapper();
            NumbersWrapper numbersWrapper = objectMapper.readValue(responseBody, NumbersWrapper.class);
            List<Integer> numbers = numbersWrapper.getNumbers();
            return numbers;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(); 
        }
    }
    


    private void updateWindow(List<Integer> numbers) {
        for (Integer number : numbers) {
            if (uniqueNumbers.add(number)) { 
                if (window.size() == WINDOW_SIZE) {
                    Integer removed = window.poll();
                    uniqueNumbers.remove(removed);
                }
                window.add(number);
            }
        }
    }

    private SimpleClientHttpRequestFactory getClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(500);
        clientHttpRequestFactory.setReadTimeout(500);
        return clientHttpRequestFactory;
    }

    private static class NumbersWrapper {
        private List<Integer> numbers;

        public List<Integer> getNumbers() {
            return numbers;
        }

        public void setNumbers(List<Integer> numbers) {
            this.numbers = numbers;
        }
    }

}
