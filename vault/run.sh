#!/bin/bash

VAULT_RETRIES=5
echo "Vault is starting..."
until vault status > /dev/null 2>&1 || [ "$VAULT_RETRIES" -eq 0 ]; do
	echo "Waiting for vault to start...: $((VAULT_RETRIES--))"
	sleep 1
done

echo "Authenticating to vault..."
vault login token=00000000-0000-0000-0000-000000000000

echo "Initializing vault..."
vault secrets enable -version=2 -path=test kv

echo "Adding entries..."
vault kv put test/integration fertilizer.key=afr145
vault kv put test/to-delete foobar.key=lorem
vault kv put test/existing the.key=lorem

echo "Complete..."
