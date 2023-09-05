# Тестовый джоб для доклада на SmartData 2023

## Сборка джобы
```
gradle clean build
```
Полученный артефакт job-example-01.jar, содержит джобы Example[1-9], которые могут быть запущеныв любом кластере Flink 1.17

## Генерация тестовых данных
_требуется установленный docker-compose_

```
cd ./generate
docker-compose up --force-recreate -d # Запуск Zookeper и Kafka в Docker
./generator.py # Создаются топики и наполняются рандомными тестовыми данными
docker-compose rm --force -v --stop # Останавливаем Zookeper и Kafka в Docker
```
Тестовая Kafka запускается на localhost:9093


