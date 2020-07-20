package ru.skillbranch.kotlinexample

import android.text.Editable
import androidx.annotation.VisibleForTesting
import java.lang.IllegalArgumentException
import java.math.BigInteger
import java.security.MessageDigest
import java.security.SecureRandom

class User private constructor(
    private val firstName: String,
    private val lastName: String?,
    private val email: String? = null,
    private val rawPhone: String? = null,
    meta: Map<String, Any>? = null){

    val userInfo: String

    private val fullName: String
        get() = listOfNotNull(firstName,lastName)
        .joinToString(" ")
        .capitalize()

    private val initials: String
        get() = listOfNotNull(firstName,lastName)
        .map{it.first().toUpperCase()}
        .joinToString ( " ")

     private var phone: String? = null
        set(value) {
        //    field = if(value != null) "+".plus(value?.replace("""\D+""".toRegex(),"")) else null
            field = convertPhone(value)
        }

    private var _login: String? = null

    var login: String
        set(value) {
        _login = value.toLowerCase()
        }
        get() =_login!!

     private  var salt: String? = null
     private lateinit var passwordHash: String

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
     var accessCode: String? = null

    constructor(firstName: String,lastName: String?,email: String?,password : String ) :
            this(firstName,lastName,email = email,meta = mapOf("auth" to "password")){
        println("Secondary email constructor")
        passwordHash = encript(password)
    }

    fun requestAccessCode() {

        val code = generateAccessCode()
        passwordHash =encript(code)
        println("Phone passwordHash is $passwordHash")
        accessCode = code
        sendAccessCodeToUser(rawPhone,code)

    }

    constructor(firstName: String, lastName: String?,rawPhone : String) :this
            ( firstName, lastName , rawPhone = rawPhone, meta = mapOf("auth" to "sms")){
        println("Secondary phone constructor")
        requestAccessCode()
    }
    init {
        println("First init block, primary constructor was called")
        check(firstName.isNotBlank()) {"firstName must not be blank"}
        check(!email.isNullOrBlank()|| !rawPhone.isNullOrBlank())
            {"email or phone must not be null"}

        phone = rawPhone

        login = email ?: phone!!

        userInfo = """
              firstName: $firstName
              lastName: $lastName
              login: $login
              fullName: $fullName
              initials: $initials
              email: $email
              phone: $phone
              meta: $meta
            """.trimIndent()
    }

    private fun String.md5() : String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(toByteArray())
        val hexString = BigInteger(1,digest).toString(16)
        return hexString.padStart(32,'0')
    }

    private fun sendAccessCodeToUser(phone : String?, code : String){
        println("... sending access code $code on $phone")
    }


    fun generateAccessCode() : String {
        val possible = "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm0123456789"
        return StringBuilder().apply {
             repeat(6) {
                 (possible.indices).random().also {
                     index -> append(possible[index])
                 }
             }
        }.toString()
    }

    fun checkPassword (pass: String) = encript(pass) == passwordHash.also {
        println("Checking passwordHash is $passwordHash")
    }

    fun changePassword (oldPas : String, newPass : String) {
        if (checkPassword(oldPas)) {
            passwordHash = encript((newPass))
            if (!accessCode.isNullOrEmpty())
                accessCode = newPass
            println("Password $oldPas has been changed on new password $newPass" )
        } else throw IllegalArgumentException("The entered password does not match current password")
    }



    private fun encript (password: String) :String {
        if (salt.isNullOrEmpty()) {
            salt = ByteArray(16).also { SecureRandom().nextBytes(it) }.toString()
        }
        println("Salt while encrypt: $salt")
        return salt.plus(password).md5()
    }

    override fun toString(): String{
        return "{fullName = ${firstName + " " + lastName}, email = ${email}, phone = ${rawPhone}}"
    }

    companion object Factory {
        fun makeUser (fullName :String,
                      email :String? = null,
                      password: String? = null,
                      phone: String? = null) : User
        {
            val usr : User
            val (firstName, lastName) = fullName.fullNameToPair()
            when {
                !phone.isNullOrBlank() -> {
                    usr = User(firstName,lastName,phone)
                    if (usr.phone?.length != 12)
                        throw IllegalArgumentException("Enter a valid phone number starting with a + and containing 11 digits")
                    return usr
                }
                !email.isNullOrBlank()&&!password.isNullOrBlank() -> {
                    return User(firstName,lastName,email,password)
                }
                else -> throw IllegalArgumentException("Email or phone must not b null or blank")
            }
        }


        fun convertPhone (rawPhone: String?) : String? {
           return if(rawPhone != null) "+".plus(rawPhone?.replace("""\D+""".toRegex(),"")) else null
        }

        private fun String.fullNameToPair(): Pair<String,String?> =
             this.split(" ")
                .filter { it.isNotBlank() }
                .run {
                    when (size)
                    {
                        1 -> first() to null
                        2 -> first() to last()
                        else -> throw  IllegalArgumentException(
                            "FullName must contain only first name and lsat name. current split " + "result: ${this@fullNameToPair}"
                        )
                    }
                }
    }

}

