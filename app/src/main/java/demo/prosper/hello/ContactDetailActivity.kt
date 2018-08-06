package demo.prosper.hello

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_contact_detail.*
import java.text.SimpleDateFormat
import java.util.*

class ContactDetailActivity : AppCompatActivity() {
    companion object {
        const val NAME_KEY = "name"
        const val PHONE_KEY = "phone"
        const val EMAIL_KEY = "email"
        const val ADDED_DATE_KEY = "date"
        const val IMAGE_KEY = "image"

        fun launch(context: Context, name:String?, phone:String?, email:String?, date:String?, image:String?) {
            val intent = Intent(context, ContactDetailActivity::class.java)
            intent.putExtra(NAME_KEY, name)
            intent.putExtra(PHONE_KEY, phone)
            intent.putExtra(EMAIL_KEY, email)
            intent.putExtra(ADDED_DATE_KEY, date)
            intent.putExtra(IMAGE_KEY, image);
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_detail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val name = intent.getStringExtra(NAME_KEY)
        val phone = intent.getStringExtra(PHONE_KEY)
        val email = intent.getStringExtra(EMAIL_KEY)
        val addedDate = intent.getStringExtra(ADDED_DATE_KEY)
        val image = intent.getStringExtra(IMAGE_KEY)

        if (Utils.validateValue(name)) {
            nameTV.text = name
            nameTV.visibility = View.VISIBLE
        }
        if (Utils.validateValue(phone)) {
            phoneTV.text = phone
            phoneLL.visibility = View.VISIBLE
        }
        if (Utils.validateValue(email)) {
            emailTV.text = email
            emailLL.visibility = View.VISIBLE
        }
        if (Utils.validateValue(addedDate)) {
            val cal = Calendar.getInstance()
            cal.time = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH).parse(addedDate)
            dateTV.text = SimpleDateFormat("EEE, MMM dd, yyyy", Locale.ENGLISH).format(cal.time)
            dateLL.visibility = View.VISIBLE
        }
        if (Utils.validateValue(image)) {
            imageIV.setImageURI(Uri.parse(image))
            imageIV.scaleType = ImageView.ScaleType.CENTER_CROP
        }

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (android.R.id.home == item?.itemId){
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}
