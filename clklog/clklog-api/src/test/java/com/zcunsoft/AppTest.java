package com.zcunsoft;

import com.zcunsoft.clklog.api.utils.TimeUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * Unit test for simple App.
 */
public class AppTest {
    @TestFactory
    Collection<DynamicTest> dynamicTestsFromCollection() throws IOException {
        SimpleDateFormat yMdFormat = new SimpleDateFormat("yyyy-MM-dd");

        List<DynamicTest> dynamicTestList = new ArrayList<>();


        //test week
        long[] weekFrame = TimeUtils.getCurrentWeekTimeFrame(Timestamp.valueOf("2023-06-20 00:00:00"));
        long[] targetWeekArr = new long[]{getTimestamp("2023-06-19"), getTimestamp("2023-06-25")};
        long[] resultWeekArr = new long[]{weekFrame[0], weekFrame[1]};
        dynamicTestList.add(dynamicTest("test week frame", () -> Assertions.assertArrayEquals(targetWeekArr, resultWeekArr, "ok")));

        //test month
        long[] monthFrame = TimeUtils.getCurrentMonthTimeFrame(Timestamp.valueOf("2023-06-20 00:00:00"));
        long[] targetMonthArr = new long[]{getTimestamp("2023-06-01"), getTimestamp("2023-06-30")};
        long[] resultMonthArr = new long[]{monthFrame[0], monthFrame[1]};
        dynamicTestList.add(dynamicTest("test month frame", () -> Assertions.assertArrayEquals(targetMonthArr, resultMonthArr, "ok")));

        //test year
        long[] yearFrame = TimeUtils.getCurrentYearTimeFrame(Timestamp.valueOf("2023-06-20 00:00:00"));
        long[] targetYearArr = new long[]{getTimestamp("2023-01-01"), getTimestamp("2023-12-31")};
        long[] resultYearArr = new long[]{yearFrame[0], yearFrame[1]};
        dynamicTestList.add(dynamicTest("test year frame", () -> Assertions.assertArrayEquals(targetYearArr, resultYearArr, "ok")));

        return dynamicTestList;
    }

    private long getTimestamp(String time) {
        return Timestamp.valueOf(time + " 00:00:00").getTime();
    }
}
