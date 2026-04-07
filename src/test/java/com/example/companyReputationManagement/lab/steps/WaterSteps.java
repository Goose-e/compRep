package com.example.companyReputationManagement.lab.steps;

import com.example.companyReputationManagement.controllers.labController.FuncController;
import com.example.companyReputationManagement.controllers.labController.WeatherController;
import com.example.companyReputationManagement.iservice.services.lab.LabService;
import com.example.companyReputationManagement.iservice.services.lab.WeatherService;
import com.example.companyReputationManagement.mapper.LabMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class WaterSteps {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MockMvc mockMvc;

    private MvcResult lastResult;
    private float receivedTemp;
    private final Map<String, Float> rememberedVolumes = new HashMap<>();

    public WaterSteps() {
        WeatherService weatherService = new WeatherService();
        LabMapper labMapper = new LabMapper(weatherService);
        LabService labService = new LabService(labMapper);
        this.mockMvc = MockMvcBuilders.standaloneSetup(
                new WeatherController(labService),
                new FuncController(labService)
        ).build();
    }

    @Given("water APIs are available")
    public void waterApisAreAvailable() {
        Assertions.assertNotNull(mockMvc);
    }

    @When("I request temperature for city {string}")
    public void iRequestTemperatureForCity(String city) throws Exception {
        lastResult = mockMvc.perform(get("/api/weather/{city}", city)).andReturn();
        JsonNode body = objectMapper.readTree(lastResult.getResponse().getContentAsString());
        receivedTemp = (float) body.path("responseEntity").path("temp").asDouble();
    }

    @When("I calculate water norm with weight {int} and time {int} using received temperature")
    public void iCalculateWaterNormWithWeightAndTimeUsingReceivedTemperature(int weight, int time) throws Exception {
        calculate(weight, time, receivedTemp);
    }

    @When("I calculate water norm with weight {int} and time {int} at temperature {int}")
    public void iCalculateWaterNormWithWeightAndTimeAtTemperature(int weight, int time, int tempValue) throws Exception {
        calculate(weight, time, tempValue);
    }

    @When("I remember calculated water volume as {string}")
    public void iRememberCalculatedWaterVolumeAs(String key) throws Exception {
        rememberedVolumes.put(key, getVolumeFromLastResponse());
    }

    @Then("response status should be {int}")
    public void responseStatusShouldBe(int status) {
        Assertions.assertNotNull(lastResult);
        Assertions.assertEquals(status, lastResult.getResponse().getStatus());
    }

    @Then("calculated water volume should be greater than {int}")
    public void calculatedWaterVolumeShouldBeGreaterThan(int value) throws Exception {
        Assertions.assertTrue(getVolumeFromLastResponse() > value);
    }

    @Then("calculated water volume should be {word} {int}")
    public void calculatedWaterVolumeShouldBeComparison(String comparison, int value) throws Exception {
        JsonNode body = objectMapper.readTree(lastResult.getResponse().getContentAsString());
        JsonNode volumeNode = body.path("responseEntity").path("volume");

        if ("absent".equals(comparison)) {
            Assertions.assertTrue(volumeNode.isMissingNode() || volumeNode.isNull());
            return;
        }

        float volume = getVolumeFromLastResponse();
        if ("greater".equals(comparison)) {
            Assertions.assertTrue(volume > value);
        } else {
            Assertions.fail("Unsupported comparison: " + comparison);
        }
    }

    @Then("calculated water volume should be greater than remembered {string}")
    public void calculatedWaterVolumeShouldBeGreaterThanRemembered(String key) throws Exception {
        Float remembered = rememberedVolumes.get(key);
        Assertions.assertNotNull(remembered, "Remembered value was not saved: " + key);
        Assertions.assertTrue(getVolumeFromLastResponse() > remembered);
    }

    private void calculate(float weight, float time, float temp) throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "weight", weight,
                "time", time,
                "temp", temp
        ));

        lastResult = mockMvc.perform(post("/api/water/norm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andReturn();
    }

    private float getVolumeFromLastResponse() throws Exception {
        JsonNode body = objectMapper.readTree(lastResult.getResponse().getContentAsString());
        JsonNode volumeNode = body.path("responseEntity").path("volume");
        Assertions.assertFalse(volumeNode.isMissingNode() || volumeNode.isNull(), "Volume is missing in response");
        return (float) volumeNode.asDouble();
    }
}
