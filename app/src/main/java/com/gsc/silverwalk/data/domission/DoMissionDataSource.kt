package com.gsc.silverwalk.data.domission

import com.gsc.silverwalk.ui.domission.DoMissionForm
import com.gsc.silverwalk.data.Result

class DoMissionDataSource {

    fun requestGoogleFitApi(result: (Result<DoMissionForm>) -> Unit) {
        result(
            Result.Success(
                DoMissionForm(0, 0.0, 0.0, 0, 0)
            )
        )
    }

}