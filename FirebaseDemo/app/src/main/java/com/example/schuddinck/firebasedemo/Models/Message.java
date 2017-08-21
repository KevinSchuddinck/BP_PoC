package com.example.schuddinck.firebasedemo.Models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by SCHUDDINCK on 04-Jul-17.
 */

@IgnoreExtraProperties
public class Message
{
    public Date date;
    public String body;

    public Message(String body, Date date)
    {
        this.date = date;
        this.body = body;
    }

    public Message()
    {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", body);
        result.put("date", date);

        return result;
    }

    @Override
    public String toString()
    {
        return body;
    }
}
