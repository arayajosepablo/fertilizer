package com.flowerbed.fertilizer.vault;

import java.util.NoSuchElementException;

public interface VaultService {

  String getSecret(String path, String key) throws NoSuchElementException;

  void deleteSecret(String path);

  void putSecret(String path, String key, String value);

  boolean isNewSecret(final String path, final String key);

}
