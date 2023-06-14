import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemContainerUserBinding
import com.example.myapplication.listeners.UserListener
import com.example.myapplication.models.User

class UsersAdapter(private var users: List<User>, private var userListener: UserListener) :
    RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    private val selectedUsers = mutableListOf<User>()
    private var selectionMode = false

    fun setSelectionMode(mode: Boolean) {
        selectionMode = mode
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemContainerUserBinding: ItemContainerUserBinding = ItemContainerUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserViewHolder(itemContainerUserBinding, userListener)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.setUserData(user)

        if (selectionMode) {
            val isSelected = selectedUsers.contains(user)
            holder.setSelected(isSelected)
        }
    }

    override fun getItemCount(): Int {
        return users.size
    }

    inner class UserViewHolder(
        private val binding: ItemContainerUserBinding,
        private val userListener: UserListener
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var user: User

        init {
            binding.root.setOnClickListener {
                if (selectionMode) {
                    toggleSelection()
                } else {
                    userListener.onUserClicked(user)
                }
            }
        }

        fun setUserData(user: User) {
            this.user = user
            binding.textName.text = user.name
            binding.textEmail.text = user.email
            binding.imageProfile.setImageBitmap(getUserImage(user.image))
        }

        fun setSelected(isSelected: Boolean) {
            binding.root.isSelected = isSelected
        }

        private fun toggleSelection() {
            val isSelected = selectedUsers.contains(user)
            if (isSelected) {
                selectedUsers.remove(user)
                setSelected(false)
            } else {
                selectedUsers.add(user)
                setSelected(true)
            }
        }

        private fun getUserImage(encodedImage: String): Bitmap {
            val bytes = Base64.decode(encodedImage, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)!!
        }
    }
}
