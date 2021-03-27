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
