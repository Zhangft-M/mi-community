package org.mi.common.core.serializer;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-02 19:39
 **/
public class CustomDateFormat extends DateFormat {

    private DateFormat dateFormat;

    private SimpleDateFormat format1 = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");

    public CustomDateFormat(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    @Override
    public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
        return dateFormat.format(date, toAppendTo, fieldPosition);
    }

    @Override
    public Date parse(String source, ParsePosition pos) {
        Date date = null;

        try {

            date = format1.parse(source, pos);
        } catch (Exception e) {

            date = dateFormat.parse(source, pos);
        }

        return date;
    }

    @Override
    public Object clone() {
        Object format = dateFormat.clone();
        return new CustomDateFormat((DateFormat) format);
    }
}
