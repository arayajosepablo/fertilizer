# Fertilizer application
HTTP server that has endpoints for creating, reading and deleting secrets related to a user's personal environment. Vault from Hashicorp has been chosen as the backing service for secrets. The server should not allow secrets to be overridden.

## How to run it
Docker is required.
- First checkout the repository
- Then `cd` to `theRepositoryPath/vault` and run ` chmod +x run.sh`. This `.sh` file add some seed data in the vault.
- Finally execute `docker-compose up -d`. The first time this will take a few minutes.

## Endpoints
### 1. POST secret: localhost:8080/v1/secrets/
Adds a new secret in the Vault. Secrets can not be overwritten, so if the secret already exist an error message will be sent. This is validated using `path` property, so it should be unique.

Payload:
```json
{
    "path": "github",
    "key": "github.oauth2.key",
    "value": "foobar"
}
```

Curl command:
```shell
curl --location --request POST 'localhost:8080/v1/secrets/' \
--header 'Content-Type: application/json' \
--data-raw '{
    "path": "github",
    "key": "github.oauth2.key",
    "value": "foobar"
}'
```

### 2. GET secret: localhost:8080/v1/secrets/{path}/{key}
Gets a secret using the `path` and the `key` specified in the URL.

Curl command:
```shell
curl --location --request GET 'localhost:8080/v1/secrets/github/github.oauth2.key'
```

### 3. DELETE secret: localhost:8080/v1/secrets/{path}
Deletes a secret using the `path`.

Curl command:
```shell
curl --location --request DELETE 'localhost:8080/v1/secrets/github/'
```