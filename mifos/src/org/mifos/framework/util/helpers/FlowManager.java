/**

 * FlowManager.java    version: 1.0



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
package org.mifos.framework.util.helpers;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.hibernate.helper.QueryResult;

public class FlowManager implements Serializable{

	private Map<String, Flow> flowData = new HashMap<String, Flow>();

	private Map<String, String> classData = new HashMap<String, String>();

	public FlowManager() {
	}

	public void addFLow(String key, Flow value, String className) {
		if (classData.containsKey(className)) {
			flowData.remove(classData.get(className));
		}
		classData.put(className,key);
		flowData.put(key, value);
	}

	public boolean isFlowValid(String key) {
		return flowData.containsKey(key);
	}

	public Flow getFlow(String key) {
		return flowData.get(key);
	}

	public Flow getFlowWithValidation(String key) throws PageExpiredException {
		if (!isFlowValid(key)) {
			throw new PageExpiredException();
		}
		return getFlow(key);
	}

	/**
	 * This is placeholder method documenting the use of non-Serializable
	 * QueryResults.  It should be removed after QueryResults are made
	 * Serializable or refactored.
	 */	
	public void addQueryResultToFlow(String flowKey, String key, QueryResult value)
			throws PageExpiredException {
		Flow flow = getFlowWithValidation(flowKey);
		flow.addQueryResultToSession(key, value);
	}
	
	public void addObjectToFlow(String flowKey, String key, Serializable value)
			throws PageExpiredException {
		Flow flow = getFlowWithValidation(flowKey);
		flow.addObjectToSession(key, value);
	}

	public void addCollectionToFlow(String flowKey, String key, Collection<? extends Serializable> value)
			throws PageExpiredException {
		Flow flow = getFlowWithValidation(flowKey);
		flow.addCollectionToSession(key, value);
	}
		
	public Object getFromFlow(String flowKey, String key)
			throws PageExpiredException {
		if (!isFlowValid(flowKey)) {
			throw new PageExpiredException();
		}
		Flow flow = getFlow(flowKey);
		return flow.getObjectFromSession(key);
	}


	public void removeFlow(String key) {
		flowData.remove(key);
	}

	public void removeFromFlow(String flowKey, String key)
			throws PageExpiredException {
		if (!isFlowValid(flowKey)) {
			throw new PageExpiredException();
		}
		Flow flow = getFlow(flowKey);
		flow.removeFromSession(key);
	}

}
