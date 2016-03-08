package com.wicam.d_default_phonebook.list_page;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

import com.wicam.R;
import com.wicam.a_common_utils.ItemFavoriteAsyncTask;
import com.wicam.a_common_utils.WicamColors;
import com.wicam.a_common_utils.account_related.item_detail_comment.ItemData;
import com.wicam.a_common_utils.common_values.MyCache;
import com.wicam.a_common_utils.common_values.Security;
import com.wicam.a_common_utils.common_values.Singleton;
import com.wicam.d_default_phonebook.PhonebookData;
import com.wicam.d_default_phonebook.detail_page.PhonebookDetailActivity;

import java.util.ArrayList;

/**
 * Created by Hyeonmin on 2015-07-10.
 */
public class PhonebookListAdapter extends RecyclerView.Adapter<PhonebookListAdapter.ViewHolder> {

    private PhonebookListActivity phonebookListActivity;
    private ArrayList<ItemData> phonebookList;

    public PhonebookListAdapter(PhonebookListActivity phonebookListActivity, ArrayList<ItemData> phonebookList) {
        this.phonebookListActivity = phonebookListActivity;
        this.phonebookList = phonebookList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.d_phonebook_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        if (!Singleton.create().isCanBindView()) // 필터를 빨리 바꾸던가 해도 리스트를 다 받기 전에는 BindView하지 안도록
            return;

        PhonebookData phonebookData = (PhonebookData)phonebookList.get(position);

        ((RelativeLayout)holder.itemView.findViewById(R.id.recyclerview_loading)).setVisibility(phonebookData.isToShowLoading() && position == phonebookList.size() - 1 ? View.VISIBLE : View.GONE);
        holder.cardView.setVisibility(phonebookData.isEmpty() ? View.GONE : View.VISIBLE);
        ((RelativeLayout)holder.itemView.findViewById(R.id.recyclerview_empty)).setVisibility(!phonebookData.isEmpty() ? View.GONE : View.VISIBLE);
        ((TextView)holder.itemView.findViewById(R.id.empty_word)).setText("아직 입력된 광고·포스터가 없습니다.");
        ((Space)holder.itemView.findViewById(R.id.recyclerview_last_space)).setVisibility(position == phonebookList.size() - 1 ? View.VISIBLE : View.GONE);

        holder.phonebookName.setText(phonebookData.getItemName());
        holder.favoriteToggleBtn.setBackgroundResource(phonebookList.get(position).getMyFavorite() == 0 ? R.drawable.item_list_favorite_off : R.drawable.item_list_favorite_on);

        holder.callButton.setVisibility((phonebookData.getPhone1().equalsIgnoreCase("") && phonebookData.getPhone2().equalsIgnoreCase("")) ? View.GONE : View.VISIBLE);

        if (phonebookData.isEmpty())
            return;

    }

    @Override
    public int getItemCount() {
        return phonebookList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public CardView cardView;
        public TextView phonebookName;
        public ImageButton favoriteToggleBtn, callButton;

        public ViewHolder(View itemView) {
            super(itemView);

            phonebookName = (TextView)itemView.findViewById(R.id.phonebook_list_name);
            favoriteToggleBtn = (ImageButton)itemView.findViewById(R.id.phonebook_list_favorite_toggle_button);
            favoriteToggleBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new ItemFavoriteAsyncTask(phonebookListActivity, phonebookList, getPosition()).execute(new Security().WEB_ADDRESS + "item_favorite_toggle.php?item_id="
                            + ((PhonebookData) phonebookList.get(getPosition())).getItemId() + "&user_id=" + new MyCache(phonebookListActivity).getMyId()
                            + "&default_code=" + new MyCache(phonebookListActivity).getDefaultCode()
                            + "&content_id=" + new MyCache(phonebookListActivity).getContentId() + "&content_type=" + new MyCache(phonebookListActivity).getContentType());
                }
            });

            callButton = (ImageButton)itemView.findViewById(R.id.phonebook_list_call_btn);
            callButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int tel_count = 0;
                    if (!((PhonebookData)phonebookList.get(getPosition())).getPhone1().trim().equalsIgnoreCase(""))
                        tel_count++;
                    if (!((PhonebookData)phonebookList.get(getPosition())).getPhone2().trim().equalsIgnoreCase(""))
                        tel_count++;
                    String[] tel = new String[tel_count];
                    int tel_pointer = 0;
                    if (!((PhonebookData)phonebookList.get(getPosition())).getPhone1().trim().equalsIgnoreCase(""))
                        tel[tel_pointer++] = ((PhonebookData)phonebookList.get(getPosition())).getPhone1().trim();
                    if (!((PhonebookData)phonebookList.get(getPosition())).getPhone2().trim().equalsIgnoreCase(""))
                        tel[tel_pointer++] = ((PhonebookData)phonebookList.get(getPosition())).getPhone2().trim();

                    if (tel_count > 1) {
                        final String phonebook_tel[] = {tel[0], tel[1]};
                        new AlertDialog.Builder(phonebookListActivity).setTitle("번호를 선택하세요")
                                .setItems(phonebook_tel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(Intent.ACTION_DIAL);
                                        String str = phonebook_tel[i];
                                        str = str.replace("-", "");
                                        intent.setData(Uri.parse("tel:" + str));
                                        phonebookListActivity.startActivity(intent);
                                    }
                                }).show();
                    }
                    else {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        String str = tel[0];
                        str = str.replace("-", "");
                        intent.setData(Uri.parse("tel:" + str));
                        phonebookListActivity.startActivity(intent);

                    }
                }
            });

            cardView = (CardView)itemView.findViewById(R.id.phonebook_list_card_view);
            cardView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            phonebookName.setTextColor(new WicamColors().WC_BLUE);
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            phonebookName.setTextColor(new WicamColors().TEXT_NORMAL);
                            break;
                        case MotionEvent.ACTION_UP:
                            phonebookName.setTextColor(new WicamColors().TEXT_NORMAL);

                            // 댓글 등을 위해, 어느 아이템인지 페이지에 들어가면서부터 명시
                            //-------------------------------------------------//
                            Singleton.create().setItemPosition(getPosition());
                            Singleton.create().setItemDataList(phonebookList);
                            //-------------------------------------------------//
                            Intent intent = new Intent(phonebookListActivity, PhonebookDetailActivity.class);
                            phonebookListActivity.startActivityForResult(intent, 0);
                            break;
                    }
                    return false;
                }
            });

        }
    }
}
