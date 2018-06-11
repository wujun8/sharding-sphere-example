package io.shardingsphere.example.spring.namespace.mybatis.fixtrue.service;

import com.bytesvc.ServiceException;

public interface ITransferService {

	public void transfer(String sourceAcctId, String targetAcctId, double amount) throws ServiceException;

}
