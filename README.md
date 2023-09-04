# Тестовый джоб для доклада на SmartData 2023

## Сборка джобы
```
gradle clean build
```
Полученный артефакт job-example-01.jar, содержит все джобы Example[1-9], которые могут быть запущеныв любом кластере Flink 1.17

## Генерация тестовых данных
```
generate/start_kafka.py # Запуск Zookeper и Kafka в Docker
generate/generator.py # Создаются топики и наполняются рандомными тестовыми данными
generate/stop_kafka.py # Останавливаем Zookeper и Kafka в Docker
```
Тестовая Kafka запускается на localhost:9093


