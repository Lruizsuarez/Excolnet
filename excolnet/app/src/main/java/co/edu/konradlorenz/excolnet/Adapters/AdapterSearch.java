package co.edu.konradlorenz.excolnet.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import co.edu.konradlorenz.excolnet.Activities.ProfileActivity;
import co.edu.konradlorenz.excolnet.Entities.Usuario;
import co.edu.konradlorenz.excolnet.Factory.Adapter;
import co.edu.konradlorenz.excolnet.R;

public class AdapterSearch extends RecyclerView.Adapter<AdapterSearch.MyViewHolder>  implements Adapter {
    private ArrayList<Usuario> list;
    private Context context;
    private LinearLayout friendSearched;
    private View view;
    private String ACTIVITY_NAME = "AdapterSearch";

    public AdapterSearch() {
    }

    public AdapterSearch(Context context, ArrayList<Usuario> list) {
        this.context = context;
        this.list = list;
    }

    public ArrayList<Usuario> getList() {
        return list;
    }

    public void setList(ArrayList<Usuario> list) {
        this.list = list;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_search, parent, false);
        friendSearched = view.findViewById(R.id.textViewSearchL);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.name.setText(list.get(position).getDisplayName());
        Glide.with(context).load(list.get(position).getPhotoUrl()).apply(RequestOptions.circleCropTransform()).into(holder.image);

        friendSearched.setOnClickListener(v -> {
            Intent intentSearch = new Intent(view.getContext(), ProfileActivity.class);
            intentSearch.putExtra("ACTIVITY_CALLED_NAME", ACTIVITY_NAME);
            intentSearch.putExtra("USER_ID", list.get(position).getUid());
            intentSearch.putExtra("USER", list.get(position));
            view.getContext().startActivity(intentSearch);
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.user_name_search);
            image = itemView.findViewById(R.id.user_image_search);

        }
    }


}
