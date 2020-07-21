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

    fun importUsers(list: List<String>): List<User> {

        val usrsList  = mutableListOf<User>()

        for(str in list){

            val strs = str.split(";")
            //Полное имя пользователя; email; соль:хеш пароля; телефон

            var fullName  =""
            var email : String? = null
            var silk  = ""
            var Hash  = ""
            var phone  =""

            for ((index, word) in strs.withIndex())
            {
                when (index) {
                    0 -> fullName = word
                    1 -> email = word
                    2 -> {
                        silk = word.split(':')[0]
                        Hash = word.split(':')[1]
                    }
                    3 -> phone = word
                }
            }


            val currentUser : User? =

            when {
                !phone.isNullOrEmpty() -> {
                    try {
                        val registerUserByPhone = registerUserByPhone(fullName, phone)
                        registerUserByPhone.salt = silk
                        registerUserByPhone.passwordHash = Hash
                        registerUserByPhone
                    } catch (e: IllegalArgumentException) {
                        null
                    }
                }
                else -> {

                    try {
                        val registerUserByEmail = User.makeUser(fullName = fullName,
                            email =  email, password = "12345")
                        registerUserByEmail.salt = silk
                        registerUserByEmail.passwordHash = Hash
                        registerUserByEmail
                    } catch (e: IllegalArgumentException) {
                        null
                    }
                }
            }

            if (currentUser != null) {
                usrsList.add(currentUser)
            }
        }
        return usrsList
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