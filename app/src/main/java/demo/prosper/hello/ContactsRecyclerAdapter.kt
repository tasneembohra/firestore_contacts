package demo.prosper.hello

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.firebase.firestore.Query

open class ContactsRecyclerAdapter(query: Query, var context: Context): FirestoreAdapter<ContactViewHolder>(mQuery = query) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
       return ContactViewHolder(itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_contacts_layout, parent, false))
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(context, mSnapshots[position])
    }
}