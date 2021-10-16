package com.example.stefatools

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_pliki_acitivity.*
import com.example.stefatools.Strefa as Strefa


class PlikiAcitivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pliki_acitivity)

        val listaPlikow:Array<String> = this.fileList()
        Toast.makeText(this,listaPlikow[0],Toast.LENGTH_SHORT).show()
        val adapterPlikow = ArrayAdapter(this,R.layout.plik_wiersz,R.id.nazwaPliku,listaPlikow)
        lista_plikow.adapter = adapterPlikow

        val nowaListaStref = ArrayList<Strefa>()
        val plik = openFileInput(listaPlikow[0])
        val liniepliku:Array<String> = plik.bufferedReader().readLines().toTypedArray()

        for (i in 0 until liniepliku.size) {
                    val linia = liniepliku[i]
                    val words = linia.split(";")
                    val nowaStrefa:Strefa = Strefa(words[0].toInt(),words[1],words[2],words[3])
                    nowaListaStref.add(nowaStrefa)
        }

        val testoweLinie = ArrayList<String>()

        for (i in 0 until nowaListaStref.size){
            val linieTestowa = "${nowaListaStref[i].numer}${nowaListaStref[i].nazwa}${nowaListaStref[i].dlugosc}${nowaListaStref[i].opis}"
            testoweLinie.add(linieTestowa)
        }

        val adapterLiniPliku = ArrayAdapter(this, android.R.layout.simple_list_item_1,testoweLinie)

        lista_wewnatrz.adapter = adapterLiniPliku


        }

    






}


