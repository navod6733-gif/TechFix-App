package com.techfix.app.ui.payment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.techfix.app.R;

import java.util.ArrayList;
import java.util.List;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.ViewHolder> {

    public interface Listener {
        void onPayClicked(PaymentDisplay item);
    }

    private List<PaymentDisplay> items = new ArrayList<>();
    private final Listener listener;

    public PaymentAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setItems(List<PaymentDisplay> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_payment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PaymentDisplay item = items.get(position);

        holder.serviceName.setText(item.serviceName);
        holder.branchName.setText(item.branchName);
        holder.amount.setText(String.format("Rs. %.2f", item.amount));

        boolean isPaid = item.existingPayment != null && "Paid".equals(item.existingPayment.status);

        holder.payButton.setVisibility(isPaid ? View.GONE : View.VISIBLE);
        holder.paidBadge.setVisibility(isPaid ? View.VISIBLE : View.GONE);

        holder.payButton.setOnClickListener(v -> listener.onPayClicked(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView serviceName, branchName, amount, paidBadge;
        Button payButton;

        ViewHolder(View itemView) {
            super(itemView);
            serviceName = itemView.findViewById(R.id.pServiceName);
            branchName = itemView.findViewById(R.id.pBranchName);
            amount = itemView.findViewById(R.id.pAmount);
            payButton = itemView.findViewById(R.id.pPayButton);
            paidBadge = itemView.findViewById(R.id.pPaidBadge);
        }
    }
}