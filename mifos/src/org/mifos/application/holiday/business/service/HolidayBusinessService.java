package org.mifos.application.holiday.business.service;

import java.util.List;

import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.holiday.business.RepaymentRuleEntity;
import org.mifos.application.holiday.persistence.HolidayPersistence;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.UserContext;

public class HolidayBusinessService extends BusinessService {

	@Override
	public BusinessObject getBusinessObject(UserContext userContext) {
		return null;
	}
/*
	public void isValidHolidayState(Short levelId, Short stateId,
			boolean isCustomer) throws ServiceException {
		try {
			Integer records = new HolidayPersistence().isValidHolidayState(
					levelId, stateId, isCustomer);
			if (records.intValue() != 0)
				throw new ServiceException(
						HolidayConstants.EXCEPTION_STATE_ALREADY_EXIST);
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}
*/
	public List<HolidayBO> 	getHolidays(int year, int localId) throws ServiceException {
		try {
			return new HolidayPersistence().getHolidays(year, localId);
		} catch (PersistenceException pe) {
			throw new ServiceException(pe);
		}
	}
	
	public List<RepaymentRuleEntity> getRepaymentRuleTypes(int localId) throws ServiceException{
		try {
			return new HolidayPersistence().getRepaymentRuleTypes(localId);
		} catch (PersistenceException pe) {
			throw new ServiceException(pe);
		}
	}
	
}
