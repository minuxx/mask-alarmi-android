# 마스크 알라미 - 마스크 재고 위치 알림 서비스
<br/>

![logo_w](https://github.com/minuxx/mask-alarmi/assets/20331640/fc54febe-458f-47cb-aca8-097b72f62cbe)

<br/>


## 프로젝트 목표
2020년 코로나 사태로, 대부분의 판매처에 마스크 재고가 없어 시민들이 불편을 겪고 있었으며, 이로 인해 정부는 마스크 판매처의 위치, 재고 정보 데이터를 제공하는 공적 마스크 API를 공개.
공적 마스크 오픈 API를 활용해 마스크 재고량과 판매처를 알려주는 안드로이드 어플리케이션를 출시해 시민들의 불편함 해소

<br/>

## Preview
<img src="https://github.com/minuxx/mask-alarmi/assets/20331640/5969583f-1ca4-4092-b196-5ab65ce74d23" width="25%"/>

<br/>
<br/>

## 주요 기능
1. 사용자는 지도 화면에서 중심 좌표 기준 반경 1km 내의 상점 마커들을 볼 수 있다.
2. 사용자가 상점 마커를 클릭하면 상점 상세 정보 바텀 다이얼로그가 노출된다.
4. 사용자가 주소를 검색하면 해당 주소의 중심 좌표 (검색한 주소의 목록 중 가장 가까운 주소)로 지도의 카메라 이동 & 줌인되며 1 기능이 실행된다.
5. 사용자가 내 위치 버튼을 클릭하면 현재 기기의 위치로 중심 좌표가 설정되며 지도의 카메라 이동 & 줌인과 1 기능이 실행된다. 

<br/>

## 개발 환경
- Kotlin based
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