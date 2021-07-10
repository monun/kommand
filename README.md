# Kommand

### Kotlin으로 작성된 mojang brigadier library
---

* #### Features
    * 명령어 분기 지원
    * 인수 분석(Parsing)
    * 명령어 제안 지원 (TabComplete)
* #### 환경
    * JDK 16
    * Kotlin 1.5.20
    * Paper 1.17

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

위와 같은 코드는 명령문이 적을땐 간결하지만,  
따라붙는 인수나 상수가 늘어나면 코드가 매우 지저분해지고 디버깅에도 어려움을 겪게됩니다.  
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
Kommand.register("user") {
    then("create") {
        then("name" to string()) { //"name"이라는 이름의 String을 요청합니다.
            executes { context ->
                val name: String by context
                createUser(name) //명령어 실행 함수를 통해 실행
            }
        }
    }
    then("modify") {
        then("user" to MyUserArgument()) { //Custom 유저 인수
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
allprojects {
    repositories {
        ...
        mavenCentral()
    }
}
```

```kotlin
dependencies {
    implementation("io.github.monun:kommand-api:<version>")
}
```

### plugins.yml
```yaml
name: ...
version: ...
main: ...
libraries:
  - io.github.monun:kommand:<version>
```
---

### 추가정보

* 라이센스는 GPL-3.0이며 변경 혹은 삭제를 금합니다.