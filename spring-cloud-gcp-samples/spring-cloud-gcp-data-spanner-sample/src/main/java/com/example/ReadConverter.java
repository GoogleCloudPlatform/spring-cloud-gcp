package com.example;

import com.google.gson.Gson;

import org.springframework.core.convert.converter.Converter;

public class ReadConverter implements Converter<String, TraderDetails> {
    @Override
    public TraderDetails convert(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, TraderDetails.class);
    }
}
