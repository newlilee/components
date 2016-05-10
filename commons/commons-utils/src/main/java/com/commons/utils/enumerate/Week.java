package com.commons.utils.enumerate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author chenlixin at 2016年5月10日 下午4:41:37
 */
public class Week {

    private String name;
    @JsonIgnore
    private WeekDay weekDay;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("weekDay")
    public int getWeekDayBy() {
        return weekDay.getNum();
    }

    @JsonProperty("weekDay")
    public void setWeekDayBy(int weekDay) {
        this.weekDay = WeekDay.valueOf(weekDay);
    }

    @Override
    public String toString() {
        return "Week [name=" + name + ", weekDay=" + weekDay + "]";
    }
}
