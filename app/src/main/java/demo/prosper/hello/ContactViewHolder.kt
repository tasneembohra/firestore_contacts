package demo.prosper.hello

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.firestore.DocumentSnapshot
import java.util.*

class ContactViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
    val contactImage = itemView?.findViewById<ImageView>(R.id.imageIV)
    val nameTV = itemView?.findViewById<TextView>(R.id.nameTV)

    fun bind(context:Context, snapshot: DocumentSnapshot) {
        val firstName = snapshot["first_name"].toString()
        val lastName = snapshot["last_name"].toString()
        val phone = snapshot["phone"].toString()
        val email = snapshot["email"].toString()
        val image = snapshot["image"].toString()
        val date = snapshot["add_date"].toString()

        if(Utils.validateValue(firstName)) {
            nameTV?.text = firstName
            nameTV?.append(" ")
            nameTV?.append(lastName)
        } else if (Utils.validateValue(lastName)) {
            nameTV?.text = lastName
        } else if (Utils.validateValue(phone)) {
            nameTV?.text = phone
        } else if (Utils.validateValue(email)) {
            nameTV?.text = email
        }
        if (Utils.validateValue(image))
            contactImage?.setImageURI(Uri.parse(image))

        itemView.setOnClickListener({
            ContactDetailActivity.launch(context, "$firstName $lastName", phone, email, date, image)
        })
    }

}