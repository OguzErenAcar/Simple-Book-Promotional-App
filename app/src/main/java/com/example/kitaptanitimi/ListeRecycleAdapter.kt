package com.example.kitaptanitimi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.kitaptanitimi.databinding.RecycleRowBinding

class ListeRecycleAdapter(val kitapListesi : ArrayList<String>,val idListesi:ArrayList<Int>):RecyclerView.Adapter<ListeRecycleAdapter.KitapHolder>(){
    private lateinit var binding: RecycleRowBinding
    class KitapHolder(val item_View: RecycleRowBinding):RecyclerView.ViewHolder(item_View.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KitapHolder {
     //   val inflater =LayoutInflater.from(parent.context)
     //   val view =inflater.inflate(R.layout.recycle_row, parent, false)
       // System.out.println("oncreateviewholder")
        binding = RecycleRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return KitapHolder(binding)
    }

    override fun getItemCount(): Int {
        return kitapListesi.size
    }

    override fun onBindViewHolder(holder: KitapHolder, position: Int) {
      //  System.out.println("recycle ad")
        holder.item_View.recycleRowText.text=kitapListesi[position]
        holder.itemView.setOnClickListener{
            val action= ListeFragmentDirections.actionListeFragmentToTanitimFragment("recyclerdangeldim",idListesi[position])
            Navigation.findNavController(it).navigate(action)
        }
    }
}



















