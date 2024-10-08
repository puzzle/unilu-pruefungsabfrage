# Uni Luzern Prüfungsabfrage - PoC Login

## Overview

While we are developing the Exam Feedback Tool (EFT) with Shibboleth / SAML integration, we build a docker container for
the Apache server httpd and the Shibboleth Service Provider (SP). The EFT application is behind the Apache server
running in a Spring Boot environment. And finally, Spring Boot uses a Tomcat Server with specific connectors for HTTPS
and AJP (Apache Java Protocol).

![SwitchAAI](doc/SwitchAAI.drawio.png)

In the final environment the EFT application might be integrated within the docker image as well. This would simplify
the build and deployment process.

## Create & Run Service Provider

If not already done, you should set up your local SP. Therefore, clone the repository and following the installation and
configuration instructions of the cloned repo:

```
git clone git@ssh.gitlab.puzzle.ch:cga/docker/unilu-docker-shibboleth-sp.git
```

## Setup SSL for PoC Application

For simplicity, we use the same certificate and key as the Apache httpd server uses. This might be changed when
implementing the real EFT application.

1. `cd src/main/resources/certs/prod`
2. Copy the content of httpd certificate and private key to files `resources/prod/httpd.crt.pem` and
   `resources/prod/httpd.key.pem`.
3. Create the local keystore for SSL:
   `openssl pkcs12 -export -in httpd.crt.pem -inkey httpd.key.pem -name unilu-eft -out httpd.keystore.p12`
   (password see application.yml).
4. Do the similar steps for test environment in `src/main/resources/certs/test` to copy and create the files
   `httpd.crt`, `httpd.key.pem`, and `httpd.test.keystore.p12`. Create the local keystore with:
   `openssl pkcs12 -export -in httpd.crt.pem -inkey httpd.key.pem -name unilu-eft-test -out httpd.test.keystore.p12`
   (password see application-test.yml).
5. Check and/or adapt configuration in `application.yml` or `application-test.yml`, e.g.

         server:
           address: edview.unilu.ch
           port: 8443
           forward-headers-strategy: framework
           ssl:
             key-store-type: PKCS12
             key-store: classpath:certs/prod/httpd.keystore.p12
             key-store-password: <see application.xml>
             key-alias: unilu-eft
             client-auth: want
           ajp:
             protocol: https
             port: 8448
             packet.size: 65536

6. Start `PoC Login (TEST)` or `PoC Login (PROD)` run configuration to see Tomcat initialization on port 8443 (HTTPS)
   and 8448 (HTTPS)
7. Enter [Start Page (Test)](https://edview-test.unilu.ch/home) or [Start Page (Prod)](https://edview.unilu.ch/home)
   in your web browser to start the web application.
8. For the `test` environment you can choose one of the suggested users like `demostudent` (no matriculation number),
   `demouser` (complete user information), or `umlauttest` (check for solved "Umlaut" problem). For the `prod`
   environment you can use `test.ksf@stud.unilu.ch` (password
   see [cryptoplus](https://cryptopus.puzzle.ch/encryptables/7341)).

The alias `edview.unilu.ch` is used for the application host (localhost). Therefore, you might add the alias to
`/etc/hosts` if not already done. The same is true for test environment `edview-test.unilu.ch`.

## TODO:

- [ ] Mock or skip authentication for unit tests
- [ ] Mock or skip authentication for E2E tests
- [ ] http vs. https in tests
