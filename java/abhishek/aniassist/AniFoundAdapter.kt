package abhishek.aniassist


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class AniFoundAdapter (private val aniList: ArrayList<AnimalFoundInfo>) :
    RecyclerView.Adapter<AniFoundAdapter.ViewHolder>() {
    private lateinit var mListener: onItemClickListener
    interface onItemClickListener{
        fun onItemClick(position: Int)
    }
    fun setOnItemClickListener(clickListener: onItemClickListener){
        mListener = clickListener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.animal_found_list_item, parent, false)
        return ViewHolder(itemView, mListener)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentEmp = aniList[position]
        if(currentEmp.uri!="") {
            Picasso
                .get()
                .load(currentEmp.uri)
                .into(holder.pic);
        }

        if(currentEmp.category==""){
            holder.category.text = "Type: unknown"
        }
        else{
            holder.category.text = currentEmp.category
        }

        if(currentEmp.description == ""){
            holder.description.text = "Unknown"
        }
        else{
            holder.description.text = currentEmp.description
        }
        if(currentEmp.contact == ""){
            holder.contactInfo.text = "Whom to contact is not known"
        }
        else{
            holder.contactInfo.text = "Contact me: " + currentEmp.contact
        }

        holder.dateAndTime.text = currentEmp.dateTime
    }

    override fun getItemCount(): Int {
        return aniList.size
    }

    class ViewHolder(itemView: View, clickListener: onItemClickListener)
        : RecyclerView.ViewHolder(itemView) {

        val pic: ImageView = itemView.findViewById(R.id.picFound)
        val category: TextView = itemView.findViewById(R.id.tvAniFoundCategory)
        val description : TextView = itemView.findViewById(R.id.animalFoundDescription)
        val contactInfo : TextView = itemView.findViewById(R.id.contactInfoFound)
        val dateAndTime : TextView = itemView.findViewById(R.id.dateandtimeFound)

        init {
            itemView.setOnClickListener {
                clickListener.onItemClick(adapterPosition)
            }
        }

    }

}