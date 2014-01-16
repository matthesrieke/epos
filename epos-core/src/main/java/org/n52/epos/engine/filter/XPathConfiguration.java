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
package org.n52.epos.engine.filter;

import java.util.Map;

public class XPathConfiguration {

	private String expression;
	private Map<String, String> namespaceMappings;

	public XPathConfiguration(String expression,
			Map<String, String> namespaceMappings) {
		this.expression = expression;
		this.namespaceMappings = namespaceMappings;
	}

	public String getExpression() {
		return expression;
	}

	public Map<String, String> getNamespaceMappings() {
		return namespaceMappings;
	}
	
}