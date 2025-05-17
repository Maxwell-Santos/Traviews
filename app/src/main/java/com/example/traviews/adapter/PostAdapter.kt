import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.traviews.R
import com.example.traviews.model.Post
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class PostAdapter(private val posts: List<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val descriptionTextView: TextView = itemView.findViewById(R.id.textDescription)
        val txtCreatedAt: TextView = itemView.findViewById(R.id.txtCreatedAt)
        val txtFoodCost: TextView = itemView.findViewById(R.id.txtFoodCost)
        val txtEntertainmentCost: TextView = itemView.findViewById(R.id.txtEntertainmentCost)
        val txtAccommodationCost: TextView = itemView.findViewById(R.id.txtAccomodationCost)
        val imgViewPost: ImageView = itemView.findViewById(R.id.imgViewPost)

        fun bind(post: Post) {
        imgViewPost.load(post.medias[0]) {
            crossfade(true)
            placeholder(R.drawable.google_logo)
            error(R.drawable.google_logo)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]

        holder.bind(post)
        holder.descriptionTextView.text = post.description
        holder.txtCreatedAt.text = Utils.formatIsoToDate(post.createdAt)
        holder.txtFoodCost.text = Utils.formatarParaReal(post.foodCost)
        holder.txtEntertainmentCost.text = Utils.formatarParaReal(post.entertainmentCost)
        holder.txtAccommodationCost.text = Utils.formatarParaReal(post.accommodationCost)
    }
    override fun getItemCount(): Int = posts.size
}

object Utils {
    fun formatIsoToDate(isoDate: String): String {
        // Corrige o formato da string
        val cleanedDate = isoDate
            .replace("+00:00", "Z") // troca o offset por Z
            .replace(Regex("\\.(\\d{3})\\d*Z$"), ".$1Z") // reduz microssegundos para milissegundos

        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")

        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        outputFormat.timeZone = TimeZone.getTimeZone("America/Sao_Paulo")

        val date = inputFormat.parse(cleanedDate)
        return outputFormat.format(date!!)
    }

    fun formatarParaReal(valor: Int?): String {
        if (valor == null) {
            return "R$ 0,00"
        }

        val formato = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
        return formato.format(valor / 100)
    }
}