package com.flowerbed.fertilizer.service;

import java.util.NoSuchElementException;

public interface VaultHandlerService {

  String getSecretFromVault(String path, String key) throws NoSuchElementException;

  void deleteSecret(String path);

  boolean putSecret(String path, String key, String value);

}
