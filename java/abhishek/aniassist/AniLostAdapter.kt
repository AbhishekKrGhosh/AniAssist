package abhishek.aniassist


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class AniLostAdapter (private val aniList: ArrayList<AnimalLostInfo>) :
    RecyclerView.Adapter<AniLostAdapter.ViewHolder>() {
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
        if(currentEmp.animName==""){
            holder.tvAniName.text = "unknown"
        }
        else{
            holder.tvAniName.text = currentEmp.animName
        }
        if(currentEmp.category==""){
            holder.category.text = "unknown" + ": "
        }
        else{
            holder.category.text = currentEmp.category + ": "
        }
        if(currentEmp.ownerName==""){
            holder.ownerName.text = "Owner: " + "unknown"
        }
        else{
            holder.ownerName.text = "Owner: " + currentEmp.ownerName
        }
        if(currentEmp.description == ""){
            holder.description.text = "Unknown"
        }
        else{
            holder.description.text = currentEmp.description
        }
        if(currentEmp.lastSighted == ""){
            holder.lastSighted.text = "Last sighted information is not known"
        }
        else{
            holder.lastSighted.text = "Last sighted at " + currentEmp.lastSighted
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
        val tvAniName : TextView = itemView.findViewById(R.id.tvAniName)
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