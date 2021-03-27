# SilverWalk 👵🏻🧓🏻🏃🏻‍♀️🏃🏻

## 산책을 통해 신체적, 정신적 건강 증진과 활력을 찾을 수 있는 산책 독려 서비스

### Overview

#### Duration : 2020.12.? ~ 2021.03.31

#### Team member 💁🏻‍♀️💁🏻
* **Designer** : 이현승, 최지원
* **Developer** : 이소연, 최다함

#### Technology of Use

* Android
* Kotlin
* Firebase
* Google Cloud Platform

---

### Key Features

* 간소화된 UX/UI를 통한 노인 친화 서비스
* 개인 건강 데이터와 실시간 위치를 활용한 노인 맞춤형 산책 추천 서비스
* 산책 기록 공유를 통한 로컬 기반의 커뮤니티 서비스

---

### Expected Results

* Community Mapping
    * 자연스러운 참여 과정에서 커뮤니티 구성원 간의 소통이 더욱 원활해지고 활발
    * 지도를 만들어가는 과정에서 관심있는 일정 주제와 이슈를 중심으로 주민들간의 소통 촉진
* 신체적・정신적 건강 증진
* 여가 활동을 통한 생활 만족도 향상
    * 노인들의 생활 만족도에 통계적으로 유의미한 영향을 미치는 여가 활동은 신체적 여가 활동 유형으로 나타남
    * TV 앞에서 무료하게 일상을 보내는 대신 여가 활동을 통해 노인의 삶을 질적으로 향상시킬 수 있음

# Development Part


This part explains the technical structure and design of this app.


## Work Flow
![프레젠테이션1](https://user-images.githubusercontent.com/7011030/112716925-a8e75d00-8f2c-11eb-9ba9-af127ed075de.png)


## Fire Store Structure
![firebase](https://user-images.githubusercontent.com/7011030/112715359-0b3b6000-8f23-11eb-9a8f-d45a74cffc36.png)


 In the case of Image Path and User_thumbnail_path, FireStorage URL information is included, and the ImageURL is retrieved from within the app and displayed in the ImageView.


 location contains information about locations that can be selected as a walking spot.  The type contains information on the type of mission such as walk, fast walk, drink coffee, etc., and the name of the location that can be connected to the link.


location contains information about locations that can be selected as a walking spot.  The type contains information on the type of mission such as walk, fast walk, drink coffee, etc., and the name of the location that can be connected to the link.  share contains walking sharing data that can be checked in the neighborhood tab.  User's personal information (age, gender...), user's walking record, and today's mission list are included in user. By using the time(Timestamp) and time_second of history, a query is requested to the fit api to load biometric data in real time.


## Default App Architecture Design Overview

![MVVM](https://user-images.githubusercontent.com/7011030/112715836-1a6fdd00-8f26-11eb-87f3-97e77619d22d.png)
The overall structure of this app is taken from the login activity that exists in the android template. Based on the template, the structure was modified by using lambda expressions to request data from firebase and update live data.  
In addition, there were unique values (firestorageurl, google fit value) that had to be found by referring to the data in Firebase, which was unavoidably implemented in a double callback method in the datasource, and then compared the size of the document to determine the end point. did.


 ### *Example)*


> AchievementForm

    data class AchievementForm(  
        val totalSteps: Long? = null,  
        ...
    )

> Fragment

    private lateinit var achievementViewModel: AchievementViewModel
    
    achievementViewModel.achievementForm.observe(viewLifecycleOwner, Observer {
    	...
    })

> ViewModel

	class AchievementViewModel(private val achievementRepository: AchievementRepository) : ViewModel() {
	
	    private val _achievementForm = MutableLiveData<AchievementForm>()  
	    val achievementForm: LiveData<AchievementForm> = _achievementForm
	    ...
	    fun findAllHistory(context: Context) {  
	        achievementRepository.findAllHistory(context) {  
	      if (it is Result.Success) {  
	                _achievementHistoryForm.value = it.data  
	            }  
	        }  
	    }
	    ...
    }

> AchievementRepository

    class AchievementRepository(val dataSource: AchievementDataSource) {
    	...
    	fun findAllHistory(context: Context, result: (Result<AchievementHistoryForm>) -> Unit) {  
            dataSource.findAllHistory(context, fitnessOptions, result)  
        }  
        ...
    }

> AchievementHistory

    class AchievementDataSource {
        fun findAllHistory(  
            context: Context,  
            fitnessOptions: FitnessOptions,  
            result: (Result<AchievementHistoryForm>) -> Unit  
        ) {  
            Firebase.firestore  
		        .collection("users")  
                .document(UserInfo.getInstance().uid)  
                .collection("history")  
                .get()  
                .addOnSuccessListener { querySnapshot ->
                ...
                result(Result.Success(AchievementHistoryForm(...)))
                ...
		    }
	    }

> AchievementViewModelFactory

    class AchievementViewModelFactory : ViewModelProvider.Factory {  
        @Suppress("UNCHECKED_CAST")  
        override fun <T : ViewModel> create(modelClass: Class<T>): T {  
            if (modelClass.isAssignableFrom(AchievementViewModel::class.java)) {  
                return AchievementViewModel(  
                    achievementRepository = AchievementRepository(  
                        dataSource = AchievementDataSource()  
                    )  
                ) as T  
		    }  
            throw IllegalArgumentException("Unkown ViewModel class")  
        }  
    }



## Description of Activity/Fragment

### Mission Fragment

<img  src="https://user-images.githubusercontent.com/7011030/112718639-b229f700-8f37-11eb-8823-4c0d9b44e905.jpg"  width="20%"  height="20%">
<img  src="https://user-images.githubusercontent.com/7011030/112718640-b35b2400-8f37-11eb-8a20-d1469ea6b9f2.jpg"  width="20%"  height="20%">


- First, the user -> today_mission collection is read from Firebase and the mission information is saved as a list in the repository.  When all today_mission information is read, the first index value is read, the location -> image collection is read, and the image in firestorage is retrieved using the firestore path information.
- When the mission fragment is executed, it requests weather information and stores the value in the missionstartdialog. (Uses OpenWeather API - RetrofitClient)
- If you press the start button, missionstartdialog is displayed, and comments and weather information according to the current time and weather are displayed.
- Yes, I did. If you click, the currently selected mission information is stored in the intent and sent to domissionactivity, and domissionactivty is executed.


### Achievement Fragment

<img  src="https://user-images.githubusercontent.com/7011030/112718859-14cfc280-8f39-11eb-93bf-287d9a92e368.jpg"  width="20%"  height="20%">

- First, get all historty information in the user -> history collection.
- By using the time in history, google fit's historyclient is called to get the biometric data of the time zone that the mission was performed.
- By using the location information of history, the google map is zoomed to the location through geocoding of the google map api, and the location where the mission was executed is shown in mapview.
- When all the history information is read, statistics are obtained by properly classifying the information by time when the today, weekly, monthely, and yearly tabs are clicked.
- Click on the history card to launch the history activity to view more detailed information.


### History Activity
<img  src="https://user-images.githubusercontent.com/7011030/112719245-57929a00-8f3b-11eb-9425-9b1dad546134.jpg"  width="20%"  height="20%">

- Place the value from the achievement fragment into the ui properly.
- Using the location information received from the achievement fragment, the information of the user who completed this mission in the location -> done_users collection is retrieved. And it uses that information to generate statistics and place it in the ui appropriately.

### Neighborhood Fragment

<img  src="https://user-images.githubusercontent.com/7011030/112718994-c53dc680-8f39-11eb-86a5-ad45861d5aef.jpg"  width="20%"  height="20%">

- First, we get all the information in the share collection. (It should be modified so that it can be imported sequentially by selecting appropriately according to the location information.)
- When all the information is brought, a layout is dynamically created based on that information and added to the scrollview.


### Information Fragment

<img  src="https://user-images.githubusercontent.com/7011030/112719071-1b126e80-8f3a-11eb-9d11-83a08efbf928.jpg"  width="20%"  height="20%">

- Badge feature has not yet been implemented


### DoMissionActivity

<img  src="https://user-images.githubusercontent.com/7011030/112719080-2d8ca800-8f3a-11eb-828e-8a95af850eb2.jpg"  width="20%"  height="20%">

- Starts a timer that runs every second in ViewModel.
- Whenever the timer runs, it calls google fit api historyclient based on the mission start time to get and update mph/mile/kcal information.
- Clicking the camera icon calls the ACTION_IMAGE_CAPTURE Intent, saves the picture taken in the local storage, stores the location in the Repository, and sends it to the FinishMissionActivity after the mission ends.
- Clicking the map icon runs mapActivity that shows the current location.
- Clicking the pause icon stops the timer.


### FinishMissionActivity

<img  src="https://user-images.githubusercontent.com/7011030/112719196-0bdff080-8f3b-11eb-9289-d341aa1d0a8c.jpg"  width="20%"  height="20%">

- Click the share your route button to go to shareactivity.
- Clicking the end the mission button saves the location information received from domissionactivity, the difficulty level selected from finishmissionactivity, and the current time in users -> history collection.

### ShareActivity

<img  src="https://user-images.githubusercontent.com/7011030/112719189-01bdf200-8f3b-11eb-95e3-f51a1399f6d9.jpg"  width="20%"  height="20%">


- It updates the list of my photos using the file path of the photos taken from domissionactivity.
- When you click the upload button, the photo is saved in firestorage and the url, catpion, and location route information are saved in the share collection.
