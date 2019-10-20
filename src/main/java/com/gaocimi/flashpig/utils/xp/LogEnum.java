package com.gaocimi.flashpig.utils.xp;
/**
 * 本地日志枚举
 * @author https://blog.csdn.net/u010598111/article/details/80556437
 *
 */
public enum  LogEnum {
 
 
    BUSSINESS("bussiness"),
 
    PLATFORM("platform"),
 
    DB("db"),
 
    EXCEPTION("exception"),
 
    ;
 
 
    private String category;
 
 
    LogEnum(String category) {
        this.category = category;
    }
 
    public String getCategory() {
        return category;
    }
 
    public void setCategory(String category) {
        this.category = category;
    }
}
