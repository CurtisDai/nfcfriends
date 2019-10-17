package adapter;

import android.animation.AnimatorSet;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.nfc.application.BusinessCard;
import com.nfc.application.FlipAnimator;
import com.nfc.application.R;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class BusinessCardAdapter extends RecyclerView.Adapter<BusinessCardAdapter.ViewHolder>{
    private List<BusinessCard> mBusinessCardList;

    private Context context;
    LinearLayout[] card_back_arr, card_front_arr;

    private AnimatorSet mRightOutAnimatorSet;
    private AnimatorSet mLeftInAnimatorSet;

    public static boolean isFront = false;  //是否显示背面

    //AnimatorSet  mRightOutSet;
    //Animator mLeftInSet;

    public BusinessCardAdapter(Context context, List<BusinessCard> BusinessCardList){
        mBusinessCardList = BusinessCardList;
        this.mBusinessCardList = BusinessCardList;
        this.context = context;
        card_back_arr = new LinearLayout[BusinessCardList.size()];
        card_front_arr = new LinearLayout[BusinessCardList.size()];
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        View businessCardView;
        LinearLayout card_back, card_front;

        public ViewHolder(View cardView){
            super(cardView);
            businessCardView = cardView;
            card_front = (LinearLayout) itemView.findViewById(R.id.item_front_ly);
            card_back = (LinearLayout) itemView.findViewById(R.id.item_back_ly);
        }

    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view_main = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_item, parent, false);
        final ViewHolder holder = new ViewHolder(view_main);
        holder.businessCardView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int position = holder.getAdapterPosition();
                if (isFront) {
                    isFront = false;
                    mBusinessCardList.get(position).setFront(false);
                } else {
                    isFront = true;
                    mBusinessCardList.get(position).setFront(true);
                }
                Log.d("asd", String.valueOf(position));
                FlipAnimator.flipView(context,
                        card_back_arr[position],
                        card_front_arr[position],
                        mBusinessCardList.get(position).isFront(),
                        mBusinessCardList.get(position).getPosition());
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        card_back_arr[position] = holder.card_back;
        card_front_arr[position] = holder.card_front;
    }

    @Override
    public int getItemCount(){
        return mBusinessCardList.size();
    }

}
