FROM ubuntu:16.04

# Install dependencies
RUN apt-get update -y
RUN apt-get install -y apache2
RUN apt-get install -y php libapache2-mod-php

# Install apache and write hello world message
RUN echo "Hello World!" > /var/www/index.html
COPY src/main/resources/* /var/www/html/

# Configure apache
RUN a2enmod rewrite
RUN chown -R www-data:www-data /var/www


ENV APACHE_RUN_USER  www-data
ENV APACHE_RUN_GROUP www-data
ENV APACHE_LOG_DIR   /var/log/apache2
ENV APACHE_PID_FILE  /var/run/apache2/apache2.pid
ENV APACHE_RUN_DIR   /var/run/apache2
ENV APACHE_LOCK_DIR  /var/lock/apache2
ENV APACHE_LOG_DIR   /var/log/apache2

RUN mkdir -p $APACHE_RUN_DIR
RUN mkdir -p $APACHE_LOCK_DIR
RUN mkdir -p $APACHE_LOG_DIR

# CMD ["sed", "-n", "74p", "/etc/apache2/apache2.conf"]
# CMD ["cat", "/etc/apache2/envvars"]
CMD ["/usr/sbin/apache2", "-D",  "FOREGROUND"]