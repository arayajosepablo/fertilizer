package com.flowerbed.fertilizer.controller;

import com.flowerbed.fertilizer.dto.Message;
import com.flowerbed.fertilizer.dto.SaveSecretRequest;
import com.flowerbed.fertilizer.dto.Secret;
import com.flowerbed.fertilizer.service.VaultHandlerService;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/secrets")
@Slf4j
public class SecretsController {

  private final VaultHandlerService vaultHandlerService;

  public SecretsController(final VaultHandlerService vaultHandlerService) {
    this.vaultHandlerService = vaultHandlerService;
  }

  @GetMapping(path = "{path}/{key}")
  public ResponseEntity<Object> getSecret(@PathVariable final String path,
      @PathVariable final String key) {
    try {
      return new ResponseEntity<>(new Secret(this.vaultHandlerService.getSecretFromVault(path, key)),
          HttpStatus.OK);
    } catch (NoSuchElementException e) {
      return new ResponseEntity<>(new Message(e.getMessage()), HttpStatus.NOT_FOUND);
    }
  }

  @DeleteMapping(path = "{path}")
  public ResponseEntity<Message> deleteSecret(@PathVariable final String path) {
    try {
      this.vaultHandlerService.deleteSecret(path);
      return new ResponseEntity<>(
          new Message(String.format("Success! Data deleted (if it existed) at: %s", path)),
          HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(new Message(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping
  public ResponseEntity<Message> saveSecret(@RequestBody @Valid final SaveSecretRequest request) {
    if (this.vaultHandlerService.putSecret(request.getPath(), request.getKey(), request.getValue())) {
      return new ResponseEntity<>(new Message("The secret was saved"), HttpStatus.CREATED);
    }

    return new ResponseEntity<>(new Message("Secrets can not be overwritten"),
        HttpStatus.FORBIDDEN);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
  public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
    final Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });
    return errors;
  }

}
