package com.flowerbed.fertilizer.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class SaveSecretRequest {

  @NotEmpty(message = "Path must not be empty")
  private final String path;

  @NotBlank(message = "Key must not be empty")
  private final String key;

  @NotBlank(message = "Value must not be empty")
  private final String value;
}
