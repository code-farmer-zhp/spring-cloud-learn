package com.feiniu.score.vo;

import com.alibaba.fastjson.JSONObject;
import com.feiniu.score.common.ResultCode;
import com.feiniu.score.exception.ScoreException;
import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;

/**
 * Created by peng.zhou on 2015/6/17.
 */
public class CrmScoreJsonVo {
    private static final String TYPE = "B1";
    private String ptadId;
    private String memGuid;
    private String membGradef;
    private Integer ptadType;
    private String mardfType;
    private Integer mrdfPoint;
    private String dPtad;
    private String dEfff;
    private String dEfft;

    public String getPtadId() {
        return ptadId;
    }

    public void setPtadId(String ptadId) {
        this.ptadId = ptadId;
    }

    public String getMemGuid() {
        return memGuid;
    }

    public void setMemGuid(String memGuid) {
        this.memGuid = memGuid;
    }

    public String getMembGradef() {
        return membGradef;
    }

    public void setMembGradef(String membGradef) {
        this.membGradef = membGradef;
    }

    public Integer getPtadType() {
        return ptadType;
    }

    public void setPtadType(Integer ptadType) {
        this.ptadType = ptadType;
    }

    public String getMardfType() {
        return mardfType;
    }

    public void setMardfType(String mardfType) {
        this.mardfType = mardfType;
    }

    public Integer getMrdfPoint() {
        return mrdfPoint;
    }

    public void setMrdfPoint(Integer mrdfPoint) {
        this.mrdfPoint = mrdfPoint;
    }

    public String getdPtad() {
        return dPtad;
    }

    public void setdPtad(String dPtad) {
        this.dPtad = dPtad;
    }

    public String getdEfff() {
        return dEfff;
    }

    public void setdEfff(String dEfff) {
        this.dEfff = dEfff;
    }

    public String getdEfft() {
        return dEfft;
    }

    public void setdEfft(String dEfft) {
        this.dEfft = dEfft;
    }

    @Override
    public String toString() {
        return "CrmScoreJsonVo{" +
                "ptadId='" + ptadId + '\'' +
                ", memGuid='" + memGuid + '\'' +
                ", membGradef='" + membGradef + '\'' +
                ", ptadType=" + ptadType +
                ", mardfType='" + mardfType + '\'' +
                ", mrdfPoint=" + mrdfPoint +
                ", dPtad='" + dPtad + '\'' +
                ", dEfff='" + dEfff + '\'' +
                ", dEfft='" + dEfft + '\'' +
                '}';
    }

    public static CrmScoreJsonVo convertJson(JSONObject jsonObject) {
        String ptadId = jsonObject.getString("ptad_id");
        if (StringUtils.isEmpty(ptadId)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "ptad_id 不能为空。");
        }
        String memGuid = jsonObject.getString("memb_id");
        if (StringUtils.isEmpty(memGuid)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "memb_id 不能为空。");
        }
        String membGradef = jsonObject.getString("memb_grade_f");
        if(StringUtils.isEmpty(membGradef)){
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION,"memb_grade_f 不能为空");
        }

        //来源类型
        Integer ptadType = jsonObject.getInteger("ptad_type");
        if (ptadType == null) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "ptad_type 不能为空。");
        }

        //类型
        String mardfType = jsonObject.getString("mrdf_type");
        if (StringUtils.isEmpty(mardfType)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "mrdf_type 不能为空。");
        }

        if (!StringUtils.equals(TYPE, mardfType)) {
            //如果不是B1类型（积分）
            throw new ScoreException(ResultCode.RESULT_RUN_TIME_EXCEPTION, "mrdf_type 类型错误。");
        }
        //积分
        Integer mrdfPoint = jsonObject.getInteger("mrdf_point");
        if (mrdfPoint == null) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "mrdf_point 不能为空。");
        }
        //发放日期
        String dPtad = jsonObject.getString("d_ptad");
        if (StringUtils.isEmpty(dPtad)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "d_ptad 不能为空。");
        }
        //生效日
        String dEfff = jsonObject.getString("d_eff_f");
        if (StringUtils.isEmpty(dEfff)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "d_eff_f 不能为空。");
        }
        /*//失效日 必须是今年年底 或明年年底
        String dEfft = jsonObject.getString("d_eff_t");
        if (StringUtils.isEmpty(dEfft)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "d_eff_t 不能为空。");
        }
        Calendar endTime = Calendar.getInstance();
        int thisYear = endTime.get(Calendar.YEAR);
        //今年年底
        String thisYearDue = thisYear + "1231";
        //明年年底
        String nextYearDue = (thisYear + 1) + "1231";
        //如果不是今年年底 也不是明年年底。
        if (!StringUtils.equals(dEfft, thisYearDue) && !StringUtils.equals(dEfft, nextYearDue)) {
            throw new ScoreException(ResultCode.RESULT_RUN_TIME_EXCEPTION, "失效日期不符合要求，应该为" + thisYearDue + "或" + nextYearDue);
        }*/

        CrmScoreJsonVo crmScoreJsonVo = new CrmScoreJsonVo();
        crmScoreJsonVo.setPtadId(ptadId);
        crmScoreJsonVo.setMemGuid(memGuid);
        crmScoreJsonVo.setMembGradef(membGradef);
        crmScoreJsonVo.setPtadType(ptadType);
        crmScoreJsonVo.setMardfType(mardfType);
        crmScoreJsonVo.setMrdfPoint(Math.abs(mrdfPoint));
        crmScoreJsonVo.setdPtad(dPtad);
        crmScoreJsonVo.setdEfff(dEfff);
        //crmScoreJsonVo.setdEfft(dEfft);
        return crmScoreJsonVo;

    }
}
