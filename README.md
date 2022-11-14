<h1 align="center">Приветствую всех!  
<img src="https://github.com/blackcater/blackcater/raw/main/images/Hi.gif" height="32"/></h1>
<h3 align="center"> <a href="https://vk.com/filkoof" target="_blank">Я</a> рад представить вам командный проект по созданию социальной сети, в котором принимал участие!</h3>

## О проекте

Проект писался в команде из 4 разработчиков под руководством тимлидера по методологии Scram. Написан за три месяца, в течении которых было пройдено шесть спринтов.

### В проекте было использавно:
- Docker
- Sonarqube
- Liquibase
- GitLab CI/CD
- Swagger
- Grafana
- Prometheus
- Kafka
- MapStruct
- Netty-socket.io
- Redis
- Postgres

## Моя роль:
- #### Docker:whale: :
    - Реализовал основной Docker-compose файл с нужными для проекта образами.
    - Реализовал отдельный Docker-compose файл для Sonarqube с целью экономии времени при развертывании основного.
    - В дополнительном проекте zerone mail service реализовал его сборку и пуш на Docker Hub.
- #### Подключил и настроил Sonarqube:mag:.
- #### Настроил gitLab CI/CD:recycle::
    - **В основном проекте:** 
        - *build:* компиляция проекта.
        - *test:* запуск тестов.
        - *sonarqube:* запуск сканирования.
        - *package:* упаковка проекта.
        - *migration:* миграция в бд на сервере(liquibase).
        - *deploy:* деплой на удаленный сервер, так же развертывает основной Docker-compose файл.
    - **В zerone mail service:**
        - *build:* компиляция проекта.
        - *test:* запуск тестов.
        - *package:* упаковка проекта.
        - *deploy:* деплой проекта на DockerHub.
- #### Сгенерировал тестовые данные(посты, юзеры, комментарии):black_nib:
- #### Посты:memo::
    - Реализовал вывод новостной ленты
    - Реализовал редактирование постов
- #### Коментарии:scroll::
    - **Реализовал загрузку файлов, в ходе которой переписал по новой:**
        - *добавление комментариев.* 
        - *вывод комментариев.*
- #### Реализовал настройку получаемых уведомлений:wrench:
- #### Настроил вебсокеты и Redis для хранения сессий.
- #### Уведомления(*с сокетами*):bell:
    - Создание уведомлений, при создании тригерятся связанные с ними ивенты, отправляют получателю уведомление и обновляют его счетчик непрочитынных уведомлений.
    - Вывод уведомлений.
    - Прочтение всех уведомлений или по отдельности.
- ### Мессенджер(*с сокетами*):e-mail::
    - **Вывод всех диалогов, непрочитанных сообщений в каждом и отдельно их общее количество.**
    - **Добавление новых диалогов, при добавлении выводится приветсвенное сообщение.**
    - **Отправка сообщений:**
        - При старте печати тригерится ивент start_typing и выводит всем участникам чата о том что юзер печатает сообщение, 
        по окончанию печати срабатывает ивент stop_typing.
        - При отправке сообщений тригерится ивент send_message, отправляет уведомление получателю и обновляет количество непрочитанных сообщений у получателя.
    - **Вывод сообщений в диалоге.**
    - **Чтение сообщений, при входе в диалог тригерится ивент read_message, читает сообщение и обновляет количество непрочитанных.**
    
# Cсылки:
- <a href="http://195.161.62.32/" target="_blank">Социальная сеть Zerone</a>
    - При отсутсвии желания проходить регистрацию: 
        - login: filkof@mail.ru
        - password: 12345678
    - При желании пощупать диалог с собеседником с двух окон:
        - login: filkoof@mail.ru
        - password: 12345678
        
- <a href="https://www.youtube.com/watch?v=0dMY2Og8_YA&ab_channel=%D0%9F%D1%80%D0%B5%D0%B7%D0%B5%D0%BD%D1%82%D0%B0%D1%86%D0%B8%D1%8F%D0%B8%D1%82%D0%BE%D0%B3%D0%BE%D0%B2%D1%8B%D1%85%D0%BF%D1%80%D0%BE%D0%B5%D0%BA%D1%82%D0%BE%D0%B2Skillbox&t=33m44s" target="_blank">Моя защита проекта</a>
## Итоговый отзыв тимлидера 
 ![Alt-текст](https://sun9-west.userapi.com/sun9-6/s/v1/ig2/1fk4F2teLedfgGntHRWxfKYHCVHwoGJgSDI5xbSwk9pOeBHttKIGo-FaaDQXX93ar1zoF2Vwa7aq3UStbxDlwA3l.jpg?size=960x180&quality=96&type=album "Коммент Тимлида")

## Сертификат о прохождении курса
 ![Alt-текст](https://sun9-west.userapi.com/sun9-64/s/v1/ig2/E7rRSemG12T1tPoYpBB7bv5_eOYp3W_NTMdKSSdXzg9QQNDtXhtnyk_zCcopnpjFP2cSyNLz37AjkKwgLLaHK8xb.jpg?size=1007x1426&quality=96&type=album "Сертификат")
    
<div id="header" align="center">
  <img src="https://media.giphy.com/media/scZPhLqaVOM1qG4lT9/giphy.gif" width="500"/>
</div>
