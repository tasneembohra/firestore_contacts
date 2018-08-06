package demo.prosper.hello

import android.util.Patterns
import android.text.TextUtils

class Utils {
    companion object {
        fun isValidEmail(email: CharSequence): Boolean {
            if (!TextUtils.isEmpty(email)) {
                return Patterns.EMAIL_ADDRESS.matcher(email).matches()
            }
            return true
        }

        fun isValidMobile(phone: String): Boolean {
            if (!TextUtils.isEmpty(phone)) {
                return Patterns.PHONE.matcher(phone).matches()
            }
            return true
        }

        fun validateValue(value:String?): Boolean {
            return value != null && value.trim() != "" && "null" != value && "NULL" != value
        }
    }
}