package com.example.traviews.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.traviews.R
import com.example.traviews.data.local.AuthTokenRepositoryImpl
import com.example.traviews.model.Post
import com.example.traviews.network.TraviewsApi
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone


class PostAdapter(private val posts: List<Post>, private val lifecycleScope: LifecycleCoroutineScope ) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {
    val alreadyLiked: MutableList<String> = mutableListOf()

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val descriptionTextView: TextView = itemView.findViewById(R.id.textDescription)
        val txtCreatedAt: TextView = itemView.findViewById(R.id.txtCreatedAt)
        val txtFoodCost: TextView = itemView.findViewById(R.id.txtFoodCost)
        val txtEntertainmentCost: TextView = itemView.findViewById(R.id.txtEntertainmentCost)
        val txtAccommodationCost: TextView = itemView.findViewById(R.id.txtAccomodationCost)
        val imgViewPost: ImageView = itemView.findViewById(R.id.imgViewPost)
        val txtUsername: TextView = itemView.findViewById(R.id.txtUsername)
        val btnLike: ImageView = itemView.findViewById(R.id.btnLike)
        val likeCount: TextView = itemView.findViewById(R.id.tvLikeCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]

        holder.imgViewPost.load(post.medias[0]) {
            crossfade(true)
            placeholder(R.drawable.img_placeholder)
            error(R.drawable.img_filed_load)
        }
        holder.descriptionTextView.text = post.description
        holder.txtCreatedAt.text = Utils.formatIsoToDate(post.createdAt)
        holder.txtFoodCost.text = Utils.formatToBRL(post.foodCost)
        holder.txtEntertainmentCost.text = Utils.formatToBRL(post.entertainmentCost)
        holder.txtAccommodationCost.text = Utils.formatToBRL(post.accommodationCost)
        holder.txtUsername.text = post.user.name
        holder.likeCount.text = post.likes.size.toString()

        lifecycleScope.launch {
            val id = AuthTokenRepositoryImpl.getUserId()
            if (post.likes.contains(id)) {
                holder.btnLike.setImageResource(R.drawable.favorited_24px)
                alreadyLiked.add(post.id)
            }
        }

        holder.btnLike.setOnClickListener {
            lifecycleScope.launch {
                try {
                    TraviewsApi.retrofitService.likePost(post.id)

                    val a = alreadyLiked.find { post.id == it }
                    if (!a.isNullOrEmpty()) {
                        alreadyLiked.remove(post.id)
                        holder.btnLike.setImageResource(R.drawable.favorite_24px)
                        val newLikeCount = (holder.likeCount.text.toString().toInt() - 1).toString()
                        holder.likeCount.text = newLikeCount
                    } else {
                        alreadyLiked.add(post.id)
                        holder.btnLike.setImageResource(R.drawable.favorited_24px)
                        val newLikeCount = (holder.likeCount.text.toString().toInt() + 1).toString()
                        holder.likeCount.text = newLikeCount
                    }
                } catch (e: Exception) {
                    Toast.makeText(holder.itemView.context, "Erro ao curtir o post", Toast.LENGTH_SHORT).show()
                    println("Erro: ${e.message}")
                }
            }
        }
    }
    override fun getItemCount(): Int = posts.size
}

object Utils {
    fun formatIsoToDate(isoDate: String): String {
        val cleanedDate = isoDate
            .replace("+00:00", "Z")
            .replace(Regex("\\.(\\d{3})\\d*Z$"), ".$1Z") // reduz microssegundos para milissegundos

        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")

        val outputFormat = SimpleDateFormat("d 'de' MMMM, yyyy", Locale("pt", "BR"))
        outputFormat.timeZone = TimeZone.getTimeZone("America/Sao_Paulo")

        val date = inputFormat.parse(cleanedDate)
        return outputFormat.format(date!!)
    }

    fun formatToBRL(valor: Int?): String {
        if (valor == null) {
            return "R$ 0,00"
        }

        val BRCurrencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
        return BRCurrencyFormat.format(valor)
    }
}