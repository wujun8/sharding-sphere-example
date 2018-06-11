package io.shardingsphere.example.spring.namespace.mybatis.fixtrue.service.impl;

import com.bytesvc.ServiceException;
import com.bytesvc.service.IAccountService;
import io.shardingsphere.example.spring.namespace.mybatis.fixtrue.entity.Order;
import io.shardingsphere.example.spring.namespace.mybatis.fixtrue.repository.OrderRepository;
import io.shardingsphere.example.spring.namespace.mybatis.fixtrue.service.ITransferService;
import org.bytesoft.compensable.Compensable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service("genericTransferService")
@Compensable(interfaceClass = ITransferService.class, confirmableKey = "transferServiceConfirm", cancellableKey = "transferServiceCancel")
public class GenericTransferServiceImpl implements ITransferService {

	@Resource
	private OrderRepository orderRepository;
	@org.springframework.beans.factory.annotation.Qualifier("remoteAccountService")
	@org.springframework.beans.factory.annotation.Autowired(required = false)
	private IAccountService remoteAccountService;

	@Transactional(rollbackFor = ServiceException.class)
	public void transfer(String sourceAcctId, String targetAcctId, double amount) throws ServiceException {
		this.remoteAccountService.decreaseAmount(sourceAcctId, amount);
		this.increaseAmount(targetAcctId, amount);

//		 throw new ServiceException("rollback");

	}

	private void increaseAmount(String acctId, double amount) throws ServiceException {
		Order order = new Order();
		order.setUserId(Integer.valueOf(acctId));
		order.setStatus(String.format("Try increaseAmount %s of user: %s", amount, acctId));
		orderRepository.insert(order);
		System.out.printf("exec increase: acct= %s, amount= %7.2f%n", acctId, amount);
	}

}
