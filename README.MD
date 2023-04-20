# ПРОЧИТАТЬ ПЕРЕД ИСПОЛЬЗОВАНИМ

### Очередь поднятия микро-сервисов

1) crm
2) hrs
3) brt
4) cdr

### Важно знать

* при первом запуске создастся пользователь с правами менеджера (номер: 79024333333, пароль: admin). Запрос на ендпоинт ../auth/signin вернет jwt токен, который необходим для работы со всеми остальными ендпоинтами.
* при первом запуске будут созданы пользователи с тестовыми данными (пароль test)
* при первом запуске база заполняется четырьмя тарифами при помощи миграции (FlyWay)
* при первом запуске приложения будет создан файл cdr.txt с тестовыми данными CallDataRecord. Для него будет произведена тарификация автоматически
* запрос на эндпоинт manager/billing создает новый файл cdr.txt и производит для него тарификацию
* данные о пользователе кэшируются в CRM и BRT за счет брокера сообщений Apache Kafka (приходит сообщение об обновлении данных в базе)

# Задание
*на основе примера CDR генерировать тестовые данные для проверки корректности работы приложения в разных условиях
* создать и заполнить базу клиентов тестовыми данными (можно использовать стороннее API для генерации данных)добавить в базу таблицу с тарифами с возможностью расширяемости, т.е. организовать поля таким образом, чтобы легко можно было добавить новый тариф•
* пользуясь Spring и Spring Boot поднять локальный сервер с Tomcat, который будет принимать и обрабатывать указанные запросы
# Задание*
* организовать кэш в системах CDR иB RT который будет хранить необходимые данные для работы. В случае изменения значений в базе (например пополнение счета), настроить нотификации соответствующим сервисам для автоматического изменеия значений в кэше. 
* организовать таблицу тарифов с возможностью добавить новый тариф:o82: Тариф Х: Исходящие и входящие звонки абонентам оператора Ромашка -бесплатно. Всем остальным -расчет по тарифу "поминутный".
* дополнить Swagger документацию. Добавить авторизацию в соответствующие методыОписание Swagger`a происходит в соответствии с документацией третей версии -https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.3.md