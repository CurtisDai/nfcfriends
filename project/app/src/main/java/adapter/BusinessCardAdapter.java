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
import android.widget.Toast;

import com.nfc.application.BusinessCard;
import com.nfc.application.FlipAnimator;
import com.nfc.application.R;

import java.util.List;
import android.net.Uri;



import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import library.CardAdapterHelper;

public class BusinessCardAdapter extends RecyclerView.Adapter<BusinessCardAdapter.ViewHolder>{
    private List<BusinessCard> mBusinessCardList;
    private CardAdapterHelper mCardAdapterHelper = new CardAdapterHelper();
    private Context context;
    LinearLayout[] card_front_arr;
    RelativeLayout[] card_back_arr;

    private AnimatorSet mRightOutAnimatorSet;
    private AnimatorSet mLeftInAnimatorSet;

    //AnimatorSet  mRightOutSet;
    //Animator mLeftInSet;

    public BusinessCardAdapter(Context context, List<BusinessCard> BusinessCardList){
        mBusinessCardList = BusinessCardList;
        this.mBusinessCardList = BusinessCardList;
        this.context = context;
        card_back_arr = new RelativeLayout[BusinessCardList.size()];
        card_front_arr = new LinearLayout[BusinessCardList.size()];
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        View businessCardView;
        LinearLayout card_front;
        RelativeLayout card_back;

        public ViewHolder(View cardView){
            super(cardView);
            businessCardView = cardView;
            card_front = itemView.findViewById(R.id.item_front_ly);
            card_back = itemView.findViewById(R.id.item_back_ly);
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
    }

    @Override
    public int getItemCount(){
        return mBusinessCardList.size();
    }

}
