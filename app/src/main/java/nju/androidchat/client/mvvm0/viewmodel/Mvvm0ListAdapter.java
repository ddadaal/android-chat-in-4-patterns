package nju.androidchat.client.mvvm0.viewmodel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import java.util.List;
import java.util.Objects;

import lombok.AllArgsConstructor;
import nju.androidchat.client.BR;
import nju.androidchat.client.R;
import nju.androidchat.client.mvvm0.custom.Direction;
import nju.androidchat.client.mvvm0.model.ClientMessageObservable;

@AllArgsConstructor
public class Mvvm0ListAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<ClientMessageObservable> list;

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewDataBinding viewDataBinding;
        ClientMessageObservable message = list.get(position);
        boolean isSend = message.getDirection().equals(Direction.SEND);
        int layoutId = isSend ? R.layout.item_text_receive_mvvm : R.layout.item_text_send_mvvm;
        if (convertView == null) {
            viewDataBinding = DataBindingUtil.inflate(layoutInflater, layoutId, null, false);
        } else {
            viewDataBinding = DataBindingUtil.getBinding(convertView);
        }
        Objects.requireNonNull(viewDataBinding).setVariable(BR.messageBean, list.get(position));
//这里千万不要写viewDataBinding.getRoot().getRootView()，导向了同一个，会导致滚动时崩溃
        return viewDataBinding.getRoot();
    }
}
