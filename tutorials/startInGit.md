# Большой туториал по началу работы

## Установка необходимого ПО

В первую очередь тебе необходимы 2 вещи: docker и среда разработки.

Установить докер можно [с этой страницы](https://docs.docker.com/get-started/get-docker/). Выбери свою операционную систему
и следуй инструкциям.

В качестве среды разработки можешь использовать:
* Intellij idea (достаточно бесплатной версии)
* OpenIde (это импортозамещенный форк Intellij idea)

При установке ide (или при первом запуске) она предложит тебе установить git (соглашайся) и JDK (выбирай любую 25).
Maven уже будет внутри, отдельной установки не требуется.

Если вы не работали до этого с github/gitlab с вашего пк, то скорее всего не добавили ssh ключ. Как это сделать читай в
[моем туториале](ssh.md) или в официальных доках:
* https://docs.gitlab.com/user/ssh/
* https://docs.github.com/en/authentication/connecting-to-github-with-ssh/generating-a-new-ssh-key-and-adding-it-to-the-ssh-agent

После успешной аутентификации можно будет легко клонировать на компьютер приватные git-репозитории. Проверьте это.
Не забывайте, что проект и лабы должны быть в публичном доступе.

## Создание проекта

Зайдите в web-интерфейс вашего github/gitlab и создайте пустой репозиторий с именем будущего проекта 
(имя проекта/сервиса пишется через дефис маленькими буквами в kebab-case). Например `my-labs-project`.

![githubCreateBlank.png](resources/githubCreateBlank.png)
![gitlabCreateBlank.png](resources/gitlabCreateBlank.png)

Скопируйте ssh-clone ссылку вашего репозитория. Клонируйте этот новый репозиторий на свой компьютер.

## Первые шаги

Создай/открой файл .gitignore в корне проекта и допиши туда:
```gitignore
target/
.idea/
**/dependency-reduced-pom.xml
```
Нажми Ctrl+S (Cmd+S) для применения изменений. Таким образом мы избавились от всех будущих папок target с артефактами сборки и мусора ide.
Ты увидишь, как папка .ide стала рыжей — это нормально. Если что-то внутри не рыжее — удали папку .ide и перезапусти среду.

Создай в корне репозитория файл pom.xml по шаблону и нажми всплывшую справа кнопку maven (см картинку ниже).
* groupId это уникальный идентификатор пространства имён проекта в Java-проектах.
  Обычно записывается в формате домена в обратном порядке, например `org.example` или `com.karandashov`.
  Можно добавить дополнительные слова в качестве рездела. Например, для проекта лабораторок: `com.karandashov.labs`
* artifactId должен совпадать с названием репозитория

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.karandashov.labs</groupId>
    <artifactId>my-labs-project</artifactId>
    <version>1.0.0-SNAPSHOT</version>

    <!--  Это корневой pom, поэтому при сборке не нужно паковать jar - указываем "pom"  -->
    <packaging>pom</packaging>

    <properties>
        <maven.compiler.release>25</maven.compiler.release>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven-surefire-plugin.version>3.2.2</maven-surefire-plugin.version>
        <maven.shade.plugin.version>3.4.1</maven.shade.plugin.version>
        <junit.version>6.0.2</junit.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <!--  JUnit (Jupiter) для тестирования  -->
            <dependency>
                <groupId>org.junit.jupiter</groupId>
                <artifactId>junit-jupiter</artifactId>
                <version>${junit.version}</version>
                <scope>test</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <pluginManagement>
            <plugins>
                <!-- Плагин для запуска тестов -->
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-surefire-plugin</artifactId>
                    <version>${maven-surefire-plugin.version}</version>
                </plugin>
            </plugins>
        </pluginManagement>
    </build>
</project>
```

**Нажми правой кнопкой мыши на созданный pom.xml и нажми "Add as Maven Project".** Таким образом твой pom.xml перестанет
быть просто файлом и станет началом твоего проекта.

![startPom.png](resources/startPom.png)

Таким образом получился сносный стартовый задел.

## Работа с git

Закоммитим текущие изменения. Для этого откроем слева вкладку Commit и выберем все нужные файлы.

![firstCommit.png](resources/firstCommit.png)

Так можно сделать push:
![push1.png](resources/push1.png)
![push2.png](resources/push2.png)
![gitResult.png](resources/gitResult.png)

Вы замечательны!

## Разработка

Пришло время создать первый модуль нашего многомодульного проекта.
Нажмем правой кнопкой на корневую директорию и выберем создать новый модуль.

![createModule.png](resources/createModule.png)

Большинство полей уже будут предзаполнены. Потому что основные вещи наследуются из корневого pom.
Нужно только проконтролировать Name модуля.

![createModule2.png](resources/createModule2.png)

В твоем корневом pom.xml должен автоматически появиться новый блок:
```xml
<modules>
    <module>lab-1</module>
</modules>
```

Приведи свой новый pom.xml твоего нового модуля в божеское состояние (обрати внимание на параметр mainClass ниже, это важно):
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.karandashov.labs</groupId>
        <artifactId>my-labs-project</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>lab-1</artifactId>

    <dependencies>
        <!-- Подключаем JUnit для тестирования -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <!-- Настройка сборки проекта -->
    <build>
        <plugins>
            <!-- Плагин для создания толстого JAR (fat JAR) со всеми зависимостями -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>${maven.shade.plugin.version}</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                        <configuration>
                            <!-- Создаем исполняемый JAR с указанием главного класса -->
                            <transformers>
                                <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                    <!-- ВНИМАНИЕ!!! Тут должен быть твой пакет/Main, это просто пример!!! -->
                                    <mainClass>com.karandashov.labs.Main</mainClass>
                                </transformer>
                            </transformers>
                            <shadedArtifactAttached>true</shadedArtifactAttached>
                            <shadedClassifierName>fat</shadedClassifierName>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>

</project>
```

## Запуск полноценной сборки

![runMaven.png](resources/runMaven.png)

Читай [тут](errors.md) если все сломалось!

## Запуск программы

![runProgram.png](resources/runProgram.png)

Читай [тут](errors.md) если все сломалось!

Поздравляю! Теперь ты умеешь создавать многомодульный проект и можешь приступать к реализации лабораторных!
Помогите вашим одногруппникам!