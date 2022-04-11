package com.flowerbed.fertilizer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowerbed.fertilizer.dto.SaveSecretRequest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:application.properties")
public class SecretsControllerIT {

  private static final String BASE_URL = "/v1/secrets/%s";

  final ObjectMapper objectMapper = new ObjectMapper();

  private static final String MESSAGE = "message";

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void getSecret_when_happy_path() throws Exception {
    this.mockMvc.perform(
            MockMvcRequestBuilders.get(String.format(BASE_URL, "integration/fertilizer.key"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("secret", Matchers.is("afr145")));
  }

  @Test
  public void getSecret_when_non_existing_path() throws Exception {
    this.mockMvc.perform(
            MockMvcRequestBuilders.get(String.format(BASE_URL, "non-existing-path/key"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isNotFound())
        .andExpect(MockMvcResultMatchers.jsonPath(MESSAGE, Matchers.is(
            String.format("There are not secrets in the path: /%s", "non-existing-path"))));
  }

  @Test
  public void getSecret_when_non_existing_key() throws Exception {
    this.mockMvc.perform(
            MockMvcRequestBuilders.get(String.format(BASE_URL, "existing/non-existing-key"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isNotFound())
        .andExpect(MockMvcResultMatchers.jsonPath(MESSAGE,
            Matchers.is(String.format("There's not secret with key: %s", "non-existing-key"))));
  }

  @Test
  public void deleteSecret() throws Exception {
    final String path = "to-delete";
    this.mockMvc.perform(
            MockMvcRequestBuilders.delete(String.format(BASE_URL, path))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath(MESSAGE,
            Matchers.is(String.format("Success! Data deleted (if it existed) at: %s", path))));
  }

  @Test
  public void putSecret_when_happy_path() throws Exception {
    this.mockMvc.perform(
            MockMvcRequestBuilders.post(String.format(BASE_URL, ""))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.buildRequest("integration-test", "fertilizer", "!@#az47")))
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath(MESSAGE, Matchers.is("The secret was saved")));
  }

  @Test
  public void putSecret_when_existing_path() throws Exception {
    this.mockMvc.perform(
            MockMvcRequestBuilders.post(String.format(BASE_URL, ""))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.buildRequest("integration", "fertilizer.key", "afr145")))
        .andExpect(MockMvcResultMatchers.status().isForbidden())
        .andExpect(MockMvcResultMatchers.jsonPath(MESSAGE,
            Matchers.is("Secrets can not be overwritten")));
  }

  private String buildRequest(final String path, final String key, final String value)
      throws JsonProcessingException {
    final SaveSecretRequest request = new SaveSecretRequest(path, key, value);
    return objectMapper.writeValueAsString(request);
  }

}
