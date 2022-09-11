package com.example.kitaptanitimi

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kitaptanitimi.databinding.FragmentListeBinding

class ListeFragment : Fragment() {

    var kitapIsmiListesi = ArrayList<String>()
    var kitapIdListesi = ArrayList<Int>()

    private lateinit var  listeAdapter:ListeRecycleAdapter

    private lateinit var binding:FragmentListeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // return inflater.inflate(R.layout.fragment_liste, container, false)

        binding = FragmentListeBinding.inflate(layoutInflater)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listeAdapter= ListeRecycleAdapter(kitapIsmiListesi,kitapIdListesi)
        binding.recyclerView.layoutManager=LinearLayoutManager(context)
        binding.recyclerView.adapter=listeAdapter

       sqlVeriAlma()

    }

    fun sqlVeriAlma() {

        try {
            activity?.let {
                System.out.println("sql al")
                val database = it.openOrCreateDatabase("Kitaplar", Context.MODE_PRIVATE, null)
                val cursor = database.rawQuery("SELECT * FROM 'kitaplar'", null)
                val kitapIsmiIndex = cursor.getColumnIndex("kitapismi")
                val kitapidIndex = cursor.getColumnIndex("id")
                kitapIdListesi.clear()
                kitapIsmiListesi.clear()
                while (cursor.moveToNext()) {
                    kitapIsmiListesi.add(cursor.getString(kitapIsmiIndex))
                    kitapIdListesi.add(cursor.getInt(kitapidIndex))
                    System.out.println(cursor.getString(kitapIsmiIndex))
                    System.out.println(cursor.getString(kitapidIndex))
                    System.out.println( kitapIsmiListesi)

                }
                listeAdapter.notifyDataSetChanged()
            }
        } catch (e: Exception) {
            System.out.println("hata tanitim liste $e")
        }

    }


}