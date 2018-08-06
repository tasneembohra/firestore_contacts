package demo.prosper.hello

import android.support.v7.widget.RecyclerView
import android.util.Log
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import java.util.*


/**
 * RecyclerView adapter for displaying the results of a Firestore {@link Query}.
 *
 * Note that this class forgoes some efficiency to gain simplicity. For example, the result of
 * {@link DocumentSnapshot#toObject(Class)} is not cached so the same object may be deserialized
 * many times as the user scrolls.
 *
 * See the adapter classes in FirebaseUI (https://github.com/firebase/FirebaseUI-Android/tree/master/firestore) for a
 * more efficient implementation of a Firestore RecyclerView Adapter.
 */
abstract class FirestoreAdapter<VH:RecyclerView.ViewHolder>(val mQuery: Query,
                                                            val mSnapshots: ArrayList<DocumentSnapshot> = ArrayList<DocumentSnapshot>())
    : RecyclerView.Adapter<VH>(), EventListener<QuerySnapshot> {

    private var mRegistration: ListenerRegistration? = null

    protected open fun onError(e: FirebaseFirestoreException) {};

    protected open fun onDataChanged() {}

    override fun onEvent(documentSnapshots: QuerySnapshot?, e: FirebaseFirestoreException?) {
        // Error handling
        if (e != null) {
            Log.w(javaClass.simpleName, e.message, e)
            onError(e)
            return
        }

        documentSnapshots?.getDocumentChanges()?.forEach { documentChange ->
            // Snapshot of the changed document
            val document = documentChange.document
            when (documentChange.type) {
                DocumentChange.Type.ADDED -> onDocumentAdded(documentChange)
                DocumentChange.Type.MODIFIED -> onDocumentModified(documentChange)
                DocumentChange.Type.REMOVED -> onDocumentRemoved(documentChange)
            }
        }
        onDataChanged()
    }

    /**
     * When document new has been added
     * - Add the doc to the current snapshot
     * - Notify recyclerview accordingly
     */
    private fun onDocumentAdded(change:DocumentChange) {
        mSnapshots.add(change.newIndex, change.document)
        notifyItemChanged(change.newIndex)
    }

    /**
     * When document has been modified
     * - update the doc in the current snapshot
     * - Notify recyclerview accordingly
     */
    private fun onDocumentModified(change: DocumentChange) {
        if (change.oldIndex == change.newIndex) {
            // Item changed but remained in same position
            mSnapshots[change.oldIndex] = change.document
            notifyItemChanged(change.oldIndex)
        } else {
            // Item changed and changed position
            mSnapshots.removeAt(change.oldIndex)
            mSnapshots.add(change.newIndex, change.document)
            notifyItemMoved(change.oldIndex, change.newIndex)
        }
    }

    /**
     * When document has been removed
     * - Remove the doc from the current snapshot
     * - Notify recyclerview accordingly
     */
    private fun onDocumentRemoved(change: DocumentChange) {
        mSnapshots.removeAt(change.oldIndex)
        notifyItemRemoved(change.oldIndex)
    }

    /**
     * return size of the document snapshot to render rows in RecyclerView
     */
    override fun getItemCount(): Int {
        return  mSnapshots.size
    }

    /**
     * Start listening for the EventListener - Normally call on activity onStart
     */
    fun startListening() {
        if (mRegistration == null) {
            mRegistration = mQuery.addSnapshotListener(this)
        }
    }

    /**
     * Stop listening to this EventListener - Normally call on activity onStop
     */
    fun stopListening() {
        if (mRegistration != null) {
            mRegistration!!.remove()
            mRegistration = null
        }
        mSnapshots.clear()
        notifyDataSetChanged()
    }
}