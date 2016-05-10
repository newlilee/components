package com.commons.utils.enumerate;

/**
 * @author chenlixin at 2016年5月10日 下午4:09:47
 */
public enum WeekDay {

    MON(1), TUE(2), WED(3), THU(4), FRI(5), SAT(6), SUN(7);

    final int num;

    private WeekDay(int num) {
        this.num = num;
    }

    public int getNum() {
        return num;
    }

    public static WeekDay valueOf(int num) {
        for (WeekDay wd : WeekDay.values()) {
            if (wd.getNum() == num) {
                return wd;
            }
        }
        throw new IllegalStateException(
                String.format("Unknown week day num [%s]", num));
    }
}
