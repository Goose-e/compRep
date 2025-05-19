FROM postgres:latest

ENV POSTGRES_USER=company_rep_adm
ENV POSTGRES_PASSWORD=comprep
ENV POSTGRES_DB=company_rep

# Копируем скрипты инициализации
COPY ./init.sql /docker-entrypoint-initdb.d/
