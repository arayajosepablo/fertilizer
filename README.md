# Fertilizer application
HTTP server that has endpoints for creating, reading and deleting secrets related to a user's personal environment. Vault from Hashicorp has been chosen as the backing service for secrets. The server should not allow secrets to be overridden.

## How to run it
Docker is required.

- First checkout the repository
- Then `cd` to `theRepositoryPath/vault` and run ` chmod +x run.sh`. This `.sh` file add some seed data in the vault.
- Next execute `cd ..` to return to root folder.
- Finally, execute `docker-compose up -d`. The first time this will take a few minutes.

Note: the Dockerfile to build the Hashicorp's Vault image is for an AMD64 architecture, if this is going to be run in a M1 processor it should replace `[arch=amd64]` with `[arch=arm64]` in the line `4` of [/vault/Dockerfile](vault/Dockerfile) file.

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
Returns `201` when success, and `403` when it tries to overwrite an existing secret.

### 2. GET secret: localhost:8080/v1/secrets/{path}/{key}
Gets a secret using the `path` and the `key` specified in the URL.

Curl command:
```shell
curl --location --request GET 'localhost:8080/v1/secrets/github/github.oauth2.key'
```
Returns `200` when success, and `404` if the `path` or the `key` do not exist.

### 3. DELETE secret: localhost:8080/v1/secrets/{path}
Deletes a secret using the `path`.

Curl command:
```shell
curl --location --request DELETE 'localhost:8080/v1/secrets/github/'
```
Returns `200` when success, and `500` in case of error.

## Vault UI
Vault UI can be used to check the changes performed through the API. Once docker compose is done open a browser and go to `http://localhost:8200/ui/vault/secrets`, the token is `00000000-0000-0000-0000-000000000000`.