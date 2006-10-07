/**

 * TestEditCustomerStatusAction.java version: 1.0



 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.



 * Apache License
 * Copyright (c) 2005-2006 Grameen Foundation USA
 *

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the

 * License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license

 * and how it is applied.

 *

 */

package org.mifos.application.customer.struts.action;

import java.sql.Date;
import java.util.List;

import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerFlagDetailEntity;
import org.mifos.application.customer.business.CustomerPositionEntity;
import org.mifos.application.customer.business.CustomerStatusEntity;
import org.mifos.application.customer.business.PositionEntity;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.customer.util.helpers.CustomerStatusFlag;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.application.office.util.helpers.OfficeStatus;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.components.audit.util.helpers.AuditConfigurtion;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Flow;
import org.mifos.framework.util.helpers.FlowManager;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestEditCustomerStatusAction extends MifosMockStrutsTestCase {

	private CustomerBO client;

	private CustomerBO group;

	private CustomerBO center;

	private LoanBO loanBO;

	private String flowKey;

	private OfficeBO office;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml")
				.getPath());
		setConfigFile(ResourceLoader.getURI(
				"org/mifos/application/customer/struts-config.xml")
				.getPath());
		
		UserContext userContext = TestObjectFactory.getContext();
		request.getSession().setAttribute(Constants.USER_CONTEXT_KEY,
				userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());

		Flow flow = new Flow();
		flowKey = String.valueOf(System.currentTimeMillis());
		FlowManager flowManager = new FlowManager();
		flowManager.addFLow(flowKey, flow);
		request.getSession(false).setAttribute(Constants.FLOWMANAGER,
				flowManager);
	}

	@Override
	public void tearDown() throws Exception {
		TestObjectFactory.cleanUp(loanBO);
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		TestObjectFactory.cleanUp(office);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testLoad() {
		createInitialObjects();
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("customerId", center.getCustomerId().toString());
		getRequest().getSession().setAttribute("security_param", "Center");
		actionPerform();
		verifyForward(ActionForwards.load_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST,
				request.getSession()));
		assertEquals("Size of the status list should be 2", 1,
				((List<CustomerStatusEntity>) SessionUtils.getAttribute(
						SavingsConstants.STATUS_LIST, request.getSession()))
						.size());

	}

	public void testFailurePreviewWithAllValuesNull() throws Exception {
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "preview");
		getRequest().getSession().setAttribute("security_param", "Center");
		actionPerform();
		assertEquals(2, getErrrorSize());
		assertEquals("Status id", 1,
				getErrrorSize(CustomerConstants.MANDATORY_SELECT));
		assertEquals("Notes", 1,
				getErrrorSize(CustomerConstants.MANDATORY_TEXTBOX));
		verifyInputForward();
	}

	public void testFailurePreviewWithFlagValueNull() throws Exception {
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("newStatusId", "11");
		getRequest().getSession().setAttribute("security_param", "Center");
		actionPerform();
		assertEquals(2, getErrrorSize());
		assertEquals("flag id", 1,
				getErrrorSize(CustomerConstants.MANDATORY_SELECT));
		assertEquals("Notes", 1,
				getErrrorSize(CustomerConstants.MANDATORY_TEXTBOX));
		verifyInputForward();
	}

	public void testFailurePreviewWithNotesValueNull() throws Exception {
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("newStatusId", "11");
		addRequestParameter("flagId", "1");
		getRequest().getSession().setAttribute("security_param", "Center");
		actionPerform();
		assertEquals(1, getErrrorSize());
		assertEquals("Notes", 1,
				getErrrorSize(CustomerConstants.MANDATORY_TEXTBOX));
		verifyInputForward();
	}

	public void testFailurePreviewWithNotesValueExceedingMaxLength()
			throws Exception {
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("newStatusId", "14");
		addRequestParameter("flagId", "");
		addRequestParameter(
				"notes",
				"Testing for comment length exceeding by 500 characters"
						+ "Testing for comment length exceeding by 500 characters"
						+ "Testing for comment length exceeding by 500 characters"
						+ "Testing for comment length exceeding by 500 characters"
						+ "Testing for comment length exceeding by 500 characters "
						+ "Testing for comment length exceeding by 500 characters "
						+ "Testing for comment length exceeding by 500 characters"
						+ "Testing for comment length exceeding by 500 characters"
						+ "Testing for comment length exceeding by 500 characters"
						+ "Testing for comment length exceeding by 500 characters"
						+ "Testing for comment length exceeding by 500 characters");
		getRequest().getSession().setAttribute("security_param", "Center");
		actionPerform();
		assertEquals(1, getErrrorSize());
		assertEquals("Notes", 1,
				getErrrorSize(CustomerConstants.MAXIMUM_LENGTH));
		verifyInputForward();
	}

	public void testPreviewSuccess() {
		createInitialObjects();
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("customerId", center.getCustomerId().toString());
		getRequest().getSession().setAttribute("security_param", "Center");
		actionPerform();
		verifyForward("load_success");
		assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST,
				request.getSession()));
		assertEquals("Size of the status list should be 2", 1,
				((List<AccountStateEntity>) SessionUtils.getAttribute(
						SavingsConstants.STATUS_LIST, request.getSession()))
						.size());

		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("notes", "Test");
		addRequestParameter("levelId", center.getCustomerLevel().getId()
				.toString());
		addRequestParameter("newStatusId", "14");
		addRequestParameter("flagId", "");
		getRequest().getSession().setAttribute("security_param", "Center");
		actionPerform();
		verifyForward("preview_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertNotNull("Inactive", (String) SessionUtils.getAttribute(
				SavingsConstants.NEW_STATUS_NAME, request.getSession()));
		assertNull("Since new Status is not cancel,so flag should be null.",
				SessionUtils.getAttribute(SavingsConstants.FLAG_NAME, request
						.getSession()));
	}

	public void testUpdateCenterStatus() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("customerId", center.getCustomerId().toString());
		getRequest().getSession().setAttribute("security_param", "Center");
		actionPerform();
		verifyForward("load_success");
		assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST,
				request.getSession()));
		assertEquals("Size of the status list should be 2", 1,
				((List<AccountStateEntity>) SessionUtils.getAttribute(
						SavingsConstants.STATUS_LIST, request.getSession()))
						.size());

		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("notes", "Test");
		addRequestParameter("levelId", center.getCustomerLevel().getId()
				.toString());
		addRequestParameter("newStatusId", "14");
		addRequestParameter("flagId", "");
		getRequest().getSession().setAttribute("security_param", "Center");
		actionPerform();
		verifyForward("preview_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertNotNull("Inactive", (String) SessionUtils.getAttribute(
				SavingsConstants.NEW_STATUS_NAME, request.getSession()));
		assertNull("Since new Status is not cancel,so flag should be null.",
				SessionUtils.getAttribute(SavingsConstants.FLAG_NAME, request
						.getSession()));
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "update");
		getRequest().getSession().setAttribute("security_param", "Center");
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.center_detail_page.toString());
		center = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				center.getCustomerId());
		assertFalse(center.isActive());
	}

	public void testLoadForClient() {
		createInitialObjects();
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("customerId", client.getCustomerId().toString());
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward(ActionForwards.load_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST,
				request.getSession()));
		assertEquals("Size of the status list should be 2", 2,
				((List<CustomerStatusEntity>) SessionUtils.getAttribute(
						SavingsConstants.STATUS_LIST, request.getSession()))
						.size());

	}

	public void testFailurePreviewWithAllValuesNullForClient() throws Exception {
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "preview");
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		assertEquals(2, getErrrorSize());
		assertEquals("Status id", 1,
				getErrrorSize(CustomerConstants.MANDATORY_SELECT));
		assertEquals("Notes", 1,
				getErrrorSize(CustomerConstants.MANDATORY_TEXTBOX));
		verifyInputForward();
	}

	public void testFailurePreviewWithFlagValueNullForCLient() throws Exception {
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("newStatusId", "6");
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		assertEquals(2, getErrrorSize());
		assertEquals("flag id", 1,
				getErrrorSize(CustomerConstants.MANDATORY_SELECT));
		assertEquals("Notes", 1,
				getErrrorSize(CustomerConstants.MANDATORY_TEXTBOX));
		verifyInputForward();
	}

	public void testFailurePreviewWithNotesValueNullForClient()
			throws Exception {
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("newStatusId", "6");
		addRequestParameter("flagId", "10");
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		assertEquals(1, getErrrorSize());
		assertEquals("Notes", 1,
				getErrrorSize(CustomerConstants.MANDATORY_TEXTBOX));
		verifyInputForward();
	}

	public void testPreviewSuccessForClient() {
		createInitialObjects();
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("customerId", client.getCustomerId().toString());
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward("load_success");
		assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST,
				request.getSession()));
		assertEquals("Size of the status list should be 2", 2,
				((List<AccountStateEntity>) SessionUtils.getAttribute(
						SavingsConstants.STATUS_LIST, request.getSession()))
						.size());

		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("notes", "Test");
		addRequestParameter("levelId", client.getCustomerLevel().getId()
				.toString());
		addRequestParameter("newStatusId", "6");
		addRequestParameter("flagId", "10");
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward("preview_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertNotNull("Closed", (String) SessionUtils.getAttribute(
				SavingsConstants.NEW_STATUS_NAME, request.getSession()));
		assertNotNull("Since new Status is Closed,so flag should not be null.",
				SessionUtils.getAttribute(SavingsConstants.FLAG_NAME,
						request.getSession()));
	}

	public void testPrevious() {
		createInitialObjects();
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "previous");
		addRequestParameter("customerId", client.getCustomerId().toString());
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward(ActionForwards.previous_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testCancel() {
		createInitialObjects();
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "cancel");
		addRequestParameter("input", "client");
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward(ActionForwards.client_detail_page.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testUpdateStatusForClient() throws CustomerException {
		createInitialObjects();
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("customerId", client.getCustomerId().toString());
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward("load_success");
		assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST,
				request.getSession()));
		assertEquals("Size of the status list should be 2", 2,
				((List<AccountStateEntity>) SessionUtils.getAttribute(
						SavingsConstants.STATUS_LIST, request.getSession()))
						.size());

		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("notes", "Test");
		addRequestParameter("levelId", client.getCustomerLevel().getId()
				.toString());
		addRequestParameter("newStatusId", "4");
		addRequestParameter("flagId", "");
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward("preview_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertNotNull("On Hold", (String) SessionUtils.getAttribute(
				SavingsConstants.NEW_STATUS_NAME, request.getSession()));
		assertNull("Since new Status is not Closed,so flag should be null.",
				SessionUtils.getAttribute(SavingsConstants.FLAG_NAME, request
						.getSession()));
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "update");
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.client_detail_page.toString());
		client = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				client.getCustomerId());
		assertFalse(client.isActive());
	}

	public void testUpdateStatusForClientForFirstTimeActive()
			throws CustomerException {
		createInitialObjects(CustomerStatus.CENTER_ACTIVE,
				CustomerStatus.GROUP_ACTIVE, CustomerStatus.CLIENT_PARTIAL);
		assertTrue(((ClientBO) client).getCustomerAccount()
				.getAccountActionDates().isEmpty());
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("customerId", client.getCustomerId().toString());
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward("load_success");
		assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST,
				request.getSession()));
		assertEquals("Size of the status list should be 2", 2,
				((List<AccountStateEntity>) SessionUtils.getAttribute(
						SavingsConstants.STATUS_LIST, request.getSession()))
						.size());

		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("notes", "Test");
		addRequestParameter("levelId", client.getCustomerLevel().getId()
				.toString());
		addRequestParameter("newStatusId", "3");
		addRequestParameter("flagId", "");
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward("preview_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertNotNull("Active", (String) SessionUtils.getAttribute(
				SavingsConstants.NEW_STATUS_NAME, request.getSession()));
		assertNull("Since new Status is not Closed,so flag should be null.",
				SessionUtils.getAttribute(SavingsConstants.FLAG_NAME, request
						.getSession()));
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "update");
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.client_detail_page.toString());
		client = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				client.getCustomerId());
		assertTrue(client.isActive());
		assertFalse(((ClientBO) client).getCustomerAccount()
				.getAccountActionDates().isEmpty());
		assertEquals("ActivationDate should be the current date.", DateUtils
				.getDateWithoutTimeStamp(new java.util.Date().getTime()),
				DateUtils.getDateWithoutTimeStamp(client
						.getCustomerActivationDate().getTime()));
	}

	public void testUpdateStatusForClientForActiveLoanOfficer()
			throws CustomerException {
		createInitialObjects();
		client.setCustomerStatus(new CustomerStatusEntity(
				CustomerStatus.CLIENT_PARTIAL.getValue()));
		client.update();
		HibernateUtil.commitTransaction();

		client = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				client.getCustomerId());
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("customerId", client.getCustomerId().toString());
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward("load_success");
		assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST,
				request.getSession()));
		assertEquals("Size of the status list should be 2", 2,
				((List<AccountStateEntity>) SessionUtils.getAttribute(
						SavingsConstants.STATUS_LIST, request.getSession()))
						.size());

		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("notes", "Test");
		addRequestParameter("levelId", client.getCustomerLevel().getId()
				.toString());
		addRequestParameter("newStatusId", "3");
		addRequestParameter("flagId", "");
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward("preview_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertNotNull("Active", (String) SessionUtils.getAttribute(
				SavingsConstants.NEW_STATUS_NAME, request.getSession()));
		assertNull("Since new Status is not Closed,so flag should be null.",
				SessionUtils.getAttribute(SavingsConstants.FLAG_NAME, request
						.getSession()));
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "update");
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.client_detail_page.toString());
		client = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				client.getCustomerId());
		assertTrue(client.isActive());
	}

	public void testUpdateStatusForClientWhenParentCustomerIsInPartialState()
			throws CustomerException {
		createInitialObjects(CustomerStatus.CENTER_ACTIVE,
				CustomerStatus.GROUP_PARTIAL, CustomerStatus.CLIENT_PARTIAL);
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("customerId", client.getCustomerId().toString());
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward("load_success");
		assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST,
				request.getSession()));
		assertEquals("Size of the status list should be 2", 2,
				((List<AccountStateEntity>) SessionUtils.getAttribute(
						SavingsConstants.STATUS_LIST, request.getSession()))
						.size());

		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("notes", "Test");
		addRequestParameter("levelId", client.getCustomerLevel().getId()
				.toString());
		addRequestParameter("newStatusId", "3");
		addRequestParameter("flagId", "");
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward("preview_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertNotNull("Active", (String) SessionUtils.getAttribute(
				SavingsConstants.NEW_STATUS_NAME, request.getSession()));
		assertNull("Since new Status is not Closed,so flag should be null.",
				SessionUtils.getAttribute(SavingsConstants.FLAG_NAME, request
						.getSession()));
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "update");
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyActionErrors(new String[] { ClientConstants.INVALID_CLIENT_STATUS_EXCEPTION });
		verifyForward(ActionForwards.update_failure.toString());
		client = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				client.getCustomerId());
		assertFalse(client.isActive());
	}

	public void testUpdateStatusForClientWhenClientHasActiveAccounts()
			throws CustomerException {
		createInitialObjects();
		loanBO = getLoanAccount(client,"dsafdsfds","12ed");
		client.update();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		client = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				client.getCustomerId());
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("customerId", client.getCustomerId().toString());
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward("load_success");
		assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST,
				request.getSession()));
		assertEquals("Size of the status list should be 2", 2,
				((List<AccountStateEntity>) SessionUtils.getAttribute(
						SavingsConstants.STATUS_LIST, request.getSession()))
						.size());

		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("notes", "Test");
		addRequestParameter("levelId", client.getCustomerLevel().getId()
				.toString());
		addRequestParameter("newStatusId", "6");
		addRequestParameter("flagId", "7");
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward("preview_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertNotNull("Closed", (String) SessionUtils.getAttribute(
				SavingsConstants.NEW_STATUS_NAME, request.getSession()));
		assertNotNull("Since new Status is Closed,so flag should be Duplicate.",
				SessionUtils.getAttribute(
						SavingsConstants.FLAG_NAME, request.getSession()));
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "update");
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyActionErrors(new String[] { CustomerConstants.CUSTOMER_HAS_ACTIVE_ACCOUNTS_EXCEPTION });
		verifyForward(ActionForwards.update_failure.toString());
		HibernateUtil.closeSession();
		client = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				client.getCustomerId());
		group = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				group.getCustomerId());
		center = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				center.getCustomerId());
		loanBO = (LoanBO) TestObjectFactory.getObject(LoanBO.class, loanBO
				.getAccountId());
	}

	public void testUpdateStatusForClientWhenClientIsAssignedPosition()
			throws CustomerException {
		createInitialObjects();
		CustomerPositionEntity customerPositionEntity = new CustomerPositionEntity(
				new PositionEntity(Short.valueOf("1")), client, client
						.getParentCustomer());
		group.addCustomerPosition(customerPositionEntity);
		group.update();
		HibernateUtil.commitTransaction();
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("customerId", client.getCustomerId().toString());
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward("load_success");
		assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST,
				request.getSession()));
		assertEquals("Size of the status list should be 2", 2,
				((List<AccountStateEntity>) SessionUtils.getAttribute(
						SavingsConstants.STATUS_LIST, request.getSession()))
						.size());

		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("notes", "Test");
		addRequestParameter("levelId", client.getCustomerLevel().getId()
				.toString());
		addRequestParameter("newStatusId", "6");
		addRequestParameter("flagId", "7");
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward("preview_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertNotNull("Closed", (String) SessionUtils.getAttribute(
				SavingsConstants.NEW_STATUS_NAME, request.getSession()));
		assertNotNull("Since new Status is Closed,so flag should be Duplicate.",
				SessionUtils.getAttribute(
						SavingsConstants.FLAG_NAME, request.getSession()));
		for (CustomerPositionEntity customerPosition : group
				.getCustomerPositions()) {
			assertNotNull(customerPosition.getCustomer());
			break;
		}
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "update");
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.client_detail_page.toString());
		client = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				client.getCustomerId());
		assertFalse(client.isActive());
		for (CustomerFlagDetailEntity customerFlagDetailEntity : client
				.getCustomerFlags()) {
			assertFalse(customerFlagDetailEntity.getStatusFlag()
					.isBlackListed());
			break;
		}
		for (CustomerPositionEntity customerPosition : group
				.getCustomerPositions()) {
			assertNull(customerPosition.getCustomer());
			break;
		}
	}

	public void testChangeStatusToActiveForClient() throws Exception {
		createObjectsForClient("Client");
		client.setPersonnel(null);
		client.update();
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("customerId", client.getCustomerId().toString());
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward("load_success");
		assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST,
				request.getSession()));
		assertEquals("Size of the status list should be 2", 2,
				((List<AccountStateEntity>) SessionUtils.getAttribute(
						SavingsConstants.STATUS_LIST, request.getSession()))
						.size());

		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("notes", "Test");
		addRequestParameter("levelId", client.getCustomerLevel().getId()
				.toString());
		addRequestParameter("newStatusId", CustomerStatus.CLIENT_ACTIVE
				.getValue().toString());
		addRequestParameter("flagId", "");
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward("preview_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertNotNull("Active", (String) SessionUtils.getAttribute(
				SavingsConstants.NEW_STATUS_NAME, request.getSession()));
		assertNull("Since new Status is not Closed,so flag should be null.",
				SessionUtils.getAttribute(SavingsConstants.FLAG_NAME, request
						.getSession()));
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "update");
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyActionErrors(new String[] { ClientConstants.CLIENT_LOANOFFICER_NOT_ASSIGNED });
		verifyForward(ActionForwards.update_failure.toString());
	}

	public void testChangeStatusToActiveForClientForMeetingNull()
			throws Exception {
		createClientWithoutMeeting("Client");
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("customerId", client.getCustomerId().toString());
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward("load_success");
		assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST,
				request.getSession()));
		assertEquals("Size of the status list should be 2", 2,
				((List<AccountStateEntity>) SessionUtils.getAttribute(
						SavingsConstants.STATUS_LIST, request.getSession()))
						.size());

		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("notes", "Test");
		addRequestParameter("levelId", client.getCustomerLevel().getId()
				.toString());
		addRequestParameter("newStatusId", CustomerStatus.CLIENT_ACTIVE
				.getValue().toString());
		addRequestParameter("flagId", "");
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward("preview_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "update");
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyActionErrors(new String[] { GroupConstants.MEETING_NOT_ASSIGNED });
		verifyForward(ActionForwards.update_failure.toString());
	}

	public void testLoadSuccessForGroup() throws PageExpiredException {
		createInitialObjects();
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "loadStatus");
		addRequestParameter("customerId", group.getCustomerId().toString());
		getRequest().getSession().setAttribute("security_param", "Group");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward(ActionForwards.loadStatus_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST,
				request));
		assertEquals("Size of the status list should be 2", 2,
				((List<CustomerStatusEntity>) SessionUtils.getAttribute(
						SavingsConstants.STATUS_LIST, request)).size());
	}

	public void testPreviewSuccessForGroup() throws PageExpiredException {
		createInitialObjects();
		invokeLoadSuccessfully();
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "previewStatus");
		addRequestParameter("notes", "Test");
		addRequestParameter("levelId", group.getCustomerLevel().getId()
				.toString());
		addRequestParameter("newStatusId", "10");
		addRequestParameter("flagId", "");
		getRequest().getSession().setAttribute("security_param", "Group");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward(ActionForwards.previewStatus_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertEquals(getStatusName(CustomerStatus
				.getStatus(Short.valueOf("10"))), (String) SessionUtils
				.getAttribute(SavingsConstants.NEW_STATUS_NAME, request));
		assertNull("Since new Status is not Closed,so flag should be null.",
				SessionUtils.getAttribute(SavingsConstants.FLAG_NAME, request));
	}

	public void testPreviewStatusFailureWithAllValuesNullForGroup()
			throws Exception {
		createInitialObjects();
		invokeLoadSuccessfully();
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "previewStatus");
		addRequestParameter("levelId", group.getCustomerLevel().getId()
				.toString());
		addRequestParameter("flagId", "20");
		getRequest().getSession().setAttribute("security_param", "Group");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		assertEquals(2, getErrrorSize());
		assertEquals("Status id", 1,
				getErrrorSize(CustomerConstants.MANDATORY_SELECT));
		assertEquals("Notes", 1,
				getErrrorSize(CustomerConstants.MANDATORY_TEXTBOX));
		verifyInputForward();
	}

	public void testPreviewStatusFailureWithFlagValueNullForGroup()
			throws Exception {
		createInitialObjects();
		invokeLoadSuccessfully();
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "previewStatus");
		addRequestParameter("notes", "Test");
		addRequestParameter("levelId", group.getCustomerLevel().getId()
				.toString());
		addRequestParameter("newStatusId", "12");
		getRequest().getSession().setAttribute("security_param", "Group");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		assertEquals(1, getErrrorSize());
		assertEquals("flag id", 1,
				getErrrorSize(CustomerConstants.MANDATORY_SELECT));
		verifyInputForward();
	}

	public void testPreviewStatusFailureWhenStatusIsNull()
			throws PageExpiredException {
		createInitialObjects();
		invokeLoadSuccessfully();
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "previewStatus");
		addRequestParameter("levelId", group.getCustomerLevel().getId()
				.toString());
		addRequestParameter("flagId", "20");
		addRequestParameter("notes", "Test");
		getRequest().getSession().setAttribute("security_param", "Group");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		assertEquals(1, getErrrorSize());
		assertEquals("Status", 1,
				getErrrorSize(CustomerConstants.MANDATORY_SELECT));
		verifyInputForward();
	}

	public void testPreviewStatusFailureWhenNotesIsNull()
			throws PageExpiredException {
		createInitialObjects();
		invokeLoadSuccessfully();
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "previewStatus");
		addRequestParameter("levelId", group.getCustomerLevel().getId()
				.toString());
		addRequestParameter("newStatusId", "12");
		addRequestParameter("flagId", "20");
		getRequest().getSession().setAttribute("security_param", "Group");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		assertEquals(1, getErrrorSize());
		assertEquals("Notes", 1,
				getErrrorSize(CustomerConstants.MANDATORY_TEXTBOX));
		verifyInputForward();
	}

	public void testPreviousStatus() {
		createInitialObjects();
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "previousStatus");
		addRequestParameter("customerId", group.getCustomerId().toString());
		getRequest().getSession().setAttribute("security_param", "Group");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward(ActionForwards.previousStatus_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testCancelStatus() {
		createInitialObjects();
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "cancelStatus");
		addRequestParameter("input", "group");
		getRequest().getSession().setAttribute("security_param", "Group");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward(ActionForwards.group_detail_page.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testUpdateStatusSuccess() {
		createInitialObjects(CustomerStatus.CENTER_ACTIVE,
				CustomerStatus.GROUP_PARTIAL, CustomerStatus.CLIENT_CLOSED);
		invokeLoadAndPreviewSuccessfully(CustomerStatus.GROUP_CLOSED,
				CustomerStatusFlag.GROUP_CLOSED_BLACKLISTED);
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "updateStatus");
		addRequestParameter("input", "group");
		getRequest().getSession().setAttribute("security_param", "Group");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward(ActionForwards.group_detail_page.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertNull(request.getAttribute(Constants.FLOWMANAGER));
		assertTrue(group.isBlackListed());
	}

	public void testUpdateStatusSuccessWhileChangingStatusToActive() {
		createInitialObjects(CustomerStatus.CENTER_ACTIVE,
				CustomerStatus.GROUP_PARTIAL, CustomerStatus.CLIENT_CLOSED);
		invokeLoadAndPreviewSuccessfully(CustomerStatus.GROUP_ACTIVE, null);
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "updateStatus");
		addRequestParameter("input", "group");
		getRequest().getSession().setAttribute("security_param", "Group");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward(ActionForwards.group_detail_page.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertNull(request.getAttribute(Constants.FLOWMANAGER));
		assertEquals("ActivationDate should be the current date.", DateUtils
				.getDateWithoutTimeStamp(new java.util.Date().getTime()),
				DateUtils.getDateWithoutTimeStamp(group
						.getCustomerActivationDate().getTime()));
	}

	public void testUpdateStatusFailureWhenGroupHasActiveAccounts()
			throws CustomerException {
		createInitialObjects();
		loanBO = getLoanAccount(group,"dsafdsfsdgfdg","23vf");
		group.update();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		invokeLoadAndPreviewSuccessfully(CustomerStatus.GROUP_CLOSED,
				CustomerStatusFlag.GROUP_CLOSED_OTHER);
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "updateStatus");
		addRequestParameter("input", "group");
		getRequest().getSession().setAttribute("security_param", "Group");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
		verifyActionErrors(new String[] { CustomerConstants.CUSTOMER_HAS_ACTIVE_ACCOUNTS_EXCEPTION });
		HibernateUtil.closeSession();
		center = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				center.getCustomerId());
		group = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				group.getCustomerId());
		client = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				client.getCustomerId());
		loanBO = (LoanBO) TestObjectFactory.getObject(LoanBO.class, loanBO
				.getAccountId());
	}

	public void testUpdateStatusFailureWhenGroupHasActiveClients()
			throws CustomerException {
		createInitialObjects();
		invokeLoadAndPreviewSuccessfully(CustomerStatus.GROUP_CLOSED,
				CustomerStatusFlag.GROUP_CLOSED_OTHER);
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "updateStatus");
		addRequestParameter("input", "group");
		getRequest().getSession().setAttribute("security_param", "Group");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
		verifyActionErrors(new String[] { CustomerConstants.ERROR_STATE_CHANGE_EXCEPTION });
	}

	public void testUpdateStatusFailureWhenGroupHasActiveClientsWhileChangingStatusCancel()
			throws CustomerException {
		createInitialObjects();
		invokeLoadAndPreviewSuccessfully(CustomerStatus.GROUP_CANCELLED,
				CustomerStatusFlag.GROUP_CANCEL_OTHER);
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "updateStatus");
		addRequestParameter("input", "group");
		getRequest().getSession().setAttribute("security_param", "Group");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
		verifyActionErrors(new String[] { GroupConstants.GROUP_CLIENTS_ARE_ACTIVE });
	}

	public void testUpdateStatusFailureWhenGroupHasActiveClientsWhenOfficeInactiveWhileChangingStatusCancelToPartial()
			throws NumberFormatException, Exception {
		createInitialObjectsOfficeInactive(CustomerStatus.GROUP_CANCELLED,
				CustomerStatus.CLIENT_CLOSED);
		OfficeBO officeBO = group.getOffice();
		officeBO.update(officeBO.getOfficeName(), officeBO.getShortName(),
				OfficeStatus.INACTIVE, officeBO.getOfficeLevel(), officeBO
						.getParentOffice(), null, null);
		HibernateUtil.commitTransaction();
		invokeLoadAndPreviewSuccessfully(CustomerStatus.GROUP_PARTIAL, null);
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "updateStatus");
		addRequestParameter("input", "group");
		getRequest().getSession().setAttribute("security_param", "Group");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
		verifyActionErrors(new String[] { GroupConstants.BRANCH_INACTIVE });
	}

	public void testUpdateStatusFailureWhenGroupHasActiveClientsWhenCenterIsInactiveWhileChangingStatusCancelToPartial()
			throws CustomerException {
		createInitialObjects(CustomerStatus.CENTER_ACTIVE,
				CustomerStatus.GROUP_CANCELLED, CustomerStatus.CLIENT_CLOSED);
		center.changeStatus(CustomerStatus.CENTER_INACTIVE.getValue(), null,
				"center is inactive now");
		HibernateUtil.commitTransaction();
		invokeLoadAndPreviewSuccessfully(CustomerStatus.GROUP_PARTIAL, null);
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "updateStatus");
		addRequestParameter("input", "group");
		getRequest().getSession().setAttribute("security_param", "Group");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
		verifyActionErrors(new String[] { GroupConstants.CENTER_INACTIVE });
	}

	public void testUpdateStatusFailureWhenGroupIsUnderBranchWhileChangingStatusCancelToPartial()
			throws CustomerException {
		createInitialObjectsWhenCenterHierarchyNotExist(
				CustomerStatus.GROUP_CANCELLED, CustomerStatus.CLIENT_CLOSED);
		invokeLoadAndPreviewSuccessfully(CustomerStatus.GROUP_PARTIAL, null);
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "updateStatus");
		addRequestParameter("input", "group");
		getRequest().getSession().setAttribute("security_param", "Group");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
		verifyActionErrors(new String[] { GroupConstants.LOANOFFICER_INACTIVE });
	}

	public void testChangeStatusToActiveForGroupUnderBranchWithNoLO()
			throws CustomerException {
		createInitialObjectsWhenCenterHierarchyNotExistWithNoLO(
				CustomerStatus.GROUP_PARTIAL, CustomerStatus.CLIENT_CLOSED);
		invokeLoadAndPreviewSuccessfully(CustomerStatus.GROUP_ACTIVE, null);
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "updateStatus");
		addRequestParameter("input", "group");
		getRequest().getSession().setAttribute("security_param", "Group");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
		verifyActionErrors(new String[] { GroupConstants.GROUP_LOANOFFICER_NOT_ASSIGNED });
	}

	public void testUpdateStatusFailureWhenGroupIsUnderBranchWitnNoMeetingsWhileChangingStatusToActive()
			throws CustomerException {
		createInitialObjectsWhenCenterHierarchyNotExistWithNoMeeting(
				CustomerStatus.GROUP_PARTIAL, CustomerStatus.CLIENT_CLOSED);
		invokeLoadAndPreviewSuccessfully(CustomerStatus.GROUP_ACTIVE, null);
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "updateStatus");
		addRequestParameter("input", "group");
		getRequest().getSession().setAttribute("security_param", "Group");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
		verifyActionErrors(new String[] { GroupConstants.MEETING_NOT_ASSIGNED });
	}

	private void invokeLoadSuccessfully() {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "loadStatus");
		addRequestParameter("customerId", group.getCustomerId().toString());
		getRequest().getSession().setAttribute("security_param", "Group");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward(ActionForwards.loadStatus_success.toString());
	}

	private void invokeLoadAndPreviewSuccessfully(CustomerStatus groupStatus,
			CustomerStatusFlag groupStatusFlag) {
		invokeLoadSuccessfully();
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "previewStatus");
		addRequestParameter("notes", "Test");
		addRequestParameter("levelId", group.getCustomerLevel().getId()
				.toString());
		if (groupStatus != null)
			addRequestParameter("newStatusId", groupStatus.getValue()
					.toString());
		if (groupStatusFlag != null)
			addRequestParameter("flagId", groupStatusFlag.getValue().toString());
		getRequest().getSession().setAttribute("security_param", "Group");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward(ActionForwards.previewStatus_success.toString());
	}

	private void createInitialObjects() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center",
				CustomerStatus.CENTER_ACTIVE.getValue(), "1.1", meeting,
				new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group",
				CustomerStatus.GROUP_ACTIVE.getValue(), "1.1.1", center,
				new Date(System.currentTimeMillis()));
		client = TestObjectFactory.createClient("Client",
				CustomerStatus.CLIENT_ACTIVE.getValue(), "1.1.1", group,
				new Date(System.currentTimeMillis()));
	}

	private void createInitialObjects(CustomerStatus centerStatus,
			CustomerStatus groupStatus, CustomerStatus clientStatus) {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", centerStatus
				.getValue(), "1.1", meeting, new Date(System
				.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", groupStatus.getValue(),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		client = TestObjectFactory.createClient("Client", clientStatus
				.getValue(), "1.1.1", group, new Date(System
				.currentTimeMillis()));
	}

	private void createInitialObjectsWhenCenterHierarchyNotExist(
			CustomerStatus groupStatus, CustomerStatus clientStatus) {
		Short officeId = new Short("3");
		Short personnel = new Short("1");
		group = TestObjectFactory.createGroupUnderBranch("Group", groupStatus,
				officeId, getMeeting(), personnel);
		client = TestObjectFactory.createClient("new client", clientStatus
				.getValue(), group, new java.util.Date());
	}

	private void createInitialObjectsWhenCenterHierarchyNotExistWithNoLO(
			CustomerStatus groupStatus, CustomerStatus clientStatus) {
		Short officeId = new Short("3");
		group = TestObjectFactory.createGroupUnderBranch("Group", groupStatus,
				officeId, getMeeting(), null);
		client = TestObjectFactory.createClient("new client", clientStatus
				.getValue(), group, new java.util.Date());
	}

	private void createObjectsForClient(String name) throws Exception {
		office = TestObjectFactory.createOffice(OfficeLevel.BRANCHOFFICE,
				TestObjectFactory.getOffice(Short.valueOf("1")),
				"customer_office", "cust");
		client = TestObjectFactory.createClient(name, getMeeting(),
				CustomerStatus.CLIENT_PARTIAL.getValue(), new java.util.Date());
	}

	private void createClientWithoutMeeting(String name) throws Exception {
		office = TestObjectFactory.createOffice(OfficeLevel.BRANCHOFFICE,
				TestObjectFactory.getOffice(Short.valueOf("1")),
				"customer_office", "cust");
		client = TestObjectFactory.createClient(name, null,
				CustomerStatus.CLIENT_PARTIAL.getValue(), new java.util.Date());
	}

	private void createInitialObjectsOfficeInactive(CustomerStatus groupStatus,
			CustomerStatus clientStatus) throws NumberFormatException,
			Exception {
		office = TestObjectFactory.createOffice(OfficeLevel.BRANCHOFFICE,
				TestObjectFactory.getOffice(Short.valueOf("1")),
				"customer_office", "cust");
		group = TestObjectFactory.createGroupUnderBranch("Group", groupStatus,
				office.getOfficeId(), getMeeting(), null);
		client = TestObjectFactory.createClient("new client", clientStatus
				.getValue(), group, new java.util.Date());
	}

	private void createInitialObjectsWhenCenterHierarchyNotExistWithNoMeeting(
			CustomerStatus groupStatus, CustomerStatus clientStatus) {
		Short officeId = new Short("3");
		Short personnel = new Short("1");
		group = TestObjectFactory.createGroupUnderBranch("Group", groupStatus,
				officeId, null, personnel);
		client = TestObjectFactory.createClient("new client", clientStatus
				.getValue(), group, new java.util.Date());
	}

	private MeetingBO getMeeting() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		//meeting.setMeetingStartDate(new GregorianCalendar());
		return meeting;
	}

	private LoanBO getLoanAccount(CustomerBO customerBO,String offeringName,String shortName) {
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				offeringName, shortName,Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), center.getCustomerMeeting().getMeeting());
		return TestObjectFactory.createLoanAccount("42423142341", customerBO,
				Short.valueOf("5"), new Date(System.currentTimeMillis()),
				loanOffering);
	}

	private String getStatusName(CustomerStatus customerStatus)
			throws PageExpiredException {
		List<CustomerStatusEntity> customerStatusList = (List<CustomerStatusEntity>) SessionUtils
				.getAttribute(SavingsConstants.STATUS_LIST, request);
		for (CustomerStatusEntity custStatus : customerStatusList) {
			if (customerStatus.getValue().equals(custStatus.getId()))
				return custStatus.getName();
		}
		return null;
	}
}
