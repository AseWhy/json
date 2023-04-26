package io.github.asewhy.json;

import org.json.JSONObject;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JsonGeneratorTest {
    @Test
    public void jsonGenTest() {
        System.out.println(
            JsonGenerator.common()
                .writeStartObject()
                .writeField(
                    "content",
                    "$[tts:{\"content\":\"%account_full_name%!\n\nВо избежание передачи дела в судебное производство просим Вас срочно погасить задолженность %account_debt% рублей, за квартиру по адресу: г. Московский, 3-й мкр., д. 4.\nС уважением, управляющая компания %company_name%.\",\"speed\":1,\"voice\":\"alena\",\"emotion\":\"neutral\"}]$"
                )
            .writeEndObject()
        );
        System.out.println(
            JSONObject.quote(
                "$[tts:{\"content\":\"%account_full_name%!\n\nВо избежание передачи дела в судебное производство просим Вас срочно погасить задолженность %account_debt% рублей, за квартиру по адресу: г. Московский, 3-й мкр., д. 4.\nС уважением, управляющая компания %company_name%.\",\"speed\":1,\"voice\":\"alena\",\"emotion\":\"neutral\"}]$"
            )
        );
    }
}