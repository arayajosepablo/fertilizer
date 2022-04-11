package com.flowerbed.fertilizer.vault;

import com.flowerbed.fertilizer.vault.impl.VaultServiceImpl;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.vault.core.VaultKeyValueOperations;
import org.springframework.vault.core.VaultKeyValueOperationsSupport.KeyValueBackend;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;

public class VaultServiceTest {

  @Mock
  private VaultTemplate vaultTemplate;

  private VaultServiceImpl subject;

  private final static String PATH = "path";
  private final static String KEY = "key";
  private final static String PASSWORD = "password";
  private final static String NON_EXISTING = "none";
  private final static String SECRET = "secret";

  private Map<String, Object> data;
  private VaultResponse response;
  private VaultKeyValueOperations operations;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
    subject = new VaultServiceImpl(vaultTemplate, "secret");
    data = new HashMap<>();
    response = new VaultResponse();
    operations = new VaultKeyValueOperationsMock();
  }

  @Test
  public void getSecret() {
    this.arrangeGetSecretTests();

    Mockito.when(this.vaultTemplate.opsForKeyValue(SECRET, KeyValueBackend.KV_2))
        .thenReturn(operations);

    final String result = this.subject.getSecret(PATH, KEY);

    Assertions.assertEquals(PASSWORD, result, "Gotten password doesn't match with the expected");
    Mockito.verify(this.vaultTemplate).opsForKeyValue(SECRET, KeyValueBackend.KV_2);

  }

  @Test
  public void getSecret_when_not_secret() {
    this.arrangeGetSecretTests();
    final String exception = String.format("There are not secrets in the path: /%s", NON_EXISTING);

    Mockito.when(this.vaultTemplate.opsForKeyValue(SECRET, KeyValueBackend.KV_2))
        .thenReturn(operations);

    final NoSuchElementException thrown = Assertions.assertThrows(NoSuchElementException.class,
        () -> this.subject.getSecret(NON_EXISTING, KEY));

    Assertions.assertEquals(exception, thrown.getMessage(),
        "Actual exception is different to expected one");
    Mockito.verify(this.vaultTemplate).opsForKeyValue(SECRET, KeyValueBackend.KV_2);

  }

  @Test
  public void getSecret_when_not_key() {
    this.arrangeGetSecretTests();
    final String exception = String.format("There's not secret with key: %s", NON_EXISTING);

    Mockito.when(this.vaultTemplate.opsForKeyValue(SECRET, KeyValueBackend.KV_2))
        .thenReturn(operations);

    final NoSuchElementException thrown = Assertions.assertThrows(NoSuchElementException.class,
        () -> this.subject.getSecret(PATH, NON_EXISTING));

    Assertions.assertEquals(exception, thrown.getMessage(),
        "Actual exception is different to expected one");
    Mockito.verify(this.vaultTemplate).opsForKeyValue(SECRET, KeyValueBackend.KV_2);
  }

  @Test
  public void deleteSecret() {
    this.arrangeGetSecretTests();

    Mockito.when(this.vaultTemplate.opsForKeyValue(SECRET, KeyValueBackend.KV_2))
        .thenReturn(operations);

    this.subject.deleteSecret(PATH);

    Mockito.verify(this.vaultTemplate).opsForKeyValue(SECRET, KeyValueBackend.KV_2);
  }

  @Test
  public void putSecret() {
    Mockito.when(this.vaultTemplate.opsForKeyValue(SECRET, KeyValueBackend.KV_2))
        .thenReturn(operations);

    this.subject.putSecret(PATH, KEY, PASSWORD);

    Mockito.verify(this.vaultTemplate).opsForKeyValue(SECRET, KeyValueBackend.KV_2);
  }

  private void arrangeGetSecretTests() {
    data.put(KEY, PASSWORD);
    response.setData(data);
    operations.put(PATH, response);
  }

}
