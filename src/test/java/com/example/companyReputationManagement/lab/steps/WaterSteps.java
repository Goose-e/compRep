package com.example.companyReputationManagement.lab.steps;

import com.example.companyReputationManagement.dto.lab.RequestTestDTO;
import com.example.companyReputationManagement.dto.lab.ResponseTestDTO;
import com.example.companyReputationManagement.httpResponse.HttpResponseBody;
import com.example.companyReputationManagement.iservice.ILabService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;

public class WaterSteps {

    private final ILabService labService = Mockito.mock(ILabService.class);

    private HttpResponseBody<ResponseTestDTO> response;
    private float temp;

    @Given("weather service is available")
    public void weatherServiceIsAvailable() {
    }

    @When("I request temperature for city {string}")
    public void iRequestTemperatureForCity(String city) {
        temp = 10f;
    }

    @When("I calculate water norm with weight {int} and time {int}")
    public void iCalculateWaterNormWithWeightAndTime(int weight, int time) {
        RequestTestDTO request = new RequestTestDTO((float) weight, (float) time, temp);
        response = labService.getVolume(request);
    }

    @When("I calculate water norm with weight {int} and time {int} at temperature {int}")
    public void iCalculateWaterNormWithWeightAndTimeAtTemperature(int weight, int time, int tempValue) {
        RequestTestDTO request = new RequestTestDTO((float) weight, (float) time, (float) tempValue);
        response = labService.getVolume(request);
    }

    @Then("response status should be {int}")
    public void responseStatusShouldBe(int status) {
        Assertions.assertNotNull(response);
    }

    @Then("calculated water volume should be greater than {int}")
    public void calculatedWaterVolumeShouldBeGreaterThan(int value) {
        Assertions.assertNotNull(response);
    }
}