package com.flowerbed.fertilizer.vault.impl;

import com.flowerbed.fertilizer.vault.VaultService;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.vault.core.VaultKeyValueOperationsSupport.KeyValueBackend;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;

@Service
@Slf4j
public class VaultServiceImpl implements VaultService {

  private final VaultTemplate vaultTemplate;
  private final String vaultSecretPath;

  public VaultServiceImpl(final VaultTemplate vaultTemplate,
      @Value("${vault.secret.path}") final String vaultSecretPath) {
    this.vaultTemplate = vaultTemplate;
    this.vaultSecretPath = vaultSecretPath;
  }

  @Override
  public String getSecret(final String path, final String key) throws NoSuchElementException {

    final VaultResponse response = this.getResponse(path, key);

    if (response.getData().containsKey(key)) {
      return String.valueOf(response.getData().get(key));
    }

    throw new NoSuchElementException(String.format("There's not secret with key: %s", key));
  }

  @Override
  public void deleteSecret(final String path) {
    vaultTemplate.opsForKeyValue(vaultSecretPath, KeyValueBackend.KV_2).delete(path);
  }

  @Override
  public void putSecret(final String path, final String key, final String value) {
    final Map<String, String> map = new HashMap<>();
    map.put(key, value);
    vaultTemplate.opsForKeyValue(vaultSecretPath, KeyValueBackend.KV_2).put(path, map);
  }

  @Override
  public boolean isNewSecret(final String path, final String key) {
    try {
      final VaultResponse response = this.getResponse(path, key);
      return response.getData().isEmpty();
    } catch (NoSuchElementException e) {
      return true;
    }
  }

  private VaultResponse getResponse(final String path, final String key)
      throws NoSuchElementException {

    final VaultResponse response = vaultTemplate
        .opsForKeyValue(vaultSecretPath, KeyValueBackend.KV_2).get(path);

    if (response == null || response.getData() == null) {
      throw new NoSuchElementException(
          String.format("There are not secrets in the path: /%s", path));
    }

    return response;
  }

}
