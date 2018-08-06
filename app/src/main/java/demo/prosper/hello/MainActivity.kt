package demo.prosper.hello

import android.content.Intent
import android.os.Bundle
import android.provider.Settings.Secure.ANDROID_ID
import android.provider.Settings.Secure.getString
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    // Access a Cloud Firestore instance from your Activity
    private val mFirestore = FirebaseFirestore.getInstance()
    private lateinit var mQuery:Query
    private lateinit var mAdapter:ContactsRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val deviceId =  getString(contentResolver, ANDROID_ID)
        mQuery = mFirestore.collection("devices").document(deviceId).collection("contacts")
                .orderBy("first_name")
                .orderBy("last_name")
                .orderBy("phone")

        initializeAdapter()
        fab.setOnClickListener { view ->
            startActivity(Intent(this, AddContactActivity::class.java))
        }
    }

    fun initializeAdapter() {
        mAdapter = object : ContactsRecyclerAdapter(mQuery,this) {
            override fun onDataChanged() {
                // Show/hide content if the query returns empty.
                if (itemCount == 0) {
                    recyclerView.visibility = View.GONE
                } else {
                    recyclerView.visibility = View.VISIBLE
                }
            }
            override fun onError(e: FirebaseFirestoreException) {
                // Show a snackbar on errors
                Snackbar.make(findViewById(android.R.id.content),
                        "Error: check logs for info.", Snackbar.LENGTH_LONG).show()
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = mAdapter
        mAdapter.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        mAdapter.stopListening()
    }
}
