package com.bibliotecadebolso.app.util

import android.util.Log
import com.bibliotecadebolso.app.data.model.request.BookTitle
import com.bibliotecadebolso.app.data.model.request.Borrow
import com.bibliotecadebolso.app.data.model.request.BorrowStatus
import java.time.LocalDateTime

object BorrowGeneratorUtils {

    fun fillListWithOnlyReturned(times: Int, list: MutableList<Borrow>) {
        for (i in 1..times) {
            val now = LocalDateTime.now()
            val date =
                if (i % 2 == 0) now.plusDays(1).plusHours(1)
                else if (i % 3 == 0) now.plusWeeks(1).plusDays(1)
                else now.plusMonths(1).plusDays(1)

            val borrow = Borrow(i, i, i.toByte().times(4).toString(),
                BorrowStatus.RETURNED,date,null,now,now,
                BookTitle("teste")
            )
            list.add(
                borrow
            )
        }
    }

    fun fillListWithOnlyPending(times: Int, list: MutableList<Borrow>) {
        for (i in 1..times) {
            val status = BorrowStatus.PENDING
            val now = LocalDateTime.now()

            val date =
                if (i/times.toFloat() <= 0.34 ) now.minusDays(1).minusHours(1)
                else if (i/times.toFloat() in 0.34..0.67) now.minusWeeks(1).minusDays(1)
                else now.minusMonths(1).minusWeeks(1)

            val borrow = Borrow(i, i, i.toByte().times(4).toString(),status,date,null,now,now,
                BookTitle("teste")
            )
            list.add(
                borrow
            )
        }
    }
}