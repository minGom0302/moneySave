package com.example.moneysave.etc;

import android.app.Activity;
import android.widget.Toast;

public class Backspace {
    private long backspacePressedTime = 0;
    private final Activity activity;
    private Toast toast;

    // 뒤로가기 누른 화면(Activity)를 받아 저장
    public Backspace(Activity activity) {
        this.activity = activity;
    }

    // 보여줄 Toast msg, 단 선언 시 내용을 작성할 수 있음
    private void showToast(String msg) {
        toast = Toast.makeText(activity, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    // 뒤로가기 누르면서 msg를 넘겼을 때
    public void onBackPressed(String msg) {
        // System.currentTimeMillis : 70년 1월 1일 00시부터 현재까지의 시간을 가져옴
        // 2000 millisecond = 2 second (2초)
        if (System.currentTimeMillis() > backspacePressedTime + 2000) {
            // 0으로 초기화한 pressedTime에 값을 넣음
            backspacePressedTime = System.currentTimeMillis();
            showToast(msg);
            return;
        }

        // 두번 누르면 종료시키고 Toast msg도 캔슬한다.
        if (System.currentTimeMillis() <= backspacePressedTime + 2000) {
            activity.finish();
            toast.cancel();
        }
    }
}
