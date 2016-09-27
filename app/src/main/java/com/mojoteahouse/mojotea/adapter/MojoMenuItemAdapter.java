package com.mojoteahouse.mojotea.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mojoteahouse.mojotea.R;
import com.mojoteahouse.mojotea.data.MojoMenu;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

public class MojoMenuItemAdapter extends RecyclerView.Adapter<MojoMenuItemAdapter.MojoMenuViewHolder> {

    private Context context;
    private LayoutInflater layoutInflater;
    private List<MojoMenu> mojoMenuList;
    private MojoMenuItemClickListener itemClickListener;
    private String priceFormat;

    public interface MojoMenuItemClickListener {

        void onItemClicked(MojoMenu mojoMenu);
    }

    public MojoMenuItemAdapter(Context context,
                               List<MojoMenu> mojoMenuList,
                               MojoMenuItemClickListener itemClickListener) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        Collections.sort(mojoMenuList);
        this.mojoMenuList = mojoMenuList;
        this.itemClickListener = itemClickListener;
        priceFormat = context.getString(R.string.mojo_menu_price_text);
    }

    @Override
    public MojoMenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View menuView = layoutInflater.inflate(R.layout.mojo_menu_list_item, parent, false);
        return new MojoMenuViewHolder(menuView);
    }

    @Override
    public void onBindViewHolder(final MojoMenuViewHolder holder, int position) {
        MojoMenu mojoMenu = mojoMenuList.get(position);

        String imageUri = mojoMenu.getImageUrl();
        if (TextUtils.isEmpty(imageUri)) {
            Picasso.with(context)
                    .load(R.drawable.ic_no_image)
                    .into(holder.mojoMenuImageView);
        } else {
            Picasso.with(context)
                    .load(imageUri)
                    .error(R.drawable.ic_no_image)
                    .into(holder.mojoMenuImageView);
        }

        holder.nameText.setText(mojoMenu.getName());
        holder.chineseNameText.setText(mojoMenu.getChineseName());
        holder.priceText.setText(String.format(priceFormat, mojoMenu.getPrice()));
        if (mojoMenu.isSoldOut()) {
            holder.cardLayout.setAlpha(0.6f);
            holder.cardLayout.setEnabled(false);
        } else {
            holder.cardLayout.setAlpha(1f);
            holder.cardLayout.setEnabled(true);
        }
        holder.newProductTextView.setVisibility(mojoMenu.isNewMenu() ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return mojoMenuList == null ? 0 : mojoMenuList.size();
    }

    public void updateMojoMenuList(List<MojoMenu> mojoMenuList) {
        if (mojoMenuList != null) {
            Collections.sort(mojoMenuList);
            this.mojoMenuList.clear();
            this.mojoMenuList.addAll(mojoMenuList);
            notifyDataSetChanged();
        }
    }


    class MojoMenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CardView cardLayout;
        private ImageView mojoMenuImageView;
        private TextView newProductTextView;
        private TextView nameText;
        private TextView priceText;
        private TextView chineseNameText;

        MojoMenuViewHolder(View itemView) {
            super(itemView);

            cardLayout = (CardView) itemView.findViewById(R.id.mojo_menu_card_view);
            mojoMenuImageView = (ImageView) itemView.findViewById(R.id.mojo_menu_image_view);
            newProductTextView = (TextView) itemView.findViewById(R.id.new_mojo_item_text);
            nameText = (TextView) itemView.findViewById(R.id.name_text);
            priceText = (TextView) itemView.findViewById(R.id.price_text);
            chineseNameText = (TextView) itemView.findViewById(R.id.chinese_name_text);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            MojoMenu mojoMenu = mojoMenuList.get(getLayoutPosition());
            itemClickListener.onItemClicked(mojoMenu);
        }
    }
}
