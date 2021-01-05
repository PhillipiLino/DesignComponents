package com.phillipilino.componentssample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.phillipilino.basicviews.NotificationType
import com.phillipilino.photoeditor.MainActivity
import com.phillipilino.viewpager.loopViewPager.LoopViewPagerItem
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val items = listOf(
            LoopViewPagerItem("https://www.saboravida.com.br/wp-content/uploads/2018/11/7-pratos-classicos-da-gastronomia-francesa-2.jpg", "Foie Gras"),
            LoopViewPagerItem("https://www.saboravida.com.br/wp-content/uploads/2018/11/7-pratos-classicos-da-gastronomia-francesa-3.jpg", "Escargot"),
            LoopViewPagerItem("https://www.saboravida.com.br/wp-content/uploads/2018/11/7-pratos-classicos-da-gastronomia-francesa-4.jpg", "Boeuf Bourguignon"),
            LoopViewPagerItem("https://www.saboravida.com.br/wp-content/uploads/2018/11/7-pratos-classicos-da-gastronomia-francesa-5.jpg", "Soup D’oignon"),
            LoopViewPagerItem("https://www.saboravida.com.br/wp-content/uploads/2018/11/7-pratos-classicos-da-gastronomia-francesa-6.jpg", "Ratatouille"),
            LoopViewPagerItem("https://www.saboravida.com.br/wp-content/uploads/2018/11/7-pratos-classicos-da-gastronomia-francesa-7.jpg", "Croissant"),
            LoopViewPagerItem("https://www.saboravida.com.br/wp-content/uploads/2018/11/7-pratos-classicos-da-gastronomia-francesa-8.jpg", "Macaron, o Doce da Rainha")
        )
//
        loop_view_pager.loadItems(items) { item, position ->
            notification.show()

            if (notification.type == NotificationType.ERROR) {
                notification.type = NotificationType.ATTENTION
                return@loadItems
            }

            if (notification.type == NotificationType.ATTENTION) {
                notification.type = NotificationType.SUCCESS
                return@loadItems
            }

            if (notification.type == NotificationType.SUCCESS) {
                notification.type = NotificationType.ERROR
                return@loadItems
            }
        }

//        startActivity(Intent(this, MainActivity::class.java))
    }

    override fun onResume() {
        super.onResume()
        loop_view_pager.startAutoMove(3000, 1000)
    }

    override fun onPause() {
        super.onPause()
        loop_view_pager.stopAutoMove()
    }
}