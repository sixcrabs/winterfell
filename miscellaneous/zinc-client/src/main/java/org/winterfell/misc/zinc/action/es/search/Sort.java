package org.winterfell.misc.zinc.action.es.search;

import java.io.Serializable;

/**
 * @author Alex
 * @version v1.0 2022/11/16
 */
public class Sort implements Serializable {

    private static final long serialVersionUID = -5402033073130957949L;

    /**
     * 排序字段
     */
    private final String field;

    /**
     * 正序还是倒序
     */
    private Sorting order;

    public Sort(String field) {
        this.field = field;
    }


    /**
     * string 值模式 倒叙 -xxx
     *
     * @return
     */
    public String toValue() {
        return (Sorting.DESC.equals(order) ? "-" : "").concat(field);
    }

    /**
     * -xx
     * xx
     * @param value
     * @return
     */
    public static Sort fromValue(String value) {
        if (value.startsWith("-")){
            return new Sort(value.substring(1)).setOrder(Sorting.DESC);
        }
        return new Sort(value).setOrder(Sorting.ASC);
    }


    public enum Sorting {
        //
        ASC("asc"),
        DESC("desc");

        private final String name;

        Sorting(String s) {
            name = s;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public String getField() {
        return field;
    }

    public Sorting getOrder() {
        return order;
    }

    public Sort setOrder(Sorting order) {
        this.order = order;
        return this;
    }
}
