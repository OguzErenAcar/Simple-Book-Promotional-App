package com.example.kitaptanitimi

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.example.kitaptanitimi.databinding.ActivityMainBinding
import com.example.kitaptanitimi.databinding.FragmentTanitimBinding
import java.io.ByteArrayOutputStream
import java.util.jar.Manifest
import kotlin.coroutines.coroutineContext


class TanitimFragment : Fragment() {

    var secilen_gorsel: Uri? = null
    var secilenBitmap: Bitmap? = null
    private var _binding: FragmentTanitimBinding? = null

    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTanitimBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.button.setOnClickListener {
            kaydet(it)
        }
        binding.imageView.setOnClickListener {
            gorselsec(it)
            activity?.let {
                if (ContextCompat.checkSelfPermission(
                        it.applicationContext,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    //izin verilmedi
                    requestPermissions(
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        1
                    )
                } else {
                    //galeriye gitmek
                    val galeriIntent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(galeriIntent, 2)
                }
            }
        }

        //Tanıtım view e geçiş
        arguments?.let {
            var gelenBilgi = TanitimFragmentArgs.fromBundle(it).bilgi
            if (gelenBilgi.equals("menudengeldim")) {
                //yeni kitap eklemeye geldi
                binding.kitapismitext.setText("")
                binding.yazarismitext.setText("")
                binding.button.visibility = View.VISIBLE
                val gorselSecmeArkaPlanı =
                    BitmapFactory.decodeResource(context?.resources, R.drawable.frame_image)
            }
            else if (gelenBilgi.equals("recyclerdangeldim")){
                //daha önce olusturulan kitap
                binding.button.visibility = View.INVISIBLE
                val secilenId = TanitimFragmentArgs.fromBundle(it).id
                System.out.println("secilen id  "+secilenId)
                context?.let {
                    try {
                        val db = it.openOrCreateDatabase("Kitaplar", Context.MODE_PRIVATE, null)
                        val cursor = db.rawQuery("SELECT * FROM kitaplar WHERE id =?",
                            arrayOf(secilenId.toString()) )

                        val kitapIsmiIndex = cursor.getColumnIndex("kitapismi")
                        val kitapYazariIndex = cursor.getColumnIndex("kitapyazari")
                        val kitapGorseliIndex = cursor.getColumnIndex("gorsel")
                        while (cursor.moveToNext()) {//row sayısı kadar true
                            binding.kitapismitext.setText(cursor.getString(kitapIsmiIndex))
                            binding.yazarismitext.setText(cursor.getString(kitapYazariIndex))

                            val byteDizisi = cursor.getBlob(kitapGorseliIndex)
                            System.out.println("bytedizi "+ byteDizisi)
                            System.out.println("bytesize "+ byteDizisi.size)
                            val bitmap = BitmapFactory.decodeByteArray(byteDizisi, 0, byteDizisi.size)
                            binding.imageView.setImageBitmap(bitmap)

                        }
                        cursor.close()
                    } catch (e: Exception) {
                        System.out.println("hata")
                        e.printStackTrace()
                    }
                }

            }
        }

    }

    fun kaydet(view: View) {
        val kitapIsmi = binding.kitapismitext.text.toString()
        val yazarIsmı = binding.yazarismitext.text.toString()

        if (secilenBitmap != null) {
            val kucukBitmap = kucukBitmapOlustur(secilenBitmap!!, 300)

            val outputStream = ByteArrayOutputStream()
            kucukBitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream)
            val byteDizisi = outputStream.toByteArray()

            val arr = arrayOf<Byte>(25, 41, 85, 3,79,46,15,25, 41, 85, 3,79,46,15,25, 41, 85, 3,79,46,15,25, 41, 85, 3,79,46,15,25, 41, 85, 3,79,46,15,25, 41, 85, 3,79,46,15)


            System.out.println("kaydet dizi size  "+arr.size)
            try {
                context?.let {
                    val database =  it.openOrCreateDatabase("Kitaplar", Context.MODE_PRIVATE, null)
                    database.execSQL("CREATE TABLE IF NOT EXISTS kitaplar (id INTEGER PRIMARY KEY ,kitapismi VARCHAR,kitapyazari VARCHAR,gorsel VARCHAR )")
                   // database?.execSQL("INSERT INTO kitaplar  (kitapismi , kitapyazari, gorsel) VALUES('${kitapIsmi}','${yazarIsmı}','${arr}')")

                    val sqlstring ="INSERT INTO kitaplar (kitapismi , kitapyazari, gorsel) VALUES (?, ?, ?)"
                    //direk
                    val statement =database.compileStatement(sqlstring)
                    statement.bindString(1,kitapIsmi)
                    statement.bindString(2,yazarIsmı)
                    statement.bindBlob(3,byteDizisi)
                    //bindblop olmadan byte dizisi yazdırırsan diziyi doru kaydetmeiyor
                    statement.execute()

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            //geri listeye dön
            val action = TanitimFragmentDirections.actionTanitimFragmentToListeFragment()
            Navigation.findNavController(view).navigate(action)

        }
    }

    fun gorselsec(view: View) {
        System.out.println("deneme")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    fun kucukBitmapOlustur(kullanicininSectigiBitmap: Bitmap, maximumBoyut: Int): Bitmap {

        var width = kullanicininSectigiBitmap.width
        var height = kullanicininSectigiBitmap.height

        val BitmapOrani: Double = width.toDouble() / height.toDouble()


        if (BitmapOrani > 1) {
            width = maximumBoyut
            val kisaltilmisHeight = width / BitmapOrani
            height = kisaltilmisHeight.toInt()
        } else {

            height = maximumBoyut
            val kisaltilmisWidht = height * BitmapOrani
            width = kisaltilmisWidht.toInt()

        }

        return Bitmap.createScaledBitmap(kullanicininSectigiBitmap, width, height, true)
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode==1){

            if(grantResults.size>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                val galeriIntent= Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent,2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if(requestCode==2 && resultCode == Activity.RESULT_OK &&data!=null ){
            secilen_gorsel=data.data

            try {

                context?.let{
                    if(secilen_gorsel!=null){
                        if(Build.VERSION.SDK_INT>=28){
                            val source= ImageDecoder.createSource(it.contentResolver,secilen_gorsel!!)
                            secilenBitmap =ImageDecoder.decodeBitmap(source)
                            binding.imageView.setImageBitmap(secilenBitmap)
                        }
                        else{
                            secilenBitmap =MediaStore.Images.Media.getBitmap(it.contentResolver,secilen_gorsel)
                            binding.imageView.setImageBitmap(secilenBitmap)
                        }
                    }
                }
            }

            catch (e:Exception){
                e.printStackTrace()
            }



        }



        super.onActivityResult(requestCode, resultCode, data)
    }




}


















