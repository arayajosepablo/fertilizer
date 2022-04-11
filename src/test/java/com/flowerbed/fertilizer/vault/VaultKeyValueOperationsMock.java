package com.flowerbed.fertilizer.vault;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.vault.core.VaultKeyValueOperations;
import org.springframework.vault.support.VaultResponse;
import org.springframework.vault.support.VaultResponseSupport;

public class VaultKeyValueOperationsMock implements VaultKeyValueOperations {

  private final Map<String, Object> map = new HashMap<>();

  @Override
  public List<String> list(String s) {
    return null;
  }

  @Override
  public VaultResponse get(String s) {
    return (VaultResponse) map.get(s);
  }

  @Override
  public void delete(String s) {

  }

  @Override
  public KeyValueBackend getApiVersion() {
    return null;
  }

  @Override
  public <T> VaultResponseSupport<T> get(String s, Class<T> aClass) {
    return null;
  }

  @Override
  public boolean patch(String s, Map<String, ?> map) {
    return false;
  }

  @Override
  public void put(String s, Object o) {
    map.put(s, o);
  }

}
