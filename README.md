<h1 align="center">Социальная сеть Zerone </h1>
<h3 align="center"> <a href="https://vk.com/filkoof" target="_blank">Я</a> рад представить вам командный проект по созданию социальной сети, в котором принимал участие!</h3>

## О проекте

Проект писался в команде из 4 разработчиков под руководством тимлидера по методологии Scram. Написан за три месяца, в течении которых было пройдено шесть спринтов.

### В проекте было использавно:
- Spring:
    - Sprin Boot
    - Spring Security
    - Spring Data
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
- H2
- Lombok

## Моя роль:
- #### Docker:whale: :
    - Реализовал основной Docker-compose файл с нужными для проекта образами.
    - Реализовал отдельный Docker-compose файл для Sonarqube с целью экономии времени при развертывании основного.
    - В дополнительном проекте zerone mail service реализовал его сборку и пуш на Docker Hub.
- #### Подключил и настроил Sonarqube:mag:.
(весь code smels уже исправлен, показан для наглядности)
<img src="https://sun9-north.userapi.com/sun9-84/s/v1/ig2/5tr90WXwBYEVr4BMaSm0i1EeqeP1LFFY4lgHdlAZttD9uZWuzBlE9CccXCSmrQSZotJMcq60sVaW1RDPL5moJhUX.jpg?size=1244x1015&quality=96&type=album" /></h1>

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
 
<img src="https://sun9-north.userapi.com/sun9-88/s/v1/ig2/wf3p4g-ny8RVHYpIC5ii7MnhzNBMYld8DrbddDZMEtKQqgK1LH6GNiiwjoL_3TZYyWrOvG3F3I70N5c67QiT8x4O.jpg?size=1037x190&quality=96&type=album" /></h1>

- #### Вручную написал тестовые данные(посты, юзеры, комментарии):black_nib:
<img src="https://sun9-east.userapi.com/sun9-59/s/v1/ig2/rnzosqO4BiChvZ9yRRsNxuZTMa8zYmFnkkUeOkJGrXCFW1uqYZzXmvx4q1T-p5GaQuTZHpFtTsKvuldyAfXYE6LB.jpg?size=1000x836&quality=96&type=album" /></h1>


- #### Посты:memo::
    - Реализовал вывод новостной ленты
    - Реализовал редактирование постов
    
- #### Коментарии:scroll::
    - **Реализовал загрузку файлов, в ходе которой переписал по новой:**
        - *добавление комментариев.* 
        - *вывод комментариев.*
        
 ![image](https://user-images.githubusercontent.com/100158318/211787068-88bd3a84-d1b3-41e7-a3ef-e9b787cd2102.png)

 
- #### Реализовал настройку получаемых уведомлений:wrench:
 ![image](https://sun9-west.userapi.com/sun9-15/s/v1/ig2/etBwOr773Gnc4pVNL90nUi73hgGNLfN8Bk3j2zhDiOjIAAD2bT349O39Gk6yZQ2k8FSrf9IB_TRvkZTJFCNLjKv_.jpg?size=1010x401&quality=96&type=album)


- #### Настроил вебсокеты и Redis для хранения сессий.
- #### Уведомления(*с сокетами*):bell:
    - Создание уведомлений, при создании тригерятся связанные с ними ивенты, отправляют получателю уведомление и обновляют его счетчик непрочитынных уведомлений.
    - Вывод уведомлений.
    - Прочтение всех уведомлений или по отдельности.
 
 ![image](https://user-images.githubusercontent.com/100158318/211788405-cd35cc36-207e-4bd5-b66e-4d93f2cb83d1.png)

 
- ### Мессенджер(*с сокетами*):e-mail::
    - **Вывод всех диалогов, непрочитанных сообщений в каждом и отдельно их общее количество.**
    - **Добавление новых диалогов, при добавлении выводится приветсвенное сообщение.**
    - **Отправка сообщений:**
        - При старте печати тригерится ивент start_typing и выводит всем участникам чата о том что юзер печатает сообщение, 
        по окончанию печати срабатывает ивент stop_typing.
        - При отправке сообщений тригерится ивент send_message, отправляет уведомление получателю и обновляет количество непрочитанных сообщений у получателя.
    - **Вывод сообщений в диалоге.**
    - **Чтение сообщений, при входе в диалог тригерится ивент read_message, читает сообщение и обновляет количество непрочитанных.**
    
    
 ![image](https://sun9-west.userapi.com/sun9-10/s/v1/ig2/u6J5QdEpOvDxUbrm8GGVg08bN2GwD_5Tu0gTzW7b6ZYhbFKwh_fw28Ztkmqj10I2lU-NizIXk5HoVBioxWKgq5v7.jpg?size=1468x1275&quality=96&type=album)
 
  ![image](https://sun9-north.userapi.com/sun9-85/s/v1/ig2/bpmH718LbD2vzQNpWMdvRY8wA8EUrmgKEiNM0h9RnP2sds2zyG-RHzzXm8p17KVgYF6dGBrEI8ZpzyZrq6F-L24e.jpg?size=1001x1200&quality=96&type=album)
    
# Cсылки:
- <a href="http://zerone-network.ru/" target="_blank">Социальная сеть Zerone</a>
    - При отсутсвии желания проходить регистрацию: 
        - login: filkof@mail.ru
        - password: 12345678
    - При желании пощупать диалог с собеседником с двух окон:
        - login: filkoof@mail.ru
        - password: 12345678
        
- <a href="https://www.youtube.com/watch?v=0dMY2Og8_YA&ab_channel=%D0%9F%D1%80%D0%B5%D0%B7%D0%B5%D0%BD%D1%82%D0%B0%D1%86%D0%B8%D1%8F%D0%B8%D1%82%D0%BE%D0%B3%D0%BE%D0%B2%D1%8B%D1%85%D0%BF%D1%80%D0%BE%D0%B5%D0%BA%D1%82%D0%BE%D0%B2Skillbox&t=33m44s" target="_blank">Моя защита проекта</a>
## Отзыв тимлидера 
 ![Alt-текст](https://sun9-west.userapi.com/sun9-6/s/v1/ig2/1fk4F2teLedfgGntHRWxfKYHCVHwoGJgSDI5xbSwk9pOeBHttKIGo-FaaDQXX93ar1zoF2Vwa7aq3UStbxDlwA3l.jpg?size=960x180&quality=96&type=album "Коммент Тимлида")
