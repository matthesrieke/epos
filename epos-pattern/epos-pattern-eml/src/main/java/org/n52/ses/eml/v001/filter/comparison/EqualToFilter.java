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
/**
 * Part of the diploma thesis of Thomas Everding.
 * @author Thomas Everding
 */

package org.n52.ses.eml.v001.filter.comparison;

import java.util.HashSet;


import net.opengis.fes.x20.BinaryComparisonOpType;


/**
 * filters via ==
 * 
 * @author Thomas Everding
 *
 */
public class EqualToFilter extends ABinaryComparisonFilter{
	
	/**
	 * 
	 * Constructor
	 *
	 * @param binaryOp filter definition
	 * @param propertyNames names of the properties used in this filter / pattern
	 */
	public EqualToFilter(BinaryComparisonOpType binaryOp, HashSet<Object > propertyNames) {
		this.initialize(binaryOp, propertyNames);
	}

	
	@Override
	public String createExpressionString(boolean complexPatternGuard) {
		String result = "";
		
		if (this.first.getUsedProperty() != null || this.second.getUsedProperty() != null) {
			
			result += createUsedPropertyString();

		}
		
		String firstString = this.first.createExpressionString(complexPatternGuard);
		String secondString = this.second.createExpressionString(complexPatternGuard);
		
		/*
		 * check if the format is parsable with esper. otherwise
		 * change it (add quotes etc.)
		 */
		String[] a = checkDataTypes(complexPatternGuard, firstString, secondString);
		firstString = a[0];
		secondString = a[1];
		
		result += "("
						+ firstString
						+ " = "
						+ secondString
						+")";
		return result;
	}
}