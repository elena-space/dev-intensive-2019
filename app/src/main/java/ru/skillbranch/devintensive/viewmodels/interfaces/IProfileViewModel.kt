package ru.skillbranch.devintensive.viewmodels.interfaces

import androidx.lifecycle.LiveData
import ru.skillbranch.devintensive.models.Profile

/**
 * @author Space
 * @date 28.07.2019
 */

interface IProfileViewModel {

    val profileData: LiveData<Profile>

    val appTheme: LiveData<Int>

    val isRepositoryErrorVisible: LiveData<Boolean>

    fun onRepositoryChanged(repository: String)

    fun saveProfileData(profile: Profile)

    fun switchTheme()
}