package com.wicam.d_default_delivery.menu_page;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wicam.R;
import com.wicam.a_common_utils.WicamColors;

import java.util.ArrayList;

/**
 * Created by Hyeonmin on 2015-08-02.
 */
public class DeliveryMenuActivity extends Activity {
    private RecyclerView menuRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<DeliveryMenuData> menuList = new ArrayList<DeliveryMenuData>();
    private Button callButton;
    String phone1;
    String phone2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.d_delivery_menu);

        // 배달상세페이지로부터 전달된 메뉴 문자열을 줄바꿈 기준으로 분할하여 ArryList에 입력
        String menuString = getIntent().getStringExtra("menu");
        phone1 = getIntent().getStringExtra("phone1");
        phone2 = getIntent().getStringExtra("phone2");

        String[] menuArray = menuString.split("\n");
        for (String str : menuArray) {
            if (!str.trim().equalsIgnoreCase(""))
                menuList.add(new DeliveryMenuData(str));
        }

        callButton = (Button)findViewById(R.id.delivery_menu_call_button);
        callButton.setOnTouchListener(new View.OnTouchListener() {
                                          @Override
                                          public boolean onTouch(View v, MotionEvent event) {
                                              switch (event.getAction()) {
                                                  case MotionEvent.ACTION_DOWN:
                                                      ((TextView) findViewById(R.id.delivery_menu_call_text)).setTextColor(new WicamColors().WC_BLUE);
                                                      break;
                                                  case MotionEvent.ACTION_UP:
                                                      ((TextView) findViewById(R.id.delivery_menu_call_text)).setTextColor(new WicamColors().TEXT_NORMAL);

                                                      int tel_count = 0;
                                                      if (phone1 != null && !phone1.trim().equalsIgnoreCase(""))
                                                          tel_count++;
                                                      if (phone2 != null && !phone2.trim().equalsIgnoreCase(""))
                                                          tel_count++;
                                                      String[] tel = new String[tel_count];
                                                      int tel_pointer = 0;
                                                      if (phone1 != null && !phone1.trim().equalsIgnoreCase(""))
                                                          tel[tel_pointer++] = phone1;
                                                      if (phone2 != null && !phone2.trim().equalsIgnoreCase(""))
                                                          tel[tel_pointer++] = phone2;

                                                      if (tel_count == 0)
                                                          return false;

                                                      if (tel_count > 1) {
                                                          final String delivery_tel[] = {tel[0], tel[1]};
                                                          new AlertDialog.Builder(DeliveryMenuActivity.this).setTitle("번호를 선택하세요")
                                                                  .setItems(delivery_tel, new DialogInterface.OnClickListener() {
                                                                      @Override
                                                                      public void onClick(DialogInterface dialogInterface, int i) {
                                                                          Intent intent = new Intent(Intent.ACTION_DIAL);
                                                                          String str = delivery_tel[i];
                                                                          str = str.replace("-", "");
                                                                          intent.setData(Uri.parse("tel:" + str));
                                                                          startActivity(intent);
                                                                      }
                                                                  }).show();

                                                      }
                                                      else {
                                                          Intent intent = new Intent(Intent.ACTION_DIAL);
                                                          String str = tel[0];
                                                          str = str.replace("-", "");
                                                          intent.setData(Uri.parse("tel:" + str));
                                                          startActivity(intent);
                                                      }

                                                      break;
                                              }
                                              return false;
                                          }
                                      });

            menuRecyclerView=(RecyclerView)

            findViewById(R.id.delivery_menu_recycler_view);

            menuRecyclerView.setHasFixedSize(true);
            layoutManager=new

            LinearLayoutManager(this);

            menuRecyclerView.setLayoutManager(layoutManager);

            menuRecyclerView.setAdapter(new

                            DeliveryMenuAdapter(this, menuList)

            );
        }

    }
