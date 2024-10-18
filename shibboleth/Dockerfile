FROM rockylinux/rockylinux:9

ARG NODE_ENV=test
ENV NODE_ENV=$NODE_ENV

COPY shibboleth.repo /etc/yum.repos.d/shibboleth.repo
RUN dnf update -y
RUN dnf install sudo shibboleth httpd mod_ssl -y

# /etc/httpd/conf/httpd.conf
COPY resources/${NODE_ENV}/httpd.conf \
     /etc/httpd/conf/

# /etc/httpd/conf.d/ssl.conf
# /etc/httpd/conf.d/proxy.conf
COPY resources/${NODE_ENV}/ssl.conf \
     resources/${NODE_ENV}/proxy.conf \
     /etc/httpd/conf.d/

# /etc/pki/tls/certs/httpd.crt.pem
COPY resources/${NODE_ENV}/httpd.crt.pem \
     /etc/pki/tls/certs/

# /etc/pki/tls/private/httpd.key.pem
COPY resources/${NODE_ENV}/httpd.key.pem \
     /etc/pki/tls/private/

COPY resources/${NODE_ENV}/shibboleth2.xml \
     resources/SWITCHaaiRootCA.crt.pem \
     resources/attribute-map.xml \
     resources/attribute-policy.xml \
     resources/${NODE_ENV}/sp.crt.pem \
     resources/${NODE_ENV}/sp.key.pem \
     /etc/shibboleth/

COPY resources/entrypoint \
     /usr/local/sbin/

COPY resources/${NODE_ENV}/index.html \
     /var/www/html/

EXPOSE 443

ENTRYPOINT ["entrypoint"]