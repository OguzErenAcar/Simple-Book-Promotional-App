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

   var  secilen_gorsel : Uri?=null
   var secilenBitmap : Bitmap? =null

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
            activity?.let{
                if(ContextCompat.checkSelfPermission(it.applicationContext,android.Manifest.permission.READ_EXTERNAL_STORAGE ) !=PackageManager.PERMISSION_GRANTED){
                    //izin verilmedi
                    requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1)
                }
                else{
                    //izin zaten verilmiş galeriye gti
                    //galeriye gitme
                    val galeriIntent= Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(galeriIntent,2)
                }
            }
        }
        arguments?.let {
            var gelenBilgi =TanitimFragmentArgs.fromBundle(it).bilgi

            if(gelenBilgi.equals("menudengeldim"))
            {
                //yeni yemek eklemeye geldi
                binding.kitapismitext.setText("")
                binding.yazarismitext.setText("")
                binding.button.visibility=View.VISIBLE
                val gorselSecmeArkaPlanı= BitmapFactory.decodeResource(context?.resources,R.drawable.frame_image)


            }else {
                binding.button.visibility=View.INVISIBLE
                //daha önce olusturulan yemek
                val secilenId = TanitimFragmentArgs.fromBundle(it).id

                context?.let{

                    try{


                        val db = it.openOrCreateDatabase( "Kitaplar",Context.MODE_PRIVATE,null)
                        val cursor =db.rawQuery("SELECT * FROM kitaplar WHERE id =?", arrayOf(secilenId.toString()))

                        val kitapIsmiIndex =cursor.getColumnIndex("kitapismi")
                        val kitapYazariIndex=cursor.getColumnIndex("kitapyazari")
                        val kitapGorseli =cursor.getColumnIndex("gorsel")
                        while (cursor.moveToNext()){
                            binding.kitapismitext.setText(cursor.getString(kitapIsmiIndex))
                            binding.yazarismitext.setText(cursor.getString(kitapYazariIndex))


                                if (kitapGorseli==null){
                                    System.out.println("bos")
                                }
                            System.out.println(kitapGorseli.javaClass)

                            val byteDizisi = cursor.getBlob(kitapGorseli)
                            val bitmap =BitmapFactory.decodeByteArray(byteDizisi,0, byteDizisi.size)
                            binding.imageView.setImageBitmap(bitmap)


                        }
                        cursor.close()
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }

            }
        }

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



    fun kaydet(view: View){

        val kitapIsmi =binding.kitapismitext.text.toString()
        val yazarIsmı = binding.yazarismitext.text.toString()

        if(secilenBitmap!=null){
            val kucukBitmap =kucukBitmapOlustur(secilenBitmap!!,300)

            val outputStream =ByteArrayOutputStream()
            kucukBitmap.compress(Bitmap.CompressFormat.PNG ,50,outputStream)
            val byteDizisi =outputStream.toByteArray()

            try{
                context?.let{
                    val database =context?.openOrCreateDatabase("Kitaplar", Context.MODE_PRIVATE,null)
                    database?.execSQL("CREATE TABLE IF NOT EXISTS kitaplar (id INTEGER PRIMARY KEY ,kitapismi VARCHAR,kitapyazari VARCHAR,gorsel VARCHAR )")
                  database?.execSQL ("INSERT INTO kitaplar  (kitapismi , kitapyazari, gorsel) VALUES('${kitapIsmi}','${yazarIsmı}','${byteDizisi}')")
               /*     val sqlString ="INSERT INTO kitaplar  (kitapismi , kitapyazari, gorsel) VALUES(?,?,?)"
                    val statment =database?.compileStatement(sqlString)
                    statment?.bindString(1,kitapIsmi)
                    statment?.bindString(2,yazarIsmı )
                    statment?.bindBlob(3 , byteDizisi)*/
                    /*System.out.println(kitapIsmi)
                    System.out.println(yazarIsmı)*/

                 //   println("Deneme --> ${statment}")

                  /*  context?.let {
                        System.out.println("sql al__")
                        val database = it.openOrCreateDatabase("Kitaplar", Context.MODE_PRIVATE, null)
                        val cursor = database.rawQuery("SELECT * FROM kitaplar ", null)
                        val kitapIsmiIndex = cursor.getColumnIndex("kitapismi")
                        val kitapidIndex = cursor.getColumnIndex("id")
                        while (cursor.moveToNext()) {

                            System.out.println(cursor.getString(kitapIsmiIndex))
                            System.out.println(cursor.getString(kitapidIndex))

                        }
                    }*/
                }
            }catch (e:Exception){
                System.out.println("hata tanitim $e")
                e.printStackTrace()
            }
            val action =TanitimFragmentDirections.actionTanitimFragmentToListeFragment()
            Navigation.findNavController(view).navigate(action)

        }

        System.out.println("deneme")
    }
    fun gorselsec(view: View){
        System.out.println("deneme")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    fun kucukBitmapOlustur(kullanicininSectigiBitmap :Bitmap , maximumBoyut:Int):Bitmap{

        var width =kullanicininSectigiBitmap.width
        var height =kullanicininSectigiBitmap.height

        val BitmapOrani : Double =width.toDouble()/height.toDouble()


        if(BitmapOrani>1){
            width =maximumBoyut
            val kisaltilmisHeight =width/BitmapOrani
            height=kisaltilmisHeight.toInt()
        }
        else {

        height=maximumBoyut
            val kisaltilmisWidht =height*BitmapOrani
            width=kisaltilmisWidht.toInt()

        }

        return  Bitmap.createScaledBitmap(kullanicininSectigiBitmap,width,height,true)
    }

}


















