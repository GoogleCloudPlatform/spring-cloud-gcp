package com.example;
import com.google.cloud.spanner.Value;
import com.google.gson.Gson;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

@Component
@WritingConverter
public class WriteConverter implements Converter<TraderDetails, Value> {
    @Override
    public Value convert(TraderDetails details) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(details);
        return Value.json(jsonString);
    }
}
