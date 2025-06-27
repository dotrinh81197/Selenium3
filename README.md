# Automation Testing Framework with Selenide

This is an automation testing framework using **Java 21**, **Selenide 7.9.x**, and **TestNG**.  
It is structured for easy scalability, readability, and maintainability.

## ✅ Task-progress

- [x] **Build selenide framework**
- [ ] **Report**
    - [x] Allure Report
    - [ ] Report Portal
- [ ] **Retry failed testcases**
    - [x] Retry immediately after the testcase failed
    - [ ] Retry failed testcases after all testcase done
- [x] Parallel execution
- [x] Cross browser testing
- [ ] Selenium Grid
- [ ] Implement testcase
    - [x] Agoda - TC1
    - [x] Agoda - TC2
    - [ ] Agoda - TC3
    - [ ] Vietjet - TC1
    - [ ] Vietjet - TC2
- [x] CI - Schedule test and send the notification result email

## 🧰 Tech Stack

- **Java 21**
- **Selenide 7.9.x**
- **TestNG**
- **Maven**
- **WebDriverManager**

## 🚀 Getting Started

### Prerequisites

- Java 21 installed
- Maven installed
- Chrome browser
- IntelliJ IDEA (recommended)

### Run Tests

```bash
mvn clean test
```

### Run Specific Test

```bash
mvn -Dtest=GoogleSearchTest test
```

```bash
mvn test -DsuiteXmlFile=testng.xml
```

```bash
mvn test -Denv=agoda -Dbrowser=firefox -DHEADLESS=false -DpageLoadStrategy=eager 

```