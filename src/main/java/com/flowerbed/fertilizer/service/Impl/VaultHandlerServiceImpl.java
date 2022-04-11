package com.flowerbed.fertilizer.service.Impl;

import com.flowerbed.fertilizer.service.VaultHandlerService;
import com.flowerbed.fertilizer.vault.VaultService;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class VaultHandlerServiceImpl implements VaultHandlerService {

  private final VaultService vaultService;

  public VaultHandlerServiceImpl(final VaultService vaultService) {
    this.vaultService = vaultService;
  }

  @Override
  public String getSecretFromVault(final String path, final String key)
      throws NoSuchElementException {
    return this.vaultService.getSecret(path, key);
  }

  @Override
  public void deleteSecret(final String path) {
    this.vaultService.deleteSecret(path);
  }

  @Override
  public boolean putSecret(final String path, final String key, final String value) {
    if (this.vaultService.isNewSecret(path, key)) {
      this.vaultService.putSecret(path, key, value);
      return true;
    }
    return false;
  }

}
