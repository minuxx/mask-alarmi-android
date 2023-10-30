# 마스크 알라미: 마스크 재고 위치 알림 서비스
<br/>

![logo_w](https://github.com/minuxx/mask-alarmi/assets/20331640/fc54febe-458f-47cb-aca8-097b72f62cbe)

<br/>


## 프로젝트 목표
2020년 코로나 사태로 사람들이 마스크를 구하지 못해 불편을 겪는 것을 해소하고자 진행했던 프로젝트이다.<br/>
공적 마스크 오픈 API를 통해 마스크 재고량, 판매처를 알려주는 위치 기반의 안드로이드 어플리케이션 서비스 출시

<br/>

## Preview
<img src="https://github.com/minuxx/mask-alarmi-android/assets/20331640/98208202-7def-4bf3-b18a-c3b61754f558" width="25%"/>
<img src="https://github.com/minuxx/mask-alarmi-android/assets/20331640/e61e4a48-5c51-472b-958f-38daf57e8b57" width="25%"/>

<br/>
<br/>

## 주요 기능
- 지도 화면에서 특정 좌표 중심 반경 1km 내 판매처 마커 렌더링
  - 현재 기기 위치 좌표
  - 검색한 주소 위치 좌표 
- 판매처 마커 클릭 시 상세 정보 바텀 다이얼로그 노출
- '내 위치' 버튼 클릭 시 기기 위치 좌표로 카메라 이동

<br/>

## 개발 환경
*Java 기반의 1.0.0 버전 -> 아래 기술 스택으로 1.1.0 리팩토링

- Kotlin based, Lambda Functions for asynchronous.
- Minimum SDK level 23

### Jetpack
- LiveData
- Lifecycle
- ViewModel
- Room

### Library
- Retrofit2 & OkHttp3
- Naver Map SDK for Android
- Gson

### Architecture
- MVVM Architecture (View - ViewModel - Model)
- Repository Pattern

<br/>

## 리팩토링
- <s>Java -> Kotlin</s>
- <s>아키텍처 재설계 : UI 레이어, 데이터 레이어 나누기</s>
- <s>MVVM 패턴 적용</s>
- <s>Repository 패턴 적용</s>
- 의존성 주입 : Hilt 적용
- 데이터 스트림 비동기 제어 : Lambda Function + LiveData &rarr; Coroutines + Flow
- DataBinding 적용 : [View - ViewModel - Model] &rarr; [View - DataBinding - ViewModel - Model]

<br/>

## Articles
- [4년 전 코드 리팩토링하기 (1)](https://minuxxx.tistory.com/37)
- [4년 전 코드 리팩토링하기 (2) - 아키텍처 재설계](https://minuxxx.tistory.com/39)
- [4년 전 코드 리팩토링하기 (3) - MVVM 패턴](https://minuxxx.tistory.com/42)
- [4년 전 코드 리팩토링하기 (4) - Repository 패턴](https://minuxxx.tistory.com/43)
