# 마스크 알라미 - 마스크 재고 위치 알림 서비스
<br/>

![logo_w](https://github.com/minuxx/mask-alarmi/assets/20331640/fc54febe-458f-47cb-aca8-097b72f62cbe)

<br/>


## 프로젝트 목표
2020년 코로나 사태로, 대부분의 판매처에 마스크 재고가 없어 시민들이 불편을 겪고 있었으며 이로 인해 정부는 마스크 판매처, 재고량 데이터를 제공하는 공적 마스크 API를 공개.
시민들의 불편함 해소를 위해 공적 마스크 오픈 API를 활용해 마스크 재고량과 판매처를 알려주는 위치 기반의 안드로이드 어플리케이션 출시

<br/>

## Preview
<img src="https://github.com/minuxx/mask-alarmi/assets/20331640/5969583f-1ca4-4092-b196-5ab65ce74d23" width="25%"/>

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
- 데이터 스트림 비동기 제어 : Lambda Function + LiveData -> Coroutines + Flow
- DataBinding 적용 : [View - ViewModel - Model] -> [View - DataBinding - ViewModel - Model]
