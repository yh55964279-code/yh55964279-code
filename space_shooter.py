"""pygame로 만든 아케이드 스타일의 세로 스크롤 슈팅 게임.

게임 실행 방법:
    python space_shooter.py

화면을 열 수 없는 환경(예: 헤드리스 서버)에서는 SDL_VIDEODRIVER=dummy 를
설정한 뒤 실행하면 디스플레이 요구 사항을 비활성화할 수 있습니다. 외부 에셋 대신
단순한 색상 도형을 사용해 휴대성을 높였습니다.
"""

from space_shooter.main import main


if __name__ == "__main__":
    main()
