# Uni Luzern Prüfungsabfrage - PoC Login

## Setup SSL for EFT application

1. `cd src/main/resources/certs`
1. `openssl pkcs12 -export -in unilu-sp.crt -inkey unilu-sp-pk.pem -name unilu-eft -out unilu-eft.p12`
   (password see application.yml)
1. Check and/or adapt configuration in `application.yml` or `application-test.yml`

       server:
          address: edview.unilu.ch
          port: 8443
          forward-headers-strategy: framework
          ssl:
             key-store-type: PKCS12
             key-store: classpath:certs/unilu-eft.p12
             key-store-password: uni-lu-eft-2024
             key-alias: unilu-eft

1. Start `PoC Login (TEST)` or `PoC Login (PROD)` run configuration to see Tomcat initialization on port 8443 (HTTPS)
1. Enter [https://edview-test.unilu.ch:8443](https://edview-test.unilu.ch:8443)
   or [https://edview.unilu.ch:8443](https://edview.unilu.ch:8443) in your web browser to start the web
   application.

The alias `edview.unilu.ch` is used for the application host (localhost). Therefore, you might add the alias to
`/etc/hosts` if not already done.

## Docker

### Build Image

`docker build . -t uni-luzern`

### Run Image

`docker run --rm  -v ./static:/resources -p 8080:8080 uni-luzern:latest`
