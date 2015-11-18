package technology.mainthread.apps.isitup.view.adapter;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import technology.mainthread.apps.isitup.R;
import technology.mainthread.apps.isitup.model.IsItUpInfo;
import technology.mainthread.apps.isitup.model.StatusCode;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.ViewHolder> {

    private final Resources resources;
    private final Listener listener;
    private List<IsItUpInfo> favourites;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        View container;
        @Bind(R.id.status_icon)
        ImageView statusIcon;
        @Bind(R.id.txt_label)
        TextView label;
        @Bind(R.id.txt_detail)
        TextView detail;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            container = view;
        }
    }

    public FavouritesAdapter(Resources resources, List<IsItUpInfo> favourites, Listener listener) {
        this.resources = resources;
        this.favourites = favourites;
        this.listener = listener;
    }

    public void reloadData(List<IsItUpInfo> data) {
        favourites = data;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return favourites.get(position).getId();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favourite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final IsItUpInfo item = favourites.get(position);
        holder.statusIcon.setImageResource(item.getStatusCode() == StatusCode.UP ? R.drawable.ic_up_arrow : R.drawable.ic_down_arrow);
        holder.statusIcon.setContentDescription(resources.getString(item.getStatusCode() == StatusCode.UP ? R.string.status_up : R.string.status_down));
        holder.label.setTextColor(resources.getColor(item.getStatusCode() == StatusCode.UP ? R.color.primary : R.color.second_primary));
        holder.label.setText(item.getDomain());
        if (item.getStatusCode() == StatusCode.UP) {
            holder.detail.setText(item.getResponseCode() + ", " + (int) (item.getResponseTime() * 1000) + "ms");
        } else {
            CharSequence relativeTime = DateUtils.getRelativeTimeSpanString(item.getLastChecked());
            holder.detail.setText(relativeTime);
        }
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClicked(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return favourites.size();
    }

    public interface Listener {
        void onItemClicked(IsItUpInfo info);
    }
}
