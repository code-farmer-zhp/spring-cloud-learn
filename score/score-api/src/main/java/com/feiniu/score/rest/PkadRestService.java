package com.feiniu.score.rest;

import javax.ws.rs.core.Response;

public interface PkadRestService {

	Response takePkad(String data);

	Response getCardInfo(String data);

	Response getPkadList(String data);

	Response getPkadListCount(String data);

	Response getMrstUiList(String data);

    Response getCardInfoBatch(String data);

    Response getTakenCardInfo(String data);

    Response getTakenCardInfoBatch(String data);

    Response getPkadListForERP(String data);

	Response getPkadCountForERP(String data);

	Response getLastPkad(String data);

	Response executeNoTakenCouponIdJob();
	Response executeJob();}
