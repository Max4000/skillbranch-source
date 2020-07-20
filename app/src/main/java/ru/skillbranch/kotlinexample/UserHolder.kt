package ru.skillbranch.kotlinexample

import androidx.annotation.VisibleForTesting
import java.lang.IllegalArgumentException

object UserHolder {
    private val map = mutableMapOf<String,User>()

    fun registerUser(fullName: String, email : String, password : String): User {

        val usr: User = User.makeUser(fullName = fullName, email = email, password = password)

        when {
            SearchInMapByInfo(usr.userInfo) -> throw IllegalArgumentException("A user with this email already exists")
            else -> map[usr.login] = usr
        }

        return usr
    }

    fun SearchInMapByInfo(info : String) : Boolean {

        for((_, currentUser) in map){
            if (currentUser.userInfo == info) {
                return true
            }
        }

        return false

    }

    fun requestAccessCode(phone: String) {
        map[User.convertPhone(phone)]?.requestAccessCode()
    }

    fun registerUserByPhone(fullName: String,phone : String) : User {

        val usr : User = User.makeUser(fullName = fullName,phone = phone)

        when {
            SearchInMapByInfo(usr.userInfo) -> throw IllegalArgumentException("A user with this phone already exists")
            else -> map[usr.login] = usr
        }

        return usr
    }


    fun loginUser(login : String, password: String):String? {
        return when {
            login.contains("@") -> {
                map[login.trim()]?.let {
                    if (it.checkPassword(password)) it.userInfo
                    else null
                }
            }
            else ->  {
                map[User.convertPhone(login.trim())]?.let {
                    if (it.checkPassword(password)) it.userInfo
                    else null
                }
            }
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun clearHolder() {
        map.clear()
    }
}