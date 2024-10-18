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

COPY resources/${NODE_ENV}/shibboleth2.xml \
     resources/attribute-map.xml \
     resources/attribute-policy.xml \
     /etc/shibboleth/

COPY resources/entrypoint \
     /usr/local/sbin/

COPY resources/${NODE_ENV}/index.html \
     /var/www/html/

EXPOSE 443

ENTRYPOINT ["entrypoint"]