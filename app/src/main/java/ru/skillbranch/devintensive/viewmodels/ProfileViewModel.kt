package ru.skillbranch.devintensive.viewmodels

import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.skillbranch.devintensive.models.Profile
import ru.skillbranch.devintensive.repositories.PreferencesRepository
import ru.skillbranch.devintensive.viewmodels.interfaces.IProfileViewModel

class ProfileViewModel : ViewModel(), IProfileViewModel {

    override val profileData = MutableLiveData<Profile>()
    override val appTheme = MutableLiveData<Int>()
    override val isRepositoryErrorVisible = MutableLiveData<Boolean>()

    private val repoExcludeSet = setOf("enterprise", "features", "topics", "collections", "trending", "events",
            "marketplace", "pricing", "nonprofit", "customer-stories", "security", "login", "join")

    init {
        profileData.value = PreferencesRepository.getProfile()
        appTheme.value = PreferencesRepository.getAppTheme()
    }

    override fun onRepositoryChanged(repository: String) {
        isRepositoryErrorVisible.value = !isRepositoryUrlValid(repository)
    }

    private fun isRepositoryUrlValid(repository: String) =
            Regex("^(?:(?:https://)|(?:http://))?(?:www\\.)?(?:github.com/)((?:[a-z0-9])+(?:[-]?[a-z0-9]+)?)/?\$", RegexOption.IGNORE_CASE)
                    .find(repository)
                    ?.run { groupValues.size == 2 && !repoExcludeSet.contains(groupValues[1]) } ?: repository.isEmpty()

    override fun saveProfileData(profile: Profile) = with(if (isRepositoryErrorVisible.value == true) profile.copy(repository = "") else profile) {
        PreferencesRepository.saveProfile(this)
        profileData.value = this
        isRepositoryErrorVisible.value = false
    }

    override fun switchTheme() {
        if (appTheme.value == MODE_NIGHT_YES) appTheme.value = MODE_NIGHT_NO else appTheme.value = MODE_NIGHT_YES
        PreferencesRepository.saveAppTheme(appTheme.value!!)
    }
}