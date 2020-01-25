package com.siskopsya.siskopsya.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.siskopsya.siskopsya.R;
import com.siskopsya.siskopsya.menu.HutangMurobahahActivity;
import com.siskopsya.siskopsya.menu.HutangQordhulhasanActivity;
import com.siskopsya.siskopsya.menu.InvestasiActivity;
import com.siskopsya.siskopsya.menu.SHUActivity;
import com.siskopsya.siskopsya.menu.SimpananActivity;
import com.siskopsya.siskopsya.menu.TabunganActivity;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MyViewHolder> {

    private ArrayList<String>
            judulList= new ArrayList<>(),
            saldoList= new ArrayList<>(),
            gambarList= new ArrayList<>();

    private Activity mActivity;
    Intent intent;
    String dx;
    private Context context;
    private int lastPosition = -1;

    public MenuAdapter( ArrayList<String> judulList,
                        ArrayList<String> saldoList,
                        ArrayList<String>gambarList, Context context) {
        this.judulList = judulList;
        this.saldoList = saldoList;
        this.gambarList = gambarList;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtJudul, txtSaldo;
        CardView cardMenu;
        ImageView imgSamping;
        public MyViewHolder(View view) {
            super(view);
            txtJudul = (TextView) view.findViewById(R.id.txt_judul);
            txtSaldo = (TextView) view.findViewById(R.id.txt_saldo);
            cardMenu = view.findViewById(R.id.card_menu);
            imgSamping = view.findViewById(R.id.img_samping);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.menu_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        //format harga
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');

        DecimalFormat decimalFormat = new DecimalFormat("Rp #,###", symbols);
        String prezzo = decimalFormat.format(Integer.parseInt(saldoList.get(position)));

        holder.txtJudul.setText(judulList.get(position));
        holder.txtSaldo.setText(prezzo);
        String gambarURL = "https://yayasansehatmadanielarbah.com/api-siskopsya/assets/image/"+gambarList.get(position).toString();
        Glide.with(context)
                .load(gambarURL)
                .into(holder.imgSamping);
        holder.cardMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String target = judulList.get(position).toString();
                switch (target){
                    case "Simpanan":
                        Intent p = new Intent(v.getContext()
                                , SimpananActivity.class);
                        v.getContext().startActivity(p);
                        break;
                    case "Tabungan":
                        Intent q = new Intent(v.getContext()
                                , TabunganActivity.class);
                        v.getContext().startActivity(q);
                        break;
                    case "Hutang Murobahah":
                        Intent r = new Intent(v.getContext()
                                , HutangMurobahahActivity.class);
                        v.getContext().startActivity(r);
                        break;
                    case "Hutang Qordhulhasan":
                        Intent s = new Intent(v.getContext()
                                , HutangQordhulhasanActivity.class);
                        v.getContext().startActivity(s);
                        break;
                    case "Investasi":
                        Intent t = new Intent(v.getContext()
                                , InvestasiActivity.class);
                        v.getContext().startActivity(t);
                        break;
                    case "SHU":
                        Intent u = new Intent(v.getContext()
                                , SHUActivity.class);
                        v.getContext().startActivity(u);
                        break;
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return judulList.size();
    }
}