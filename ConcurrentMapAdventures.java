package com.example;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentMapAdventures {

    public static void main(String[] args) throws Exception {


        Map<String, Integer> concurMap = new ConcurrentHashMap<>();
        concurMap.put("a", 1);
        concurMap.put("b", 2);

        int iterations = 50;
        for (String key : concurMap.keySet()) {
            concurMap.put(key + "-extra", 123);

            Thread.sleep(50);

            if (--iterations <= 0 ) { break; }
        }

        System.out.println("What's in the map?" + concurMap);

        // What's in the map?{a=1, b-extra=123, b=2, a-extra=123, a-extra-extra=123}
        // What's in the map?{a=1, b-extra=123, b=2, a-extra=123, a-extra-extra=123}
        // What's in the map?{a=1, b-extra=123, b=2, a-extra=123, a-extra-extra=123}


    }
}