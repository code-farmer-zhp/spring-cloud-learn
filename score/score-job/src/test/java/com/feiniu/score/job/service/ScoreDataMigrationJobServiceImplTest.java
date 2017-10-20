package com.feiniu.score.job.service;

import com.feiniu.score.job.TestBasicJob;
import org.apache.commons.lang3.time.FastDateFormat;
import org.dbunit.dataset.DataSetException;
import org.junit.Test;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class ScoreDataMigrationJobServiceImplTest extends TestBasicJob {

	FastDateFormat sdf = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

	@Test
	public void test() {
		try {
			backupCustomTable(tables);
		} catch (DataSetException | IOException e) {
			e.printStackTrace();
		}
		getTableAndDBBaseInfo(memGuid);
	}

	//@Override
	@Test
	public void doTest() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2015);
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.DATE, 14);
		calendar.set(Calendar.HOUR_OF_DAY, 17);
		calendar.set(Calendar.MINUTE, 4);
		calendar.set(Calendar.SECOND, 34);
		Date startTime = calendar.getTime();
		System.out.println(sdf.format(startTime));
		
		calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2015);
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.DATE, 14);
		calendar.set(Calendar.HOUR_OF_DAY, 17);
		calendar.set(Calendar.MINUTE, 4);
		calendar.set(Calendar.SECOND, 36);
		Date endTime = calendar.getTime();
		System.out.println(sdf.format(endTime));
		//ccoreDataMigrationJobService.migrationScoreMainData(startTime, endTime);
	}

	public static void main(String[] args) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2015);
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.DATE, 14);
		calendar.set(Calendar.HOUR_OF_DAY, 17);
		calendar.set(Calendar.MINUTE, 4);		
		calendar.set(Calendar.SECOND, 35);
		FastDateFormat sdf = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
		System.out.println(sdf.format(calendar.getTime()));
	}
}
