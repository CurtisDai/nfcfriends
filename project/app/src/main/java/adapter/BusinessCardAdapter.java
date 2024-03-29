package adapter;

import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.nfc.application.BusinessCard;
import com.nfc.application.FlipAnimator;
import com.nfc.application.R;

import java.util.List;
import android.net.Uri;



import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import library.CardAdapterHelper;

public class BusinessCardAdapter extends RecyclerView.Adapter<BusinessCardAdapter.ViewHolder>{
    private List<BusinessCard> mBusinessCardList;
    private CardAdapterHelper mCardAdapterHelper = new CardAdapterHelper();
    private Context context;
    LinearLayout[] card_front_arr;
    RelativeLayout[] card_back_arr;

    private AnimatorSet mRightOutAnimatorSet;
    private AnimatorSet mLeftInAnimatorSet;

    public BusinessCardAdapter(Context context, List<BusinessCard> BusinessCardList){
        mBusinessCardList = BusinessCardList;
        this.mBusinessCardList = BusinessCardList;
        this.context = context;
        card_back_arr = new RelativeLayout[BusinessCardList.size()];
        card_front_arr = new LinearLayout[BusinessCardList.size()];
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView t_name;
        TextView t_telephone;
        TextView t_orgnization;
        TextView t_email;
        TextView t_location;
        View businessCardView;
        LinearLayout card_front;
        RelativeLayout card_back;
        CircleImageView t_image;
        ImageView f_image;

        public ViewHolder(View cardView){
            super(cardView);
            businessCardView = cardView;
            card_front = itemView.findViewById(R.id.item_front_ly);
            card_back = itemView.findViewById(R.id.item_back_ly);
            t_name = card_back.findViewById(R.id.t_name);
            t_telephone= card_back.findViewById(R.id.t_call);
            t_orgnization = card_back.findViewById(R.id.t_orgnization);
            t_email = card_back.findViewById(R.id.t_email);
            t_location = card_back.findViewById(R.id.t_location);
            t_image = card_back.findViewById(R.id.icon_image);
            f_image = card_front.findViewById(R.id.imageView);
        }

    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view_main = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_item, parent, false);
        final ViewHolder holder = new ViewHolder(view_main);
        mCardAdapterHelper.onCreateViewHolder(parent, view_main);
        holder.businessCardView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int position = holder.getAdapterPosition();
                Boolean front = mBusinessCardList.get(position).isFront();
                Log.d("asd", String.valueOf(position));
                FlipAnimator.flipView(context,
                        card_front_arr[position],
                        card_back_arr[position],
                        front);
                mBusinessCardList.get(position).setFront(!front);
            }
        });


        ImageView email = holder.card_back.findViewById(R.id.email);

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TO-DO show the map activity
                Toast.makeText(context, "show email", Toast.LENGTH_LONG).show();

                String address = "awesomeyeli@gmail.com";

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"+address));

                context.startActivity(emailIntent);
            }
        });


        ImageView location = (ImageView) holder.card_back.findViewById(R.id.location);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TO-DO show the map activity
                Toast.makeText(context, "show map", Toast.LENGTH_LONG).show();
                String address = "university of melbourne";
                Intent intent = new Intent(context,com.nfc.application.MapsActivity.class);
                intent.putExtra("address",address);
                context.startActivity(intent);
            }
        });

        ImageView call = (ImageView)holder.card_back.findViewById(R.id.call);
        call.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //TO-DO show call others
                Toast.makeText(context, "show telephone", Toast.LENGTH_LONG).show();
                String number  ="12345";
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+number));

                context.startActivity(intent);


            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        mCardAdapterHelper.onBindViewHolder(holder.itemView, position, getItemCount());
        card_back_arr[position] = holder.card_back;
        card_front_arr[position] = holder.card_front;
        Log.d("position", String.valueOf(position));
        BusinessCard card = mBusinessCardList.get(position);
        Glide.with(context)
                .load(card.getCover_url())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(holder.f_image);
        holder.t_name.setText(card.getName());
        holder.t_location.setText(card.getAddress());
        holder.t_email.setText(card.getEmail());
        holder.t_orgnization.setText(card.getOrganization());
        holder.t_telephone.setText(card.getTelephone());
        Glide.with(context)
                .load(card.getProfilePic_url())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(holder.t_image);
    }

    @Override
    public int getItemCount(){
        return mBusinessCardList.size();
    }

}
