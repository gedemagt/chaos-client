package com.jhalkjar.caoscomp.backend;

import com.codename1.charts.util.ColorUtil;

import static com.codename1.charts.util.ColorUtil.*;

public enum Grade {
    GREEN, YELLOW, BLUE, PURPLE, RED, BLACK, GRAY, NO_GRADE;

    public static int getColorInt(Grade grade){
        if (grade == GREEN){
            return ColorUtil.GREEN;
        }else if(grade == YELLOW){
            return ColorUtil.YELLOW;
        }else if(grade == BLUE){
            return ColorUtil.BLUE;
        }else if(grade == PURPLE){
            return ColorUtil.MAGENTA;
        }else if(grade == RED){
            return ColorUtil.red(255);
        }else if(grade == BLACK){
            return ColorUtil.BLACK;
        }else if(grade == GRAY){
            return ColorUtil.LTGRAY;
        }else{
            return 0;
        }
    }
}
