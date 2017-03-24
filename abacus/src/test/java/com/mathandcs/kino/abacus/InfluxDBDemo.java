package com.mathandcs.kino.abacus;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Random;

@SpringBootApplication
@EnableScheduling
@ComponentScan("com.nothing.*")
public class InfluxDBDemo {

    private static final Logger LOG = LoggerFactory.getLogger(InfluxDBDemo.class);

    private static final String URL = "http://localhost:8086";

    private static final String USERNAME = "admin";

    private static final String PASSWORD = "admin";

    private static final String DATABASE = "helloworld";

    public static void main(String[] args) {
        SpringApplication.run(InfluxDBDemo.class, args);
    }

    @Scheduled(fixedRate = 1000)
    public static void doInsert() {
        Random random = new Random();
        InfluxDBDemo.insert(random.nextInt(1000));
    }

    private static void insert(int num) {
        try {
            InfluxDB db = InfluxDBFactory.connect(URL, USERNAME, PASSWORD);
            db.setDatabase(DATABASE);
            Point.Builder builder = Point.measurement("test_measurement");  // 创建Builder，设置表名
            builder.addField("count", num);  // 添加Field
            builder.tag("TAG_CODE", "TAG_VALUE_" + num);    // 添加Tag
            Point point = builder.build();
            db.write(point);
        } catch (Exception e) {
            LOG.error("Failed to insert into influxDB.", e);
        }
    }

}

