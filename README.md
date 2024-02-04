# Shareit
Приложение позволяет позволяет пользователям добавлять свои личные вещи для аренды или находить нужные вещи для временного использования у других пользователей. Приложение позволяет бронировать вещи и обеспечивает возможность оставлять отзывы о совершенных арендах, что помогает другим пользователям принимать  решения при выборе нужных вещей.

## Стек
- Java 11
- Maven 4
- Spring Boot 2
- Spring Data
- PostgreSQl
- Mockito
- MapStruct
- Lombok
- Docker

## API
Service URL: http://localhost:9090.  
Id текущего пользователя передается в заголовке запроса "X-Sharer-User-Id". 

<details>
  <summary>Пользователи /users</summary>
  <br>
  
- POST /users - создать нового пользователя.
- PATCH /users/{id} - обновить информацию о пользователе id.
- DELETE /users/{id} - удалить пользователя id. 
- GET /users - получить список всех пользователей.
- GET /users/{id} - получить информацию о пользователе id. 
  
</details>

<details>
  <summary>Предметы /items</summary>
  <br>

- POST /items - добавить новую вещь.
- PATCH /items/{itemId} - обновить информацию о вещи id.
- GET /items - получить список вещей, которые создал текущий ползователь. 
- GET /items/{itemId} - получить информацию о вещи id. 
- GET /items/search - получить список вещей, содержащих в названии или описании переданный в параметре запроса поисковой текстовый запрос.
- POST /items/{itemId}/comment - добавить комментарий к вещи itemId от текущего пользователя. 
  
</details>

<details>
  <summary>Аренда /bookings</summary>
  <br>

- POST /bookings - создать новое бронирование.
- PATCH /bookings/{bookingId} - обновить информацию о бронировании id.
- GET /bookings - получение списка всех бронирований текущего пользователя.
- GET /bookings/{bookingId} - получить информацию о бронировании id. 
- GET /bookings/owner - получение списка бронирований для всех вещей текущего пользователя.
  
</details>

<details>
  <summary>Запрос предмета /requests</summary>
  <br>
  
- POST /requests - создать новый запрос
- GET /requests - получить список запросов, созданных текущим пользователем, вместе с данными об ответах на них.
- GET /requests/{bookingId} - получить информацию о запросе id. 
- GET /requests/all - получить список запросов, созданных другими пользователями.

</details>

## Сборка
1. Клонируйте репозиторий:
```Bash
git clone https://github.com/OrlovDeniss/java-shareit.git
```
2. Перейдите в каталог проекта: 
```Bash
cd java-shareit
```
3. Скомпилируйте исходные файлы:
```Bash
mvn clean package
```
4. Запустите проект:
```Bash
docker compose up
```
## Статус проекта
Завершен.
