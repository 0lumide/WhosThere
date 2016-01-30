package co.mide.whosthere;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Adapter to populate the search results
 * Created by Olumide on 1/30/2016.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>  {
    public ArrayList<SearchResult> adapterData;

    public RecyclerViewAdapter(){
        adapterData = new ArrayList<>();
    }

    public void clear(){
        adapterData.clear();
        notifyDataSetChanged();
    }

    public void addData(String query, String results){
        adapterData.add(new SearchResult(query, results));
        notifyItemInserted(adapterData.size() - 1);
    }

    public RecyclerViewAdapter(ArrayList<SearchResult> data){
        this();
        if(data != null)
            adapterData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        CardView v = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_search_result, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.phoneNumber.setText(adapterData.get(position).getPhoneNumber());
        holder.name.setText(adapterData.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return adapterData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public TextView phoneNumber;

        public ViewHolder(View view){
            super(view);
            name = (TextView)view.findViewById(R.id.person_name);
            phoneNumber = (TextView)view.findViewById(R.id.person_number);
        }
    }

    public static class SearchResult{
        String phoneNumber;
        String name;

        public SearchResult(String name, String phoneNumber){
            this.name = name;
            this.phoneNumber = phoneNumber;
        }

        public String getName(){
            return name;
        }

        public String getPhoneNumber(){
            return phoneNumber;
        }
    }
}
