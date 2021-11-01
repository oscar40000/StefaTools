@file:Suppress("SpellCheckingInspection")

package com.example.stefatools

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.lang.reflect.Type
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList
import android.widget.ArrayAdapter as ArrayAdapter1


@Suppress("SameParameterValue", "UNCHECKED_CAST")
class MainActivity : AppCompatActivity(), ListaAdapter.OnItemClickListener {
    private var listaStref = generator(1)
    private var adapter = ListaAdapter(listaStref, this)
    private val authority = "com.example.stefatools.FileProvider"


    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        recycler_view.adapter = adapter
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.setHasFixedSize(true)
        wpisPiersza.requestFocus()

        val tloDelete = ColorDrawable(Color.parseColor("#ff0000"))
        val tloAdd = ColorDrawable(Color.parseColor("#e1ad01"))
        val deleteIcon = ContextCompat.getDrawable(this, R.drawable.ic_delete)!!
        val addIcon = ContextCompat.getDrawable(this, R.drawable.ic_add)!!
        //blok slownika
        val dzial = resources.getStringArray(R.array.dzial)
        val adapterDzialu = ArrayAdapter1<String>(
            this,
            android.R.layout.simple_spinner_dropdown_item, dzial
        )
        nazwa_dzialu.adapter = adapterDzialu

        val slownikElektro = resources.getStringArray(R.array.elekto)
        val slownikDeko = resources.getStringArray(R.array.deko)
        val slownikGarten = resources.getStringArray(R.array.garten)
        val slownikHolz = resources.getStringArray(R.array.holz)
        val slownikSanitar = resources.getStringArray(R.array.sanitar)
        val slownikSonderprize = resources.getStringArray(R.array.sonderprize)
        val slownikWerkzeug = resources.getStringArray(R.array.werkzeug)
        val slownikFamila = resources.getStringArray(R.array.famila)
        val slownikKeine = resources.getStringArray(R.array.keine)

        val adapterElektro = ArrayAdapter1<String>(
            this,
            android.R.layout.simple_list_item_1, slownikElektro
        )
        val adapterDeko = ArrayAdapter1<String>(
            this,
            android.R.layout.simple_list_item_1, slownikDeko
        )
        val adapterGarten = ArrayAdapter1<String>(
            this,
            android.R.layout.simple_list_item_1, slownikGarten
        )
        val adapterHolz = ArrayAdapter1<String>(
            this,
            android.R.layout.simple_list_item_1, slownikHolz
        )
        val adapterSanitar = ArrayAdapter1<String>(
            this,
            android.R.layout.simple_list_item_1, slownikSanitar
        )
        val adapterSonderprize = ArrayAdapter1<String>(
            this,
            android.R.layout.simple_list_item_1, slownikSonderprize
        )
        val adapterWerkzeug = ArrayAdapter1<String>(
            this,
            android.R.layout.simple_list_item_1, slownikWerkzeug
        )
        val adapterFamila = ArrayAdapter1<String>(
            this,
            android.R.layout.simple_list_item_1, slownikFamila
        )
        val adapterKeine = ArrayAdapter1<String>(
            this,
            android.R.layout.simple_list_item_1, slownikKeine
        )

        wpisNazwa.setAdapter(adapterKeine)

        nazwa_dzialu.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0) wpisNazwa.setAdapter(adapterElektro)
                if (position == 1) wpisNazwa.setAdapter(adapterDeko)
                if (position == 2) wpisNazwa.setAdapter(adapterGarten)
                if (position == 3) wpisNazwa.setAdapter(adapterHolz)
                if (position == 4) wpisNazwa.setAdapter(adapterSanitar)
                if (position == 5) wpisNazwa.setAdapter(adapterSonderprize)
                if (position == 6) wpisNazwa.setAdapter(adapterWerkzeug)
                if (position == 7) wpisNazwa.setAdapter(adapterFamila)
                if (position == 8) wpisNazwa.setAdapter(adapterKeine)
            }

        }
        //blok slownika koniec


        //otwieranie backupa
        if (listaStref.size == 1) {
            try {
                otworzFile()
            } catch (e: Exception) {
            }
        }
        //koniec otwierania backupa


        //przycik send
        wpisOpis.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                val ostatninumer = listaStref[(listaStref.size - 1)].numer + 1
                val numeraktualny = findViewById<TextView>(R.id.wpisNumer).text.toString().toInt()
                if (numeraktualny == ostatninumer) {
                    dodawanieOstatniej()
                    saveData()
                    // Toast.makeText(this, " numer aktualny $numeraktualny ,ostatni numer $ostatninumer dodawanie ostatniej", Toast.LENGTH_LONG).show()
                } else {
                    edycja()
                    saveData()
                    // Toast.makeText(this, " numer aktualny $numeraktualny ,ostatni numer $ostatninumer edycja", Toast.LENGTH_LONG).show()
                }
                true
            } else {
                false
            }
        }
        //============


        //swipe left right
        val itemTouchHelperCallback =
            object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ) = false

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val index = viewHolder.absoluteAdapterPosition

                    when (direction) {
                        ItemTouchHelper.LEFT -> {

                            if (index == 0) {
                                adapter.notifyItemChanged(index)
                                Toast.makeText(
                                    this@MainActivity,
                                    "You cannot delete this zone",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()

                            } else {
                                val skanowana = listaStref[index]
                                listaStref.removeAt(index)
                                numerstrefyzatwierdz()
                                saveData()
                                Snackbar.make(
                                    recycler_view,
                                    "You deleted zone: ${skanowana.numer}  ${skanowana.nazwa}",
                                    Snackbar.LENGTH_LONG
                                )
                                    .setAction("UNDO") {
                                        listaStref.add(index, skanowana)
                                        numerstrefyzatwierdz()
                                    }.show()

                            }

                        }
                        ItemTouchHelper.RIGHT -> {

                            val indexPrawo: Int = index + 1
                            listaStref.add(indexPrawo, Strefa(indexPrawo, "", "", ""))
                            saveData()
                            numerstrefyzatwierdz()
                            findViewById<TextView>(R.id.wpisNumer).text =
                                listaStref[indexPrawo].numer.toString()
                            aktualny()
                            findViewById<EditText>(R.id.wpisNazwa).setText("")
                            findViewById<EditText>(R.id.wpisDlugosc).setText("")
                            findViewById<EditText>(R.id.wpisOpis).setText("")
                            wpisNazwa.requestFocus()
                            adapter.notifyItemChanged(index)

                        }
                    }
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    val itemView = viewHolder.itemView
                    val iconMarginVertical =
                        (viewHolder.itemView.height - deleteIcon.intrinsicHeight) / 2

                    if (dX > 0) {
                        tloAdd.setBounds(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                        addIcon.setBounds(
                            itemView.left + iconMarginVertical,
                            itemView.top + iconMarginVertical,
                            itemView.left + iconMarginVertical + deleteIcon.intrinsicWidth,
                            itemView.bottom - iconMarginVertical
                        )
                    } else {
                        tloDelete.setBounds(
                            itemView.right + dX.toInt(),
                            itemView.top,
                            itemView.right,
                            itemView.bottom
                        )
                        deleteIcon.setBounds(
                            itemView.right - iconMarginVertical - deleteIcon.intrinsicWidth,
                            itemView.top + iconMarginVertical,
                            itemView.right - iconMarginVertical,
                            itemView.bottom - iconMarginVertical
                        )
                        deleteIcon.level = 0
                    }

                    tloDelete.draw(c)
                    tloAdd.draw(c)

                    c.save()

                    if (dX > 0) {
                        c.clipRect(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                    } else {
                        c.clipRect(
                            itemView.right + dX.toInt(),
                            itemView.top,
                            itemView.right,
                            itemView.bottom
                        )
                    }

                    deleteIcon.draw(c)
                    addIcon.draw(c)

                    c.restore()

                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }

            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recycler_view)
        //koniec swipe

        ustawDzial()
        wpisPiersza.setOnFocusChangeListener { view: View, hasFocus ->
            if (!hasFocus) numerstrefyzatwierdz()
        }

//        when {
//            intent?.action == Intent.ACTION_SEND -> {
//                while ("text/csv" == intent.type) handleSendText(intent) // Handle text being sent
//            }
//        }
//
//    }


        //    @SuppressLint("NotifyDataSetChanged")
//    private fun handleSendText(intent: Intent) {
//        intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
//            listaStref.clear()
//            adapter.notifyDataSetChanged()
//            listaStref.add(Strefa(1, "", "", ""))
//            recycler_view.scrollToPosition(0)
//        }
//    }


    }


    fun zatwierdzPierwsza(view: View) {

        numerstrefyzatwierdz()
        wpisNazwa.requestFocus()
        ustawDzial()
        saveData()
    }

    fun numerstrefyzatwierdz() {
        val numerPierwszejStrefy =
            findViewById<EditText>(R.id.wpisPiersza).text.toString().toInt()
        for (i in 0 until listaStref.size) {
            listaStref[i].numer = numerPierwszejStrefy + i
            adapter.notifyItemChanged(i)

        }
        adapter.notifyDataSetChanged()
        val nastenaStrefaZatwierdz = (numerPierwszejStrefy + listaStref.size).toString()
        findViewById<TextView>(R.id.wpisNumer).text =
            (nastenaStrefaZatwierdz.toInt() - 1).toString()
        aktualny()

    }

    fun dodaj(view: View) {
        val ostatninumer = listaStref[(listaStref.size - 1)].numer + 1
        val pierwszynumer = listaStref[0].numer
        val numeraktualny = findViewById<TextView>(R.id.wpisNumer).text.toString().toInt()
        val numerWpisanyNaGorze = findViewById<EditText>(R.id.wpisPiersza).text.toString().toInt()
        if (numerWpisanyNaGorze != pierwszynumer) {
            numerstrefyzatwierdz()
        } else {
            if (numeraktualny == ostatninumer) {
                dodawanieOstatniej()
                saveData()

            } else {
                edycja()
                saveData()

            }
            wpisNazwa.selectAll()
        }
    }

    fun zapisz(view: View) {
        val numerDzialu = findViewById<EditText>(R.id.wpisPiersza).text.toString()
        val time: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            (LocalDate.now()).toString()
        } else {
            ""
        }


        val filename = "$time,$numerDzialu.csv"
        val fileOutputStream: FileOutputStream
        openFileOutput(filename, MODE_PRIVATE).also { fileOutputStream = it }
        for (i in 0 until listaStref.size) {
            fileOutputStream.write("${listaStref[i].numer};${listaStref[i].nazwa};${listaStref[i].dlugosc};${listaStref[i].opis};\n".toByteArray())
        }
        fileOutputStream.close()
        val file = File(filesDir, filename)
        val path = FileProvider.getUriForFile(this, authority, file)

        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_STREAM, path)
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        shareIntent.putExtra(Intent.EXTRA_TITLE, "test")
        shareIntent.type = "application/csv"
        saveData()
        startActivity(Intent.createChooser(shareIntent, "Share..."))


    }

    fun naSamDol(view: View) {
        naSamDolF()
    }

    private fun naSamDolF() {

        val index = listaStref.size - 1
        val txtNumer = listaStref[index].numer
        recycler_view.scrollToPosition(index)
        findViewById<TextView>(R.id.wpisNumer).text = (txtNumer + 1).toString()
        aktualny()
        wpisNazwa.requestFocus()
        saveData()

    }

    override fun onItemClick(position: Int) {
        findViewById<TextView>(R.id.wpisNumer).text = listaStref[position].numer.toString()
        aktualny()
        findViewById<EditText>(R.id.wpisNazwa).setText(listaStref[position].nazwa)
        findViewById<EditText>(R.id.wpisDlugosc).setText(listaStref[position].dlugosc)
        findViewById<EditText>(R.id.wpisOpis).setText(listaStref[position].opis)
        var numerAktualny = listaStref[position].numer
        wpisNazwa.requestFocus()
        wpisNazwa.selectAll()

    }

    private fun saveData() {
        val sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(listaStref)
        editor.putString("backup", json)
        editor.apply()
    }

    fun usunFile(view: View) {
        recycler_view.scrollToPosition(0)
        listaStref.clear()
        adapter.notifyDataSetChanged()
        listaStref.add(Strefa(1, "", "", ""))
        recycler_view.scrollToPosition(0)
        Snackbar.make(
            recycler_view,
            "!!You delete all Data!!",
            Snackbar.LENGTH_LONG
        )
            .setAction("UNDO") {
                otworzFile()
            }.show()
        findViewById<TextView>(R.id.wpisPiersza).text = 1.toString()
        numerstrefyzatwierdz()
    }

    private fun ustawDzial() {

        if (listaStref[0].numer == 1000) nazwa_dzialu.setSelection(6, false)
        if (listaStref[0].numer == 1500) nazwa_dzialu.setSelection(0, false)
        if (listaStref[0].numer == 2000) nazwa_dzialu.setSelection(1, false)
        if (listaStref[0].numer == 3000) nazwa_dzialu.setSelection(3, false)
        if (listaStref[0].numer == 4000) nazwa_dzialu.setSelection(4, false)
        if (listaStref[0].numer == 5000 || R.id.wpisNumer <= 8000) nazwa_dzialu.setSelection(
            2,
            false
        )

    }

    private fun otworzFile() {
        val listaStref2: ArrayList<Strefa>
        val sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("backup", null)
        val type: Type = object : TypeToken<ArrayList<Strefa>>() {}.type
        listaStref2 = gson.fromJson<Any>(json, type) as ArrayList<Strefa>

        listaStref.clear()
        for (i in 0 until listaStref2.size) {
            listaStref.add(listaStref2[i])

        }
        adapter.notifyDataSetChanged()

        val index = listaStref.size - 1
        val txtNumer = listaStref[index].numer
        recycler_view.scrollToPosition(index)
        findViewById<TextView>(R.id.wpisPiersza).text = (listaStref[0].numer).toString()
        findViewById<TextView>(R.id.wpisNumer).text = (txtNumer + 1).toString()
        aktualny()
        wpisNazwa.requestFocus()
    }

    @SuppressLint("CutPasteId")
    private fun edycja() {

        val txtnumerpierwszej = findViewById<EditText>(R.id.wpisPiersza).text.toString().toInt()
        val txtnumer = findViewById<TextView>(R.id.wpisNumer).text.toString()
        val txtNazwa =
            findViewById<EditText>(R.id.wpisNazwa).text.toString().toUpperCase(Locale.getDefault())
        val txtDlugosc = findViewById<EditText>(R.id.wpisDlugosc).text.toString()
        val txtOpis = findViewById<EditText>(R.id.wpisOpis).text.toString()
        val pozycja: Int = (txtnumer.toInt()) - txtnumerpierwszej
        val strefaEdycja = Strefa(txtnumer.toInt(), txtNazwa, txtDlugosc, txtOpis)
        listaStref[pozycja] = strefaEdycja
        adapter.notifyItemChanged(pozycja)

        if (pozycja == (listaStref.size - 1)) {
            findViewById<TextView>(R.id.wpisNumer).text = (txtnumer.toInt() + 1).toString()
            aktualny()
            wpisNazwa.requestFocus()
        } else {
            findViewById<TextView>(R.id.wpisNumer).text = (txtnumer.toInt() + 1).toString()
            aktualny()
            findViewById<EditText>(R.id.wpisNazwa).setText(listaStref[pozycja + 1].nazwa)
            findViewById<EditText>(R.id.wpisDlugosc).setText(listaStref[pozycja + 1].dlugosc)
            findViewById<EditText>(R.id.wpisOpis).setText(listaStref[pozycja + 1].opis)
            wpisNazwa.requestFocus()
            wpisNazwa.selectAll()
            if (listaStref.size > 10) recycler_view.scrollToPosition(pozycja + 3)
        }
    }

    @SuppressLint("CutPasteId")
    private fun dodawanieOstatniej() {
        val txtNumer = findViewById<TextView>(R.id.wpisNumer).text.toString().toInt()
        val textNazwa = findViewById<EditText>(R.id.wpisNazwa).text.toString().toUpperCase()
        val textDlugosc = findViewById<EditText>(R.id.wpisDlugosc).text.toString()
        val textOpis = findViewById<EditText>(R.id.wpisOpis).text.toString()
        val index = listaStref.size - 1
        val strefaDodana = Strefa(txtNumer, textNazwa, textDlugosc, textOpis)
        listaStref.add(index + 1, strefaDodana)
        adapter.notifyItemInserted(index + 1)
        recycler_view.scrollToPosition(index + 1)
        findViewById<TextView>(R.id.wpisNumer).text = (txtNumer + 1).toString()
        aktualny()
        wpisNazwa.requestFocus()

    }

    private fun generator(Size: Int): ArrayList<Strefa> {
        val list = ArrayList<Strefa>()
        for (i in 0 until Size) {
            val item = Strefa(i + 1, " ", "", "")
            list += item
        }
        return list
    }

    fun idzDoOpcji(view: View) {
        saveData()
        val intent = Intent(this@MainActivity, PlikiAcitivity::class.java)
        startActivity(intent)

        //val listaPlikow:Array<String> = this.fileList()
        //Toast.makeText(this,"${listaPlikow[0]}",Toast.LENGTH_SHORT).show()
    }

    fun kopiaNaDol(view: android.view.View) {
        naSamDol(view)
        dodawanieOstatniej()
        wpisNazwa.selectAll()

    }

    fun aktualny (){
        val aktualny = findViewById<TextView>(R.id.wpisNumer).text.toString().toInt()
        val strefa0 = Strefa(listaStref[0].numer,listaStref[0].nazwa,listaStref[0].dlugosc,listaStref[0].opis,aktualny)
        listaStref[0]=strefa0
        adapter.notifyDataSetChanged()


    }

}










