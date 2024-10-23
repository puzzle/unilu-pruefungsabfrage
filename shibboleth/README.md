# Shibboleth SP with Apache httpd

## Create & Run Docker Image Locally
1. Download the required certificates from [here](https://files.puzzle.ch/apps/files/files/7229088?dir=/Styles%20Uni%20Luzern/Certificates) and place them in `./secrets`. You may need to create the folder if it does not already exist
2. Add aliases for `edview.unilu.ch` and `edview-test.unilu.ch` to your [host file](https://linuxhandbook.com/etc-hosts-file/).  
3. Create and run the docker image using the Docker Compose file `docker compose up -d --no-deps --build shibboleth`.

When you start a session within the docker image you can find the metadata file from `AAI Test` or `SWITCH AAI` under
`/var/cache/shibboleth/` which will be refreshed every hour.

## Generate New Certificates
The following steps can be done for `test` and / or `prod` environment. The commands allow the parameter `test` or
`prod`. The default environment is `test`. See section [Commands](#Commands)s for further information.

1. Create the host certificate for Apache HTTP/S server using the command `httpd-certificate`. The configuration is
   defined in `httpd.crt.conf`. The result is the public certificate `httpd.crt` and its private key file
   `httpd.key.pem`.
2. Create the Shibboleth Service Provider certificate using the command `sp-certificate`. The configuration is defined
   in `sp.crt.conf`. The result is the public certificate `sp.crt.pem` and its private key file `sp.key.pem`. When the
   certificate is provided by the customer you can copy the content of certificate and private key to the placeholder
   files.
3. Now you are ready to start your local docker container, see [here](#create--run-docker-image-locally) how.

To use the self-signed host certificate created in step 1 you should add this certificate to the Java trust store using
the following commands for test or prod environment:

    keytool -importcert -trustcacerts -file resources/prod/httpd.crt.pem -alias unilu-httpd -cacerts
    keytool -importcert -trustcacerts -file resources/test/httpd.crt.pem -alias unilu-httpd-test -cacerts

## Commands

| Command                                           | Parameter                                                | Description                                        |                                        
|---------------------------------------------------|----------------------------------------------------------|----------------------------------------------------|
| docker compose up -d --no-deps --build shibboleth | [test \| prod] (must be adjusted in `docker-compose.yml` | build and start Docker container                   |
| httpd-certificate                                 | [test \| prod]                                           | create the Apache HTTP/S server certificate        |
| sp-certificate                                    | [test \| prod]                                           | create the Shibboleth Service Provider certificate |

## Mandatory Files
The following files are mandatory and need to be supplied via the volume mounts specified in the `docker-compose.yml` file.

| Name                    | Target Dir           | Where to acquire                                                                                                                                                                       |                                        
|-------------------------|----------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| sp.crt.pem              | /etc/shibboleth      | Download from here [here](https://files.puzzle.ch/apps/files/files/7229088?dir=/Styles%20Uni%20Luzern/Certificates) or generate as described [here](#create--run-docker-image-locally) |
| sp.key.pem              | /etc/shibboleth      | Download from here [here](https://files.puzzle.ch/apps/files/files/7229088?dir=/Styles%20Uni%20Luzern/Certificates) or generate as described [here](#create--run-docker-image-locally) |
| SWITCHaaiRootCA.crt.pem | /etc/shibboleth      | Download file from [here](https://files.puzzle.ch/apps/files/files/7229088?dir=/Styles%20Uni%20Luzern/Certificates)                                                                    |
| httpd.crt.pem           | /etc/pki/tls/certs   | Download from here [here](https://files.puzzle.ch/apps/files/files/7229088?dir=/Styles%20Uni%20Luzern/Certificates) or generate as described [here](#create--run-docker-image-locally) |
| httpd.key.pem           | /etc/pki/tls/private | Download from here [here](https://files.puzzle.ch/apps/files/files/7229088?dir=/Styles%20Uni%20Luzern/Certificates) or generate as described [here](#create--run-docker-image-locally) |

## Useful Links

### Tests for TEST environment

- Test link: https://edview-test.unilu.ch/Shibboleth.sso/Session (HTTP Status 200 and message "A valid session was not
  found." expected)
- Status link: https://edview-test.unilu.ch/Shibboleth.sso/Status (HTTP Status 200 and StatusHandler XML document
  expected)
- Login: https://edview-test.unilu.ch/Shibboleth.sso/Login (processing login on AAI Test)
- Logout: https://edview-test.unilu.ch/Shibboleth.sso/Logout (logout from AAI Test)
- Metadata link: https://edview-test.unilu.ch/Shibboleth.sso/Metadata (download Metadata file expected)

### Tests for PROD environment

- Test link: https://edview.unilu.ch/Shibboleth.sso/Session (HTTP Status 200 and message "A valid session was not
  found." expected)
- Status link: https://edview.unilu.ch/Shibboleth.sso/Status (HTTP Status 200 and StatusHandler XML document
  expected)
- Login: https://edview.unilu.ch/Shibboleth.sso/Login (processing login on SWITCH AAI)
- Logout: https://edview.unilu.ch/Shibboleth.sso/Logout (logout from SWITCH AAI)
- Metadata link: https://edview.unilu.ch/Shibboleth.sso/Metadata (download Metadata file expected)

### Apache and SSL

- How to install Apache on Rocky Linux 9: https://www.linuxteck.com/how-to-install-apache-on-rocky-linux/
- mod_ssl on Rocky Linux: https://docs.rockylinux.org/guides/web/mod_SSL_apache/

### Shibboleth Service Provider

- Shibboleth Service Provider: https://shibboleth.atlassian.net/wiki/spaces/SP3/overview?homepageId=2058387896
- Shibboleth RPMs: https://shibboleth.net/downloads/service-provider/RPMS/
- Create an Embedded Certificate: https://help.switch.ch/aai/guides/sp/embedded-certificate/
- JavaHowTo (Shibboleth 3): https://shibboleth.atlassian.net/wiki/spaces/SP3/pages/2067400159/JavaHowTo

### Create Certificate

- Creating a Host Certificate: https://node-security.com/posts/openssl-creating-a-host-certificate/
- Create your own CA: https://arminreiter.com/2022/01/create-your-own-certificate-authority-ca-using-openssl/
- Create CA cert and key: https://www.apachelounge.com/viewtopic.php?t=8891