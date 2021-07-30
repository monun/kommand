### 2.4.0

* `KommandSource#broadcast` 함수 추가
* 함수 이름 변경 `KommandArgument#custom -> dynamic`
* 다음 인스턴스를 약한 단일 참조로 변경
  * CommandContext
  * CommandSourceStack
* `KommandArgument#suggests` 함수 추가 (오버로드)
* `KommandContext#source` 필드 추가 (lazy)
* 일부 내부 함수 향상

---
### 2.3.0

* `KommandSource#feedback` 함수 추가
* custom 인수 지원

---

### 2.2.0

* plugin.yml의 libraries 업데이트
* 플러그인 비활성화 시 명령어 제거
* 플러그인 리로드 지원

---

### 2.1.0

* Kotlin 1.5.21
* pom에 dependencies가 빠져있던 버그 수정

---

### 2.0.1

* DslMarker 사용
* 매핑 오류 수정

---

### 2.0.0

* 1.17, 1.17.1 지원
* Brigadier를 이용하는 DSL
* 일부 마인크래프트 인수를 제외하고 구현
