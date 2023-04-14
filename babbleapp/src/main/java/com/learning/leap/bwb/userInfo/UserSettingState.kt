package com.learning.leap.bwb.userInfo

import com.learning.leap.bwb.R

data class UserSettingState(
    val userName:String = "",
    val birthday:String = "",
    val gender:Gender = Gender.NOT_NOW,
    val groupCode:String = "",
    val error:UserSettingError = UserSettingError.None(),
    val loading: Boolean = false
)

enum class Gender{
    MALE,
    FEMALE,
    NOT_NOW
}

interface Error {
    val errorTitle: Int
    val errorMessage:Int
}
sealed class UserSettingError:Error{
    data class Age(override val errorTitle: Int = R.string.BabbleError,
                   override val errorMessage: Int = R.string.userNotBornYetError ) :UserSettingError()

    data class EmptyName(override val errorTitle: Int = R.string.userNameNameErrorTitle,
                   override val errorMessage: Int = R.string.userNameEmptyError) :UserSettingError()

    data class LongName(override val errorTitle: Int = R.string.userNameNameErrorTitle,
                   override val errorMessage: Int = R.string.userNameToLongError ) :UserSettingError()

    data class Network(override val errorTitle: Int = R.string.BabbleError,
                       override val errorMessage: Int = R.string.userSave) :UserSettingError()

    data class NoPrompt(override val errorTitle: Int = R.string.BabbleError,
                       override val errorMessage: Int = R.string.noPromptsForUser) :UserSettingError()

    data class None(override val errorTitle: Int = R.string.BabbleError,
                       override val errorMessage: Int = R.string.BabbleError) :UserSettingError()
}
