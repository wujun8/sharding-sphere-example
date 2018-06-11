package io.shardingsphere.example.spring.namespace.mybatis.fixtrue.service.cancel;

import com.bytesvc.ServiceException;
import io.shardingsphere.example.spring.namespace.mybatis.fixtrue.entity.Order;
import io.shardingsphere.example.spring.namespace.mybatis.fixtrue.repository.OrderRepository;
import io.shardingsphere.example.spring.namespace.mybatis.fixtrue.service.ITransferService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service("transferServiceCancel")
public class TransferServiceCancel implements ITransferService {

	@Resource
	private OrderRepository orderRepository;

	@Transactional(rollbackFor = ServiceException.class)
	public void transfer(String sourceAcctId, String targetAcctId, double amount) throws ServiceException {
		Order order = new Order();
		order.setUserId(Integer.valueOf(targetAcctId));
		order.setStatus(String.format("Cancel decrease amount %s of user: %s", amount, targetAcctId));
		orderRepository.insert(order);
		System.out.printf("exec decrease: acct= %s, amount= %7.2f%n", targetAcctId, amount);
	}

}
