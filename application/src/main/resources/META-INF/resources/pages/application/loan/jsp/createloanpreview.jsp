<%--
Copyright (c) 2005-2011 Grameen Foundation USA
All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
implied. See the License for the specific language governing
permissions and limitations under the License.

See also http://www.apache.org/licenses/LICENSE-2.0.html for an
explanation of the license and how it is applied.
--%>

<%@taglib uri="/tags/struts-html" prefix="html"%>
<%@taglib uri="/tags/struts-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/loan/loanfunctions" prefix="loanfn"%>
<%@ taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<tiles:insert definition=".withoutmenu">
	<tiles:put name="body" type="string">
	<span id="page.id" title="CreateLoanPreview"></span>

		<SCRIPT SRC="pages/application/loan/js/CreateLoanAccountPreview.js"></SCRIPT>
		<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
		<c:if test="${requestScope.perspective == 'redoLoan'}">
            <script>
            function fun_cancel(form)
            {
                location.href="AdminAction.do?method=load";
            }
            </script>
        </c:if>
        <SCRIPT>
		function displayAdminFormula(feeId , index ){
			var span = document.getElementById("feeFormulaAdminSpan"+index);
			if( feeId!= null)
				{	
					if(document.getElementById("FEE_"+feeId)!=null ){
						formula = document.getElementById("FEE_"+feeId).value;
						span.style.display="block";
						span.innerHTML ="% of "+formula;
					}else{
						span.style.display="none";
						span.innerHTML="";
					}
				}
				else
				{	
					span.style.display="none";
					span.innerHTML="";
				}
		}
		</SCRIPT>
		<STYLE TYPE="text/css">
		   	.viewQuestionResponseDiv {
		   	     padding : 0pt;
	         }
	   		  
	         .viewQuestionResponseDiv ul {
	   	        list-style-type : none;
	   	        padding : 0pt; 
             }	
		</STYLE>
		<html-el:form action="/loanAccountAction.do">
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanOffering')}" var="LoanOffering" />
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanAccountOwner')}" var="customer" />
            <c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanAmount')}" var="loanAmount" />
            <c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'interestRate')}" var="interestRate" />
            <c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'noOfInstallments')}" var="noOfInstallments" />
            <c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'disbursementDate')}" var="disbursementDate" />
            <c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'gracePeriodDuration')}" var="gracePeriodDuration" />
            <c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'fundName')}" var="fundName" />
            <c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'interestDeductedAtDisbursement')}" var="interestDeductedAtDisbursement" />
            <c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'businessActivityId')}" var="businessActivityId" />
            <c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'collateralTypeId')}" var="collateralTypeId" />
            <c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'collateralNote')}" var="collateralNote" />
            <c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'externalId')}" var="externalId" />
            <c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'accountFees')}" var="accountFees" />
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanIndividualMonitoringIsEnabled')}" var="loanIndividualMonitoringIsEnabled" />
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanaccountownerisagroup')}" var="loanaccountownerisagroup" />			
			
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td height="470" align="left" valign="top" bgcolor="#FFFFFF">
						<table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
							<tr>
								<td align="center" class="heading">
									&nbsp;
								</td>
							</tr>
						</table>
						<table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
							<tr>
								<td class="bluetablehead">
									<table width="100%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td width="25%">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td>
															<img src="pages/framework/images/timeline/tick.gif" width="17" height="17">
														</td>
														<td class="timelineboldgray">
															<mifos:mifoslabel name="loan.Select" />
															<mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" />
															<mifos:mifoslabel name="loan.Slash" />
															<mifos:mifoslabel name="${ConfigurationConstants.GROUP}" />

														</td>
													</tr>
												</table>
											</td>
											<td width="25%" align="center">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td>
															<img src="pages/framework/images/timeline/tick.gif" width="17" height="17">
														</td>
														<td class="timelineboldgray">
															<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" />
															<mifos:mifoslabel name="loan.acc_info" />
														</td>
													</tr>
												</table>
											</td>
											<td width="25%" align="right">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td>
															<img src="pages/framework/images/timeline/tick.gif" width="17" height="17">
														</td>
														<td class="timelineboldgray">
															<mifos:mifoslabel name="loan.review/edit_ins" />
														</td>
													</tr>
												</table>
											</td>
											<td width="25%" align="right">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td>
															<img src="pages/framework/images/timeline/bigarrow.gif" width="17" height="17">
														</td>
														<td class="timelineboldorange">
															<mifos:mifoslabel name="loan.review&submit" />
														</td>
													</tr>
												</table>
											</td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
						<table width="90%" border="0" align="center" cellpadding="0" cellspacing="0" class="bluetableborder">
							<tr>
								<td width="70%" height="24" align="left" valign="top" class="paddingleftCreates">
									<table width="98%" border="0" cellspacing="0" cellpadding="3">
                                        <c:if test="${requestScope.perspective == 'redoLoan'}">
                                        <tr>
                                            <td><span class="fontnormalRedBold"><mifos:mifoslabel name="loan.redo_loan_note"/></span></td>
                                        </tr>
                                        </c:if>
                                        <tr>
											<td class="headingorange">
												<span class="heading">
                                                    <c:choose>
                                                    <c:when test="${requestScope.perspective == 'redoLoan'}">
                                                        <mifos:mifoslabel name="admin.redo" />
                                                    </c:when>
                                                    <c:otherwise>
                                                        <mifos:mifoslabel name="accounts.create" />
                                                    </c:otherwise>
                                                    </c:choose>
                                                    <mifos:mifoslabel name="${ConfigurationConstants.LOAN}" />
                                                    <mifos:mifoslabel name="accounts.accountLower" />&nbsp;-&nbsp;
                                                </span>
												<mifos:mifoslabel name="loan.preview" />
												<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" />
												<mifos:mifoslabel name="loan.acc_info" />
											</td>
										</tr>
										<tr>
											<td class="fontnormal">
												<mifos:mifoslabel name="loan.reviewaccontinfo" />
											</td>
										</tr>
										<tr>
											<td>
												<font class="fontnormalRedBold"> <span id="createloanpreview.error.message"><html-el:errors bundle="loanUIResources" /></span> </font>
											</td>
										</tr>
										<tr>
											<td class="fontnormal">
												<br>
												<span class="fontnormalbold"> <mifos:mifoslabel name="loan.acc_owner" />: </span>
												<c:out value="${customer.displayName}" />
											</td>
										</tr>
									</table>
									<br>
									<table width="93%" border="0" cellpadding="0" cellspacing="0">
										<tr>
											<td width="100%" height="23" class="fontnormalboldorange">
												<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" />
												<mifos:mifoslabel name="loan.acc_info" />
											</td>
										</tr>
										<tr>
											<td height="23" class="fontnormalbold">
												<span class="fontnormal"></span> <span class="fontnormal"></span>
												<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" />
												<mifos:mifoslabel name="loan.instancename" />
												:&nbsp; <span class="fontnormal"> <c:out value="${LoanOffering.prdOfferingName}" /><br> <br> </span>
												<mifos:mifoslabel name="loan.instance_info" />
												<span class="fontnormal"><br> </span>
												<mifos:mifoslabel name="loan.description" />
												:&nbsp; <span class="fontnormal"> <c:out value="${LoanOffering.description}" /> <br> </span>
												<mifos:mifoslabel name="${ConfigurationConstants.INTEREST}" />
												<mifos:mifoslabel name="loan.interest_type" />
												:&nbsp; <span class="fontnormal"> <c:out value="${LoanOffering.interestTypes.name}" /> <br> </span>
												<mifos:mifoslabel name="loan.freq_of_inst" />
												:&nbsp; <span class="fontnormal"> <c:out value="${LoanOffering.loanOfferingMeeting.meeting.meetingDetails.recurAfter}" />&nbsp; <c:choose>
														<c:when test="${LoanOffering.loanOfferingMeeting.meeting.meetingDetails.recurrenceType.recurrenceId == 1}">
															<mifos:mifoslabel name="loan.week(s)" />
														</c:when>
														<c:otherwise>
															<mifos:mifoslabel name="loan.month(s)" />
														</c:otherwise>
													</c:choose><br> </span>
												<mifos:mifoslabel name="loan.principle_due" />
												:&nbsp; <span class="fontnormal"> <c:choose>
														<c:when test="${LoanOffering.prinDueLastInst}">
															<mifos:mifoslabel name="loan.yes" />
														</c:when>
														<c:otherwise>
															<mifos:mifoslabel name="loan.no" />
														</c:otherwise>
													</c:choose> <br> </span>
												<mifos:mifoslabel name="loan.grace_period_type" />
												:&nbsp; <span class="fontnormal"> <c:out value="${LoanOffering.gracePeriodType.name}" /><br> <br> <br> </span>
											</td>
										</tr>
										<!-- Loan Account Details -->

								 <c:if test="${loanIndividualMonitoringIsEnabled == '1'}">
									<c:if test="${loanaccountownerisagroup == 'yes'}">
									    <tr>
											<td valign="top">
	                                            
                                                <mifoscustom:mifostabletag source="loanAccountDetailsView" scope="session" xmlFileName="LoanAccountDetails.xml" moduleName="org/mifos/accounts/loan/util/resources" passLocale="true" />

											</td>
										</tr>
										<br><br>
                                    </c:if>
                                 </c:if>										
										<!--  -->
										<tr>
											<td>
												&nbsp;
											</td>
										</tr>
										<tr>
											<td class="fontnormalbold">
												<mifos:mifoslabel name="loan.amount" />
												:&nbsp; <span class="fontnormal"> <span id="createloanpreview.text.loanamount"><fmt:formatNumber value="${loanAmount}" /></span> <mifos:mifoslabel name="loan.allowed_amount"></mifos:mifoslabel>&nbsp; <fmt:formatNumber value="${loanAccountActionForm.minLoanAmount}" /> &nbsp; - &nbsp; <fmt:formatNumber
														value="${loanAccountActionForm.maxLoanAmount}" />) </span>
											</td>
										</tr>
										<tr>
											<td class="fontnormalbold">
												<mifos:mifoslabel name="${ConfigurationConstants.INTEREST}" />
												<mifos:mifoslabel name="loan.interest_rate" />
												:&nbsp; <span class="fontnormal"> <fmt:formatNumber value="${interestRate}" /> <mifos:mifoslabel name="loan.allowed_interest1" /> <mifos:mifoslabel name="${ConfigurationConstants.INTEREST}" /> <mifos:mifoslabel
														name="loan.allowed_interest2" /> :&nbsp; <fmt:formatNumber value="${LoanOffering.minInterestRate}" />&nbsp; - &nbsp; <formatNumber value="${LoanOffering.maxInterestRate}" />) </span>
											</td>
										</tr>
										<tr>
											<td class="fontnormalbold">
												<mifos:mifoslabel name="loan.no_of_inst" />
												:&nbsp; <span class="fontnormal"> <c:out value="${noOfInstallments}" /> <mifos:mifoslabel name="loan.allowed_no_of_inst"></mifos:mifoslabel>&nbsp; <fmt:formatNumber value="${loanAccountActionForm.minNoInstallments}" />&nbsp;
													- &nbsp; <fmt:formatNumber value="${loanAccountActionForm.maxNoInstallments}" />) </span>
											</td>
										</tr>
										<tr>
											<td class="fontnormalbold">
												<mifos:mifoslabel name="loan.proposed_date" />
												:&nbsp; <span class="fontnormal"><c:out value="${disbursementDate}" /> </span>
											</td>
										</tr>
										<tr>
											<td class="fontnormalbold">
												<mifos:mifoslabel name="loan.grace_period" />
												:&nbsp; <span class="fontnormal"> <c:out value="${gracePeriodDuration}" />&nbsp; <mifos:mifoslabel name="loan.inst"></mifos:mifoslabel> </span>
											</td>
										</tr>
										<tr>
											<td class="fontnormalbold">
												<mifos:mifoslabel name="loan.source_fund" />
												:&nbsp; <span class="fontnormal"> <c:out value="${fundName}" /> </span>
											</td>
										</tr>
										<tr>
											<td class="fontnormalbold">
												<mifos:mifoslabel name="${ConfigurationConstants.INTEREST}" />
												<mifos:mifoslabel name="loan.interest_disb" />
												:&nbsp; <span class="fontnormal"> <c:choose>
														<c:when test="${interestDeductedAtDisbursement}">
															<mifos:mifoslabel name="loan.yes" />
														</c:when>
														<c:otherwise>
															<mifos:mifoslabel name="loan.no" />
														</c:otherwise>
													</c:choose> </span>
											</td>
										</tr>
										<c:if test="${loanaccountownerisagroup != 'yes'}">
											<tr id="Loan.PurposeOfLoan">
												<td class="fontnormalbold">
													<mifos:mifoslabel name="loan.business_work_act" keyhm="Loan.PurposeOfLoan" isManadatoryIndicationNotRequired="yes" />
													<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" isColonRequired="yes" />
													&nbsp; <span class="fontnormal"> <c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessActivities')}" var="busId">
															<c:if test="${busId.id eq businessActivityId}">
																<c:out value="${busId.name}" />
															</c:if>
														</c:forEach> </span>
												</td>
											</tr>
										</c:if>
										
										<tr id="Loan.CollateralType">
											<td class="fontnormalbold">
												<mifos:mifoslabel name="loan.collateral_type" keyhm="Loan.CollateralType" isColonRequired="yes" isManadatoryIndicationNotRequired="yes" />
												&nbsp; <span class="fontnormal"> <c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'CollateralTypes')}" var="collateralType">
														<c:if test="${collateralType.id eq collateralTypeId}">
															<c:out value="${collateralType.name}" />
														</c:if>
													</c:forEach></span>
											</td>
										</tr>
										<tr id="Loan.CollateralNotes">
											<td class="fontnormalbold">
												<mifos:mifoslabel name="loan.collateral_notes" keyhm="Loan.CollateralNotes" isColonRequired="yes" isManadatoryIndicationNotRequired="yes" />
												&nbsp; <span class="fontnormal"><br> <c:out value="${collateralNote}" /> </span>
											</td>
										</tr>
                                        <tr id="Loan.ExternalId">
                                            <td class="fontnormalbold">
                                                <mifos:mifoslabel name="accounts.externalId" keyhm="Loan.ExternalId" isColonRequired="yes" isManadatoryIndicationNotRequired="yes" />
                                                &nbsp; <span class="fontnormal"><c:out value="${externalId}" /> </span>
                                            </td>
                                        </tr>
										<c:if test="${!empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}">
										<tr>
											<td class="fontnormalbold">
											<br>
											    <mifos:mifoslabel name="loan.additionalInfo" bundle="loanUIResources"/><br>
							                    	<c:forEach var="cfdef" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}">
														<c:forEach var="cf" items="${sessionScope.loanAccountActionForm.customFields}">
															<c:if test="${cfdef.fieldId==cf.fieldId}">
																<mifos:mifoslabel name="${cfdef.lookUpEntity.entityType}" bundle="loanUIResources"></mifos:mifoslabel>: 
										        		  	 	<span class="fontnormal">
                                                                       <c:out value="${cf.fieldValue}" />
															</span><br>
														</c:if>
													</c:forEach>
									  			</c:forEach>
											</td>
										</tr>
										</c:if>
										<tr>
											<td class="fontnormalbold">
												<br>
												<mifos:mifoslabel name="loan.charged_applied" />
												<br>
												<br>
												<c:forEach var="fee" items="${requestScope.feeFormulaList}" varStatus="loopStatus3">
													<input type="hidden" id="FEE_${fee.feeFormulaId}" value="${fee.feeFormulaName}" />
												</c:forEach>
												<c:forEach items="${accountFees}" var="feesSet">
													<c:if test="${!empty feesSet.feeId}">
														<!--<span class="fontnormal">-->
														<table cellpadding="0" cellspacing="0">
															<tr>
																<td class="fontnormalbold" align="left">
																	<c:out value="${feesSet.feeName}" />
																	&nbsp;
																	<mifos:mifoslabel name="loan.amt" />
																	:&nbsp;
																</td>
																<td class="fontnormal" align="left">
																	<fmt:formatNumber value="${feesSet.amount}" />
																	&nbsp;&nbsp;
																</td>
																<td class="fontnormal" align="left">
																	<!--<SPAN id="feeFormulaAdminSpan${feesSet.feeId}" class="fontnormal"/></SPAN>
															<SCRIPT> displayAdminFormula(${feesSet.feeId},${feesSet.feeId}); </SCRIPT> -->
																</td>
																<td class="fontnormal" align="left">
																	&nbsp;
																	<mifos:mifoslabel name="loan.periodicity" />
																	:
																	<c:choose>
																		<c:when test="${feesSet.periodic}">
																			<c:out value="${feesSet.feeSchedule}" />
																		</c:when>
																		<c:otherwise>
																			<mifos:mifoslabel name="loan.periodicityTypeFlat" />
																		</c:otherwise>
																	</c:choose>
																	<br>
																</td>
														</table>
														<!--</span>-->
													</c:if>
												</c:forEach>
												<br>

												<span class="fontnormal"> <br/> <html-el:button styleId="createloanpreview.button.edit" property="editButton" styleClass="insidebuttn" onclick="fnEdit(this.form)">
													<mifos:mifoslabel name="loan.edit_loan_acc" />
												</html-el:button> </span>
												<span class="fontnormal"> <br> <br> </span>

												<table width="80%" border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td class="headingorange">
															<mifos:mifoslabel name="loan.install_paid"></mifos:mifoslabel>
														</td>
														<td>
															&nbsp;
														</td>
													</tr>
													<tr>
														<td height="22" class="fontnormalbold" align="center">
															<mifos:mifoslabel name="loan.amount_due" />
														</td>
													</tr>
													<tr>
														<td align="center" class="blueline">
															<img src="pages/framework/images/trans.gif" width="5" height="3">
														</td>
													</tr>
													<tr>
														<td valign="top">
                                                            <c:choose>
                                                                <c:when test="${requestScope.perspective == 'redoLoan'}">
                                                                    <loanfn:getLoanRepaymentTable />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <mifoscustom:mifostabletag source="installments" scope="session" 
                                                                    xmlFileName="ProposedRepaymentSchedule.xml" moduleName="org/mifos/accounts/loan/util/resources" passLocale="true" />
                                                                </c:otherwise>
                                                            </c:choose>
														</td>
													</tr>
												</table>
												<span class="fontnormal"> <br> <html-el:button styleId="createloanpreview.button.edit" property="editButton" styleClass="insidebuttn" onclick="fnScheduleEdit(this.form)">
													<mifos:mifoslabel name="loan.edit_loan_schedule" />
												</html-el:button> </span>
											</td>
										</tr>
									</table>
									<c:set var="questionsHostForm" value="${loanAccountActionForm}" scope="request" />
									<c:import url="/pages/application/surveys/jsp/viewQuestionResponses.jsp">
									   <c:param name="editResponseURL" value="loanAccountAction.do?method=editQuestionResponses&currentFlowKey=${requestScope.currentFlowKey}"/>
									   <c:param name="responseDivStyleClass" value="viewQuestionResponseDiv"/>									   
									</c:import>
									<table width="93%" border="0" cellpadding="0" cellspacing="0">
										<tr>
											<td align="center" class="blueline">
												&nbsp;
											</td>
										</tr>
									</table>
									<br>
									<html-el:hidden property="method" value="create" />
									<table width="93%" border="0" cellpadding="0" cellspacing="0">
										<tr>
											<td align="center">
												<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'pendingApprovalDefined')}" var="PendingApproval" />
												<c:if test="${requestScope.perspective == 'redoLoan'}">
                                                    <html-el:button styleId="createloanpreview.button.submitForApproval" property="submitForApprovalButton" styleClass="buttn" onclick="javascript:fun_submitForApproval(this.form)">
                                                        <mifos:mifoslabel name="loan.submit" />
                                                    </html-el:button>
                                                </c:if>
                                                <c:if test="${requestScope.perspective != 'redoLoan'}">
                                                    <html-el:button styleId="createloanpreview.button.saveForLater" property="saveForLaterButton" styleClass="buttn"  onclick="javascript:fun_saveForLater(this.form)">
                                                        <mifos:mifoslabel name="loan.saveForLater" />
                                                    </html-el:button>
                                                    &nbsp;
                                                    <c:choose>
                                                        <c:when test='${PendingApproval == true}'>
                                                            <html-el:button styleId="createloanpreview.button.submitForApproval" property="submitForApprovalButton" styleClass="buttn"  onclick="javascript:fun_submitForApproval(this.form)">
                                                                <mifos:mifoslabel name="loan.submitForApproval" />
                                                            </html-el:button>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <html-el:button styleId="createloanpreview.button.approve" property="approvedButton" styleClass="buttn"  onclick="javascript:fun_approved(this.form)">
                                                                <mifos:mifoslabel name="loan.approved" />
                                                            </html-el:button>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:if>
												&nbsp;

												<html-el:button styleId="createloanpreviews.button.cancel" property="cancelButton" styleClass="cancelbuttn"  onclick="javascript:fun_cancel(this.form)">
													<mifos:mifoslabel name="loan.cancel" />
												</html-el:button>
											</td>
											<!-- This hidden field is being used in the customPreview method of the LoanAction class to discriminate the preview method call-->
											<html-el:hidden property="preview_mode" value="create" />
											<html-el:hidden property="input" value="accountPreview" />
											<html-el:hidden value="" property="stateSelected" />
											<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
                                            <html-el:hidden property="perspective" value="${requestScope.perspective}" />
                                        </tr>
									</table>
									<br>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</html-el:form>
	</tiles:put>
</tiles:insert>
