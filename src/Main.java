import data.Sales;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Collections;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {

        try {
            // establishing connection..
            Connection conn = DriverManager.getConnection("jdbc:sqlite:" + System.getProperty("user.dir") + "/src/data/database.db");
            //conn.setAutoCommit(false);

            System.out.println("Connection established!");

            // adding DB if not exists
            // creating table if not exists
            PreparedStatement create = conn.prepareStatement("""
                    CREATE TABLE if not exists sales (
                        region TEXT,
                        country TEXT,
                        item_type TEXT,
                        sales_channel TEXT,
                        order_priority TEXT,
                        order_date TEXT,
                        units_sold REAL,
                        total_profit REAL
                    );
                    """);

            var sycc = create.execute();
            System.out.println("Created database? " + sycc);

            // updates for all except SELECT, for SELECT - query
            // create.executeUpdate();
            // create.close();

            // open the CSV file
            BufferedReader reader = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/src/data/Продажа.csv"));
            System.out.println(".csv opened to read!");

            // reading header and columns
            // pretty useless : we know them beforehand
            String header = reader.readLine();
            String[] columns = header.split(",");

            // create a prepared statement for inserting the data
            String placeholders = String.join(",", Collections.nCopies(columns.length, "?"));
            String sql = String.format("INSERT INTO sales (region, country, item_type, sales_channel," +
                    "order_priority, order_date, units_sold, total_profit) VALUES (%s)", placeholders);
            PreparedStatement ps = conn.prepareStatement(sql);

            // creating school object and passing all strings to be parsed:
            // read the remaining lines of the CSV file and insert the data into the database
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");

                // parsing array to school obj
                Sales s = new Sales(values);

                Object[] arr = s.getArray();

                System.out.println("Got: " + s.getData());

                for (int i = 0; i < values.length; i++) {
                    if (i < 6) {
                        ps.setString(i + 1, (String) arr[i]);
                    } else {
                        ps.setDouble(i + 1, (Double) arr[i]);
                    }
                }

                // executing addition of new element
                //var executionResult = ps.executeUpdate();
                //System.out.println("Adding new element, status: " + executionResult);
            }
            System.out.println("Data from .csv added to .db successfully!");

            /*
            Задание №1
            Постройте график по
            общему кол-ву проданных товаров, объединив их по регионам
            */
            PreparedStatement task1 = conn.prepareStatement("""
                    select region, sum(units_sold) as num
                    from sales
                    group by region
                    """);

            ResultSet r1 = task1.executeQuery();
            System.out.println("Задание №1");

            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            // getting data
            while (r1.next()) {
                String region = r1.getString("region");
                double profits = r1.getDouble("num");
                System.out.println(region + ": " + String.format("%.2f", profits));
                dataset.addValue(profits, region, region);
            }

            // Create a chart
            JFreeChart chart = ChartFactory.createBarChart(
                    "Regions", // chart title
                    "Region", // domain axis label
                    "Value", // range axis label
                    dataset, // data
                    PlotOrientation.VERTICAL, // orientation
                    false, // include legend
                    true, // tooltips?
                    false // URLs?
            );

            // Create a panel to display the chart
            ChartPanel panel = new ChartPanel(chart);

            // Create a frame to hold the chart
            JFrame frame = new JFrame("Bar Chart Example");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(panel);
            frame.pack();
            frame.setVisible(true);

            /*
            Задание №2
            Выведите в консоль страну с самым высоким общим доходом среди регионов европы и азии
             */
            PreparedStatement task2 = conn.prepareStatement("""
                    SELECT country, sum(total_profit) as profits  
                                           FROM sales
                                           WHERE region IN ('Asia', 'Europe')
                                           GROUP BY country
                                           order by profits desc 
                                           limit 1
                    """);

            ResultSet r2 = task2.executeQuery();
            System.out.println("Задание №2");

            // getting data
            while (r2.next()) {
                String county = r2.getString("country");
                double profits = r2.getDouble("profits");
                System.out.println(county + ": " + String.format("%.2f", profits));
            }

            /*
            Задание №3
            Найдите страну с общим доходом 420-440 тыс. среди
            регионов Бл.Восток-Сев.Африка и Субсах.Африка с
            самым высоким общим доходом
             */
            PreparedStatement task3 = conn.prepareStatement("""
                    select * from (
                    SELECT country, sum(total_profit) as profits  
                                           FROM sales
                                           WHERE region IN ('Sub-Saharan Africa', 'Middle East and North Africa')
                                           GROUP BY country
                                           order by profits desc 
                                           )
                    where profits >= 420000 and profits <= 440000
                    """);

            ResultSet r3 = task3.executeQuery();
            System.out.println("Задание №3");

            // getting data
            while (r3.next()) {
                String county = r3.getString("country");
                double avgStudents = r3.getDouble("profits");
                System.out.println(county + ": " + String.format("%.2f", avgStudents));
            }


        } catch (SQLException e) {
            System.out.println("Error: " + e.getSQLState());
            System.out.println(e.getMessage());
        } catch (FileNotFoundException e) {
            System.out.println("Can't open .csv file!");
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println("Can't read line from .csv file!");
            System.out.println(e.getMessage());
        }

    }
}