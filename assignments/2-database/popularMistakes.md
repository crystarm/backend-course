# Популярные ошибки 2 лабораторной

Очень много повторений ошибок из лабораторной 1, пробегитесь [по списку](../1-io-and-collections/popularMistakes.md) еще раз!

## try-with-resources для работы с JDBC

try-with-resources — это конструкция в Java, которая автоматически закрывает ресурсы после использования блока кода.
Она особенно полезна при работе с JDBC, где необходимо закрывать соединения с базой данных, чтобы не привести к утечкам ресурсов.

```java
try (
    Connection conn = connectionFactory.getConnection();
    PreparedStatement ps = conn.prepareStatement(sql);
    ResultSet rs = ps.executeQuery()
) {
    // работа с БД
} catch (SQLException e) {
    // обработка ошибки
}
```

Не колхозьте свой блок finally для закрытия ресурсов!

## При использовании liquibase: сваливание всех changeset в один файл

Посмотрите, как устроен пример [jbcd-3b-tools](../../jbcd-3-database/jbcd-3b-tools).
В ресурсах лежит файл [db.changelog-master.xml](../../jbcd-3-database/jbcd-3b-tools/src/main/resources/db/changelog/db.changelog-master.xml)
который является master-файлом и подключает все остальные изменения в отдельных файлах.
Это позволяет легко поддерживать и расширять базу данных, не переписывая все изменения в одном месте.

## Хардкод параметров подключения к БД

Параметры БД: url, user, password, schema и др — не должны быть жестко записаны в код.
Вынесите их в конфиг или хотя бы в env-переменные.

## Подключение к БД в static блоке 

Использование static блоков для подключения к БД может привести к проблемам с многопоточностью и управлением ресурсами.
Не используйте static там, где он неуместен! Посмотрите примеры в [jbcd-3-database](../../jbcd-3-database) как можно сделать без него.

## Использование дефолтных учеток и схем

Не используйте пользователя postgres и схему public. Создайте своего пользователя и схему.

## Не использование логгера

Если подключаете slf4j к модулю — перестаньте использовать 'System.out.println'! 

```
private static final Logger log = LoggerFactory.getLogger(YourCurrentClass.class);

log.debug("Debug message");
log.info("Hello, world!");
log.warn("Something is not right!");
log.error("Something went wrong!");
```

## Использование устаревших версий

Используйте актуальные версии JDK и внешних сервисов. В проекте приведены актуальные версии инструментов, 
[например тут](../../test-database/docker-compose.yml)

## Обработка исключений

Вижу много работ, в которых при возникновении ошибки БД просто возвращают некоторое дефолтное значение. Например:
```java
@Override
public List<CarEntity> findAll() {
    List<CarEntity> list = new ArrayList<>();
    try (Connection conn = ds.getConnection();
         Statement st = conn.createStatement();
         ResultSet rs = st.executeQuery("SELECT * FROM car")) {
        while (rs.next()) list.add(new CarEntity(rs.getInt("id"), rs.getString("model"), rs.getInt("year")));
    } catch (SQLException e) {
        log.error("Ошибка findAll", e);
    }
    return list;
}
```
**Так делать очень плохо!** Вы просто проглатываете исключения и игнорируете их. Администратор может просто не заметить
неполадку и узнает об инциденте только от недовольных пользователей.

Лучше создайте свое исключение (от RuntimeException) и пробросьте исключение выше, чтобы вызывающий код мог обработать его.

