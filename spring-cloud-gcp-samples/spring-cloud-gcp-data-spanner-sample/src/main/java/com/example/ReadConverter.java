package com.example;
import com.google.cloud.spanner.Value;
import com.google.gson.Gson;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

@Component
@ReadingConverter
public class ReadConverter implements Converter<String, TraderDetails> {
    @Override
    public TraderDetails convert(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, TraderDetails.class);
    }
}
