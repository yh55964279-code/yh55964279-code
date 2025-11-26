# Custom Enchantment Example (Paper/Spigot)

이 저장소는 **Minecraft Paper 1.21.4** 서버용 번개 커스텀 인첸트와 표 기반 각인(시길) 세트를 제공하는 예제 플러그인입니다. 다이아/네더라이트 검에 "Lightning" 인첸트가 부여되어 있으면 몹을 타격할 때 번개를 소환하고 피해를 추가합니다. 오른쪽 클릭 시에는 검기(블레이드 웨이브) 파티클이 전방으로 뻗으며 주변 몹에게 추가 피해를 줍니다. 추가로, 스크린샷으로 전달된 표 내용을 그대로 옮긴 8종의 각인을 만들어 `sigil` 명령으로 무기에 부여할 수 있습니다.

### Paper 1.21.4 대응 사항
- 모든 커스텀 인첸트(`Lightning`, 각종 Sigil)는 `translationKey()`, `displayName()`, `description()` 등 1.20+ API에서 요구하는 메서드를 구현합니다.
- 등록 로직은 Paper의 정식 `RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT)` API를 사용하여 런타임에 커스텀 인첸트를 등록하므로 `Enchantment.registerEnchantment()` 같은 삭제된 메서드를 더 이상 호출하지 않습니다.
- 속성 보정은 `Attribute.MAX_HEALTH`, `Attribute.MOVEMENT_SPEED` 등 최신 상수를 사용하고 Adventure 텍스트(`Component`) 기반 메시지를 활용합니다.

## 구성
- 메인 플러그인: `com.example.customenchant.CustomEnchantPlugin`
- 커스텀 인첸트: `LightningEnchantment`
- 이벤트 리스너: `LightningStrikeListener` (타격 시 번개 + 레벨당 추가 피해)
- 이벤트 리스너: `SwordAuraListener` (오른쪽 클릭 시 검기 파티클로 전방 범위 공격)
- 각인 매니저: `SigilManager`, `SigilEffectListener`, `SigilCommand`
- 플러그인 메타: `src/main/resources/plugin.yml`
- 커맨드: `/lightningenchant [level]` (들고 있는 검에 커스텀 인첸트 부여, 기본 1레벨)
- 커맨드: `/sigil list` / `/sigil <id> [level]` (표에 있는 각인 부여 또는 목록 확인)

## 표 기반 각인 목록
`/sigil list` 명령을 입력하면 아래 각인을 모두 확인할 수 있습니다.

| 장비 | ID | 이름 | 최대 레벨 | 효과 요약 |
| --- | --- | --- | --- | --- |
| 갑옷 | `growth` | 생장 | V | 장착한 갑옷의 레벨 합만큼 최대 체력이 +1 (0.5 하트)씩 증가합니다. |
| 갑옷 | `impulse` | 충동 | V | 이속 +0.4%p × 레벨, 공속 +0.6%p × 레벨, 대신 최대 체력 −0.4%p × 레벨. |
| 갑옷 | `endurance` | 인내 | V | 받는 피해 −1.4%p × 레벨, 최대 체력 +(레벨−1), 가하는 피해 −10%p × 레벨. |
| 갑옷 | `reuse` | 재사용 | V | 피해를 받은 직후 최종 피해 × (3% × 레벨)을 즉시 회복(소수점 버림). |
| 방패 | `iron_wall` | 철벽 | V | 방패를 손에 쥔 동안 레벨당 4%씩 추가 피해 감소. (양손 착용 시 가장 높은 수치만 적용) |
| 방패 | `fortification` | 요새화 | I | 방패를 들면 이동 속도 50% 감소 대신 25% 추가 피해 감소. 주 무기 손/보조 손 모두 적용. |
| 근거리 | `strife` | 투쟁심 | V | 무기에 부여 시 가하는 피해 +3%p × 레벨. |
| 근거리 | `madness` | 광기 | I | 체력이 70% 미만일 때 남은 체력 비율과 (피해÷현재 체력)만큼 추가 피해, 70% 이상일 땐 피해 50% 감소. |

## 빌드 방법 (JAR 생성)
1. **Java 21+** 과 **Gradle 8+**가 설치된 환경에서 실행하세요. (네트워크에서 Paper 1.21.4 API를 내려받아야 합니다.)
2. 프로젝트 루트에서 다음을 실행합니다.
   ```bash
   gradle build
   ```
3. 결과 JAR은 `build/libs/custom-enchant-plugin-1.0.0.jar`에 생성됩니다. 해당 파일을 서버 `plugins/` 폴더에 넣고 서버를 재시작하세요.

> 참고: 네트워크가 제한된 환경에서는 Paper API와 Gradle 배포본을 내려받지 못해 `gradle build`가 실패할 수 있습니다. 이 경우 네트워크 허용 후 다시 시도하거나 로컬에 캐시된 Gradle/의존성을 사용하세요.

## 인게임 사용법
1. 권한: `customenchant.lightning`, `customenchant.sigil` (기본 OP). 필요하면 퍼미션 플러그인에서 부여하세요.
2. 원하는 검(다이아/네더라이트)을 손에 들고 `/lightningenchant [레벨]` 명령을 실행합니다. 레벨을 생략하면 1레벨이 적용됩니다.
3. `sigil list`로 각인 효과를 확인하고, `/sigil <id> [레벨]` 명령으로 표에 있는 각인을 장비에 부여합니다.
4. Lightning 무기로 몹을 타격하면 번개가 소환되고 레벨에 따라 추가 피해가 들어갑니다.
5. 오른쪽 클릭을 하면 검기(파티클 웨이브)가 전방으로 10블록 뻗으며 맞닿은 몹에게 추가 피해를 줍니다. 쿨다운은 2초입니다.

## Python 통계 헬퍼 패키지
루트에 추가된 `simple_stats` 패키지는 외부 의존성 없이 기본적인 기술 통계를 계산하는 경량 도구입니다. `mean`, `median`, `mode`,
`variance`, `standard_deviation`, `summary` 함수를 제공합니다.

### 사용법
1. 프로젝트 루트에서 파이썬 인터프리터를 실행합니다.
   ```bash
   python
   ```
2. 필요한 함수를 임포트하고 사용합니다.
   ```python
   from simple_stats import summary, mean

   summary([1, 2, 2, 5])
   # {'count': 4, 'min': 1.0, 'max': 5.0, 'mean': 2.5, 'median': 2.0,
   #  'mode': [2.0], 'variance': 2.25, 'standard_deviation': 1.5}

   mean({10, 20, 30})
   # 20.0
   ```

3. 유닛 테스트는 다음 명령으로 실행할 수 있습니다.
   ```bash
   python -m unittest discover -s tests -p "test_*.py"
   ```
