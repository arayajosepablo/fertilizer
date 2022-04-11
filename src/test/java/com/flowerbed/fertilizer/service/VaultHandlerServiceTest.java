package com.flowerbed.fertilizer.service;

import com.flowerbed.fertilizer.service.Impl.VaultHandlerServiceImpl;
import com.flowerbed.fertilizer.vault.VaultService;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class VaultHandlerServiceTest {

  @Mock
  private VaultService vaultService;

  @InjectMocks
  private VaultHandlerServiceImpl subject;

  private final static String PATH = "path";
  private final static String KEY = "key";
  private final static String PASSWORD = "password";
  private final static String NON_EXISTING = "none";

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void getSecretFromVault() {
    Mockito.when(this.vaultService.getSecret(PATH, KEY)).thenReturn(PASSWORD);

    final String result = this.subject.getSecretFromVault(PATH, KEY);

    Assertions.assertEquals(PASSWORD, result, "Actual password is different to expected one");
    Mockito.verify(this.vaultService).getSecret(PATH, KEY);
  }

  @Test
  public void getSecretFromVault_when_non_existing_path() {
    final String exception = String.format("There are not secrets in the path: /%s", NON_EXISTING);

    Mockito.when(this.vaultService.getSecret(NON_EXISTING, KEY))
        .thenThrow(new NoSuchElementException(exception));

    final NoSuchElementException thrown = Assertions.assertThrows(NoSuchElementException.class,
        () -> this.subject.getSecretFromVault(NON_EXISTING, KEY));

    Assertions.assertEquals(exception, thrown.getMessage(),
        "Actual exception is different to expected one");
    Mockito.verify(this.vaultService).getSecret(NON_EXISTING, KEY);
  }

  @Test
  public void getSecretFromVault_when_non_existing_key_in_path() {
    final String exception = String.format("There's not secret with key: %s", KEY);

    Mockito.when(this.vaultService.getSecret(PATH, NON_EXISTING))
        .thenThrow(new NoSuchElementException(exception));

    final NoSuchElementException thrown = Assertions.assertThrows(NoSuchElementException.class,
        () -> this.subject.getSecretFromVault(PATH, NON_EXISTING));

    Assertions.assertEquals(exception, thrown.getMessage(),
        "Actual exception is different to expected one");
    Mockito.verify(this.vaultService).getSecret(PATH, NON_EXISTING);
  }

  @Test
  public void deleteSecret() {
    this.subject.deleteSecret(PATH);

    Mockito.verify(this.vaultService).deleteSecret(PATH);
  }

  @Test
  public void putSecret() {
    Mockito.when(this.vaultService.isNewSecret(PATH, KEY)).thenReturn(true);

    Assertions.assertTrue(this.subject.putSecret(PATH, KEY, PASSWORD), "It should return true");

    Mockito.verify(this.vaultService).isNewSecret(PATH, KEY);
    Mockito.verify(this.vaultService).putSecret(PATH, KEY, PASSWORD);
  }

  @Test
  public void putSecret_when_existing_path() {
    Mockito.when(this.vaultService.isNewSecret(PATH, KEY)).thenReturn(false);

    Assertions.assertFalse(this.subject.putSecret(PATH, KEY, PASSWORD), "It should return false");

    Mockito.verify(this.vaultService).isNewSecret(PATH, KEY);
    Mockito.verify(this.vaultService, Mockito.never()).putSecret(PATH, KEY, PASSWORD);
  }

}
