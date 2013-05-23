/**
 * Copyright (C) 2013
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.epos.engine.filter.it;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.Assert;
import org.junit.Test;
import org.n52.epos.engine.EposEngine;
import org.n52.epos.engine.rules.RuleInstance;
import org.n52.epos.event.EposEvent;
import org.n52.epos.filter.FilterInstantiationException;
import org.n52.epos.filter.FilterInstantiationRepository;
import org.n52.epos.filter.PassiveFilter;
import org.n52.epos.rules.PassiveFilterAlreadyPresentException;
import org.n52.epos.rules.Rule;
import org.n52.epos.rules.RuleListener;
import org.n52.epos.transform.TransformationException;
import org.n52.epos.transform.TransformationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OvershootUndershootRuleIT {

	private static final Logger logger = LoggerFactory.getLogger(OvershootUndershootRuleIT.class);
	private static final long waitTime = 5000;
	protected Object mutex = new Object();
	protected EposEvent result;

	@Test
	public void shouldCompleteRoundtripForNotification() throws XmlException, IOException,
			InterruptedException, PassiveFilterAlreadyPresentException, FilterInstantiationException, TransformationException {
		RuleListener notificationReceiver = initializeConsumer();
		
		Rule rule = subscribe(notificationReceiver, "Overshoot_Rule1.xml");
		
		notification();
		
		synchronized (mutex) {
			if (result == null) {
				mutex.wait(waitTime);
			}	
		}
		
		Assert.assertNotNull("Did not receive result back!", result);
		logger.info("Received event back: {}", result);
		
		unsubscribe(rule);
		
		result = null;
		notification();
		
		synchronized (mutex) {
			if (result == null) {
				mutex.wait(waitTime);
			}	
		}
		
		Assert.assertNull("Received a result. But the Rule should have been removed!", result);
	}

	private void unsubscribe(Rule rule) {
		EposEngine.getInstance().unregisterRule(rule);
	}

	@Test
	public void shouldCompleteRoundtripForNotificationWithUOM() throws XmlException, IOException,
			InterruptedException, PassiveFilterAlreadyPresentException, FilterInstantiationException, TransformationException {
		RuleListener notificationReceiver = initializeConsumer();
		
		Rule rule = subscribe(notificationReceiver, "Overshoot_Rule2.xml");
		
		notification();
		
		synchronized (mutex) {
			if (result == null) {
				mutex.wait(waitTime);
			}	
		}
		
		Assert.assertNotNull("Did not receive result back!", result);
		logger.info("Received event back: {}", result);
		
		unsubscribe(rule);
		
		result = null;
		notification();
		
		synchronized (mutex) {
			if (result == null) {
				mutex.wait(waitTime);
			}	
		}
		
		Assert.assertNull("Received a result. But the Rule should have been removed!", result);
	}

	private void notification() throws XmlException, TransformationException, IOException {
		List<XmlObject> notis = readNotifications();
		for (XmlObject xmlObject : notis) {
			EposEngine.getInstance().filterEvent((EposEvent) TransformationRepository.Instance.transform(
					xmlObject, EposEvent.class));
		}
	}



	private RuleListener initializeConsumer() throws IOException, InterruptedException {
		return new RuleListener() {
			
			@Override
			public void onMatchingEvent(EposEvent event) {
				synchronized (mutex) {
					result = event;
					mutex.notifyAll();
				}
			}

			@Override
			public void onMatchingEvent(EposEvent event,
					Object desiredOutputToConsumer) {
				synchronized (mutex) {
					result = event;
					mutex.notifyAll();
					logger.info("Desired output: {}", desiredOutputToConsumer);
				}				
			}
		};
	}

	private Rule subscribe(RuleListener notificationReceiver, String file) throws PassiveFilterAlreadyPresentException, FilterInstantiationException, XmlException, IOException {
		RuleInstance rule = new RuleInstance(notificationReceiver);
		rule.setPassiveFilter((PassiveFilter)
				FilterInstantiationRepository.Instance.instantiate(readSubscription(file)));
		EposEngine.getInstance().registerRule(rule);
		return rule;
	}


	private List<XmlObject> readNotifications() throws XmlException, IOException {
		List<XmlObject> result = new ArrayList<XmlObject>();
		result.add(readXmlContent("Overshoot_Notify1.xml"));
		result.add(readXmlContent("Overshoot_Notify2.xml"));
		return result;
	}

	private XmlObject readXmlContent(String resource) throws XmlException, IOException {
		return XmlObject.Factory.parse(getClass().getResourceAsStream(resource));
	}



	public XmlObject readSubscription(String file) throws XmlException, IOException {
		return readXmlContent(file);
	}


}
