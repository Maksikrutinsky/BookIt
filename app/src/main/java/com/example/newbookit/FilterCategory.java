package com.example.newbookit;

import android.widget.Filter;

import java.util.ArrayList;
public class FilterCategory extends Filter {
    //arrayList in which we want to search
    ArrayList<ModelCaegory> filterList;
    //adapter in which filter need to be implemnted
    AdapterCategory adapterCategory;

    //constrctor

    public FilterCategory(ArrayList<ModelCaegory> filterList , AdapterCategory adapterCategory)
    {
        this.filterList = filterList;
        this.adapterCategory = adapterCategory;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results = new FilterResults();
        if (charSequence != null && charSequence.length() > 0)
        {
            //change to upper case , or lower case to avoid case sensitivity
            charSequence = charSequence.toString().toUpperCase();
            ArrayList<ModelCaegory>filteredModel = new ArrayList<>();


            for(int i=0; i < filterList.size();i++){
//validate
                if(filterList.get(i).getCategory().toUpperCase().contains(charSequence)){
                    //add to filterend list
                    filteredModel.add(filterList.get(i));

                }
            }
            results.count = filteredModel.size();
            results.values = filteredModel;

        }
        else{
            results.count = filterList.size();
            results.values = filterList;
        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults results) {
        //apply filter cahnges
        adapterCategory.caegoryArrayList = (ArrayList<ModelCaegory>)results.values;

        adapterCategory.notifyDataSetChanged();


    }
}
