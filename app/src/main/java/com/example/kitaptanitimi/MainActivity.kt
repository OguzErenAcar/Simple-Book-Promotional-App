package com.example.kitaptanitimi

import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.Navigation
import com.example.kitaptanitimi.databinding.ActivityMainBinding
import java.util.concurrent.Executor
import  android.widget.Button

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.kitap_ekle,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if(item.itemId==R.id.kitap_ekleme_itemi)
    {
        val action =ListeFragmentDirections.actionListeFragmentToTanitimFragment("menudengeldim",0)
        Navigation.findNavController(this,R.id.fragment).navigate(action)
    }
        return super.onOptionsItemSelected(item)
    }


}
















