package com.example.justmoveit.activity;

import static com.example.justmoveit.activity.MainActivity.userSP;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.justmoveit.R;
import com.example.justmoveit.api.UserTicketApi;
import com.example.justmoveit.model.ReservedTicket;
import com.example.justmoveit.model.Ticket;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TicketInfoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_done);

        Intent intent = getIntent();
        ReservedTicket reservedTicket = (ReservedTicket) intent.getExtras().getSerializable("ticket");

        // 만료 여부 - 만료시 배경색 회색으로 변경
        if(reservedTicket.isExpired()){
            ImageView ticketBg = findViewById(R.id.ticket_bg);
            ticketBg.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#a5a5aa")));
            findViewById(R.id.reserve_cancel_button).setVisibility(View.INVISIBLE);
        }

        Ticket ticket = reservedTicket.getTicket();

        // 영화 예매 정보 뿌림
        getSetTexts(ticket);

        // 예매 취소
        findViewById(R.id.reserve_cancel_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                ConnectionThread thread = new ConnectionThread(ticket);
                thread.start();

                try {
                    thread.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void getSetTexts(Ticket ticket) {
        ((TextView) findViewById(R.id.movie_title)).setText(ticket.getMovieTitle());
        ((TextView) findViewById(R.id.reserve_date)).setText(ticket.getReservationTime());
        ((TextView) findViewById(R.id.viewing_date)).setText((ticket.getReservationTime().split(" "))[0] + " " + ticket.getStartTime());
        ((TextView) findViewById(R.id.total_cost)).setText((ticket.getTotalCost()==null? "알 수 없음": ticket.getTotalCost()));

        int[] clsf = ReservedTicket.convertClassificationToInt(ticket.getClassification());
        ((TextView) findViewById(R.id.classification)).setText(
                (clsf[0]>0?"성인 "+clsf[0]+"명  ":"").concat(clsf[1]>0?"어린이 "+clsf[1]+"명":""));

        ((TextView) findViewById(R.id.reserve_seat)).setText(ticket.getSeat().replace(",", ", "));
    }

    private static class ConnectionThread extends Thread {
        Ticket ticket;

        public ConnectionThread(Ticket ticket){
            this.ticket = ticket;
        }

        @Override
        public void run() {
            synchronized (this){
                cancelReservation();
                notify();
            }
        }

        private void cancelReservation() {
            UserTicketApi service = UserTicketApi.retrofit.create(UserTicketApi.class);
            service.cancelTicket(ticket.getTicketId()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    SharedPreferences.Editor editor = userSP.edit();
                    // SP에서 사용자 예매 정보 불러옴
                    Gson gson = new Gson();
                    String json = userSP.getString("user_tickets", "");
                    ArrayList<ReservedTicket> reservedTickets = gson.fromJson(json, TypeToken.getParameterized(List.class, Ticket.class).getType());
                    // id에 해당하는 티켓 예매 정보 삭제
                    for(int i=0, n=reservedTickets.size(); i<n; i++){
                        if(Objects.equals(reservedTickets.get(i).getTicket().getTicketId(), ticket.getTicketId())){
                            reservedTickets.remove(i);
                            break;
                        }
                    }
                    // 다시 삽입
                    editor.putString("user_tickets", gson.toJson(reservedTickets));
                    editor.apply();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("canceling ticket reservation failed", t.getMessage());
                }
            });
        }
    }
}
