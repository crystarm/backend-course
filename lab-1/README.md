# Лабораторная работа №1: IO и коллекции

## Вариант 17

Считать имена персонажей из `characters.csv` в `SortedSet<String>` и записать в новый файл имена без дубликатов, отсортированные по алфавиту.

## Решение

- исходный CSV находится в `src/main/resources/characters.csv`;
- `CsvCharacterRepository` читает поле `name` сразу в `TreeSet<String>`;
- `TreeSet` удаляет повторяющиеся имена и поддерживает естественный алфавитный порядок;
- `CharacterNameCsvWriter` сохраняет результат в CSV;
- `Main` связывает чтение и запись, не содержит логики обработки данных.

Добавление элемента в `TreeSet` занимает `O(log n)`, поэтому обработка всех строк занимает `O(n log n)`. Память — `O(n)` для уникальных имён.

## Сборка и проверка

```bash
mvn clean test
mvn package
```

## Запуск

С выходным файлом по умолчанию `result/unique-character-names.csv`:

```bash
java -jar target/lab-1-1.0.0.jar
```

Можно передать другой путь первым аргументом:

```bash
java -jar target/lab-1-1.0.0.jar output/names.csv
```

Формат результата:

```csv
name
Abadango Cluster Princess
Abradolf Lincler
...
```
