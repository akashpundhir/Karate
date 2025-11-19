---

✅ Karate Framework - API Test Automation
Karate Logo

A clean, efficient API test automation framework using Karate.

🔗 Repository: github.com/akashpundhir/KarateFramework

---

## 📌 Overview

This project demonstrates API & Kafka testing using Karate DSL with Java utilities for extended functionality.

✅ API Testing (REST, JSON validation)
✅ Kafka Integration (Produce/Consume messages)
✅ JUnit 5 Test Runner
✅ Maven Build
✅ Logback Logging


> ✅ **No complex Java code needed** – write tests in `.feature` files
> ✅ **Built-in assertions, mocking, and reporting**
> ✅ **Easy to extend and integrate into CI/CD pipelines**

---

## 📁 Project Structure

```
📦 todo/test/java
├── 📂 examples.users
│   ├── KafkaUtils.java       # Kafka producer/consumer helpers
│   └── UsersRunner.java     # JUnit 5 Test Runner
├── 📜 todo-kafka.feature    # Karate feature file (API + Kafka tests)
├── 📜 karate-config.js       # Global config (base URLs, headers)
├── 📜 logback-test.xml       # Logging configuration
📦 JRE System Library [Java-11]
📦 Maven Dependencies
📦 target
├── 📜 pom.xml                # Maven build file
└── 📜 test-output.xml        # Test reports
```

---

## ✨ Features

- ✅ **BDD-style API testing** using `.feature` files
- ✅ **No Java coding required** for basic tests
- ✅ **Built-in assertions** with `match` for JSON/XML validation
- ✅ **Parallel test execution** via JUnit 5
- ✅ **HTML reports** with request/response logs
- ✅ **Reusable configuration** via `karate-config.js`
- ✅ **Easy CI/CD integration** (GitHub Actions, Jenkins, etc.)

---

## 🛠️ Prerequisites

- Java 11 or higher
- Maven 3.6+
- Git (optional, for cloning)

---

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/akashpundhir/KarateFramework.git
cd KarateFramework
```

### 2. Run Tests

#### Run all tests (parallel execution)

```bash
mvn test -Dtest=KarateUhlis
```

> This runs all `.feature` files in parallel using the `KarateUhlis` JUnit runner.

#### Run a specific feature

```bash
mvn test -Dtest=UsersRunner
```

> This runs only the `todo-karate.feature` using the `UsersRunner`.

### 3. View Reports

After execution, open the HTML report:

```bash
open target/karate-reports/karate-summary.html
```

> Reports include:
> - Test status (pass/fail)
> - Request/response details
> - Execution timeline

---

## 📝 Example Test (`todo-karate.feature`)

```gherkin
Feature: Sample API Test - Todo Service

Background:
  * url 'https://jsonplaceholder.typicode.com'
  * header Accept = 'application/json'

Scenario: Get a todo item by ID
  Given path '/todos/1'
  When method get
  Then status 200
  And match response ==
  """
  {
    userId: 1,
    id: 1,
    title: "delectus aut autem",
    completed: false
  }
  """
```

---

## ⚙️ Configuration (`karate-config.js`)

Customize environment settings, base URLs, headers, and more:

```js
function fn() {
  var env = karate.env || 'dev'; // default to 'dev' if not set

  var config = {
    baseUrl: 'https://jsonplaceholder.typicode.com',
    authToken: null
  };

  if (env === 'prod') {
    config.baseUrl = 'https://api.example.com';
  }

  // You can also read from system properties or environment variables
  karate.log('Current environment:', env);
  karate.log('Base URL:', config.baseUrl);

  return config;
}
```

Run with a specific environment:

```bash
mvn test -DargLine="-Dkarate.env=prod" -Dtest=KarateUhlis
```

---

## 📊 Logging (`logback-test.xml`)

Control log level and output format:

```xml
<configuration>
  <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
      <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
    </encoder>
  </appender>
  <root level="INFO">
    <appender-ref ref="STDOUT" />
  </root>
</configuration>
```

---

## 🔄 CI/CD Integration (GitHub Actions Example)

Create `.github/workflows/karate-tests.yml`:

```yaml
name: Karate API Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Set up JDK 11
        uses: actions/setup-java@v3
        with:
          java-version: '11'
          distribution: 'temurin'
      - name: Run Karate Tests
        run: mvn test -Dtest=KarateUhlis
      - name: Upload Test Report
        uses: actions/upload-artifact@v3
        if: always()
        with:
          name: karate-report
          path: target/karate-reports/
```

---

## 🧪 Extending the Framework

### Add a new test suite

1. Create a new folder under `src/test/resources/examples/`, e.g., `posts/`
2. Add a `.feature` file, e.g., `posts-api.feature`
3. Create a JUnit runner (optional):

```java
package examples.posts;

import com.intuit.karate.junit5.Karate;

class PostsRunner {
    @Karate.Test
    Karate testPosts() {
        return Karate.run("posts-api").relativeTo(getClass());
    }
}
```

4. Run with: `mvn test -Dtest=PostsRunner`

---

## 📚 Resources

- 📖 [Official Karate Documentation](https://karatelabs.github.io/karate/)
- 🧪 [Karate Demo Examples](https://github.com/karatelabs/karate/tree/master/karate-demo)
- 💬 [Karate Slack Community](https://karatelabs.slack.com)
- 📺 [Karate YouTube Channel](https://www.youtube.com/c/KarateDSL)

---

## 📄 License

This project is licensed under the **MIT License**.

---

## 🙌 Author

**Akash Pundhir**
🔗 [GitHub](https://github.com/akashpundhir) | 💼 [LinkedIn](https://linkedin.com/in/akashpundhir)

> 💡 **Tip**: Start small, write clear scenarios, and reuse steps with `Background` and `call`.

---

> ⭐ **If this helps, please give the repo a star!** ⭐

---

This README now **matches your exact folder structure**, explains your runners (`KarateUhlis`, `UsersRunner`), and guides users on how to run and extend the framework.
