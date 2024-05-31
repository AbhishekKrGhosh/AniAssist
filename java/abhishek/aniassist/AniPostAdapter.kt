package abhishek.aniassist


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class AniPostAdapter (private val aniList: ArrayList<AnimalPostInfo>) :
    RecyclerView.Adapter<AniPostAdapter.ViewHolder>() {
    private lateinit var mListener: onItemClickListener
    interface onItemClickListener{
        fun onItemClick(position: Int)
    }
    fun setOnItemClickListener(clickListener: onItemClickListener){
        mListener = clickListener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.animal_list_item, parent, false)
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
            holder.category.text = "Type: " + currentEmp.category
        }
        if(currentEmp.problem==""){
            holder.ownerName.text = "Problem: unknown"
        }
        else{
            holder.ownerName.text = "Problem: " + currentEmp.problem
        }
        if(currentEmp.decription == ""){
            holder.description.text = "unknown"
        }
        else{
            holder.description.text = currentEmp.decription
        }
        if(currentEmp.condition == ""){
            holder.lastSighted.text = "Condition: unknown"
        }
        else{
            holder.lastSighted.text = "Condition: "+ currentEmp.condition
        }

        holder.dateAndTime.text = currentEmp.dateTime
    }

    override fun getItemCount(): Int {
        return aniList.size
    }

    class ViewHolder(itemView: View, clickListener: onItemClickListener)
        : RecyclerView.ViewHolder(itemView) {

        val pic: ImageView = itemView.findViewById(R.id.pic)
        val category: TextView = itemView.findViewById(R.id.tvAniCategory)
        val ownerName : TextView = itemView.findViewById(R.id.ownerName)
        val description : TextView = itemView.findViewById(R.id.animalDescription)
        val lastSighted : TextView = itemView.findViewById(R.id.lastInfo)
        val dateAndTime : TextView = itemView.findViewById(R.id.dateandtime)

        init {
            itemView.setOnClickListener {
                clickListener.onItemClick(adapterPosition)
            }
        }

    }

}