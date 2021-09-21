package com.example;

import com.google.cloud.spanner.Value;
import com.google.gson.Gson;

import org.springframework.core.convert.converter.Converter;

public class WriteConverter implements Converter<TraderDetails, Value> {
    @Override
    public Value convert(TraderDetails details) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(details);
        return Value.json(jsonString);
    }
}
