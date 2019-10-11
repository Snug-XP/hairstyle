package com.gaocimi.flashpig.utils;

import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liyutg
 * @Date 2019/1/8 1:17
 * @description
 */

/** 自适应 日期格式化 属性编辑器
 * Created by wikeL on 2016/6/17.
 */
public class CustomDatePropertyEditor implements PropertyEditor {
    private DateFormat defaultDateFormat;
    public static final String defaultDatePatter = "yyyy-MM-dd HH:mm:ss";
    private boolean allowEmpty;

    private Map<String ,DateFormat> dateFormatMap = new HashMap<String, DateFormat>();

    private Map<String,String> datePatternMap = new HashMap();
    public static final Map<String,String> defaultDatePatternMap = new HashMap();
    static {
        defaultDatePatternMap.put("yyyy-MM-dd"  ,"[0-9]{4}-[0-9]{2}-[0-9]{2}");
        defaultDatePatternMap.put("yyyy-MM-dd HH:mm:ss"  ,"[0-9]{4}-[0-9]{2}-[0-9]{2}[\\s]{1,2}[0-9]{2}:[0-9]{2}:[0-9]{2}");
        defaultDatePatternMap.put("yyyy/MM/dd"  ,"[0-9]{4}/[0-9]{2}/[0-9]{2}");
        defaultDatePatternMap.put("yyyy/MM/dd HH:mm:ss"  ,"[0-9]{4}/[0-9]{2}/[0-9]{2}[\\s]{1,2}[0-9]{2}:[0-9]{2}:[0-9]{2}");
    }

    //date
    private Object value;

    /**
     *
     * @param dateFormat 默认日期格式化
     * @param allowEmpty 是否允许为空
     * @param datePatternMap 日期模式 , 日期模式对应的正则表达式
     */
    public CustomDatePropertyEditor(DateFormat dateFormat, boolean allowEmpty, Map<String,String> datePatternMap) {
        if(null == dateFormat){
            defaultDateFormat = new SimpleDateFormat(defaultDatePatter);
            defaultDateFormat.setLenient(false);
        }else{
            this.defaultDateFormat = dateFormat;
        }
        this.allowEmpty = allowEmpty;

        this.adDdatePattern(datePatternMap);
        this.initDateFormatMap();
    }

    private void initDateFormatMap(){
        for(Map.Entry<String,String> entry : this.getDatePatternMap().entrySet()){
            try{
                SimpleDateFormat dateFormat = new SimpleDateFormat(entry.getKey());
                dateFormat.setLenient(false);
                this.getDateFormatMap().put(entry.getKey() ,dateFormat);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getAsText() {
        Date value = (Date)this.getValue();

        String dateStr = "";
        if(null != value && null != this.defaultDateFormat){
            dateStr = this.defaultDateFormat.format(value);
        }

        return dateStr;
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if(this.allowEmpty && ( null == text || text.trim().length() == 0 ) ) {
            this.setValue((Object)null);
            return;
        }
        Date d = null;
        for(Map.Entry<String ,DateFormat> e : this.getDateFormatMap().entrySet()){
            String datePatter = e.getKey();
            DateFormat format = e.getValue();
            String regex = this.getDatePatternMap().get(datePatter);
            if(null != regex && text.matches(regex)){
                //System.err.println("datePatter: " + datePatter + " ,regex: " + regex + " ,format: " + format);
                try {
                    d = (Date)format.parse(text);
                } catch (Exception ex) {
                    String msg = String.format("dateFormat[%s] Could not parse date [%s] : " ,e.getValue() ,text);
                    throw new IllegalArgumentException(msg, ex);
                }
                break;
            }
        }
        if(null == d && null != this.getDefaultDateFormat()){
            try {
                d = (Date)this.getDefaultDateFormat().parse(text);
            } catch (Exception ex) {
                String msg = String.format("defaultDateFormat[%s] Could not parse date [%s] : " ,this.getDefaultDateFormat() ,text);
                throw new IllegalArgumentException(msg, ex);
            }
        }
        this.setValue(d);
    }

    @Override
    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public boolean isPaintable() {
        return false;
    }

    @Override
    public void paintValue(Graphics gfx, Rectangle box) {

    }

    @Override
    public String getJavaInitializationString() {
        return null;
    }

    @Override
    public String[] getTags() {
        return null;
    }

    @Override
    public Component getCustomEditor() {
        return null;
    }

    @Override
    public boolean supportsCustomEditor() {
        return false;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {

    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {

    }

    public boolean isAllowEmpty() {
        return allowEmpty;
    }

    public void setAllowEmpty(boolean allowEmpty) {
        this.allowEmpty = allowEmpty;
    }

    public DateFormat getDefaultDateFormat() {
        return defaultDateFormat;
    }

    public void setDefaultDateFormat(DateFormat defaultDateFormat) {
        this.defaultDateFormat = defaultDateFormat;
    }

    public Map<String,String> getDatePatternMap() {
        if(null == this.datePatternMap){
            this.datePatternMap = new HashMap();
        }
        if(datePatternMap.isEmpty()){
            datePatternMap.putAll(defaultDatePatternMap);
        }
        return datePatternMap;
    }

    private void adDdatePattern(Map<String,String> datePatternMap) {
        if(null == datePatternMap){
            return;
        }
        for(Map.Entry<String,String> entry : datePatternMap.entrySet()){
            if(null != entry.getKey() && null != entry.getValue()){
                this.getDatePatternMap().put(entry.getKey() , entry.getValue());
            }
        }
    }

    public Map<String, DateFormat> getDateFormatMap() {
        if(null == dateFormatMap){
            dateFormatMap = new HashMap<String, DateFormat>();
        }
        return dateFormatMap;
    }
}
