package data;

import java.util.ArrayList;
import java.util.List;

public class Sales {
    String region;
    String country;
    String item_type;
    String sales_channel;
    String order_priority;
    String order_date;
    double units_sold;
    double total_profit;

    public double tryParseDouble(String value, int defaultVal) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }
    public Sales(String[] data){
        this.region = data[0];
        this.country = data[1];
        this.item_type = data[2];
        this.sales_channel = data[3];
        this.order_priority = data[4];

        this.order_date = data[5];
        this.units_sold = tryParseDouble(data[6], 0);
        this.total_profit = tryParseDouble(data[7], 0);
    }

    public String getData(){
        return region + "," + country + "," + item_type + "," + sales_channel + "," +
                order_priority + "," + order_date + "," + units_sold + "," + total_profit;
    }

    public Object[] getArray(){
        List<Object> resList = new ArrayList<>();
        resList.add(region);
        resList.add(country);
        resList.add(item_type);
        resList.add(sales_channel);
        resList.add(order_priority);
        resList.add(order_date);
        resList.add(units_sold);
        resList.add(total_profit);

        return resList.toArray();
    }
}
