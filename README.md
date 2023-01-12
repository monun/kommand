# Kommand

[![Java](https://img.shields.io/badge/Java-17-ED8B00.svg?logo=openjdk)](https://www.azul.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.6.21-585DEF.svg?logo=kotlin)](http://kotlinlang.org)
[![Gradle](https://img.shields.io/badge/Gradle-7.5-02303A.svg?logo=gradle)](https://gradle.org)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.monun/kommand-api)](https://search.maven.org/artifact/io.github.monun/kommand-api)
[![GitHub](https://img.shields.io/github/license/monun/kommand)](https://www.gnu.org/licenses/gpl-3.0.html)
[![YouTube](https://img.shields.io/badge/YouTube-각별-red.svg?logo=youtube)](https://www.youtube.com/channel/UCDrAR1OWC2MD4s0JLetN0MA)

### Command DSL for PaperMC Plugin (Brigadier)

---

* #### Features
    * 명령어 분기 지원
    * 인수 분석(Parsing)
    * 명령어 제안 지원 (TabComplete)
* #### Supported minecraft versions
    * 1.17.1
    * 1.18
    * 1.18.1
    * 1.18.2
    * 1.19
    * 1.19.1
    * 1.19.2
    * 1.19.3

---

Paper에는 명령어 실행을 위한 프레임워크가 없습니다.  
다른 프레임워크를 사용하지 않고 독립형으로 명령어 실행 코드를 작성시 다음과 같은 패턴으로 작성하게 됩니다.

```kotlin
class CommandDispatcher : CommandExecutor {
    ...
    override fun onCommand(...) {
        val commandName = args[0]

        if ("first".equals(commandName, true)) {
            // first 명령 처리
        } else if ("second".equals(commandName, true)) {
            // second 명령 처리
        } else {
            // Error
        }
        //when 문으로도 구현 가능
    }
}
...
```

위와 같은 코드는 명령문이 적을땐 간결하지만, 따라붙는 인수나 상수가 늘어나면 코드가 매우 지저분해지고 디버깅에도 어려움을 겪게됩니다.

그래서 명령 처리는 신경쓰지 않고 최종 실행코드만 작성 할 수 있는 프레임워크가 필요해졌습니다.

*Kommand*는 위와 같은 문제를 해결하고 명령문을 보다 직관적인 코드작성을 위한 DSL을 제공합니다.

---
아래와 같은 명령 체계가 있다고 생각해봅시다.

* user
    * create \<username>
    * modify \<user>
        * name \<newName>
        * tag \<newTag>

다음 코드는 Kommand의 DSL을 사용한 코드입니다.

```kotlin
//in JavaPlugin
kommand {
    register("user") {
        then("create") {
            then("name" to string()) { //"name"이라는 이름의 String을 요청합니다.
                executes { context ->
                    val name: String by context
                    createUser(name) //명령어 실행 함수를 통해 실행
                }
            }
        }
        "modify" { // 빠른 명령 작성 - then 함수와 동일
            then("user" to dynamic { ... }) { //dynamic 유저 인수
                then("name") {
                    then("newName" to string()) {
                        executes { context ->
                            val user: User by context
                            val newName: String = it["newName"]
                            setUserName(user, newName)
                        }
                    }
                }
                then("tag") {
                    then("newTag" to string()) {
                        executes {
                            //함수 인수에 의한 타입 추론
                            setUserTag(it["user"], it["newTag"])
                        }
                    }
                }
            }
        }
    }
}
```

분기문을 직접 작성하지 않고도 명령어를 실행 할 수 있는 코드가 완성되었습니다.

사용자가 명령문을 알맞게 작성하면 *executes*블록 내의 코드가 실행됩니다.

---

### Gradle

```kotlin
repositories {
    mavenCentral()
}
```

```kotlin
dependencies {
    implementation("io.github.monun:kommand-api:<version>")
}
```

### plugin.yml

```yaml
name: ...
version: ...
main: ...
libraries:
  - io.github.monun:kommand-core:<version>
```

---

### NOTE

* 라이센스는 GPL-3.0이며 변경 혹은 삭제를 금합니다.

### Contributors

* **[patrick-choe](https://github.com/patrick-choe)**
    * maven central 배포
    * 기본 Argument 일부 작성
    * mojang map 이용 환경 구축
* **[Jhyub](https://github.com/Jhyub)**
    * DslMarker를 이용한 DSL 규칙 향상
* **[pikokr](https://github.com/pikokr)**
    * 1.18.2 지원
