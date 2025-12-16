# Space Shooter (파이썬 비행 슈팅 게임)

간단한 2D 비행 슈팅 게임을 파이썬과 pygame으로 구현했습니다. 외부 이미지 없이 도형으로 기체를 그리기 때문에 추가 리소스가 필요 없습니다. 게임 로직은 `space_shooter` 패키지로 모듈화되어 있어 플레이어, 탄환, 적, 게임 루프를 개별 파일로 관리합니다.

## 실행 방법
1. pygame을 설치합니다.
   ```bash
   pip install pygame
   ```
2. 게임을 실행합니다. (두 방법 모두 동일하게 동작합니다.)
   ```bash
   python space_shooter.py
   # 혹은
   python -m space_shooter
   ```
3. 키 조작
   - 이동: 방향키 ← ↑ → ↓
   - 공격: 스페이스바
   - 게임 오버 후 재시작: R 키

### 화면을 열 수 없는 환경에서 실행하기
GUI를 띄울 수 없는 환경에서는 다음처럼 가상 디스플레이 드라이버를 사용해 실행할 수 있습니다.
```bash
SDL_VIDEODRIVER=dummy python space_shooter.py
```

게임을 종료하려면 창을 닫거나 `Ctrl+C`를 누르세요.
