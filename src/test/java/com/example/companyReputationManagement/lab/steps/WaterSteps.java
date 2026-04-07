package com.example.companyReputationManagement.lab.steps;

import com.example.companyReputationManagement.dto.lab.RequestTestDTO;
import com.example.companyReputationManagement.dto.lab.ResponseTestDTO;
import com.example.companyReputationManagement.httpResponse.HttpResponseBody;
import com.example.companyReputationManagement.iservice.ILabService;
import io.cucumber.java.ru.Допустим;
import io.cucumber.java.ru.Когда;
import io.cucumber.java.ru.Тогда;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;

public class WaterSteps {

    private ILabService labService = Mockito.mock(ILabService.class);

    private HttpResponseBody<ResponseTestDTO> response;
    private float temp;

    @Допустим("сервис погоды доступен")
    public void сервисДоступен() {
        // просто заглушка
    }

    @Когда("я запрашиваю температуру для города {string}")
    public void получитьТемпературу(String city) {
        temp = 10f; // можно замокать
    }

    @Когда("я рассчитываю норму воды с весом {int} и временем {int}")
    public void рассчитатьНорму(int weight, int time) {

        RequestTestDTO request = new RequestTestDTO((float) weight, (float) time);

        response = labService.getVolume(request);
    }

    @Когда("я рассчитываю норму воды с весом {int} и временем {int} при температуре {int}")
    public void рассчитатьСТемпературой(int weight, int time, int tempValue) {

        RequestTestDTO request = new RequestTestDTO((float) weight, (float) time);


        response = labService.getVolume(request);
    }

    @Тогда("статус ответа равен {int}")
    public void проверитьСтатус(int status) {

        if (status == 400) {
            Assertions.assertFalse(response.getErrors().isEmpty());
        } else {
            Assertions.assertTrue(response.getErrors().isEmpty());
        }
    }

    @Тогда("рассчитанный объем воды больше {int}")
    public void проверитьОбъем(int value) {
        Assertions.assertTrue(response.getResponseEntity().volume() > value);
    }
}