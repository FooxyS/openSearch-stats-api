# openSearch-stats-api

Spring Boot REST API для сбора статистики из OpenSearch

## Описание

Этот проект реализует REST API на Spring Boot, который обращается к OpenSearch и возвращает агрегированные статистические данные по заданному индексу.
Написан multi-stage, можно запустить имея только docker.

## Стэк

- Java 24
- Maven 3.9.9
- Spring Boot 3.5.0
- Docker + Docker Compose

## Структура поддерживаемых данных

Данные, которые загружаются в OpenSearch, должны иметь следующую структуру:

```json
{
  "ups_adv_output_load": 50,
  "ups_adv_battery_temperature": 35,
  "@timestamp": "2024-06-15T12:00:00Z",
  "host": "host-123",
  "ups_adv_battery_run_time_remaining": 200,
  "ups_adv_output_voltage": 230
}
```

## Быстрый старт

1. **Клонируйте репозиторий:**
  ```bash
  git clone <repo-url>
  cd my-api
  ```

2. **Запустите OpenSearch и API через Docker Compose:**
  ```bash
  docker-compose up --build
  ```

3. **Создание индекса:**
  ```bash
  curl -X PUT "http://localhost:9200/test_index?pretty" \
  -H 'Content-Type: application/json' \
    -d '{
      "mappings": {
        "properties": {
          "ups_adv_output_load": { "type": "integer" },
          "ups_adv_battery_temperature": { "type": "integer" },
          "@timestamp": { "type": "date" },
          "host": { "type": "keyword" },
          "ups_adv_battery_run_time_remaining": { "type": "integer" },
          "ups_adv_output_voltage": { "type": "integer" }
        }
      }
    }'
  ```

4. **Добавление данных через _bulk:**
Файл BulkData.ndjson уже подготовлен, но также может быть сгенерирован через консольное приложение функцией bulk.

  ```bash
  curl -X POST "http://localhost:9200/_bulk?pretty" \
  -H "Content-Type: application/x-ndjson" \
  --data-binary "@BulkData.ndjson"
  ```

## Использование

### Получение статистики по индексу

`GET /{index}/stats`

- **Параметры:**  
  `index` — имя индекса в OpenSearch

- **Пример запроса:**
  ```
  GET http://localhost:8080/test_index/stats
  ```

- **Пример ответа:**
  ```json
  {
    "avg_battery": 123.45,
    "max_voltage": 230,
    "unique_hosts": ["host1", "host2"]
  }
  ```

## Доступ к OpenSearch

- URL: http://localhost:9200

## Структура проекта

- `src/main/java/com/example/my_api/MyApplication.java` — основной класс приложения и контроллер
- `BulkData.ndjson` — пример данных для загрузки в OpenSearch
- `docker-compose.yml` — для запуска OpenSearch и API
