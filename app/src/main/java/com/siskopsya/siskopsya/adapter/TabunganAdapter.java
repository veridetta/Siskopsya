package com.siskopsya.siskopsya.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.siskopsya.siskopsya.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

public class TabunganAdapter extends RecyclerView.Adapter<TabunganAdapter.MyViewHolder> {

    private ArrayList<String>
            jenisList= new ArrayList<>(),
            totalSaldoList = new ArrayList<>(),
            debitList= new ArrayList<>(),
            kreditList= new ArrayList<>(),
            saldoList = new ArrayList<>();


    private Activity mActivity;
    Intent intent;
    String dx;
    private Context context;
    private int lastPosition = -1;
    public TabunganAdapter( ArrayList<String> jenisList,
                         ArrayList<String> totalSaldoList,
                         ArrayList<String> debitList,
                         ArrayList<String> kreditList,
                         ArrayList<String> saldoList,
                         Context context) {
        this.jenisList = jenisList;
        this.totalSaldoList = totalSaldoList;
        this.debitList = debitList;
        this.kreditList = kreditList;
        this.saldoList = saldoList;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtJenis, txtTotalSaldo, txtDebit, txtKredit, txtSaldo;
        LinearLayout ly_tabungan, ly_deskripsi;

        public MyViewHolder(View view) {
            super(view);
            txtJenis = (TextView) view.findViewById(R.id.no_rek);
            txtTotalSaldo = view.findViewById(R.id.saldo_rek);
            txtDebit = (TextView) view.findViewById(R.id.debit);
            txtKredit = (TextView) view.findViewById(R.id.kredit);
            txtSaldo = (TextView) view.findViewById(R.id.saldo);
            ly_tabungan = view.findViewById(R.id.ly_tabungan);
            ly_deskripsi = view.findViewById(R.id.ly_deskripsi);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tabungan_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        //format harga
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        DecimalFormat decimalFormat = new DecimalFormat("Rp #,###", symbols);
        //inisial format rupiah
        String totalSaldo = decimalFormat.format(Integer.parseInt(totalSaldoList.get(position)));
        String debit = decimalFormat.format(Integer.parseInt(debitList.get(position)));
        String kredit = decimalFormat.format(Integer.parseInt(kreditList.get(position)));
        String saldo = decimalFormat.format(Integer.parseInt(saldoList.get(position)));

        //set data ke view
        holder.txtJenis.setText("No Rek. "+jenisList.get(position));
        holder.txtTotalSaldo.setText(totalSaldo);
        holder.txtDebit.setText(debit);
        holder.txtKredit.setText(kredit);
        holder.txtSaldo.setText(saldo);

        final boolean[] opened = new boolean[getItemCount()];
        //click show detail
        holder.ly_tabungan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!opened[0]){
                    holder.ly_deskripsi.setVisibility(View.VISIBLE);

                } else {
                    holder.ly_deskripsi.setVisibility(View.GONE);

                }
                opened[0] = !opened[0];
            }
        });
    }
    @Override
    public int getItemCount() {
        return jenisList.size();
    }
}
