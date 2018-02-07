package com.jhalkjar.caoscomp.backend;

import com.codename1.charts.util.ColorUtil;

import static com.codename1.charts.util.ColorUtil.*;

public enum Grade {
    GREEN, YELLOW, BLUE, PURPLE, RED, BLACK, WHITE, NO_GRADE;

    public static int getColorInt(Grade grade){
        if (grade == green){
            return ColorUtil.GREEN;
        }else if(grade == yellow){
            return ColorUtil.YELLOW;
        }else if(grade == blue){
            return ColorUtil.BLUE;
        }else if(grade == purple){
            return ColorUtil.MAGENTA;
        }else if(grade == red){
            return ColorUtil.red(255);
        }else if(grade == black){
            return ColorUtil.BLACK;
        }else if(grade == gray){
            return ColorUtil.LTGRAY;
        }else{
            return 0;
        }
    }
}
