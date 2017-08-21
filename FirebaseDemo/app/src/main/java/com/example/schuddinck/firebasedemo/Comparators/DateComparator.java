package com.example.schuddinck.firebasedemo.Comparators;

import com.example.schuddinck.firebasedemo.Models.Message;

import java.util.Comparator;

/**
 * Created by SCHUDDINCK on 06-Jul-17.
 */

public class DateComparator implements Comparator<Message>
{
    @Override
    public int compare(Message o1, Message o2) {
        return o1.date.compareTo(o2.date);
    }
}
