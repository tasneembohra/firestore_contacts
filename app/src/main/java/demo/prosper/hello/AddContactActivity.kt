package demo.prosper.hello

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.provider.MediaStore
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_contact.*
import java.io.File
import java.util.*


class AddContactActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_CODE_TAKE_PICTURE = 1
        private const val PERMISSION_REQUEST_CODE = 2
    }

    private val mFirestore = FirebaseFirestore.getInstance()
    // TODO Use Firebase Storage to upload image to cloud
    // private val mFirebaseStorage = FirebaseStorage.getInstance("gs://hello-prosper-app.appspot.com")
    private var mFileTemp: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun addToContacts(v: View) {
        val email = emailTIET.text.toString()
        val phone = phoneTIET.text.toString()
        val firstName =  firstNameTIET.text.toString()
        val lastName = lastNameTIET.text.toString()
        val date = Date()
        var image:String? = null

        // Validate values before going ahead
        var flag = true
        if (!Utils.isValidEmail(email)) {
            emailTIET.error = getString(R.string.email_error)
            flag = false
        }
        if (!Utils.isValidMobile(phone)) {
            phoneTIET.error = getString(R.string.phone_number_error)
            flag = false
        }

        // Check if validation is successful and something has been added
        if (flag && !(TextUtils.isEmpty(email) && TextUtils.isEmpty(firstName) && TextUtils.isEmpty(lastName) && TextUtils.isEmpty(phone))) {
            val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
            val map = HashMap<String, Any>()
            map["first_name"] = firstName
            map["last_name"] = lastName
            map["email"] = email
            map["phone"] = phone
            map["add_date"] = date
            if (mFileTemp != null) {
                image = mFileTemp!!.path
                map["image"] = image
            }
            mFirestore.collection("devices").document(deviceId).collection("contacts")
                    .add(map)
                    .addOnSuccessListener {
                        ContactDetailActivity.launch(this, "$firstName $lastName", phone, email, date.toString(), image)
                        finish()
                    }.addOnFailureListener({
                        Snackbar.make(mainRL, getString(R.string.error), Snackbar.LENGTH_SHORT).show();
                    })

        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (android.R.id.home == item?.itemId){
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_TAKE_PICTURE && mFileTemp != null) {
            val imageUri = mFileTemp!!.path
            imageIV.setImageURI(Uri.parse(imageUri))
            imageIV.scaleType = ImageView.ScaleType.CENTER_CROP
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            callCameraIntent()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun initializeFile() {
        val path = File(Environment.getExternalStorageDirectory(), "contactimages")
        if (!path.exists()) path.mkdirs()
        mFileTemp = File(path, SystemClock.currentThreadTimeMillis().toString()+".jpg")
    }

    fun captureImage(v:View) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ) {
            callCameraIntent()
        } else {
            val permissions = arrayOf<String>(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE)
        }
    }

    private fun callCameraIntent() {
        try {
            initializeFile()
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val imageUri = FileProvider.getUriForFile(this, "demo.prosper.hello.fileprovider", mFileTemp!!)
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri)
            val resolvedIntentActivities = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            for (resolvedIntentInfo in resolvedIntentActivities) {
                val packageName = resolvedIntentInfo.activityInfo.packageName
                grantUriPermission(packageName, imageUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE)
        } catch (e: Exception) {
            Log.e(AddContactActivity::class.simpleName, "Error while capturing image", e)
            Snackbar.make(mainRL, getString(R.string.error), Snackbar.LENGTH_SHORT).show();
        }
    }
}
