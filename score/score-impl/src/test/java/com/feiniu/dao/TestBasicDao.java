package com.feiniu.dao;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.feiniu.score.dao.score.ScoreCommentDetailDao;
import com.feiniu.score.dao.score.ScoreCommonDao;
import com.feiniu.score.dao.score.ScoreGetBillDao;
import com.feiniu.score.dao.score.ScoreGetCommmentDetailDao;
import com.feiniu.score.dao.score.ScoreGetOrderDetailDao;
import com.feiniu.score.dao.score.ScoreMainLogDao;
import com.feiniu.score.dao.score.ScoreMemberDao;
import com.feiniu.score.dao.score.ScoreOrderDetailDao;
import com.feiniu.score.dao.score.ScoreYearDao;
import com.feiniu.score.dao.score.ScoreYearLogDao;
import com.feiniu.score.datasource.DataSourceUtils;
import com.feiniu.score.util.ShardUtils;



public abstract class TestBasicDao {
	//Dbunit隔离测试使用
	public static IDatabaseConnection dbunitCon;
	private  File tempFile;
	public IDataSet ds ;
	
	public static String memGuid="00004A51-DA01-2C45-216B-309AEEAA6945";
	 
	public static String[] tables = {"score_member","score_main_log","score_order_detail","score_year","score_year_log"};
	public static ClassPathXmlApplicationContext context = null;

	public static ScoreMainLogDao scoreMainLogDao =null;
	public static ScoreMemberDao scoreMemberDao = null;
	public static ScoreOrderDetailDao scoreOrderDetailDao = null;
	public static ScoreYearDao scoreYearDao = null;
	public static ScoreYearLogDao scoreYearLogDao = null;
	
	public static ScoreCommonDao scoreCommonDao = null;
 
	public static ScoreGetBillDao scoreGetBillDao = null;
	
	public static ScoreCommentDetailDao scoreCommentDetailDao = null;
	
	
	public static ScoreGetOrderDetailDao scoreGetOrderDetailDao = null;
	public static ScoreGetCommmentDetailDao scoreGetCommmentDetailDao = null;

	 
	@BeforeClass
	public  static void init() throws Exception {
		
	
		context = new ClassPathXmlApplicationContext("applicationContext-score-impl.xml");
		scoreMainLogDao = context.getBean(ScoreMainLogDao.class);
		scoreMemberDao = context.getBean(ScoreMemberDao.class);
		scoreOrderDetailDao = context.getBean(ScoreOrderDetailDao.class);
		scoreYearDao = context.getBean(ScoreYearDao.class);
		scoreYearLogDao = context.getBean(ScoreYearLogDao.class);
		scoreCommonDao = context.getBean(ScoreCommonDao.class);
		scoreGetBillDao = context.getBean(ScoreGetBillDao.class);
		scoreCommentDetailDao = context.getBean(ScoreCommentDetailDao.class);
		
		scoreGetOrderDetailDao = context.getBean(ScoreGetOrderDetailDao.class);
		scoreGetCommmentDetailDao = context.getBean(ScoreGetCommmentDetailDao.class);
		String dataSourceName = DataSourceUtils.getDataSourceKey(memGuid);
		
		DataSource  dataSource= (DataSource) context.getBean(dataSourceName);
		//int num = ShardUtils.getDbNo(memGuid);
		dbunitCon = new DatabaseConnection(dataSource.getConnection()); 
		DatabaseConfig config = dbunitCon.getConfig();
		config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory());
	 
		
		//categoryService = context.getBean(CategoryService.class);
		//categoryGroupService = context.getBean(CategoryGroupService.class);
		//categoryChangeLogService = context.getBean(CategoryChangeLogService.class);
		 
	}
 
	 
			

	public void priFiledToMock(Object tclass, String priField, Object value)
			throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {
		Class<?> clazz = tclass.getClass();
		Field field = clazz.getDeclaredField(priField);
		field.setAccessible(true);
		field.set(tclass, value);
	}


	public void getTableAndDBBaseInfo(String memGuid) {
		 System.out.println("数据源序号："+ShardUtils.getDbNo(memGuid)+"选择的表序号:"+ShardUtils.getTableNo(memGuid));
	}
	
	/**
	 * xml的基础数据集
	 * @param tname
	 * @return
	 * @throws DataSetException
	 */
	protected IDataSet createDateSet(String tname) throws DataSetException {
		String path = "dbunit_xml/" + tname + ".xml";
		InputStream is = TestBasicDao.class.getClassLoader()
				.getResourceAsStream(path);
		assertNotNull("dbunit的基本数据文件不存在"+path, is);
		return new XmlDataSet(is);
	}

	/**
	 * 创建所以表数据的备份xml文件
	 * @throws SQLException
	 * @throws IOException
	 * @throws DataSetException
	 */
	protected  void backupAllTable() throws SQLException, IOException,
			DataSetException {
		IDataSet ds = dbunitCon.createDataSet();
		writeBackupFile(ds);
	}

	private  void writeBackupFile(IDataSet ds) throws IOException,
			DataSetException {
		tempFile = File.createTempFile("back", "xml");
		XmlDataSet.write(ds, new FileWriter(tempFile));
	}

	/**
	 * 创建用户指定多个表数据的备份xml文件
	 * @param tname
	 * @throws DataSetException
	 * @throws IOException
	 */
	protected   void backupCustomTable(String[] tname) throws DataSetException,
			IOException {
		QueryDataSet ds = new QueryDataSet(dbunitCon);
		for (String str : tname) {
			ds.addTable(str+ShardUtils.getTableNo(memGuid));
		}
		writeBackupFile(ds);
	}

	/**
	 * 创建用户指定一个表数据的备份xml文件
	 * @param tname
	 * @throws DataSetException
	 * @throws IOException
	 */
	protected  void bakcupOneTable(String tname) throws DataSetException,
			IOException {
		backupCustomTable(new String[] { tname });
	}

	/**
	 * 还原用户备份的xml文件数据
	 * @throws FileNotFoundException
	 * @throws DatabaseUnitException
	 * @throws SQLException
	 */
	protected  void resumeTable() throws FileNotFoundException,
			DatabaseUnitException, SQLException {
		if(tempFile ==null){
			return;
		}
		IDataSet ds = new XmlDataSet(new FileInputStream(tempFile));
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon, ds);
	}
	@After
	public void close() throws Exception {
		//恢复数据
		resumeTable();
	 	//context.close();
	}
	
	
	@AfterClass
	public static void destory() {
		
		try {
			if (dbunitCon != null){
				dbunitCon.close();
			}
			//context.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * 所有需要测试的方法，在这里引用
	 */
	public abstract void doTest(); 
}
