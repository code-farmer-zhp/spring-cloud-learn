package com.feiniu.score.dao.growth;

import com.feiniu.score.common.ConstantGrowth;
import com.feiniu.score.entity.growth.GrowthDetail;
import com.feiniu.score.log.CustomLog;
import com.feiniu.score.mapper.growth.GrowthDetailMapper;
import com.feiniu.score.util.DateUtil;
import com.feiniu.score.util.ShardUtils;
import com.feiniu.score.vo.GrowthOrderDetail;
import com.feiniu.score.vo.GrowthOrderDetailByOg;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class GrowthDetailDaoImpl implements GrowthDetailDao {


	 public static final CustomLog log = CustomLog.getLogger(GrowthDetailDaoImpl.class);

	@Autowired
	private GrowthDetailMapper growthDetailMapper;
	@Override
	public int deleteGrowthDetailById(String memGuid, Long gdSeq) {
		return growthDetailMapper.deleteGrowthDetailById(gdSeq, ShardUtils.getTableNo(memGuid));
	}
	@Override
	public int saveGrowthDetail(String memGuid, GrowthDetail gd) {

		/**
		 * add by jiahch 20150709
		 * 唯一：

				          登录： MEM_GUID , GROWTH_CHANGEL=登录 ， 时间（精确到天）
				飞牛赠送：CRM自定义
				购物获得：ORDER_INFO_ID , GROWTH_CHANGEL=购物获得
				评论获得：ORDER_INFO_ID , GROWTH_CHANGEL=评论获得
				评论置顶：ORDER_INFO_ID , GROWTH_CHANGEL=评论置顶
				          精华：ORDER_INFO_ID , GROWTH_CHANGEL=精华
		      退货回收(购物)：ORDER_INFO_ID , RG_SEQ , RL_SEQ
		      退货回收(评论)：ORDER_INFO_ID ，  GROWTH_CHANGEL=退货回收(评论)
		 */

		Integer c = gd.getGrowthChannel();
		String uniqueKey = gd.getUniqueKey();
		String groupKey = gd.getUniqueKey();

		if(c != null){
			// 登录
			if(c.equals(ConstantGrowth.DETAIL_GROWTH_CHANNEL_DL)){
				String loginDate;
				if(gd.getLoginDateTmp()==null) {
					loginDate = DateUtil.getFormatDate(new Date(),"yyyy-MM-dd");
				}else{
					loginDate = DateUtil.getFormatDate(gd.getLoginDateTmp(),"yyyy-MM-dd");
				}
				uniqueKey = c+"_"+gd.getMemGuid()+"_"+loginDate;
				groupKey = c+"_"+gd.getMemGuid()+"_"+loginDate;
			}else if(c.equals(ConstantGrowth.DETAIL_GROWTH_CHANNEL_FNZS) || c.equals(ConstantGrowth.DETAIL_GROWTH_CHANNEL_FNQX)
					|| c.equals(ConstantGrowth.DETAIL_GROWTH_CHANNEL_FNTZFF) || c.equals(ConstantGrowth.DETAIL_GROWTH_CHANNEL_FNTZQX)){  // 飞牛赠送

				// DO NOTHING
			}else if(c.equals(ConstantGrowth.DETAIL_GROWTH_CHANNEL_GW)     // 购物
					|| c.equals(ConstantGrowth.DETAIL_GROWTH_CHANNEL_PL)  // 评论
					|| c.equals(ConstantGrowth.DETAIL_GROWTH_CHANNEL_PLZD)  // 评论置顶
					|| c.equals(ConstantGrowth.DETAIL_GROWTH_CHANNEL_PLJH)){  //  精华

				uniqueKey = c+"_"+gd.getOrderInfoId();

			}else if(c.equals(ConstantGrowth.DETAIL_GROWTH_CHANNEL_TH_GW)){   // 退货回收(购物)

				uniqueKey = c+"_"+gd.getOrderInfoId()+"_"+gd.getRgSeq()+"_"+gd.getRlSeq();

			}else if(c.equals(ConstantGrowth.DETAIL_GROWTH_CHANNEL_TH_PL)){   // 退货回收(评论)

				uniqueKey = c+"_"+gd.getOrderInfoId();

			}else{
				log.error("当前获得成长值来源:"+c+"暂时不能支持! memGuid = "+gd.getMemGuid()+" orderInfoId = "+gd.getOrderInfoId(),"saveGrowthDetail");
			}

			if(StringUtils.isNotBlank(uniqueKey)){
				gd.setUniqueKey(uniqueKey);
			}

			if(StringUtils.isNotBlank(groupKey)){
				gd.setGroupKey(groupKey);
			}
		}
		int changeRows=growthDetailMapper.saveGrowthDetail(gd,ShardUtils.getTableNo(memGuid));
//		if(changeRows>0 && gd.getGdSeq()!=null){
//			growthLogDao.saveLog(memGuid, gd, gd.getGdSeq(), "growth_detail");
//		}
		return changeRows;
	}
	@Override
	public GrowthDetail getGrowthDetailById(String memGuid, Long gdSeq) {
		return growthDetailMapper.getGrowthDetailById(gdSeq, ShardUtils.getTableNo(memGuid));
	}
	@Override
	public int updateGrowthDetail(String memGuid, GrowthDetail gd) {
		int rows=growthDetailMapper.updateGrowthDetail(gd, ShardUtils.getTableNo(memGuid));
//		if(rows>0 && gd.getGdSeq()!=null){
//			growthLogDao.updateLog(memGuid, gd, gd.getGdSeq(), "growth_detail");
//		}
		return rows;
	}
	@Override
	public List<GrowthDetail> getGrowthDetailListByMemGuid(String memGuid,
			Map<String, Object> paramMap) {
		return growthDetailMapper.getGrowthDetailListByMemGuid(memGuid, paramMap, ShardUtils.getTableNo(memGuid));
	}
	@Override
	public int getGrowthDetailListCountByMemGuid(String memGuid) {
		return growthDetailMapper.getGrowthDetailCountByMemGuid(memGuid, ShardUtils.getTableNo(memGuid));
	}

	@Override
	public List<GrowthDetail> getGrowthDetailListBySelective(String memGuid,
			Map<String, Object> paramMap) {
		return growthDetailMapper.getGrowthDetailListBySelective(paramMap, ShardUtils.getTableNo(memGuid));
	}

	@Override
    public List<GrowthDetail> findDetailByOrder(String memGuid,Long orderInfoId,List<Integer>growthChannels){


    	return growthDetailMapper.findDetailByOrder(orderInfoId, growthChannels, ShardUtils.getTableNo(memGuid));
    }




	@Override
	public int getGrowthDetailCountBySelective(String memGuid,
			Map<String, Object> paramMap) {
		return growthDetailMapper.getGrowthDetailCountBySelective(paramMap, ShardUtils.getTableNo(memGuid));
	}

	@Override
	public int getGrowthOrderDetailCountBySelective(String memGuid,
			Map<String, Object> paramMap) {
		return growthDetailMapper.getGrowthOrderDetailCountBySelective(paramMap, ShardUtils.getTableNo(memGuid));
	}



	@Override
	public  List<GrowthOrderDetail>  getGrowthOrderDetailBySelective(String memGuid,
			Map<String, Object> paramMap) {
		return growthDetailMapper.getGrowthOrderDetailBySelective(paramMap, ShardUtils.getTableNo(memGuid));
	}

	@Override
	public  List<GrowthOrderDetail>  getGrowthOrderDetailOfChannel(String memGuid,
			Integer Channel) {
		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("growthChannel",Channel);
		paramMap.put("memGuid",memGuid);
		return growthDetailMapper.getGrowthOrderDetailBySelective(paramMap, ShardUtils.getTableNo(memGuid));
	}

	@Override
	public Integer getSumValueByMemGuid(String memGuid, int tableNo) {
		return growthDetailMapper.getSumValueByMemGuid(memGuid, tableNo);
	}
	
	@Override
	public  List<GrowthOrderDetailByOg>  getGrowthDetailGroupByOg(String memGuid,
			Map<String, Object> paramMap) {
		return growthDetailMapper.getGrowthDetailGroupByOg(paramMap, ShardUtils.getTableNo(memGuid));
	}

	@Override
	public  int  getGrowthDetailCountGroupByOg(String memGuid,
			Map<String, Object> paramMap) {
		return growthDetailMapper.getGrowthDetailCountGroupByOg(paramMap, ShardUtils.getTableNo(memGuid));
	}

	@Override
	public  List<GrowthOrderDetailByOg>  getGrowthDetailGroupByOgWithKey(String memGuid,
																  Map<String, Object> paramMap) {
		return growthDetailMapper.getGrowthDetailGroupByOgWithKey(paramMap, ShardUtils.getTableNo(memGuid));
	}

	@Override
	public  int  getGrowthDetailCountGroupByOgWithKey(String memGuid,
											   Map<String, Object> paramMap) {
		return growthDetailMapper.getGrowthDetailCountGroupByOgWithKey(paramMap, ShardUtils.getTableNo(memGuid));
	}
}
