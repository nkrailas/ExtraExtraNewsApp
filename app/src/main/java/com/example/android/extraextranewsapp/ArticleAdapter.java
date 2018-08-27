package com.example.android.extraextranewsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ArticleAdapter extends ArrayAdapter<Article> {

    /**
     * Create a custom constructor.
     *
     * @param context      Current context used to inflate the layout file.
     * @param articlesList List of articles that is the data source for the adapter.
     */

    public ArticleAdapter(Context context, List<Article> articlesList) {
        super(context, 0, articlesList);
    }

    /**
     * Create view for a ListView.
     *
     * @param position    Position in list of data to be displayed in list item view.
     * @param convertView Recycled view to populate.
     * @param parent      Parent ViewGroup used for inflation.
     */

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing list item view (aka convertView) can being reused.
        // If not (aka convertView is null), then inflate a new list item view.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.article_list_item,
                    parent, false);

            // Get the Article object at this position in the list of articles.
            Article currentArticle = getItem(position);

            // Find the TextViews with view IDs for section, title, author, date.
            // Get and set the values for section, title, author, date.
            TextView sectionView = listItemView.findViewById(R.id.article_section);
            sectionView.setText(currentArticle.getSectionName());

            TextView titleView = listItemView.findViewById(R.id.article_title);
            titleView.setText(currentArticle.getArticleTitle());

            TextView authorView = listItemView.findViewById(R.id.article_author);
            authorView.setText(currentArticle.getArticleAuthor());

            TextView dateView = listItemView.findViewById(R.id.article_date);
            dateView.setText(currentArticle.getWebPubDate());

        }

        return listItemView;

    }
}
