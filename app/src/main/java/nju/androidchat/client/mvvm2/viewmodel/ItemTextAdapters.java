package nju.androidchat.client.mvvm2.viewmodel;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.databinding.BindingAdapter;

import de.hdodenhof.circleimageview.CircleImageView;
import nju.androidchat.client.R;
import nju.androidchat.client.mvvm2.model.Direction;

public class ItemTextAdapters {
    @BindingAdapter({"message_type"})
    public static void setLayout(CircleImageView circleImageView, Direction direction) {
        if (direction.equals(Direction.SEND)) {
            circleImageView.setImageResource(R.mipmap.ic_head_default_right);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) circleImageView.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
            circleImageView.setLayoutParams(params);
        } else {
            circleImageView.setImageResource(R.mipmap.ic_head_default_left);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) circleImageView.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
            circleImageView.setLayoutParams(params);
        }
    }

    @BindingAdapter({"layout_type"})
    public static void setLayoutLinear(LinearLayout linearLayout, Direction direction) {
        if (direction.equals(Direction.SEND)) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) linearLayout.getLayoutParams();
            params.addRule(RelativeLayout.START_OF, R.id.chat_item_header);
            params.addRule(RelativeLayout.ALIGN_PARENT_START);
            linearLayout.setLayoutParams(params);
        } else {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) linearLayout.getLayoutParams();
            params.addRule(RelativeLayout.END_OF, R.id.chat_item_header);
            linearLayout.setLayoutParams(params);
        }
    }

}
